package pl.akmf.ksef.sdk.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import io.alapierre.ksef.json.gson.GsonJsonSerializer;
import io.alapierre.ksef.json.jackson.JacksonJsonSerializer;
import org.junit.Test;

import java.io.File;
import java.lang.module.ModuleFinder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/** Compiles and launches real named consumers of the packaged artifacts. */
public class JpmsIT {
    @Test
    public void runsGsonConsumerOnModulePathWithoutJackson() throws Exception {
        verifyConsumer("gson", GsonJsonSerializer.class, List.of(Gson.class));
    }

    @Test
    public void runsJacksonConsumerOnModulePathWithoutGson() throws Exception {
        verifyConsumer("jackson", JacksonJsonSerializer.class,
                List.of(ObjectMapper.class, JsonFactory.class, JsonProperty.class, JavaTimeModule.class));
    }

    private static void verifyConsumer(String adapter, Class<?> implementation, List<Class<?>> libraries) throws Exception {
        Path build = Path.of(System.getProperty("jpms.buildDirectory"));
        Path work = Files.createDirectories(build.resolve("jpms-" + adapter));
        var jars = new LinkedHashSet<Path>();
        jars.add(Path.of(System.getProperty("jpms.clientJar")));
        String runtime = Files.readString(build.resolve("jpms-runtime-classpath.txt")).trim();
        for (String entry : runtime.split(Pattern.quote(File.pathSeparator))) {
            jars.add(Path.of(entry));
        }
        jars.add(jarFor(implementation));
        for (Class<?> library : libraries) {
            jars.add(jarFor(library));
        }

        // Rename our artifacts: stable JPMS names must come from the manifest, never the filename.
        List<String> modulePath = new ArrayList<>();
        var expected = new LinkedHashSet<>(List.of("io.alapierre.ksef.client", "io.alapierre.ksef.json",
                "io.alapierre.ksef.json." + adapter));
        for (Path jar : jars) {
            assertTrue("Expected packaged dependency: " + jar, Files.isRegularFile(jar));
            try (var archive = new JarFile(jar.toFile())) {
                String name = archive.getManifest().getMainAttributes().getValue("Automatic-Module-Name");
                if (name != null && name.startsWith("io.alapierre.ksef.")) {
                    assertTrue("Unexpected or duplicate KSeF module: " + name, expected.remove(name));
                    Path renamed = work.resolve("renamed-" + expected.size() + ".jar");
                    Files.copy(jar, renamed, StandardCopyOption.REPLACE_EXISTING);
                    assertEquals(name, ModuleFinder.of(renamed).findAll().iterator().next().descriptor().name());
                    jar = renamed;
                }
            }
            modulePath.add(jar.toString());
        }
        assertTrue("Missing named modules: " + expected, expected.isEmpty());
        String path = String.join(File.pathSeparator, modulePath);
        Path source = Files.createDirectories(work.resolve("src/sample"));
        Path moduleInfo = source.getParent().resolve("module-info.java");
        Files.writeString(moduleInfo, """
                module ksef.jpms.consumer {
                    requires java.net.http;
                    requires io.alapierre.ksef.client;
                    requires io.alapierre.ksef.json;
                    requires io.alapierre.ksef.json.%s;
                }
                """.formatted(adapter));
        Path main = source.resolve("Smoke.java");
        try (var input = JpmsIT.class.getResourceAsStream("/jpms/Smoke.java")) {
            assertNotNull(input);
            Files.copy(input, main, StandardCopyOption.REPLACE_EXISTING);
        }
        Path classes = Files.createDirectories(work.resolve("classes"));
        run(work, "compile", "javac", "--release", "21", "--module-path", path,
                "-d", classes.toString(), moduleInfo.toString(), main.toString());
        run(work, "validate", "java", "--module-path", path, "--validate-modules");
        run(work, "run", "java", "--module-path", classes + File.pathSeparator + path,
                "--add-modules", "ALL-MODULE-PATH", "--module", "ksef.jpms.consumer/sample.Smoke",
                implementation.getName(), adapter);
    }

    private static Path jarFor(Class<?> type) throws Exception {
        Path location = Path.of(type.getProtectionDomain().getCodeSource().getLocation().toURI());
        if (Files.isRegularFile(location)) {
            return location;
        }
        // Reactor test classpaths may point at target/classes instead of the packaged sibling JAR.
        try (var files = Files.list(location.getParent())) {
            List<Path> matches = files.filter(p -> p.getFileName().toString().endsWith(".jar"))
                    .filter(p -> !p.getFileName().toString().endsWith("-sources.jar"))
                    .filter(p -> !p.getFileName().toString().endsWith("-javadoc.jar")).toList();
            assertEquals("Expected one binary JAR in " + location.getParent(), 1, matches.size());
            return matches.getFirst();
        }
    }

    private static void run(Path directory, String step, String tool, String... arguments) throws Exception {
        String executable = tool + (System.getProperty("os.name").startsWith("Windows") ? ".exe" : "");
        var command = new ArrayList<String>();
        command.add(Path.of(System.getProperty("java.home"), "bin", executable).toString());
        command.addAll(List.of(arguments));
        Path log = directory.resolve(step + ".log");
        Process process = new ProcessBuilder(command).redirectErrorStream(true).redirectOutput(log.toFile()).start();
        if (!process.waitFor(30, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            fail(step + " timed out; see " + log);
        }
        assertEquals(step + " failed:\n" + Files.readString(log), 0, process.exitValue());
    }
}

package io.alapierre.ksef.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.alapierre.ksef.json.gson.GsonJsonSerializer;
import io.alapierre.ksef.json.jackson.JacksonJsonSerializer;
import org.junit.Test;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceQueryFilters;

import java.io.File;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class KsefSerializerParityTest {
    @Test
    public void discoversTheSameWirePropertiesInDefaultModelInstances() throws Exception {
        var jackson = new JacksonJsonSerializer();
        var gson = new GsonJsonSerializer();
        var treeReader = new ObjectMapper();
        Path classes = Path.of(InvoiceQueryFilters.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        int checked = 0;
        try (var paths = Files.walk(classes.resolve("pl/akmf/ksef/sdk/client/model"))) {
            for (Path path : paths.filter(p -> p.toString().endsWith(".class")).toList()) {
                String name = classes.relativize(path).toString().replace(File.separatorChar, '.').replaceAll("\\.class$", "");
                if (name.contains(".model.xml.")) { continue; }
                // Local upload holder, never encoded as a JSON request: its stream is sent as binary.
                if (name.endsWith(".BatchPartStreamSendingInfo")) { continue; }
                Class<?> type = Class.forName(name);
                if (type.isEnum() || type.isInterface() || Modifier.isAbstract(type.getModifiers())
                        || Throwable.class.isAssignableFrom(type)) { continue; }
                java.lang.reflect.Constructor<?> constructor;
                try { constructor = type.getConstructor(); }
                catch (NoSuchMethodException expected) { continue; }
                Object model = constructor.newInstance();
                try {
                    assertEquals(name, treeReader.readTree(jackson.toJson(model)), treeReader.readTree(gson.toJson(model)));
                } catch (JsonSerializationException e) {
                    throw new AssertionError("Cannot serialize " + name, e);
                }
                checked++;
            }
        }
        assertTrue("Expected the KSeF DTO catalog to be covered", checked > 100);
    }
}

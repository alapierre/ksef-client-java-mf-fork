package io.alapierre.ksef.batch;

import io.alapierre.ksef.batch.model.InvoiceItem;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 26.11.2025
 */
public class FileInvoiceSource implements InvoiceSource, AutoCloseable {

    private final Path directory;

    private FileInvoiceIterator currentIterator;

    public FileInvoiceSource(Path directory) {
        this.directory = directory;
    }

    @Override
    public Iterator<InvoiceItem> iterator() {
        try {
            // Jeśli iterator już istnieje, zamknij go przed utworzeniem nowego (zabezpieczenie)
            if (currentIterator != null) currentIterator.closeStream();
            currentIterator = new FileInvoiceIterator(Files.newDirectoryStream(directory));
            return currentIterator;
        } catch (IOException e) {
            throw new BatchProcessingException("Cannot open directory: " + directory, e);
        }
    }

    static class FileInvoiceIterator implements Iterator<InvoiceItem> {

        private final DirectoryStream<Path> stream;
        private final Iterator<Path> files;
        private Path nextFile;

        FileInvoiceIterator(DirectoryStream<Path> stream) {
            this.stream = stream;
            this.files = stream.iterator();
        }

        @Override
        public boolean hasNext() {
            if (nextFile != null) {
                return true;
            }

            while (files.hasNext()) {
                Path candidate = files.next();
                if (Files.isRegularFile(candidate)) {
                    nextFile = candidate;
                    return true;
                }
            }

            closeStream();
            return false;
        }

        @Override
        public InvoiceItem next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Path file = nextFile;
            nextFile = null;

            try {
                byte[] content = Files.readAllBytes(file);

                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(content);

                return new InvoiceItem(
                        file.getFileName().toString(),
                        file.getFileName().toString(),
                        content,
                        Base64.getEncoder().encodeToString(hash)
                );

            } catch (IOException | NoSuchAlgorithmException e) {
                throw new BatchProcessingException("Cannot read file or compute hash: " + file, e);
            }
        }

        private void closeStream() {
            try {
                stream.close();
            } catch (IOException ignored) {}
        }
    }

    @Override
    public void close() {
        if (currentIterator != null) {
            currentIterator.closeStream();
        }
    }

}

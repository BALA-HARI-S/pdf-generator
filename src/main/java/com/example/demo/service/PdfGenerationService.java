package com.example.demo.service;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class PdfGenerationService {

    public byte[] mergePdfFiles(MultipartFile[] files) throws IOException {
        log.info("Entering mergePdfFiles()");

        // Filter pdf files
        List<InputStream> inputStreams = Arrays.stream(files)
                .filter(file -> "application/pdf".equalsIgnoreCase(file.getContentType()))
                .map(file -> {
                    try{
                        return file.getInputStream();
                    } catch (IOException e) {
                        throw new UncheckedIOException("Reading upload failed", e);
                    }
                }).toList();

        // 2) Fail fast if nothing to merge
        if (inputStreams.isEmpty()) {
            throw new IllegalArgumentException(
                    "No valid PDF files to merge. Ensure content types are application/pdf.");
        }

        // 3) Merge
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfCopy copy = new PdfCopy(document, baos);
            document.open();

            for (InputStream is : inputStreams) {
                try (PdfReader reader = new PdfReader(is)) {
                    int pages = reader.getNumberOfPages();
                    log.info("Merging {} pages from stream", pages);
                    for (int i = 1; i <= pages; i++) {
                        copy.addPage(copy.getImportedPage(reader, i));
                    }
                }
            }

            document.close();  // triggers write & flush
            log.info("Leaving mergePdfFiles()");
            return baos.toByteArray();
        }
    }
}

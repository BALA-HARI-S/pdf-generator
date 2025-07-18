package com.example.demo.service;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class PdfGenerationService {

    public byte[] mergePdfFiles(MultipartFile[] files) throws IOException {
        log.info("Entering mergePdfFiles()");
        List<InputStream> inputStreams = new ArrayList<>();
        for (MultipartFile file : files) {
            if (Objects.equals(file.getContentType(), "Application/pdf")) {
                try {
                    inputStreams.add(file.getInputStream());
                } catch (IOException e) {
                    throw new RuntimeException("Can't add Multipart file as File Stream. " + e);
                }
            }
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfCopy copy = new PdfCopy(document, outputStream);
        document.open();
        for (InputStream inputStream : inputStreams) {
            PdfReader reader = new PdfReader(inputStream);
            int numberOfPages = reader.getNumberOfPages();

            for (int i = 1; i <= numberOfPages; i++) {
                PdfImportedPage page = copy.getImportedPage(reader, i);
                copy.addPage(page);
            }
        }
        document.close();
        copy.close();
        log.info("Leaving mergePdfFiles()");
        return outputStream.toByteArray();
    }
}

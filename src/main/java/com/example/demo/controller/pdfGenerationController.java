package com.example.demo.controller;

import com.example.demo.service.PdfGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/pdf")
@Slf4j
@RequiredArgsConstructor
public class pdfGenerationController {

    private final PdfGenerationService pdfGenerationService;

    @PostMapping("/merge")
    public ResponseEntity<byte[]> mergePdf(@RequestParam("files") MultipartFile[] files) {
        log.info("Entering mergePdf() files = {}", files.length);
        byte[] bytes;
        try {
            bytes = pdfGenerationService.mergePdfFiles(files);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("document", "Generated.pdf");
        log.info("Leaving mergePdf()");
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
}

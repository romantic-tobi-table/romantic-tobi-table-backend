package com.tomy.tomy.ocr;

public interface OcrService {
    record OcrResult(String fullText, String rawMeta) {}
    OcrResult extractText(org.springframework.web.multipart.MultipartFile file);
}

package com.cytyk.wordserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.cytyk.wordserver.common.ResponseVO;
import com.cytyk.wordserver.service.Doc2PdfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author cytyikai
 * 2019/9/3 14:15
 */
@Slf4j
@RestController
@RequestMapping("/api/word")
public class Word2PdfController {

    private final Doc2PdfService doc2PdfService;

    public Word2PdfController(Doc2PdfService doc2PdfService) {
        this.doc2PdfService = doc2PdfService;
    }

    @PostMapping("/doc2Pdf")
    public ResponseVO doc2Pdf(@RequestParam MultipartFile file) {
        try {
            JSONObject result = doc2PdfService.doc2Pdf(file);
            return ResponseVO.build().success(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.build().fail("upload file failed!" + e.getMessage());
        }
    }
}

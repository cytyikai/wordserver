package com.cytyk.wordserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.cytyk.wordserver.common.ResponseVO;
import com.cytyk.wordserver.service.Doc2PdfService;
import com.zjiecode.wxpusher.client.WxPusher;
import com.zjiecode.wxpusher.client.bean.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * @author cytyikai
 * 2019/9/3 14:15
 */
@Slf4j
@RestController
@RequestMapping("/api/word")
public class Word2PdfController {

    private final Doc2PdfService doc2PdfService;

    @Autowired
    public Word2PdfController(Doc2PdfService doc2PdfService) {
        this.doc2PdfService = doc2PdfService;
    }

    @PostMapping("/doc2Pdf")
    public ResponseVO doc2Pdf(@RequestParam MultipartFile file) {
        Message message = new Message();
        message.setAppToken("AT_QsUYIETNOhgzFAyqFrCLwY0WZhr4k6Lw");
        message.setContentType(Message.CONTENT_TYPE_TEXT);
        message.setContent("有人使用word转pdf了" + new Date());
        message.setUid("UID_BU6avTAvcjiLa1umg1rlaCoyv3am");
        WxPusher.send(message);
        try {
            JSONObject result = doc2PdfService.doc2Pdf(file);
            return ResponseVO.build().success(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseVO.build().fail("upload file failed!" + e.getMessage());
        }
    }
}

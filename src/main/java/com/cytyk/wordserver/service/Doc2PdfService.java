package com.cytyk.wordserver.service;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.common.utils.DateUtil;
import com.aspose.words.Document;
import com.cytyk.wordserver.common.Converter;
import com.cytyk.wordserver.common.FileUtils;
import com.cytyk.wordserver.common.OssUtils;
import com.cytyk.wordserver.core.OfficeService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author cytyikai
 * 2019/9/3 14:22
 */
@Service
public class Doc2PdfService {

    private final OfficeService officeService;

    private final OssUtils ossUtils;

    @Autowired
    public Doc2PdfService(OfficeService officeService, OssUtils ossUtils) {
        this.officeService = officeService;
        this.ossUtils = ossUtils;
    }

    public JSONObject doc2Pdf(MultipartFile file) throws Exception {
        JSONObject result = new JSONObject();
        String resultPath = null, url;
        Integer size;
        if (!Converter.getLicense()) {
            throw new Exception("com.aspose.words lic ERROR!");
        }
        try {
            Document doc = new Document(file.getInputStream());
            resultPath = System.getProperty("java.io.tmpdir") + System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(99);
            String inPath = resultPath + File.separator + file.getOriginalFilename();
            doc.save(inPath);
            String outPath = resultPath + File.separator + FileUtils.getPrefix(file.getOriginalFilename()) + ".pdf";
            officeService.word2pdfPage(inPath, outPath);
            File pdfFile = new File(outPath);
            PDDocument pdDocument = PDDocument.load(pdfFile);
            size = pdDocument.getNumberOfPages();
            pdDocument.close();
            url = ossUtils.presignedUrl("publicBucket", ossUtils.upload("wordserver/word2pdf/" + DateUtil.formatIso8601Date(new Date()), pdfFile), TimeUnit.DAYS.toSeconds(7L));
        } finally {
            if (null != resultPath) {
                FileSystemUtils.deleteRecursively(new File(resultPath));
            }
        }
        result.put("url", url);
        result.put("size", size);
        return result;
    }

}

package com.cytyk.wordserver.service;

import com.alibaba.fastjson.JSONObject;
import com.aspose.words.Document;
import com.cytyk.wordserver.common.Converter;
import com.cytyk.wordserver.common.FileUtils;
import com.cytyk.wordserver.core.OfficeService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author cytyikai
 * 2019/9/3 14:22
 */
@Service
public class Doc2PdfService {

    private final OfficeService officeService;

    public Doc2PdfService(OfficeService officeService) {
        this.officeService = officeService;
    }

    public JSONObject doc2Pdf(MultipartFile file) throws Exception {
        JSONObject result = new JSONObject();
        String resultPath = null;
        Integer size;
        if (!Converter.getLicense()) {
            throw new Exception("com.aspose.words lic ERROR!");
        }
        try {
//            try (OSSObject ossObject = ossUtils.downloadWithFullPath(StringUtils.isEmpty(bucket) ? ossUtils.tikuBucket : bucket, url)) {
//                Document doc = new Document(ossObject.getObjectContent());
//                resultPath = System.getProperty("java.io.tmpdir") + idWorker.nextId();
//                doc.save(resultPath + File.separator + fileName + ".docx");
//            }
            String inPath = resultPath + File.separator + file.getOriginalFilename();
            String outPath = resultPath + File.separator + FileUtils.getPrefix(file.getOriginalFilename()) + ".pdf";
            officeService.word2pdfPage(inPath, outPath);
            PDDocument pdDocument = PDDocument.load(new File(outPath));
            size = pdDocument.getNumberOfPages();
            pdDocument.close();
            File file = new File(outPath);
//            url = ossUtils.upload("paper/download/" + DateUtils.nowToString("yyyyMMddHHmmssSSS"), file, ossUtils.tikuBucket);
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

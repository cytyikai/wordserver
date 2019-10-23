package com.cytyk.wordserver.common;


import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * @author CYT
 * @date 2019/9/3 23:21.
 * Description:
 */
@Component
@Slf4j
public class OssUtils {

    @Value("${aliyun.accesskey}")
    private String accesskey;

    @Value("${aliyun.secret}")
    private String secret;

    @Value("${aliyun.oss.endPoint}")
    private String endPoint;

    @Value("${aliyun.oss.out.endPoint}")
    private String outEndPoint;

    @Value("${aliyun.oss.publicBucket}")
    public String publicBucket;

    private OSSClient ossClient;

    @PostConstruct
    public void init() {
        ossClient = new OSSClient(endPoint, accesskey, secret);
    }

    /**
     * upload file to public bucket
     */
    public String upload(String path, File file) {
        return upload(path, file, publicBucket);
    }

    /**
     * 方法描述:上传文件
     *
     * @param file 文件对象
     */
    public String upload(String path, File file, String bucket) {
        if (file == null) {
            return null;
        }
        try {
            // 创建文件路径
            // 创建文件路径
            String fileUrl;
            if (path.charAt(path.length() - 1) == File.separatorChar) {
                fileUrl = path + file.getName();
            } else {
                fileUrl = path + "/" + file.getName();
            }
            fileUrl = fileUrl.replace("\\", "/");
            if (fileUrl.startsWith("/")) {
                fileUrl = fileUrl.substring(1);
            }
            // 上传文件
            PutObjectResult result = ossClient.putObject(new PutObjectRequest(bucket, fileUrl, file));
            if (null != result) {
                return "http://" + bucket + "." + outEndPoint + "/" + fileUrl;
            }
        } catch (OSSException | ClientException oe) {
            log.error("文件上传错误", oe);
        }
        return null;
    }

    /**
     * upload file to public bucket
     */
    public String upload(String path, InputStream inputStream, String fileName) {
        return upload(path, inputStream, fileName, publicBucket);
    }

    /**
     * 方法描述:上传文件
     *
     * @param inputStream 文件流
     * @author leon 2016年12月26日 下午3:33:13
     */
    public String upload(String path, InputStream inputStream, String fileName, String bucket) {
        if (inputStream == null || StringUtils.isEmpty(path)) {
            return null;
        }
        try {
            // 创建文件路径
            String fileUrl;
            if (path.charAt(path.length() - 1) == File.separatorChar) {
                fileUrl = path + fileName;
            } else {
                fileUrl = path + "/" + fileName;
            }
            return doUpload(inputStream, bucket, fileUrl);
        } catch (OSSException | ClientException oe) {
            log.error("文件上传错误", oe);
        }
        return null;

    }

    private String doUpload(InputStream inputStream, String bucket, String fileUrl) {
        fileUrl = fileUrl.replace("\\", "/");
        if (fileUrl.startsWith("/")) {
            fileUrl = fileUrl.substring(1);
        }
        // 上传文件
        PutObjectResult result = ossClient.putObject(new PutObjectRequest(bucket, fileUrl, inputStream));
        if (null != result) {
            return "http://" + bucket + "." + outEndPoint + "/" + fileUrl;
        }
        return null;
    }

    /**
     * url签名访问
     *
     * @param bucketName bucketName
     * @param path       path
     * @param expireTime 过期时间 s
     * @return 加密连接
     */
    public String presignedUrl(String bucketName, String path, Long expireTime) {
        // 设置URL过期时间 单位秒。
        Date expiration = new Date(System.currentTimeMillis() + expireTime * 1000);
        // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
        URL url = ossClient.generatePresignedUrl(bucketName, path.replaceAll("https?", "")
                .replace("://" + bucketName + "." + endPoint + "/", ""), expiration);
        return url.toString();
    }
}


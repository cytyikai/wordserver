package com.cytyk.wordserver.common;

/**
 * @author cytyikai
 * 2019/9/3 16:26
 */
public class FileUtils {
    public static String getSuffixWithDot(String fileName) {
        if (fileName == null) {
            return null;
        }

        return fileName.substring(fileName.lastIndexOf("."));
    }

    public static String getSuffix(String fileName) {
        if (fileName == null) {
            return null;
        }

        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public static String getPrefix(String fileName) {
        if (fileName == null) {
            return null;
        }

        return fileName.substring(0, fileName.lastIndexOf("."));
    }

}

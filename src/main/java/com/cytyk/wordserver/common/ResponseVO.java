package com.cytyk.wordserver.common;

import com.alibaba.fastjson.JSON;

/**
 * @author yikai
 * 2019/04/30 10:21
 */
public class ResponseVO {
    private Integer code;
    private Object data;
    private String message;

    public ResponseVO() {
    }

    public ResponseVO(Integer code, Object data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public static ResponseVO build() {
        return new ResponseVO();
    }

    public ResponseVO success() {
        this.code = 0;
        return this;
    }

    public ResponseVO success(Object data) {
        this.code = 0;
        this.data = data;
        return this;
    }

    public ResponseVO fail() {
        this.code = -1;
        return this;
    }

    public ResponseVO fail(String message) {
        this.code = -1;
        this.message = message;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
                "\"code\": " + code + "," +
                "\"data\": " + getJsonData(data) + "," +
                "\"message\": \"" + (message == null ? "" : message) + "\"" +
                "}";
    }

    private String getJsonData(Object data) {
        if (data instanceof String) {
            return "\"" + data + "\"";
        } else {
            return JSON.toJSONString(data);
        }
    }
}

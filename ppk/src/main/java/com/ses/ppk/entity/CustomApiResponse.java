package com.ses.ppk.entity;

public class CustomApiResponse {

    private int code;
    private String message;

    public CustomApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}


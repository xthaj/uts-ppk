package com.ses.ppk.entity;

public class ApiResponse {

    private int code;
    private String message;

    public ApiResponse(int code, String message) {
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


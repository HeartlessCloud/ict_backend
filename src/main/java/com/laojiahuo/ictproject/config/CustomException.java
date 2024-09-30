package com.laojiahuo.ictproject.config;

public class CustomException extends RuntimeException {
    private int code;

    public CustomException(int code,String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

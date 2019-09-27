package ru.sigmadigital.pantus.api;

import java.io.Serializable;

public class BaseResponse implements Serializable {

    private String data;
    private int code;

    public BaseResponse(String data, int code) {
        this.data = data;
        this.code = code;
    }

    public BaseResponse(int code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }
}

package com.leeyh.boostcampproject.helper;

public class ResponseException extends Exception {

    private int mResponseCode;

    ResponseException(int responseCode) {
        super("response code : " +responseCode);
        this.mResponseCode = responseCode;
    }

    public int getRequestCode() {
        return this.mResponseCode;
    }

}

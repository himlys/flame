package com.rain.flame.remoting.exchange;

import java.util.Random;

public class ExchangeInvocationResult {
    public enum ResponseCode {
        OK((byte) 0),
        SERVER_TIMEOUT((byte) 1),
        CLIENT_TIMEOUT((byte) 2),
        BAD_REQUEST((byte) 3),
        BAD_RESPONSE((byte) 4),
        OPERATE_NOTFOUND((byte) 5),
        NOT_FOUND_CODE((byte) 6);
        private final byte value;

        ResponseCode(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }
    }

    public static ResponseCode getResponseCodeByByte(byte b) {
        for (ResponseCode code : ResponseCode.values()) {
            if (code.getValue() == b) {
                return code;
            }
        }
        return ResponseCode.NOT_FOUND_CODE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    ;
    private Long id;
    private String responseType;
    private ResponseCode responseCode;
    private Object data;

    public ExchangeInvocationResult() {
        id = new Random().nextLong();
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
package com.light.rpc.common;

import java.io.Serializable;

public class Response implements Serializable{

    private static final long serialVersionUID = 522991298940680054L;

    private String id;

    private Exception exception;

    private Object result;

    public boolean hasException() {
        return exception != null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}

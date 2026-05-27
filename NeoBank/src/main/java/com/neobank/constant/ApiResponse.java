package com.neobank.constant;

public class ApiResponse<T> {

    private Boolean success;
    private String message;
    private T data;
    private Object metadata;

    public ApiResponse() {}

    public ApiResponse(Boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public ApiResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ApiResponse(Boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public ApiResponse(Boolean success, T data, String message, Object metadata) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.metadata = metadata;
    }

    public Boolean getSuccess() {
        return success;
    }
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
    public Object getMetadata() {
        return metadata;
    }
    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }
}

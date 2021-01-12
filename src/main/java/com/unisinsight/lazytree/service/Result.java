package com.unisinsight.lazytree.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * 返回结果类
 */
@Data
public class Result<T> {
    private static final String successCode = "0000000000";
    private static final Result SUCCESS = new Result(){
        //@Override
        public void setData(Object data) {}
    };

    private String errorCode = successCode;

    private String message;

    private T data;

    private Result() {}
    public Result(String errorCode, String message) {
        this(errorCode, message, null);
    }
    public Result(String errorCode, String message,T data) {
        this.errorCode = errorCode;
        this.message = message;
        this.data = data;
    }

    public static Result success() {
        return SUCCESS;
    }
    public static <T> Result<T> success(T data) {
        return new Result(successCode, "操作成功", data);
    }
    public static <T> Result<T> success(T data, String message) {
        return new Result(successCode, message, data);
    }
    public static Result error(String errorCode, String message) {
        return new Result(errorCode, message);
    }
    public static Result error(IErrorCode error) {
        return new Result(error.getErrorCode(), error.getMessage());
    }
    public static Result of(BaseErrorCode baseErrorCode) {
        return new Result(baseErrorCode.getErrorCode(), baseErrorCode.getMessage());
    }

    @JsonIgnore
    public boolean isSuccess() {
        return successCode.equals(errorCode);
    }
}

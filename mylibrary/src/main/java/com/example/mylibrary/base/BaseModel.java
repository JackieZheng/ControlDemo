package com.example.mylibrary.base;

/**
 * 服务端响应的JSON对应的"结构体"
 *
 * @param <T> 数据类型 "结构体"
 */
public class BaseModel<T> implements IModel {
  private int code;
  private String error;
  private String message;
  private T data;

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public boolean isSuccess() {
    return this.code == 0;
  }

  public int getCode() {
    return this.code;
  }

  public String getMessage() {
    return message;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
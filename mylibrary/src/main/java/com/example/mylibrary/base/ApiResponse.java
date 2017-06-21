package com.example.mylibrary.base;

import java.io.Serializable;

/**
 * 后台服务返回的数据对应的数据结构,需要根据后台服务的特点调整类的内容
 *
 * @param <T> 数据类型 "结构体"
 */
public class ApiResponse<T> implements Serializable {
  /** 和后台服务约定的响应码 */
  private int code;
  /** 服务调用结果 描述|错误信息 */
  private String message;
  /** 数据 */
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

  public void setCode(int code) {
    this.code = code;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
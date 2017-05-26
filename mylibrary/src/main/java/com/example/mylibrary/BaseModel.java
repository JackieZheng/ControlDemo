package com.example.mylibrary;

public class BaseModel {
  private int code;
  private String error;
  private String message;

  public void setError(String error) {
    this.error = error;
  }

  public String getError() {
    return error;
  }

  public int getCode() {
    return this.code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public boolean isSuccess() {
    return this.code == 0;
  }

  public boolean isExpire() {
    return this.code == 2;
  }
}
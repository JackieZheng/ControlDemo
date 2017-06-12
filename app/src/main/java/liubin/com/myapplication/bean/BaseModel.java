package liubin.com.myapplication.bean;

import com.example.mylibrary.base.IModel;

public class BaseModel implements IModel {
  private int code;
  private String error;
  private String message;

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
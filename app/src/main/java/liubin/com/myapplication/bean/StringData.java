package liubin.com.myapplication.bean;

import java.util.List;

public class StringData extends BaseModel {
  private List<String> data;

  public List<String> getData() {
    return data;
  }

  public void setData(List<String> data) {
    this.data = data;
  }
}
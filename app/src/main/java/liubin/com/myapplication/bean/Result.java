package liubin.com.myapplication.bean;

import java.io.Serializable;

public class Result implements Serializable {
  private int icon;
  private String name;

  public int getIcon() {
    return icon;
  }

  public void setIcon(int icon) {
    this.icon = icon;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}

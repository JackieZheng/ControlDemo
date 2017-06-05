package liubin.com.myapplication.bean;

import com.google.gson.annotations.JsonAdapter;

public class User {

  @JsonAdapter(DoubleAdapter.class) private double age;
  private String name;

  public double getAge() {
    return age;
  }

  public void setAge(double age) {
    this.age = age;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}

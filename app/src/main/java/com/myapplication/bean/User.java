package com.myapplication.bean;

import com.example.mylibrary.base.adapter.DoubleAdapter;
import com.google.gson.annotations.JsonAdapter;
import java.io.Serializable;

public class User implements Serializable {

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



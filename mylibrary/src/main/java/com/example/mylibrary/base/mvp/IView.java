package com.example.mylibrary.base.mvp;

public interface IView {
  void showProgress();

  void showContent();

  void showEmpty();

  void showNetWorkError();
}

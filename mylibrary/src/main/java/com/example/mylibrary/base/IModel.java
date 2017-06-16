package com.example.mylibrary.base;

/**
 * 服务端返回的数据结果必须实现的接口
 */
public interface IModel<T> {

  /**
   * 服务端返回的结果代码
   *
   * @return 服务端返回的结果代码
   */
  int getCode();

  /**
   * 服务调用是否成功
   *
   * @return 服务调用是否成功
   */
  boolean isSuccess();

  /**
   * 服务调用返回信息
   *
   * @return 服务调用返回信息
   */
  String getMessage();

  /**
   * 服务调用错误信息
   *
   * @return 服务调用错误信息
   */
  String getError();

  /**
   * 返回的数据
   *
   * @return
   */
  T getData();
}

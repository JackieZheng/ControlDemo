package liubin.com.myapplication.fragments;

import com.example.mylibrary.base.mvp.IListMVPPresenter;

public interface IMVPPersenter /*<MODEL extends IModel>*/ extends IListMVPPresenter/*<MODEL> */ {

  /**
   * 请求数据
   *
   * @param pageSize 每页数据数目
   * @param isRefresh 是否清空原有数据
   */
  void loadData(int pageSize, final boolean isRefresh);
}
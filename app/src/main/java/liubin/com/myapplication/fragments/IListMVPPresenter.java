package liubin.com.myapplication.fragments;

import com.example.mylibrary.base.IModel;

public interface IListMVPPresenter<MODEL extends IModel> {
  /**
   * 请求数据
   *
   * @param pageSize 每页数据数目
   * @param isRefresh 是否清空原有数据
   */
  void loadData(int pageSize, final boolean isRefresh);
}

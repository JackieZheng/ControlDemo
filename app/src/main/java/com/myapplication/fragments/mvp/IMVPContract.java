package com.myapplication.fragments.mvp;

import com.example.mylibrary.base.mvp.presenter.BasePresenter;
import com.example.mylibrary.base.mvp.view.IBaseMVPView;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;

public interface IMVPContract {
  abstract class IMVPPresenter<T extends IBaseMVPView> extends BasePresenter<T> {

    public IMVPPresenter(T view, LifecycleProvider<FragmentEvent> provider) {
      super(view, provider);
    }

    /**
     * 复制数据
     */
    abstract void copy();
  }

  /**
   */
  interface IMVPView extends IBaseMVPView {
    String getContent();

    void SetContent(String content);
  }
}

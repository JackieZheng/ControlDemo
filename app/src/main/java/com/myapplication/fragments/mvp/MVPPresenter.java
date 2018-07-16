package com.myapplication.fragments.mvp;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;

public class MVPPresenter extends IMVPContract.IMVPPresenter<IMVPContract.IMVPView> {

  public MVPPresenter(IMVPContract.IMVPView view, LifecycleProvider<FragmentEvent> provider) {
    super(view, provider);
  }

  @Override
  void copy() {
    String content = mView.getContent();
    mView.SetContent("字符串的长度为: " + content.length());
  }
}

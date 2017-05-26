package com.example.mylibrary.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

public class BaseFragment extends Fragment {

  /**
   * 视图是否已经创建完成
   */
  protected boolean isViewCreated = false;

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    isViewCreated = true;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    isViewCreated = false;
  }
}

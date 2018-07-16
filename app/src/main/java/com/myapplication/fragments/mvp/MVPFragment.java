package com.myapplication.fragments.mvp;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.example.mylibrary.base.BaseFragment;
import com.example.mylibrary.base.TopBarActivity;
import com.example.mylibrary.base.mvp.fragment.BaseMVPFragment;
import com.myapplication.R;

public class MVPFragment extends BaseMVPFragment<IMVPContract.IMVPPresenter> implements IMVPContract.IMVPView {
  Unbinder mUnBinder;

  @BindView(R.id.content) TextView mContent;
  @BindView(R.id.result) TextView mResult;
  @BindView(R.id.button) TextView mButton;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @CallSuper
  @Override
  public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(com.example.mylibrary.R.layout.fragment_base_mvp, container, false);
  }

  @Override
  protected IMVPContract.IMVPPresenter initPresenter() {
    // return new ListMVPPresenter(this, this);
    return new MVPPresenter(this, this);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mUnBinder = ButterKnife.bind(this, view);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mUnBinder.unbind();
  }

  @OnClick({ R.id.button })
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.button: {
        mPresenter.copy();
        break;
      }
    }
  }

  /**
   * 初始化状态栏,标题栏
   *
   * @param activity {@link TopBarActivity}
   */
  @Override
  public void initTopBar(TopBarActivity activity) {
    super.initTopBar(activity);
    Toolbar toolBar = activity.getToolBar();
    toolBar.setTitle("MVP基本使用");
    toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    toolBar.setNavigationOnClickListener(v -> mActivity.finish());
  }

  @Override
  public String getContent() {
    return mContent.getText().toString();
  }

  @Override
  public void SetContent(String content) {
    mResult.setText(content);
  }

  @Override
  public BaseFragment getFragment() {
    return this;
  }
}

package com.myapplication.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.example.mylibrary.base.ProgressFragment;
import com.example.mylibrary.base.TopBarActivity;
import com.myapplication.R;
import java.util.Random;

/**
 * <pre> 自定义Fragment,加载中,空数据,网络异常 视图
 * 1.重写{@link #getEmptyLayoutResourceId()}返回对应的Fragment布局文件
 * </pre>
 */
public class CustomFragment extends ProgressFragment {

  @BindView(R.id.click_me) TextView mClickMe;
  Unbinder unbinder;
  private android.os.Handler mHandler;
  private int i = new Random().nextInt();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mHandler = new android.os.Handler();
    obtainData();
  }

  @Override
  public int getContentLayoutResourceId() {
    return R.layout.content_custom;
  }

  @Override
  protected int getLoadingLayoutResourceId() {
    return R.layout.custom_loading_layout;
  }

  @Override
  protected int getNetWorkErrorResourceId() {
    return R.layout.custom_network_error_layout;
  }

  @Override
  protected int getEmptyLayoutResourceId() {
    return R.layout.custom_empty_layout;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    unbinder = ButterKnife.bind(this, view);

    // 没有数据视图点击事件
    setEmptyViewClickListener(v -> obtainData());
    // 网络异常视图点击事件
    setNetWorkErrorViewClickListener(v -> obtainData());
  }

  // 修改Activity中的标题栏
  @Override
  public void initTopBar(TopBarActivity activity) {
    super.initTopBar(activity);
    Toolbar toolBar = activity.getToolBar();
    toolBar.setTitle("自定义内容");
    toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    toolBar.setNavigationOnClickListener(v -> mActivity.finish());
    activity.getStatusBar().setBackgroundResource(R.color.primary_dark);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
    mHandler.removeCallbacksAndMessages(null);
  }

  @OnClick({ R.id.click_me })
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.click_me: {
        obtainData();
        break;
      }
    }
  }

  /**
   * 获取数据
   */
  private void obtainData() {
    showLoading();//显示加载进度
    mHandler.postDelayed(() -> {
      i = i % 3;// 这样可以防止溢出
      if (i == 0) {
        showContent();//显示内容
      } else if (i == 1) {
        showEmpty();//没有数据
      } else {
        showNetWorkError();//网络异常
      }
      i++;
    }, 3000);
  }

  @Override
  protected void onEmptyViewInflated(@NonNull View emptyView) {
    super.onEmptyViewInflated(emptyView);
    TextView textView = emptyView.findViewById(R.id.data_empty_text);
    textView.setText("这里没有数据");
    textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_conn_no_network, 0, 0);
    emptyView.findViewById(R.id.click).setOnClickListener(v -> Toast.makeText(getContext(), "点击了", Toast.LENGTH_LONG).show());
  }
}

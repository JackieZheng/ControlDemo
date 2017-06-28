package liubin.com.myapplication.fragments;

import android.os.Bundle;
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
import java.util.Random;
import liubin.com.myapplication.R;

/**
 * <pre> 自定义Fragment,加载中,空数据,网络异常 视图
 * 1.重写{@link #getFragmentLayoutResourceId()}返回对应的Fragment布局文件
 * </pre>
 */
public class CustomFragment extends ProgressFragment<TopBarActivity> {

  @BindView(R.id.click_me) TextView mClickMe;
  Unbinder unbinder;
  private android.os.Handler mHandler;
  private int i = new Random().nextInt();

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mHandler = new android.os.Handler();
    obtainData();
  }

  @Override public int getFragmentLayoutResourceId() {
    return R.layout.fragment_custom;
  }

  @Override public int getContentLayoutResourceId() {
    return R.layout.content_custom;
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    unbinder = ButterKnife.bind(this, view);

    // 没有数据视图点击事件
    setEmptyViewClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        obtainData();
      }
    });
    // 网络异常视图点击事件
    setNetWorkErrorViewClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        obtainData();
      }
    });
  }

  // 修改Activity中的标题栏
  @Override public void initTopBar(TopBarActivity activity) {
    super.initTopBar(activity);
    Toolbar toolBar = activity.getToolBar();
    toolBar.setTitle("自定义内容");
    toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    toolBar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        mActivity.finish();
      }
    });
    activity.getStatusBar().setBackgroundResource(R.color.primary_dark);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
    mHandler.removeCallbacksAndMessages(null);
  }

  @OnClick({ R.id.click_me }) public void onViewClicked(View view) {
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
    showProgress();//显示加载进度
    mHandler.postDelayed(new Runnable() {
      @Override public void run() {
        i = i % 3;// 这样可以防止溢出
        if (i == 0) {
          showContent();//显示内容
        } else if (i == 1) {
          showEmpty();//没有数据
          setEmptyMessage("这里没有数据", R.drawable.ic_conn_no_network);
          getEmptyView().findViewById(R.id.click).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
              Toast.makeText(getContext(), "点击了", Toast.LENGTH_LONG).show();
            }
          });
        } else {
          showNetWorkError();//网络异常
        }
        i++;
      }
    }, 1500);
  }
}

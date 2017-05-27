package liubin.com.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.example.mylibrary.base.ProgressFragment;
import java.util.Random;

public class DrawerLayoutFragment extends ProgressFragment {

  Unbinder unbinder;
  @BindView(R.id.nav_view) NavigationView mNavView;
  @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
  //@BindView(R.id.click_me) TextView mClickMe;
  private Handler mHandler;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mHandler = new Handler();
    obtainData();
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    unbinder = ButterKnife.bind(this, view);
    mDrawerLayout.setStatusBarBackgroundColor(Color.TRANSPARENT);
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

  @Override public int getFragmentLayoutResourceID() {
    return R.layout.fragment_drawerlayout;
  }

  @Override public int getFragmentContentLayoutResourceID() {
    return R.layout.include_list_viewpager;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  //@OnClick(R.id.click_me) public void onViewClicked() {
    //obtainData();
  //}

  /**
   * 获取数据
   */
  private void obtainData() {
    showProgress();//显示加载进度
    mHandler.postDelayed(new Runnable() {
      @Override public void run() {
        if (isViewCreated) {
          Random random = new Random();
          int i = random.nextInt(100);
          if (i % 3 == 0) {
            showContent();//显示内容
          } else if (i % 3 == 1) {
            showEmpty();//没有数据
          } else {
            showNetWorkError();//网络异常
          }
        }
      }
    }, 1500);
  }
}

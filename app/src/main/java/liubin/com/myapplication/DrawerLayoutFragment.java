package liubin.com.myapplication;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.example.mylibrary.base.BaseActivity;
import com.example.mylibrary.base.BaseFragment;
import liubin.com.myapplication.fragments.CoordinatorLayoutFragment;

public class DrawerLayoutFragment extends BaseFragment<BaseActivity> {

  Unbinder unbinder;
  @BindView(R.id.nav_view) NavigationView mNavView;
  @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
  @BindView(R.id.coordinator_layout) FrameLayout mCoordinatorLayout;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getChildFragmentManager().beginTransaction()
        .replace(R.id.coordinator_layout, new CoordinatorLayoutFragment())
        .commit();
    mActivity.setTransparentForWindow();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_drawerlayout, container, false);
    unbinder = ButterKnife.bind(this, view);

    // 注意区分版本进行处理
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      // 这里状态栏颜色在 CoordinatorLayoutFragment 中设置
      mCoordinatorLayout.setPadding(0, 0, 0, 0);
    } else {
      mCoordinatorLayout.setPadding(0, mActivity.getSystemBarConfig().getStatusBarHeight(), 0, 0);
      // 设置(覆盖在 CoordinatorLayout 上)状态栏颜色
      mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primary));
      // 设置状态栏颜色无效,因为会被内部的 CoordinatorLayout 重新设置
      // mActivity.getWindow().setStatusBarColor(Color.BLUE);
    }
    mDrawerLayout.setFitsSystemWindows(false);

    return view;
  }

  @Override public void onResume() {
    super.onResume();
    // 这句用于覆盖 CoordinatorLayoutFragment 中设置的状态栏颜色,从而显示出
    // mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primary_dark));设置的颜色
    // 如果不在 CoordinatorLayoutFragment 中设置状态栏颜色这句可以不需要
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      mActivity.getWindow().setStatusBarColor(Color.TRANSPARENT);
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }
}

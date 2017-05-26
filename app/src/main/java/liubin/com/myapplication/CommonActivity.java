package liubin.com.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewStub;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommonActivity extends AppCompatActivity {
  public static String FRAGMENT_CLASS_NAME = "fragment_class_name";
  @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
  //@BindView(R.id.progress_stub) ViewStub mProgressStub;
  //@BindView(R.id.empty_stub) ViewStub mEmptyStub;
  //@BindView(R.id.network_error_stub) ViewStub mNetworkErrorStub;
  //@BindView(R.id.click_me) TextView mClickMe;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_common1);
    ButterKnife.bind(this);
    getWindow().setStatusBarColor(Color.TRANSPARENT);

    /*mEmptyStub.inflate().setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        mDrawerLayout.setVisibility(View.VISIBLE);
      }
    });*/

    //if (getIntent() != null && getIntent().hasExtra(FRAGMENT_CLASS_NAME)) {
    //  String stringExtra = getIntent().getStringExtra(FRAGMENT_CLASS_NAME);
    //  if (TextUtils.isEmpty(stringExtra)) return;
    //  // TODO: 2017/5/25 这里需要判断是否已经将Fragment加入到里面了????
    //  Fragment fragment = Fragment.instantiate(this, stringExtra, getIntent().getExtras());
    //  getSupportFragmentManager().beginTransaction()
    //      .replace(R.id.content, fragment, FRAGMENT_CLASS_NAME)
    //      .commit();
    //}
  }

  @OnClick(R.id.click_me) public void onViewClicked() {
    mDrawerLayout.setVisibility(View.VISIBLE);

  }
}

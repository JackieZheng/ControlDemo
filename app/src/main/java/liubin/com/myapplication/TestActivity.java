package liubin.com.myapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.example.mylibrary.base.BaseActivity;

public class TestActivity extends BaseActivity {

  @BindView(R.id.message) TextView mTextMessage;
  @BindView(R.id.navigation) BottomNavigationView mBottomNavigationView;
  @BindView(R.id.bottom_navigation_bar) BottomNavigationBar mBottomNavigationBar;

  private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
      new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
          mTextMessage.setSelected(true);
          switch (item.getItemId()) {
            case R.id.navigation_home:
              mTextMessage.setText(R.string.title_home);
              return true;
            case R.id.navigation_dashboard:
              mTextMessage.setText(R.string.title_dashboard);
              return true;
            case R.id.navigation_notifications:
              mTextMessage.setText(R.string.title_notifications);
              return true;
          }
          return false;
        }
      };

  @Override public int getContentResourceId() {
    return R.layout.activity_test;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ButterKnife.bind(this);

    mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    TextBadgeItem textBadgeItem = new TextBadgeItem().setText("4");
    mBottomNavigationBar//
        .setMode(BottomNavigationBar.MODE_FIXED)
        .addItem(new BottomNavigationItem(R.drawable.ic_home_black_24dp, "Home").setBadgeItem(
            textBadgeItem))
        .addItem(new BottomNavigationItem(R.drawable.ic_dashboard_black_24dp, "Books"))
        .addItem(new BottomNavigationItem(R.drawable.ic_notifications_black_24dp, "Movies & TV"))
        .setActiveColor(R.color.dark_gray)
        .setInActiveColor(R.color.light_gray)
        .setBarBackgroundColor(R.color.white)
        .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_DEFAULT)
        .initialise();

    mBottomNavigationBar.setTabSelectedListener(
        new BottomNavigationBar.SimpleOnTabSelectedListener() {
          @Override public void onTabSelected(int position) {
          }

          @Override public void onTabUnselected(int position) {
          }

          @Override public void onTabReselected(int position) {
          }
        });
  }
}

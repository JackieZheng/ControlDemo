package liubin.com.myapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.widget.TextView;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.example.mylibrary.base.BaseActivity;

public class TestActivity extends BaseActivity {

  private TextView mTextMessage;

  private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
      new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
          item.setChecked(false);
          mTextMessage.setSelected(true);
          switch (item.getItemId()) {
            case R.id.navigation_home:
              item.setChecked(true);
              mTextMessage.setText(R.string.title_home);
              return true;
            case R.id.navigation_dashboard:
              item.setChecked(true);
              mTextMessage.setText(R.string.title_dashboard);
              return true;
            case R.id.navigation_notifications:
              item.setChecked(true);
              mTextMessage.setText(R.string.title_notifications);
              return true;
          }
          return false;
        }
      };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test);

    mTextMessage = (TextView) findViewById(R.id.message);

    BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    BottomNavigationBar bottomNavigationBar =
        (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);

    TextBadgeItem textBadgeItem = new TextBadgeItem().setText("4");
    bottomNavigationBar//
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

    bottomNavigationBar.setTabSelectedListener(
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

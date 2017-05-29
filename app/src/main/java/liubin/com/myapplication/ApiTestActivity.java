package liubin.com.myapplication;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.mylibrary.ApiClient;
import com.example.mylibrary.BaseModel;
import com.example.mylibrary.StatusBarUtil;
import com.example.mylibrary.TestApi;
import com.example.mylibrary.base.TopBarActivity;
import com.r0adkll.slidr.Slidr;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.Arrays;

public class ApiTestActivity extends AppCompatActivity {

  @BindView(R.id.button2) Button mButton2;
  @BindView(R.id.toolbar) Toolbar mToolbar;
  @BindView(R.id.content) FrameLayout mContent;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Slidr.attach(this);
    setContentView(R.layout.activity_api_test);
    StatusBarUtil.setColor(this, getResources().getColor(R.color.primary));

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    final ActionBar ab = getSupportActionBar();
    ab.setHomeAsUpIndicator(R.drawable.ic_menu);
    ab.setDisplayHomeAsUpEnabled(true);
    ButterKnife.bind(this);
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.content, new BlankFragment())
        .commit();
  }

  @OnClick({ R.id.button2 }) public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.button2: {
        testApi();
        break;
      }
    }
  }

  private void testRX1() {
    Observable.fromIterable(
        Arrays.asList("http://www.baidu.com/", "http://www.google.com/", "https://www.bing.com/"))
        .concatMap(new Function<String, ObservableSource<String>>() {
          @Override public ObservableSource<String> apply(String s) throws Exception {
            return Observable.create(new ObservableOnSubscribe<String>() {
              @Override public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("asdf");
                e.onComplete();
              }
            }).subscribeOn(Schedulers.io());
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<String>() {
          @Override public void accept(String s) throws Exception {
            Log.e("a", s);
          }
        });
  }

  private void testApi() {
    ApiClient.create(TestApi.class)
        .getUser(1, 22)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<BaseModel>() {
          @Override public void accept(BaseModel user) throws Exception {
            Toast.makeText(getApplicationContext(), "wokao", Toast.LENGTH_LONG).show();
          }
        }, new Consumer<Throwable>() {
          @Override public void accept(Throwable throwable) throws Exception {
            Toast.makeText(getApplicationContext(), "wokao", Toast.LENGTH_LONG).show();
          }
        }, new Action() {
          @Override public void run() throws Exception {
            Toast.makeText(getApplicationContext(), "wokao", Toast.LENGTH_LONG).show();
          }
        });
  }
}

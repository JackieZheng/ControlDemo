package liubin.com.myapplication.fragments;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.example.mylibrary.ApiClient;
import liubin.com.myapplication.bean.BaseModel;
import liubin.com.myapplication.api.TestApi;
import com.example.mylibrary.base.ProgressFragment;
import com.example.mylibrary.base.TopBarActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.Random;
import liubin.com.myapplication.R;

/**
 * <pre>有 [自定义的顶部栏(状态栏+标题栏+标题栏阴影)] 的Activity基本使用方式
 * 1. 继承{@link ProgressFragment}并指定泛型参数为{@link TopBarActivity}
 * 2. 重写 {@link #getFragmentContentLayoutResourceID} 方法,返回内容区域的布局文件,
 * 这个布局文件将嵌入到{@link ProgressFragment} 的内容区域
 * 3. 注意请不要重写{@link #onCreateView(LayoutInflater, ViewGroup, Bundle)},
 * 如需要修改Fragment布局内容,请重写{@link #getFragmentLayoutResourceID()}方法.
 * </pre>
 */
public class BasicFragment extends ProgressFragment<TopBarActivity> {

  @BindView(R.id.click_me) TextView mClickMe;
  Unbinder unbinder;
  private android.os.Handler mHandler;
  private int i = new Random().nextInt();

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mHandler = new android.os.Handler();
    obtainData();
  }

  @Override public int getFragmentContentLayoutResourceID() {
    return R.layout.content_basic;
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

    if (mActivity instanceof TopBarActivity) {
      Toolbar toolBar = ((TopBarActivity) mActivity).getToolBar();
      toolBar.setTitle("基本测试");
      toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
      toolBar.setNavigationOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          mActivity.finish();
        }
      });
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
    mHandler.removeCallbacksAndMessages(null);
  }

  @OnClick({ R.id.click_me }) public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.click_me: {
        testApi();
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
        } else {
          showNetWorkError();//网络异常
        }
        i++;
      }
    }, 1500);
  }

  private void testApi() {
    ApiClient.create(TestApi.class)
        .getUser(1, 22)
        .subscribeOn(Schedulers.io())
        .doOnSubscribe(new Consumer<Disposable>() {
          @Override public void accept(Disposable disposable) throws Exception {
            showProgress();
          }
        })
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<BaseModel>() {
          @Override public void accept(BaseModel user) throws Exception {//onNext
            Toast.makeText(getContext(), "下一步", Toast.LENGTH_LONG).show();
          }
        }, new Consumer<Throwable>() {
          @Override public void accept(Throwable throwable) throws Exception {//onError
            showEmpty();
            Toast.makeText(getContext(), "出错了", Toast.LENGTH_LONG).show();
          }
        }, new Action() {
          @Override public void run() throws Exception {//onComplete
            Toast.makeText(getContext(), "完成", Toast.LENGTH_LONG).show();
            showContent();
          }
        });
  }
}

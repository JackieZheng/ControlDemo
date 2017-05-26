package liubin.com.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.example.mylibrary.base.ProgressFragment;
import java.util.Random;

public class BlankFragment extends ProgressFragment {

  @BindView(R.id.click_me) TextView mClickMe;
  Unbinder unbinder;
  private android.os.Handler mHandler;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mHandler = new android.os.Handler();
    obtainData();
  }

  @Override public int getFragmentContentLayoutResourceID() {
    return R.layout.content_blank;
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

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @OnClick({ R.id.click_me }) public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.click_me: {
        obtainData();
        break;
      }
    }
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

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

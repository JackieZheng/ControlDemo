package com.myapplication.fragments.mvp;

import com.example.mylibrary.base.ApiResponse;
import com.myapplication.api.CustomerApi;
import com.myapplication.bean.Result;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.List;

public class MVPPresenter extends
    com.myapplication.fragments.mvp.IMVPContract.IMVPPresenter<com.myapplication.fragments.mvp.IMVPContract.IMVPView<ApiResponse<List<Result>>>> {

  public MVPPresenter(IMVPContract.IMVPView<ApiResponse<List<Result>>> view,
      LifecycleProvider<FragmentEvent> provider) {
    super(view, provider);
  }

  @Override public void loadData(final int pageSize, final boolean isRefresh) {
    CustomerApi.queryData(pageSize)//
        .compose(mProvider.<ApiResponse<List<Result>>>bindUntilEvent(FragmentEvent.DESTROY))//生命周期绑定
        //.doOnNext(mView.getDoOnNext(pageSize))//
        .subscribeOn(Schedulers.io())// 指定在这行代码之前的subscribe在io线程执行
        .doOnSubscribe(mView.getDoOnSubscribe())//开始执行之前的准备工作
        .subscribeOn(AndroidSchedulers.mainThread())//指定 前面的doOnSubscribe 在主线程执行
        .observeOn(AndroidSchedulers.mainThread())//指定这行代码之后的subscribe在io线程执行
        .subscribe(mView.getOnNext(isRefresh), mView.getOnError());
  }
}

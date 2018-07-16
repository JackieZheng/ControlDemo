package com.myapplication.fragments.mvp;

import android.support.annotation.NonNull;
import com.example.mylibrary.base.ApiResponse;
import com.myapplication.api.MockApi;
import com.myapplication.bean.Result;
import com.myapplication.fragments.mvp.IListMVPContract.IListMVPPresenter;
import com.myapplication.fragments.mvp.IListMVPContract.IListMVPView;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;

public class ListMVPPresenter extends IListMVPPresenter<List<Result>, IListMVPView<List<Result>>> {
  public static final int PAGE_SIZE = 20;

  public ListMVPPresenter(IListMVPView<List<Result>> view, LifecycleProvider<FragmentEvent> provider) {
    super(view, provider);
  }

  @NonNull
  @Override
  public Observable<ApiResponse<List<Result>>> getRequest(boolean isRefresh) {
    return MockApi.queryData(PAGE_SIZE)//
      //.retry(timeoutRetry())//
      .compose(mProvider.bindUntilEvent(FragmentEvent.DESTROY))//
      .subscribeOn(Schedulers.io())// 指定在这行代码之前的subscribe在io线程执行
      //.doOnSubscribe(getDoOnSubscribe())//开始执行之前的准备工作
      //.subscribeOn(AndroidSchedulers.mainThread())//指定 前面的doOnSubscribe 在主线程执行
      .observeOn(AndroidSchedulers.mainThread());//指定这行代码之后的subscribe 在主线程执行
    //.subscribe(getOnNext(isRefresh), getOnError());  }
  }
}

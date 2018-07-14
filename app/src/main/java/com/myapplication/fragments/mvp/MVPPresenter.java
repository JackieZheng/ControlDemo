package com.myapplication.fragments.mvp;

import com.example.mylibrary.base.ApiResponse;
import com.myapplication.api.MockApi;
import com.myapplication.bean.Result;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.FragmentEvent;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.List;

public class MVPPresenter extends
  com.myapplication.fragments.mvp.IMVPContract.IMVPPresenter<com.myapplication.fragments.mvp.IMVPContract.IMVPView<ApiResponse<List<Result>>>> {

  public MVPPresenter(IMVPContract.IMVPView<ApiResponse<List<Result>>> view, LifecycleProvider<FragmentEvent> provider) {
    super(view, provider);
  }

  @Override
  public void loadData(final int pageSize, final boolean isRefresh) {
    mView.obtainData(isRefresh);
  }
}

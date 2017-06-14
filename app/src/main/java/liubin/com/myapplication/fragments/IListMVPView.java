package liubin.com.myapplication.fragments;

import com.example.mylibrary.base.IModel;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 列表模式的MVP模式View继承此类
 */

public interface IListMVPView<MODEL extends IModel> {

  Consumer<? super Disposable> getDoOnSubscribe();

  Consumer getOnNext(boolean isRefresh);

  Consumer<? super Throwable> getOnError();
}

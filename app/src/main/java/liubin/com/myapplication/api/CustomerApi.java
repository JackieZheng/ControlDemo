package liubin.com.myapplication.api;

import android.accounts.NetworkErrorException;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import liubin.com.myapplication.Cheeses;

/**
 * 模拟API
 */

public class CustomerApi {
  private static Random mRandom = new Random();

  /**
   * 模拟获取数据,延时1500毫秒返回数据
   *
   * @param count 获取多少条数据
   * @return {@link Observable}
   */
  public static Observable<List<String>> queryData(final int count) {
    return Observable.timer(1500, TimeUnit.MILLISECONDS)
        .flatMap(new Function<Long, ObservableSource<List<String>>>() {
          @Override public ObservableSource<List<String>> apply(Long aLong) throws Exception {
            return Observable.create(new ObservableOnSubscribe<List<String>>() {
              @Override public void subscribe(ObservableEmitter<List<String>> e) throws Exception {
                int id = mRandom.nextInt() % 3;
                if (id == 0) {
                  List<String> randomSublist = getRandomSublist(Cheeses.sCheeseStrings, count);
                  e.onNext(randomSublist);
                  e.onComplete();
                } else if (id == 1) {
                  e.onNext(new ArrayList<String>());
                  e.onComplete();
                } else {
                  if (mRandom.nextInt() % 2 == 1) {
                    e.onError(new RuntimeException("模拟异常情况"));
                  } else {
                    e.onError(new NetworkErrorException("网络异常"));
                  }
                }
              }
            });
          }
        });
  }

  private static List<String> getRandomSublist(String[] array, int amount) {
    ArrayList<String> list = new ArrayList<>(amount);
    Random random = new Random();
    while (list.size() < amount) {
      list.add(array[random.nextInt(array.length)]);
    }
    return list;
  }
}

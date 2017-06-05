package liubin.com.myapplication.api;

import android.accounts.NetworkErrorException;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import liubin.com.myapplication.Cheeses;

/**
 * 模拟API
 */

public class CustomerApi {
  private static Random mRandom = new Random();

  /**
   * 模拟获取数据
   *
   * @param count
   * @return
   */
  public static Observable<List<String>> queryData(final int count) {
    return Observable.create(new ObservableOnSubscribe<List<String>>() {
      @Override public void subscribe(ObservableEmitter<List<String>> e) throws Exception {
        Thread.sleep(1500);
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

  private static List<String> getRandomSublist(String[] array, int amount) {
    ArrayList<String> list = new ArrayList<>(amount);
    Random random = new Random();
    while (list.size() < amount) {
      list.add(array[random.nextInt(array.length)]);
    }
    return list;
  }
}

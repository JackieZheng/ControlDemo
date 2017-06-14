package liubin.com.myapplication.api;

import android.accounts.NetworkErrorException;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import liubin.com.myapplication.Cheeses;
import liubin.com.myapplication.bean.StringData;
import timber.log.Timber;

/**
 * 模拟API
 */

public class CustomerApi {
  private static Random mRandom = new Random();
  private static int id = mRandom.nextInt(8);
  private static int m = 0;

  /**
   * 模拟获取数据,延时1500毫秒返回数据
   *
   * @param count 获取多少条数据
   * @return {@link Observable}
   */
  public static Observable<StringData> queryData(final int count) {
    return Observable.timer(1500, TimeUnit.MILLISECONDS)
        .flatMap(new Function<Long, ObservableSource<StringData>>() {
          @Override public ObservableSource<StringData> apply(Long aLong) throws Exception {
            return Observable.create(new ObservableOnSubscribe<StringData>() {
              @Override public void subscribe(ObservableEmitter<StringData> e) throws Exception {
                int index = id % 8;
                StringData data = new StringData();
                Timber.e("" + index);
                switch (index) {
                  case 0:
                  case 2:
                  case 4:
                  case 6: {//有数据
                    List<String> randomSublist = getRandomSublist(Cheeses.sCheeseStrings, count);
                    data.setCode(0);
                    data.setData(randomSublist);
                    e.onNext(data);
                    e.onComplete();
                    break;
                  }
                  case 1: {//网络异常,链接超时等
                    e.onError(new NetworkErrorException("网络异常"));
                    break;
                  }
                  case 3: {//本地一般错误,如JSON转换异常,代码错误等
                    e.onError(new RuntimeException("模拟异常情况"));
                    break;
                  }
                  case 5: {//服务端异常
                    data.setCode(1);
                    data.setMessage("服务端异常");
                    e.onNext(data);
                    e.onComplete();
                    break;
                  }
                  case 7: {//服务端已经没有更多数据
                    data.setCode(0);
                    e.onNext(data);
                    e.onComplete();
                    break;
                  }
                }
                if (index == 0) {
                  if (++m % 3 == 0) {//连续三次加载有数据
                    id++;
                  }
                } else {
                  id++;
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

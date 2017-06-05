package liubin.com.myapplication.api;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import liubin.com.myapplication.Cheeses;
import liubin.com.myapplication.bean.BaseModel;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TestApi {
  @GET("/account/accountInfo") Observable<BaseModel> getUser(@Query("page") int page,
      @Query("pagesize") int pageSize);
}

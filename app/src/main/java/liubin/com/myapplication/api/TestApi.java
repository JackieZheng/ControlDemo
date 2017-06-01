package liubin.com.myapplication.api;

import io.reactivex.Observable;
import liubin.com.myapplication.bean.BaseModel;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TestApi {
  @GET("/account/accountInfo") Observable<BaseModel> getUser(@Query("page") int page,
      @Query("pagesize") int pageSize);
}

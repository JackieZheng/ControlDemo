package liubin.com.myapplication.api;

import com.example.mylibrary.base.BaseModel;
import io.reactivex.Observable;
import java.util.List;
import liubin.com.myapplication.bean.User;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {
  @GET("/account/accountInfo") Observable<BaseModel<List<User>>> getUser(@Query("page") int page,
      @Query("pagesize") int pageSize);
}

package com.myapplication.api;

import com.example.mylibrary.base.ApiResponse;
import com.myapplication.bean.User;
import io.reactivex.Observable;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {
  @GET("/account/accountInfo") Observable<ApiResponse<List<User>>> getUser(@Query("page") int page,
      @Query("pagesize") int pageSize);
}

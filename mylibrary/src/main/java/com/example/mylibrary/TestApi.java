package com.example.mylibrary;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TestApi {
  @GET("/account/accountInfo") Observable<BaseModel> getCurrentJokeData(@Query("page") int page,
      @Query("pagesize") int pageSize);
}

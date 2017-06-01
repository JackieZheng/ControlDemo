package com.example.mylibrary;

import android.support.annotation.NonNull;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 创建Api封装
 */
public class ApiClient {

  /** Retrofit 单例 */
  private static Retrofit mRetrofit;

  /**
   * 私有化构造函数
   */
  private ApiClient() {
  }

  /**
   * 创建相应的服务接口
   *
   * @param service 如Test.class
   * @param <T> 参考  TestApi
   * @return T 的实例对象
   */
  @NonNull public static <T> T create(@NonNull Class<T> service) {
    return getRetrofit().create(service);
  }

  /**
   * 获取 Retrofit 单例
   *
   * @return {@link Retrofit}对象
   */
  private static Retrofit getRetrofit() {
    if (mRetrofit == null) {
      synchronized (ApiClient.class) {
        if (mRetrofit == null) {
          mRetrofit = new Retrofit.Builder()//
              .baseUrl("http://test.api.evclub.com")// 基础URL
              .client(getHttpClient()) // HTTP客户端
              .addConverterFactory(GsonConverterFactory.create()) // 添加Gson转换工厂
              .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 添加RxJava2调用适配工厂
              .build();
        }
      }
    }
    return mRetrofit;
  }

  /**
   * 获取默认的 OkHttpClient
   *
   * @return {@link OkHttpClient}
   */
  private static OkHttpClient getHttpClient() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder()//
        .connectTimeout(10, TimeUnit.SECONDS)    //设置连接超时 10s
        .readTimeout(10, TimeUnit.SECONDS);      //设置读取超时 10s

    if (BuildConfig.DEBUG) {// 如果为 debug 模式，则添加日志拦截器
      HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
      interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
      builder.addInterceptor(interceptor);
    }
    return builder.build();
  }
}
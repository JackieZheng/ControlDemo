package com.example.mylibrary.base;

import android.support.annotation.NonNull;
import com.example.mylibrary.BuildConfig;
import com.example.mylibrary.base.adapter.BooleanAdapter;
import com.example.mylibrary.base.adapter.DoubleAdapter;
import com.example.mylibrary.base.adapter.IntegerAdapter;
import com.example.mylibrary.base.adapter.LongAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * 创建Api封装,所有的请求都从这里创建
 */
public class ApiClient {
  /** 服务对应的基础URL */
  private static final String BASE_URL = "http://test.api.evclub.com";

  /** {@link Gson} 对象 */
  private static Gson mGson;

  /** {@link Retrofit} 单例 */
  private static Retrofit mRetrofit;

  /**
   * 私有化构造函数
   */
  private ApiClient() {
  }

  /**
   * 创建相应的服务接口
   * <pre>
   *   eg: ApiClient.create(Api.class)
   * </pre>
   *
   * @param service 如 {@link Api#class}
   * @param <T> 参考  {@link Api}
   * @return T 的实例对象
   */
  @NonNull public static <T> T create(@NonNull Class<T> service) {
    return getRetrofit().create(service);
  }

  /**
   * 获取 {@link Retrofit} 单例
   *
   * @return {@link Retrofit}对象
   */
  private static Retrofit getRetrofit() {
    if (mRetrofit == null) {
      synchronized (ApiClient.class) {
        if (mRetrofit == null) {
          mRetrofit = new Retrofit.Builder()//
              .baseUrl(BASE_URL)// 基础URL
              .client(getHttpClient()) // HTTP客户端
              .addConverterFactory(GsonConverterFactory.create(getGson())) // 添加Gson转换工厂
              .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // 添加RxJava2调用适配工厂
              .build();
        }
      }
    }
    return mRetrofit;
  }

  /**
   * 获取 {@link Gson} 对象
   *
   * @return {@link Gson}
   */
  @NonNull public static Gson getGson() {
    if (mGson == null) {
      synchronized (ApiClient.class) {
        if (mGson == null) {
          mGson = new GsonBuilder()//
              .registerTypeAdapterFactory(//Boolean 类型转换/*Boolean.TYPE == boolean.class*/
                  TypeAdapters.newFactory(Boolean.TYPE, Boolean.class, new BooleanAdapter()))
              .registerTypeAdapterFactory(//Integer 类型转换/*Integer.TYPE == int.class*/
                  TypeAdapters.newFactory(Integer.TYPE, Integer.class, new IntegerAdapter()))
              .registerTypeAdapterFactory(//Double 类型转换/*Double.TYPE == double.class*/
                  TypeAdapters.newFactory(Double.TYPE, Double.class, new DoubleAdapter()))
              .registerTypeAdapterFactory(//Long 类型转换/* Long.TYPE == long.class*/
                  TypeAdapters.newFactory(Long.TYPE, Long.class, new LongAdapter()))
              .create();
        }
      }
    }
    return mGson;
  }

  /**
   * 获取默认的 {@link OkHttpClient}
   *
   * @return {@link OkHttpClient}
   */
  @NonNull private static OkHttpClient getHttpClient() {
    final OkHttpClient.Builder builder = new OkHttpClient.Builder()//
        .writeTimeout(100, TimeUnit.SECONDS)//设置写超时 100s
        .connectTimeout(10, TimeUnit.SECONDS)//设置连接超时 10s
        .readTimeout(10, TimeUnit.SECONDS);//设置读取超时 10s

    try {// Https处理
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, new TrustManager[] { new TrustAnyTrustManager() },
          new java.security.SecureRandom());
      //builder.sslSocketFactory(sc.getSocketFactory());
      builder.sslSocketFactory(sc.getSocketFactory(), new TrustAnyTrustManager());
      builder.hostnameVerifier(new TrustAnyHostnameVerifier());
    } catch (KeyManagementException ignored) {
    } catch (NoSuchAlgorithmException ignored) {
    }

    // 添加拦截器
    List<Interceptor> interceptors = builder.interceptors();
    if (BuildConfig.DEBUG) {//添加日志拦截器
      HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
      interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
      interceptors.add(interceptor);
      //builder.addInterceptor(interceptor);
    }

    interceptors.add(0, new Interceptor() {//请求拦截
      @Override public Response intercept(Chain chain) throws IOException {
        okhttp3.Request.Builder builder = chain.request().newBuilder();
        builder.addHeader("token", "token")
            .addHeader("header1", "header1")
            .addHeader("content-type", "application/json");
        return chain.proceed(builder.build());
      }
    });

    interceptors.add(new Interceptor() {//响应拦截
      @Override public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        int code = response.code();
        if (code >= 200 && code <= 300) {// 2xx表示http ok
          ResponseBody peekBody = response.peekBody(Integer.MAX_VALUE);
          JsonReader jsonReader = getGson().newJsonReader(response.body().charStream());
          TypeAdapter<?> adapter = getGson().getAdapter(TypeToken.get(ApiResponse.class));
          ApiResponse o = (ApiResponse) adapter.read(jsonReader);
          Timber.d(response.toString());
          // todo 可以在这里检测token是否过期进行一些处理
          /*Intent intent = new Intent(app, BaseActivity.class);
          intent.putExtra(BaseActivity.FRAGMENT_CLASS_NAME, BaseFragment.class.getName());
          intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
          Activity activity = app.getTopActivity();
          activity.startActivity(intent);
          activity.finish();*/
        }
        return response;
      }
    });

    return builder.build();
  }

  private static class TrustAnyTrustManager implements X509TrustManager {
    public void checkClientTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {
    }

    public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[] {};
    }
  }

  private static class TrustAnyHostnameVerifier implements HostnameVerifier {
    public boolean verify(String hostname, SSLSession session) {
      return true;
    }
  }
}
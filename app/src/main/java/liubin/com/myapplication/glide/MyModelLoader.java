package liubin.com.myapplication.glide;

import android.support.annotation.Nullable;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.signature.ObjectKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import timber.log.Timber;

import static com.bumptech.glide.load.DataSource.REMOTE;

/**
 * Glide 4.x 自定义GlideModel
 * 这里指定String类型的Model由用户处理
 */
public class MyModelLoader implements ModelLoader<String, InputStream> {

  public MyModelLoader() {
  }

  @Nullable @Override
  public LoadData<InputStream> buildLoadData(String model, int width, int height, Options options) {
    return new LoadData<InputStream>(new MyKey(model), new MyDataFetcher(model));
  }

  @Override public boolean handles(String s) {
    return true;
  }

  /**
   * 文件唯一ID
   * 这个类可以使用 {@link ObjectKey} 代替
   */
  public static class MyKey implements Key {
    String path;

    public MyKey(String path) {
      this.path = path;
    }

    @Override public void updateDiskCacheKey(MessageDigest messageDigest) {
      messageDigest.update(path.getBytes(CHARSET));
    }

    @Override public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      MyKey myKey = (MyKey) o;
      return path != null ? path.equals(myKey.path) : myKey.path == null;
    }

    @Override public int hashCode() {
      return path != null ? path.hashCode() : 0;
    }
  }

  /**
   * 如何加载数据
   */
  public static class MyDataFetcher implements DataFetcher<InputStream> {

    private String file;
    private boolean isCanceled;
    InputStream mInputStream = null;

    public MyDataFetcher(String file) {
      this.file = file;
    }

    @Override public void loadData(Priority priority, DataCallback<? super InputStream> callback) {
      // 可以在这里进行一些文件处理,比如根据文件路径处理,文件解密等
      try {
        Timber.e(file);
        if (!isCanceled) {
          mInputStream = new FileInputStream(new File(file));
        }
      } catch (FileNotFoundException e) {
        callback.onLoadFailed(e);
        Timber.e(e);
      }
      callback.onDataReady(mInputStream);
    }

    @Override public void cleanup() {
      if (mInputStream != null) {
        try {
          mInputStream.close();
        } catch (IOException e) {
          Timber.e(e);
        }
      }
    }

    @Override public void cancel() {
      isCanceled = true;
    }

    @Override public Class<InputStream> getDataClass() {
      return InputStream.class;
    }

    @Override public DataSource getDataSource() {
      //return LOCAL;
      return REMOTE;
      //return DATA_DISK_CACHE;
      //return RESOURCE_DISK_CACHE;
      //return MEMORY_CACHE;
    }
  }

  /**
   * 构造工厂类
   */
  public static class LoaderFactory implements ModelLoaderFactory<String, InputStream> {

    public LoaderFactory() {
    }

    @Override public ModelLoader<String, InputStream> build(MultiModelLoaderFactory multiFactory) {
      return new MyModelLoader();
    }

    @Override public void teardown() {

    }
  }
}
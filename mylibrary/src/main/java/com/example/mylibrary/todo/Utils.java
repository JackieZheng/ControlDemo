package com.example.mylibrary.todo;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ExifInterface;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;
import java.io.IOException;
import java.lang.reflect.Method;

public class Utils {
  /** 获取屏幕分辨率 */
  public static Point getRealScreenSize(Context context) {
    Point screenSize = null;
    try {
      screenSize = new Point();
      WindowManager windowManager =
          (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
      Display defaultDisplay = windowManager.getDefaultDisplay();
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        defaultDisplay.getRealSize(screenSize);
      } else {
        try {
          Method mGetRawW = Display.class.getMethod("getRawWidth");
          Method mGetRawH = Display.class.getMethod("getRawHeight");
          screenSize.set((Integer) mGetRawW.invoke(defaultDisplay),
              (Integer) mGetRawH.invoke(defaultDisplay));
        } catch (Exception e) {
          screenSize.set(defaultDisplay.getWidth(), defaultDisplay.getHeight());
          e.printStackTrace();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return screenSize;
  }

  /**
   * 获取图片宽度
   *
   * @param fileName
   * @return
   * @throws IOException
   */
  public static Point getImageSize(String fileName) throws IOException {
    BitmapFactory.Options options = new BitmapFactory.Options();
    // 最关键在此，把options.inJustDecodeBounds = true;
    // 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(fileName, options); // 此时返回的bitmap为null
    //options.outHeight为原始图片的高
    Point point = new Point();
    point.set(options.outWidth, options.outHeight);

    ExifInterface exifInterface = new ExifInterface(fileName);
    int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL);
    int rotation;
    switch (orientation) {
      case ExifInterface.ORIENTATION_ROTATE_90:
      case ExifInterface.ORIENTATION_TRANSPOSE:
        rotation = 90;
        break;
      case ExifInterface.ORIENTATION_ROTATE_180:
      case ExifInterface.ORIENTATION_FLIP_VERTICAL:
        rotation = 180;
        break;

      case ExifInterface.ORIENTATION_ROTATE_270:
      case ExifInterface.ORIENTATION_TRANSVERSE:
        rotation = 270;
        break;
      default:
        rotation = 0;
    }
    if (rotation == 90 || rotation == 270) {
      point.set(options.outHeight, options.outWidth);
    }
    return point;
  }
}

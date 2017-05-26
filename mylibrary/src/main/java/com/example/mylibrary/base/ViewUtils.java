package com.example.mylibrary.base;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.WebView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.graphics.Bitmap.createBitmap;

public class ViewUtils {
  /**
   * WebView 生成当前屏幕大小的图片
   *
   * @param mWebView
   * @param screenWidth
   * @param screenHeight
   * @return {@link Bitmap}
   */
  public static Bitmap webViewToImage(WebView mWebView, int screenWidth, int screenHeight) {
    // WebView 生成当前屏幕大小的图片，shortImage 就是最终生成的图片
    Bitmap shortImage = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.RGB_565);
    Canvas canvas = new Canvas(shortImage);   // 画布的宽高和屏幕的宽高保持一致
    Paint paint = new Paint();
    canvas.drawBitmap(shortImage, screenWidth, screenHeight, paint);
    mWebView.draw(canvas);
    return shortImage;
  }

  /**
   * WebView 生成长图，也就是超过一屏的图片
   *
   * @param mWebView
   * @return {@link Bitmap}
   */
  public static Bitmap webViewToLongImage(WebView mWebView) {
    mWebView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED,
        View.MeasureSpec.UNSPECIFIED),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
    mWebView.layout(0, 0, mWebView.getMeasuredWidth(), mWebView.getMeasuredHeight());
    mWebView.setDrawingCacheEnabled(true);
    mWebView.buildDrawingCache();
    Bitmap longImage =
        Bitmap.createBitmap(mWebView.getMeasuredWidth(), mWebView.getMeasuredHeight(),
            Bitmap.Config.ARGB_8888);

    // 画布的宽高和 WebView 的网页保持一致
    Canvas canvas = new Canvas(longImage);
    Paint paint = new Paint();
    canvas.drawBitmap(longImage, 0, mWebView.getMeasuredHeight(), paint);
    mWebView.draw(canvas);
    return longImage;
  }

  /**
   * View 生成图片
   *
   * @param mRoot
   * @param scaleW
   * @param scaleH
   * @param path
   * @param context
   */
  private void draw(View mRoot, float scaleW, float scaleH, String path, Context context) {
    File dest = new File(path);
    FileOutputStream fos = null;
    try {
      mRoot.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED,
          View.MeasureSpec.UNSPECIFIED),
          View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
      mRoot.layout(0, 0, mRoot.getMeasuredWidth(), mRoot.getMeasuredHeight());
      mRoot.setDrawingCacheEnabled(true);
      mRoot.buildDrawingCache();
      final Bitmap bmp =
          createBitmap((int) (mRoot.getMeasuredWidth() * scaleW), (int) (mRoot.getMeasuredHeight() * scaleH),
              Bitmap.Config.RGB_565);
      Canvas canvas = new Canvas(bmp);
      canvas.scale(scaleW, scaleH);
      mRoot.draw(canvas);

      fos = new FileOutputStream(dest);
      bmp.compress(Bitmap.CompressFormat.JPEG, 80, fos);
      bmp.recycle();
      fos.flush();

      MediaStore.Images.Media.insertImage(context.getContentResolver(), dest.getAbsolutePath(),
          null, null);
      context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(dest)));

      // 直接分享行程规划截图
      Intent intent = new Intent(Intent.ACTION_SEND);
      intent.setType("image/jpg");
      Uri uri = Uri.fromFile(dest);
      intent.putExtra(Intent.EXTRA_STREAM, uri);
      intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
      intent.putExtra(Intent.EXTRA_TEXT, "分享");
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(Intent.createChooser(intent, "分享至"));
    } catch (OutOfMemoryError e) {
      draw(mRoot, scaleW * 0.9f, scaleH * 0.9f, path, context);
    } catch (FileNotFoundException e) {
    } catch (IOException e) {
    } finally {
      try {
        if (fos != null) fos.close();
      } catch (IOException e) {
        // Silent
      }
    }
  }
}

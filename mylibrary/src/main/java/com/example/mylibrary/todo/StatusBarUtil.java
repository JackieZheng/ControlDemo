package com.example.mylibrary.todo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.example.mylibrary.R;

/**
 * Created by Jaeger on 16/2/14.
 * <p>
 * Email: chjie.jaeger@gmail.com
 * GitHub: https://github.com/laobie
 */
public class StatusBarUtil {

  public static final int DEFAULT_STATUS_BAR_ALPHA = 112;
  private static final int FAKE_STATUS_BAR_VIEW_ID = R.id.statusbarutil_fake_status_bar_view;
  private static final int FAKE_TRANSLUCENT_VIEW_ID = R.id.statusbarutil_translucent_view;
  private static final int TAG_KEY_HAVE_SET_OFFSET = -123;

  /**
   * 设置状态栏颜色
   * Api21+直接设置状态栏颜色
   * Api19+在DecorView中状态栏位置添加状态栏大小的View再设置颜色
   *
   * @param activity 需要设置的activity
   * @param color 状态栏颜色值
   */

  public static void setColor(Activity activity, @ColorInt int color) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//19以上直接设置状态栏颜色
      activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      activity.getWindow().setStatusBarColor(color);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//Api19+
      // 1. 设置透明,内容延伸到状态栏下方
      activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      // 2. 在DocorView中状态栏位置添加状态栏大小的View
      ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
      View fakeStatusBarView = decorView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
      if (fakeStatusBarView != null) {
        if (fakeStatusBarView.getVisibility() == View.GONE) {
          fakeStatusBarView.setVisibility(View.VISIBLE);
        }
        fakeStatusBarView.setBackgroundColor(color);
      } else {
        decorView.addView(createStatusBarView(activity, color));
      }
      // 设置根布局的fitsSystemWindows == true,使内容不被状态栏挡住
      setRootView(activity);
    }
  }

  /**
   * 为滑动返回界面设置状态栏颜色
   *
   * @param activity 需要设置的activity
   * @param color 状态栏颜色值
   */
  public static void setColorForSwipeBack(Activity activity, @ColorInt int color) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      ViewGroup contentView = ((ViewGroup) activity.findViewById(android.R.id.content));
      View rootView = contentView.getChildAt(0);
      int statusBarHeight = getStatusBarHeight(activity);
      if (rootView != null && rootView instanceof CoordinatorLayout) {
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) rootView;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
          coordinatorLayout.setFitsSystemWindows(false);
          contentView.setBackgroundColor(color);
          boolean isNeedRequestLayout = contentView.getPaddingTop() < statusBarHeight;
          if (isNeedRequestLayout) {
            contentView.setPadding(0, statusBarHeight, 0, 0);
            coordinatorLayout.post(new Runnable() {
              @Override public void run() {
                coordinatorLayout.requestLayout();
              }
            });
          }
        } else {
          coordinatorLayout.setStatusBarBackgroundColor(color);
        }
      } else {
        // 设置Activity内容内边距,和背景色,最上部分statusBarHeight高度的背景色作为状态栏颜色
        contentView.setPadding(0, statusBarHeight, 0, 0);
        contentView.setBackgroundColor(color);
      }
      // 设置内容显示到状态栏下层,并使状态栏透明(Api21+完全透明)
      setTransparentForWindow(activity);
    }
  }

  /**
   * 使状态栏半透明
   * <p>
   * 适用于图片作为背景的界面,此时需要图片填充到状态栏
   *
   * @param activity 需要设置的activity
   */
  public static void setTranslucent(Activity activity) {
    setTranslucent(activity, Color.argb(DEFAULT_STATUS_BAR_ALPHA, 0, 0, 0));
  }

  /**
   * 使状态栏半透明
   * <p>
   * 适用于图片作为背景的界面,此时需要图片填充到状态栏
   *
   * @param activity 需要设置的activity
   * @param color 颜色值
   */
  public static void setTranslucent(Activity activity, @ColorInt int color) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      return;
    }
    setTransparent(activity);
    addTranslucentView(activity, color);
  }

  /**
   * 针对根布局是 CoordinatorLayout, 使状态栏半透明
   * <p>
   * 适用于图片作为背景的界面,此时需要图片填充到状态栏
   *
   * @param activity 需要设置的activity
   * @param color 颜色值
   */
  public static void setTranslucentForCoordinatorLayout(Activity activity, @ColorInt int color) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      return;
    }
    transparentStatusBar(activity);
    addTranslucentView(activity, color);
  }

  /**
   * 设置状态栏全透明,只有背景色会深入到状态栏其他控件不会
   *
   * @param activity 需要设置的activity
   */
  public static void setTransparent(Activity activity) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      return;
    }
    transparentStatusBar(activity);
    setRootView(activity);
  }

  /**
   * 使状态栏透明(5.0以上半透明效果,不建议使用)
   * <p>
   * 适用于图片作为背景的界面,此时需要图片填充到状态栏
   *
   * @param activity 需要设置的activity
   */
  @Deprecated public static void setTranslucentDiff(Activity activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      // 设置状态栏透明
      activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      setRootView(activity);
    }
  }

  /**
   * 为 DrawerLayout 布局设置状态栏颜色
   * 注意在[布局文件]设置 fitsSystemWindows = true 这个属性确保DrawerLayout显示到状态栏下面
   * 原理:
   * 1. 设置 DrawerLayout 的 fitsSystemWindows = true (DrawerLayout使填充到状态栏之下)
   * 2. DrawerLayout 第一个子节点是内容节点,在这个节点添加一个状态栏高度的View作为状态栏最底下一层,并使原来View里面的内容在这个View的下方不挡住这个View
   * 3. 在窗体布局中(activity.findViewById(android.R.id.content))添加一个状态栏高度的View作为状态栏最上面一层(这一层位于DrawerLayout之上),并挡住之前添加的View
   *
   * @param activity 需要设置的activity
   * @param drawerLayout {@link DrawerLayout} drawerLayout的第一个子View必须是可以包含多个子控件的View(如:LinearLayout不能是ScrollView等)
   * @param color 状态栏颜色值
   */
  public static void setColorForDrawerLayout(Activity activity, DrawerLayout drawerLayout,
      @ColorInt int color) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      return;
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
    } else {
      activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    // 生成一个状态栏大小的矩形
    // 添加 statusBarView 到布局中
    ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
    View fakeStatusBarView = contentLayout.findViewById(FAKE_STATUS_BAR_VIEW_ID);
    if (fakeStatusBarView != null) {
      if (fakeStatusBarView.getVisibility() == View.GONE) {
        fakeStatusBarView.setVisibility(View.VISIBLE);
      }
      fakeStatusBarView.setBackgroundColor(color);
    } else {
      contentLayout.addView(createStatusBarView(activity, color), 0);
    }

    // 内容布局不是 LinearLayout 时,设置 margin_top
    if (!(contentLayout instanceof LinearLayout) && contentLayout.getChildAt(1) != null) {
      View childAt = contentLayout.getChildAt(1);
      Object tag = childAt.getTag(TAG_KEY_HAVE_SET_OFFSET);
      if (tag == null) {
        ViewGroup.MarginLayoutParams layoutParams =
            (ViewGroup.MarginLayoutParams) childAt.getLayoutParams();
        layoutParams.setMargins(childAt.getLeft(), childAt.getTop() + getStatusBarHeight(activity),
            childAt.getRight(), childAt.getBottom());
        childAt.setTag(TAG_KEY_HAVE_SET_OFFSET, true);
      /*contentLayout.getChildAt(1)
          .setPadding(contentLayout.getPaddingLeft(),
              getStatusBarHeight(activity) + contentLayout.getPaddingTop(),
              contentLayout.getPaddingRight(), contentLayout.getPaddingBottom());*/
      }
    }
    // 设置属性
    setDrawerLayoutProperty(drawerLayout, contentLayout);
    addTranslucentView(activity, color);
  }

  /**
   * 设置 DrawerLayout 属性
   *
   * @param drawerLayout DrawerLayout
   * @param drawerLayoutContentLayout DrawerLayout 的内容布局
   */
  private static void setDrawerLayoutProperty(DrawerLayout drawerLayout,
      ViewGroup drawerLayoutContentLayout) {
    ViewGroup drawer = (ViewGroup) drawerLayout.getChildAt(1);
    drawerLayout.setFitsSystemWindows(false);
    drawerLayoutContentLayout.setFitsSystemWindows(false);
    drawerLayoutContentLayout.setClipToPadding(true);
    drawer.setFitsSystemWindows(false);
  }

  /**
   * 为 DrawerLayout 布局设置状态栏透明
   *
   * @param activity 需要设置的activity
   * @param drawerLayout {@link DrawerLayout} drawerLayout的第一个子View必须是可以包含多个子控件的View(如:LinearLayout不能是ScrollView等)
   */
  public static void setTransparentForDrawerLayout(Activity activity, DrawerLayout drawerLayout) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      return;
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
    } else {
      activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
    /*if (contentLayout != null) {
      Object haveSetOffset = contentLayout.getTag(TAG_KEY_HAVE_SET_OFFSET);
      if (haveSetOffset != null && (Boolean) haveSetOffset) {
        return;
      }
      ViewGroup.MarginLayoutParams layoutParams =
          (ViewGroup.MarginLayoutParams) contentLayout.getLayoutParams();
      layoutParams.setMargins(layoutParams.leftMargin,
          layoutParams.topMargin + getStatusBarHeight(activity), layoutParams.rightMargin,
          layoutParams.bottomMargin);
      contentLayout.setTag(TAG_KEY_HAVE_SET_OFFSET, true);
    }*/

    // 内容布局不是 LinearLayout 时,设置padding top
    if (!(contentLayout instanceof LinearLayout) && contentLayout.getChildAt(1) != null) {
      contentLayout.getChildAt(1).setPadding(0, getStatusBarHeight(activity), 0, 0);
    }

    // 设置属性
    setDrawerLayoutProperty(drawerLayout, contentLayout);
  }

  /**
   * 为 DrawerLayout 布局设置状态栏透明(5.0以上半透明效果,不建议使用)
   *
   * @param activity 需要设置的activity
   * @param drawerLayout DrawerLayout
   */
  @Deprecated public static void setTranslucentForDrawerLayoutDiff(Activity activity,
      DrawerLayout drawerLayout) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      // 设置状态栏透明
      activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      // 设置内容布局属性
      ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
      contentLayout.setFitsSystemWindows(true);
      contentLayout.setClipToPadding(true);
      // 设置抽屉布局属性
      ViewGroup vg = (ViewGroup) drawerLayout.getChildAt(1);
      vg.setFitsSystemWindows(false);
      // 设置 DrawerLayout 属性
      drawerLayout.setFitsSystemWindows(false);
    }
  }

  /**
   * 为头部是 ImageView 的界面设置状态栏全透明
   *
   * @param activity 需要设置的activity
   * @param needOffsetView 需要向下偏移的 View
   */
  public static void setTransparentForImageView(Activity activity, View needOffsetView) {
    setTranslucentForImageView(activity, Color.TRANSPARENT, needOffsetView);
  }

  /**
   * 为头部是 ImageView 的界面设置状态栏透明(使用默认透明度)
   *
   * @param activity 需要设置的activity
   * @param needOffsetView 需要向下偏移的 View
   */
  public static void setTranslucentForImageView(Activity activity, View needOffsetView) {
    setTranslucentForImageView(activity, Color.argb(DEFAULT_STATUS_BAR_ALPHA, 0, 0, 0),
        needOffsetView);
  }

  /**
   * 为头部是 ImageView 的界面设置状态栏透明
   *
   * @param activity 需要设置的activity
   * @param color 颜色值
   * @param needOffsetView 需要向下偏移的 View
   */
  public static void setTranslucentForImageView(Activity activity, @ColorInt int color,
      View needOffsetView) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      return;
    }
    setTransparentForWindow(activity);
    addTranslucentView(activity, color);
    if (needOffsetView != null) {
      Object haveSetOffset = needOffsetView.getTag(TAG_KEY_HAVE_SET_OFFSET);
      if (haveSetOffset != null && (Boolean) haveSetOffset) {
        return;
      }
      ViewGroup.MarginLayoutParams layoutParams =
          (ViewGroup.MarginLayoutParams) needOffsetView.getLayoutParams();
      layoutParams.setMargins(layoutParams.leftMargin,
          layoutParams.topMargin + getStatusBarHeight(activity), layoutParams.rightMargin,
          layoutParams.bottomMargin);
      needOffsetView.setTag(TAG_KEY_HAVE_SET_OFFSET, true);
    }
  }

  /**
   * 为 fragment 头部是 ImageView 的设置状态栏透明
   *
   * @param activity fragment 对应的 activity
   * @param needOffsetView 需要向下偏移的 View
   */
  public static void setTranslucentForImageViewInFragment(Activity activity, View needOffsetView) {
    setTranslucentForImageViewInFragment(activity, DEFAULT_STATUS_BAR_ALPHA, needOffsetView);
  }

  /**
   * 为 fragment 头部是 ImageView 的设置状态栏透明
   *
   * @param activity fragment 对应的 activity
   * @param needOffsetView 需要向下偏移的 View
   */
  public static void setTransparentForImageViewInFragment(Activity activity, View needOffsetView) {
    setTranslucentForImageViewInFragment(activity, 0, needOffsetView);
  }

  /**
   * 为 fragment 头部是 ImageView 的设置状态栏透明
   *
   * @param activity fragment 对应的 activity
   * @param statusBarAlpha 状态栏透明度
   * @param needOffsetView 需要向下偏移的 View
   */
  public static void setTranslucentForImageViewInFragment(Activity activity,
      @IntRange(from = 0, to = 255) int statusBarAlpha, View needOffsetView) {
    setTranslucentForImageView(activity, statusBarAlpha, needOffsetView);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      clearPreviousSetting(activity);
    }
  }

  /**
   * 隐藏伪状态栏 View
   *
   * @param activity 调用的 Activity
   */
  public static void hideFakeStatusBarView(Activity activity) {
    ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
    View fakeStatusBarView = decorView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
    if (fakeStatusBarView != null) {
      fakeStatusBarView.setVisibility(View.GONE);
    }
    View fakeTranslucentView = decorView.findViewById(FAKE_TRANSLUCENT_VIEW_ID);
    if (fakeTranslucentView != null) {
      fakeTranslucentView.setVisibility(View.GONE);
    }
  }

  ///////////////////////////////////////////////////////////////////////////////////

  @TargetApi(Build.VERSION_CODES.KITKAT)
  private static void clearPreviousSetting(Activity activity) {
    ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
    View fakeStatusBarView = decorView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
    if (fakeStatusBarView != null) {
      decorView.removeView(fakeStatusBarView);
      ViewGroup rootView =
          (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
      rootView.setPadding(0, 0, 0, 0);
    }
  }

  /**
   * 添加状态栏View
   *
   * @param activity 需要设置的 activity
   * @param color 颜色值
   */
  private static void addStatusBarView(Activity activity, @ColorInt int color) {
    ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
    View fakeTranslucentView = contentView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
    if (fakeTranslucentView != null) {
      if (fakeTranslucentView.getVisibility() == View.GONE) {
        fakeTranslucentView.setVisibility(View.VISIBLE);
      }
      fakeTranslucentView.setBackgroundColor(color);
    } else {
      contentView.addView(createStatusBarView(activity, color));
    }
  }

  /**
   * 添加半透明矩形条
   *
   * @param activity 需要设置的 activity
   * @param color 颜色值
   */
  private static void addTranslucentView(Activity activity, @ColorInt int color) {
    ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
    View fakeTranslucentView = contentView.findViewById(FAKE_TRANSLUCENT_VIEW_ID);
    if (fakeTranslucentView != null) {
      if (fakeTranslucentView.getVisibility() == View.GONE) {
        fakeTranslucentView.setVisibility(View.VISIBLE);
      }
      fakeTranslucentView.setBackgroundColor(color);
    } else {
      contentView.addView(createTranslucentStatusBarView(activity, color));
    }
  }

  /**
   * 设置根布局参数
   */
  private static void setRootView(Activity activity) {
    ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
    for (int i = 0, count = parent.getChildCount(); i < count; i++) {
      View childView = parent.getChildAt(i);
      if (childView instanceof ViewGroup) {
        childView.setFitsSystemWindows(true);
        ((ViewGroup) childView).setClipToPadding(true);
      }
    }
  }

  /**
   * 设置内容显示到状态栏下层,并使状态栏透明(Api21+完全透明)
   */
  private static void setTransparentForWindow(Activity activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
      // 不使用设置FLAG_TRANSLUCENT_STATUS标志的原因是,设置这个标志状态栏还是有一层透明色
      //activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      // Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住。
      activity.getWindow()
          .getDecorView()
          .setSystemUiVisibility(
              View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      // Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住。
      activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
  }

  /**
   * 使状态栏透明
   */
  @TargetApi(Build.VERSION_CODES.KITKAT) private static void transparentStatusBar(
      Activity activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
      activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
    } else {
      activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
  }

  /**
   * 生成指定颜色的状态栏矩形
   *
   * @param activity 需要设置的activity
   * @param color 状态栏颜色值
   * @return 状态栏矩形条
   */
  private static View createStatusBarView(Activity activity, @ColorInt int color) {
    // 绘制一个和状态栏一样高的矩形
    View statusBarView = new View(activity);
    LinearLayout.LayoutParams params =
        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            getStatusBarHeight(activity));
    statusBarView.setLayoutParams(params);
    statusBarView.setBackgroundColor(color);
    statusBarView.setId(FAKE_STATUS_BAR_VIEW_ID);
    return statusBarView;
  }

  /**
   * 创建半透明矩形 View
   *
   * @param color 颜色值
   * @return 半透明 View
   */
  private static View createTranslucentStatusBarView(Activity activity, @ColorInt int color) {
    // 绘制一个和状态栏一样高的矩形
    View statusBarView = new View(activity);
    LinearLayout.LayoutParams params =
        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            getStatusBarHeight(activity));
    statusBarView.setLayoutParams(params);
    statusBarView.setBackgroundColor(color);
    statusBarView.setId(FAKE_TRANSLUCENT_VIEW_ID);
    return statusBarView;
  }

  /**
   * 获取状态栏高度
   *
   * @param context context
   * @return 状态栏高度
   */
  private static int getStatusBarHeight(Context context) {
    // 获得状态栏高度
    int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
    return context.getResources().getDimensionPixelSize(resourceId);
  }

  private static class back {
    /**
     * 为 DrawerLayout 布局设置状态栏透明,这个颜色值在NavigationView之上,状态栏是透明的
     * 这里的状态栏颜色分为三层,从上到下依次为,addTranslucentView方法创建的View,NavigationView ,StatusBar
     * 但是这里StatusBar设置为透明了
     *
     * @param activity 需要设置的activity
     * @param drawerLayout {@link DrawerLayout} drawerLayout的第一个子View必须是可以包含多个子控件的View(如:LinearLayout不能是ScrollView等)
     * @param color 颜色值
     */
    public static void setTranslucentForDrawerLayout(Activity activity, DrawerLayout drawerLayout,
        @ColorInt int color) {

      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
        return;
      }
      setTransparentForDrawerLayout(activity, drawerLayout);
      addTranslucentView(activity, color);
    }

    /**
     * 计算状态栏颜色
     *
     * @param color color值
     * @param alpha alpha值
     * @return 最终的状态栏颜色
     */
    public static int calculateStatusColor(@ColorInt int color, int alpha) {
      if (alpha == 0) {
        return Color.TRANSPARENT;
      }
      int red = Color.red(color);
      int green = Color.green(color);
      int blue = Color.blue(color);
      return alpha << 24 | red << 16 | green << 8 | blue;
    }

    /**
     * 设置状态栏颜色(5.0以下无半透明效果,不建议使用)
     *
     * @param activity 需要设置的 activity
     * @param color 状态栏颜色值
     */
    @Deprecated public static void setColorDiff(Activity activity, @ColorInt int color) {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
        return;
      }
      transparentStatusBar(activity);
      ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
      // 移除半透明矩形,以免叠加
      View fakeStatusBarView = contentView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
      if (fakeStatusBarView != null) {
        if (fakeStatusBarView.getVisibility() == View.GONE) {
          fakeStatusBarView.setVisibility(View.VISIBLE);
        }
        fakeStatusBarView.setBackgroundColor(color);
      } else {
        contentView.addView(createStatusBarView(activity, color));
      }
      setRootView(activity);
    }

    /**
     * 为DrawerLayout 布局设置状态栏变色(5.0以下无半透明效果,不建议使用)
     *
     * @param activity 需要设置的activity
     * @param drawerLayout DrawerLayout
     * @param color 状态栏颜色值
     */
    @Deprecated public static void setColorForDrawerLayoutDiff(Activity activity,
        DrawerLayout drawerLayout, @ColorInt int color) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 生成一个状态栏大小的矩形
        ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
        View fakeStatusBarView = contentLayout.findViewById(FAKE_STATUS_BAR_VIEW_ID);
        if (fakeStatusBarView != null) {
          if (fakeStatusBarView.getVisibility() == View.GONE) {
            fakeStatusBarView.setVisibility(View.VISIBLE);
          }
          fakeStatusBarView.setBackgroundColor(color);
        } else {
          // 添加 statusBarView 到布局中
          contentLayout.addView(createStatusBarView(activity, color), 0);
        }
        // 内容布局不是 LinearLayout 时,设置padding top
        if (!(contentLayout instanceof LinearLayout) && contentLayout.getChildAt(1) != null) {
          contentLayout.getChildAt(1).setPadding(0, getStatusBarHeight(activity), 0, 0);
        }
        // 设置属性
        setDrawerLayoutProperty(drawerLayout, contentLayout);
      }
    }
  }
}

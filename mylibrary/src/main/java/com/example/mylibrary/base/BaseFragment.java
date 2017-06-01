package com.example.mylibrary.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @param <T> 泛型参数类型为<b>{@link BaseActivity}</b>或其子类
 * 这个泛型参数将指定使用哪个Activity作为Fragment的容器</br>
 * eg: {@link BaseActivity} 表示使用没有 [自定义的顶部栏(状态栏+标题栏+标题栏阴影)] Activity</br>
 * eg: {@link TopBarActivity} 表示使用带有 [自定义的顶部栏(状态栏+标题栏+标题栏阴影)] Activity</br>
 */
public abstract class BaseFragment<T extends BaseActivity> extends Fragment {

  /** Activity,类型由泛型参数&lt;T&gt;指定 **/
  protected BaseActivity mActivity;

  /**
   * 视图是否已经创建完成
   */
  protected boolean isViewCreated = false;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mActivity = (BaseActivity) getActivity();
    if (mActivity == null) {
      throw new IllegalArgumentException("泛型参数类型不正确");
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    isViewCreated = true;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    isViewCreated = false;
  }

  /**
   * 启动Fragment todo 提取出来放到工具类
   *
   * @param fragment 当前所在Fragment
   * @param clazz 目标Fragment,必须为{@link BaseFragment}的子类,且必须声明泛型参数.
   * 泛型参数必须是{@link BaseActivity}及其子类
   * @param bundle 参数
   * @param requestCode 请求吗
   */
  public static void startActivity(Fragment fragment,
      Class<? extends BaseFragment<? extends BaseActivity>> clazz, Bundle bundle, int requestCode) {
    if (bundle == null) bundle = new Bundle();

    bundle.putString(BaseActivity.FRAGMENT_CLASS_NAME, clazz.getName());

    Intent intent = new Intent();
    intent.putExtras(bundle);
    // 泛型参数指定打开那个Activity
    intent.setClass(fragment.getContext(), (Class) getGenericType(clazz));

    if (requestCode != -1) {
      fragment.startActivityForResult(intent, requestCode);
    } else {
      fragment.startActivity(intent);
    }
  }

  /**
   * 启动Fragment todo 提取出来放到工具类
   *
   * @param fragment 当前所在Fragment
   * @param clazz 目标Fragment,必须为{@link BaseFragment}的子类,且必须声明泛型参数.
   * 泛型参数必须是{@link BaseActivity}及其子类
   * @param bundle 参数
   * @param requestCode 请求吗
   */
  public static void startActivity(Activity fragment,
      Class<? extends BaseFragment<? extends BaseActivity>> clazz, Bundle bundle, int requestCode) {
    if (bundle == null) bundle = new Bundle();
    bundle.putString(BaseActivity.FRAGMENT_CLASS_NAME, clazz.getName());

    Intent intent = new Intent();
    intent.putExtras(bundle);
    // 泛型参数指定打开那个Activity
    intent.setClass(fragment, (Class) getGenericType(clazz));

    if (requestCode != -1) {
      fragment.startActivityForResult(intent, requestCode);
    } else {
      fragment.startActivity(intent);
    }
  }

  /**
   * todo 提取出来放到工具类
   * 获取泛型的参数类型 eg: public class Test extends Base<????> 我们获取的是 ???? 的类型
   *
   * @param clazz 需要获取泛型类型的类的字节码
   * @return {@link Type} 可以强转为Class
   */
  private static Type getGenericType(Class clazz) {
    Type superClass = clazz.getGenericSuperclass();
    if (superClass instanceof Class<?>) { // sanity check, should never happen
      throw new IllegalArgumentException(
          "Internal error: TypeReference constructed without actual type information");
    }
        /*
         * 22-Dec-2008, tatu: Not sure if this case is safe -- I suspect
		 * it is possible to make it fail?
		 * But let's deal with specific
		 * case when we know an actual use case, and thereby suitable
		 * workarounds for valid case(s) and/or error to throw
		 * on invalid one(s).
		 */
    return ((ParameterizedType) superClass).getActualTypeArguments()[0];
  }
}

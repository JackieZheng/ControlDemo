package com.myapplication.fragments.kotlin

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.mylibrary.base.ApiResponse
import com.example.mylibrary.base.EndlessScrollListener
import com.example.mylibrary.base.ListFragment
import com.example.mylibrary.base.TopBarActivity
import com.myapplication.R
import com.myapplication.api.MockApi
import com.myapplication.bean.Result
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_basic.*

// 类似Java POJO
//data class Person(var age: Int = 20, var name: String = "jack") {}
// 类似单例模式
//object Status {
//  val success: Int = 1
//  fun test(): Int {
//    System.out.println(success)
//    return success
//  }
//}
//常量定义
//companion object {
//  private val ITEM_TYPE_DATA = 1
//}

/**
 * 有 [自定义的顶部栏(状态栏+标题栏+标题栏阴影)] 的Activity基本使用方式
 * 1. 继承[ListFragment]并指定泛型参数为[TopBarActivity]
 * 2. 重写 [getContentLayoutResourceId]方法,返回内容区域的布局文件,
 *    这个布局文件将嵌入到 [ProgressFragment] 的内容区域
 * 3. 注意请不要重写 [onCreateView],
 * 如需要修改Fragment布局内容,请重写 [getEmptyLayoutResourceId] 方法.
 */
class KotlinFragment : ListFragment<TopBarActivity, Result, List<Result>>() {

  /**
   * 扩展[Fragment],添加toast方法
   * @param message [CharSequence] toast消息
   * @param duration [Int]  消息显示时间长短,default : [Toast.LENGTH_SHORT]
   *
   */
  private fun Fragment.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(activity, message, duration)
        .show()
  }

  companion object {
    private const val PAGE_SIZE = 20
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    obtainData(false)//请求数据,不清空原来数据
  }

  override fun getContentLayoutResourceId(): Int {
    return R.layout.content_basic // 数据加载成功显示的 [Fragment] 内容区域
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    swipe_refresh_layout.setColorSchemeResources(//
        android.R.color.holo_blue_bright, //
        android.R.color.holo_green_light, //
        android.R.color.holo_orange_light, //
        android.R.color.holo_red_light)

    swipe_refresh_layout.setOnRefreshListener { obtainData(true) }

    recycler_view.layoutManager = LinearLayoutManager(context)
    recycler_view.adapter = KotlinAdapter(this, mData, this)
    recycler_view.addOnScrollListener(EndlessScrollListener(this))
  }

  override fun onEmptyViewInflated(emptyView: View) {
    super.onEmptyViewInflated(emptyView)
    (emptyView.findViewById<View>(com.example.mylibrary.R.id.data_empty_text) as TextView).apply {
      text = "这里没有数据"
      setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_order_empty, 0, 0)
    }
  }

  /**
   * 初始化状态栏,标题栏

   * @param activity [TopBarActivity]
   */
  override fun initTopBar(activity: TopBarActivity) {
    super.initTopBar(activity)
    val toolBar: Toolbar = activity.toolBar
    toolBar.title = "Kotlin基本使用"
    toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
    toolBar.setNavigationOnClickListener { mActivity.finish() }
  }

  override fun getRequest(isRefresh: Boolean): Observable<ApiResponse<List<Result>>> {
    return MockApi.queryData(PAGE_SIZE)//
        //.retry(timeoutRetry())//
        .subscribeOn(Schedulers.io())// 指定在这行代码之前的subscribe在io线程执行
        //.doOnSubscribe(doOnSubscribe)//开始执行之前的准备工作
        //.subscribeOn(AndroidSchedulers.mainThread())//指定 前面的doOnSubscribe 在主线程执行
        .observeOn(AndroidSchedulers.mainThread())//指定这行代码之后的subscribe在io线程执行
    //.subscribe(getOnNext(isRefresh), onError)
  }

  override fun onSuccess(data: ApiResponse<List<Result>>, isRefresh: Boolean) {
    if (!data.isSuccess) {// 服务端返回异常代码
      toast(data.message)
      return
    }

    if (isRefresh) mData.clear()
    if (data.data != null) {
      mData.addAll(data.data)
    }
  }

  override fun checkHasMore(data: ApiResponse<List<Result>>): Boolean {
    // 服务调用失败 || 数据不满一页 表示还有更多数据
    //return !data.isSuccess || !(data.data == null || data.data.size != PAGE_SIZE)
    return when {
      !data.isSuccess -> true
      !(data.data == null || data.data.size != PAGE_SIZE) -> true
      else -> false
    }
  }

  override fun onStatusUpdated() {
    swipe_refresh_layout.isRefreshing = isLoading
    recycler_view.adapter.notifyDataSetChanged()
  }
}


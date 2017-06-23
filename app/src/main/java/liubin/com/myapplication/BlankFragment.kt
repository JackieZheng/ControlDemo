package liubin.com.myapplication

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.example.mylibrary.base.ApiResponse
import com.example.mylibrary.base.EndlessScrollListener
import com.example.mylibrary.base.ListFragment
import com.example.mylibrary.base.TopBarActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import liubin.com.myapplication.api.CustomerApi
import liubin.com.myapplication.bean.Result
import liubin.com.myapplication.fragments.BasicAdapter

/**
 * 有 [自定义的顶部栏(状态栏+标题栏+标题栏阴影)] 的Activity基本使用方式
 * 1. 继承[ListFragment]并指定泛型参数为[TopBarActivity]
 * 2. 重写 [getContentLayoutResourceId]方法,返回内容区域的布局文件,
 *    这个布局文件将嵌入到 [ProgressFragment] 的内容区域
 * 3. 注意请不要重写 [onCreateView],
 * 如需要修改Fragment布局内容,请重写 [getFragmentLayoutResourceId] 方法.
 */
class BlankFragment : ListFragment<TopBarActivity, Result, List<Result>>() {
  private val PAGE_SIZE = 20
  internal var mRecyclerView: RecyclerView? = null
  internal var mSwipeRefreshLayout: SwipeRefreshLayout? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    obtainData(false)//请求数据,不清空原来数据
  }

  override fun getFragmentLayoutResourceId(): Int {
    return R.layout.fragment_custom // 自定义 [Fragment] 布局
  }

  override fun getContentLayoutResourceId(): Int {
    return R.layout.content_basic // 数据加载成功显示的内容区域
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    mSwipeRefreshLayout = view.findViewById(R.id.swip) as SwipeRefreshLayout
    mRecyclerView = view.findViewById(R.id.recyclerview) as RecyclerView

    mSwipeRefreshLayout!!.setColorSchemeResources(//
        android.R.color.holo_blue_bright, //
        android.R.color.holo_green_light, //
        android.R.color.holo_orange_light, //
        android.R.color.holo_red_light)
    mSwipeRefreshLayout!!.setOnRefreshListener { obtainData(true) }

    mRecyclerView!!.layoutManager = LinearLayoutManager(context)
    mRecyclerView!!.adapter = BasicAdapter(activity, mData, this)
    mRecyclerView!!.addOnScrollListener(EndlessScrollListener(this))

    // 这一句可以在任何时候调用
    setEmptyMessage("这里没有数据", R.drawable.ic_conn_no_network)
  }

  /**
   * 初始化状态栏,标题栏

   * @param activity [TopBarActivity]
   */
  override fun initTopBar(activity: TopBarActivity) {
    super.initTopBar(activity)
    val toolBar = activity.toolBar
    toolBar.title = "基本使用"
    toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
    toolBar.setNavigationOnClickListener { mActivity.finish() }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    mSwipeRefreshLayout = null
    mRecyclerView = null
  }

  /**
   * 请求数据

   * @param isRefresh 是否清空原来的数据
   */
  public override fun obtainData(isRefresh: Boolean) {
    CustomerApi.queryData(PAGE_SIZE)//
        .subscribeOn(Schedulers.io())// 指定在这行代码之前的subscribe在io线程执行
        .doOnSubscribe(doOnSubscribe)//开始执行之前的准备工作
        .subscribeOn(AndroidSchedulers.mainThread())//指定 前面的doOnSubscribe 在主线程执行
        .observeOn(AndroidSchedulers.mainThread())//指定这行代码之后的subscribe在io线程执行
        .subscribe(getOnNext(isRefresh), onError)
  }

  public override fun onSuccess(data: ApiResponse<List<Result>>, isRefresh: Boolean) {
    if (!data.isSuccess) {// 服务端返回异常代码
      Toast.makeText(context, data.message, Toast.LENGTH_LONG).show()
      return
    }

    if (isRefresh) mData.clear()
    if (data.data != null && data.data.isNotEmpty()) {
      mData.addAll(data.data)
    }
  }

  public override fun checkHasMore(data: ApiResponse<List<Result>>): Boolean {
    // 服务调用失败 || 数据不满一页 表示还有更多数据
    return !data.isSuccess || !(data.data == null || data.data.size != PAGE_SIZE)
  }

  override fun onStatusUpdated() {
    mSwipeRefreshLayout!!.isRefreshing = isLoading
    mRecyclerView!!.adapter.notifyDataSetChanged()
  }
}


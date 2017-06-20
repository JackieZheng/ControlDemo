package liubin.com.myapplication.fragments;

import com.example.mylibrary.base.mvp.IListMVPView;

/**
 * @param <T> 后台服务对应的响应数据结构 ,如<b> ApiResponse&lt;List&lt;User&gt;&gt;</b>
 */
public interface IMVPView<T> extends IListMVPView<T> {
}

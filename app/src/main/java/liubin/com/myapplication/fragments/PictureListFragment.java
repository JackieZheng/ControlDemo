package liubin.com.myapplication.fragments;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import com.example.mylibrary.base.BaseFragment;
import com.example.mylibrary.base.TopBarActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import liubin.com.myapplication.R;
import liubin.com.myapplication.bean.Picture;

/**
 * 图片列表页面
 */
public class PictureListFragment extends BaseFragment<TopBarActivity> {

  private boolean mSelect = false;// 是否为选择模式
  private OnItemClick onItemClickLister;// 每一项的点击事件
  private boolean isViewCreated = false;// 视图是否已经创建
  private List<Picture> mPictures = new ArrayList<>();// 数据

  private GridView mGridView;
  private PictureListAdapter mPictureListAdapter;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    show();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.content_picture_list, container, false);
    mGridView = (GridView) view.findViewById(R.id.grid_view);
    return view;
  }

  @Override public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    isViewCreated = true;
    mPictureListAdapter = new PictureListAdapter(getActivity(), mPictures, mSelect);
    mGridView.setAdapter(mPictureListAdapter);
    mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (onItemClickLister != null) {
          onItemClickLister.onClick(position, view);
        }
      }
    });
    mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (onItemClickLister != null) {
          onItemClickLister.onLongClick(view);
        }
        return true;
      }
    });
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    isViewCreated = false;
    mGridView = null;
    mPictureListAdapter = null;
  }

  /**
   * 设置数据
   *
   * @param pictures
   */
  public void setPictures(List<Picture> pictures) {
    this.mPictures = pictures;
    if (isViewCreated && mPictureListAdapter != null) {
      mPictureListAdapter.notifyDataSetChanged();
    }
  }

  /**
   * 设置是否为选择模式
   *
   * @param select bool
   */
  public void setSelect(boolean select) {
    this.mSelect = select;
    if (isViewCreated && mPictureListAdapter != null) {
      mPictureListAdapter.setSelect(select);
      mPictureListAdapter.notifyDataSetChanged();
    }
  }

  /**
   * 是否为选择模式
   */
  public boolean isSelect() {
    return mSelect;
  }

  /**
   * 设置每一项的点击事件
   *
   * @param onItemClick
   */
  public void setOnItemClick(OnItemClick onItemClick) {
    this.onItemClickLister = onItemClick;
  }

  /**
   * 刷新数据
   */
  public void update() {
    if (isViewCreated && mPictureListAdapter != null) {
      mPictureListAdapter.notifyDataSetChanged();
    }
  }

  public interface OnItemClick {
    /**
     * 点击事件
     *
     * @param index 点击的索引
     * @param view 点击的项对应的View
     */
    void onClick(int index, View view);

    /**
     * 长按事件
     *
     * @param view
     */
    void onLongClick(View view);
  }

  public void show() {
    Observable.create(new ObservableOnSubscribe<List<Picture>>() {
      @Override public void subscribe(ObservableEmitter<List<Picture>> subscriber)
          throws Exception {
        List<Picture> pictures = new ArrayList<>();
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = getActivity().getContentResolver();
        //只查询jpeg和png的图片
        Cursor mCursor = mContentResolver.query(mImageUri, null,
            MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
            new String[] { "image/jpeg", "image/png" }, "date_modified desc ");
        if (mCursor != null) {
          while (mCursor.moveToNext()) {
            //获取图片的路径
            String name =
                mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            Picture picture = new Picture();
            picture.setFileName(name);
            picture.setFilePath(path);
            pictures.add(picture);
          }
          mCursor.close();
        }
        subscriber.onNext(pictures);
        subscriber.onComplete();
      }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<Picture>>() {
          @Override public void accept(List<Picture> pictures) throws Exception {
            mPictures.addAll(pictures);
            if (isViewCreated) mPictureListAdapter.notifyDataSetChanged();
          }
        }, new Consumer<Throwable>() {
          @Override public void accept(Throwable throwable) throws Exception {

          }
        });
  }
}

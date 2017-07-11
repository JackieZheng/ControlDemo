package com.myapplication.fragments;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.myapplication.R;
import com.myapplication.bean.Picture;
import java.io.File;
import java.util.List;

public class PictureListAdapter extends BaseAdapter {

  private LayoutInflater mInflater;
  private final Activity mActivity;
  private final List<Picture> mPictures;
  private boolean mSelect = false;

  private RequestOptions options;

  public PictureListAdapter(Activity activity, List<Picture> pictures, boolean select) {
    this.mPictures = pictures;
    this.mActivity = activity;
    this.mSelect = select;
    this.mInflater = activity.getLayoutInflater();

    options = RequestOptions.centerCropTransform().placeholder(R.drawable.ic_dashboard_black_24dp);
  }

  public void setSelect(boolean select) {
    this.mSelect = select;
    notifyDataSetChanged();
  }

  @Override public int getCount() {
    return mPictures.size();
  }

  @Override public Picture getItem(int i) {
    return mPictures.get(i);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      convertView = mInflater.inflate(R.layout.item_image, parent, false);
      holder = new ViewHolder(convertView);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }
    Picture picture = getItem(position);
    if (holder != null && picture != null) {
      File file = new File(picture.getFilePath());
      if (file.exists() && file.isFile()) {
        //GlideApp.with(mActivity).load(file.getAbsoluteFile()).apply(options).into(holder.image);
        Glide.with(mActivity).load(picture.getFilePath()).apply(options).into(holder.image);
        holder.name.setVisibility(View.GONE);
      }
    }
    return convertView;
  }

  class ViewHolder {
    ImageView image;
    View mask;
    TextView name;

    ViewHolder(View view) {
      name = (TextView) view.findViewById(R.id.name);
      image = (ImageView) view.findViewById(R.id.image);
      mask = view.findViewById(R.id.mask);
      view.setTag(this);
    }
  }
}

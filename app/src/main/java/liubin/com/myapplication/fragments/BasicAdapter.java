package liubin.com.myapplication.fragments;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.mylibrary.base.BaseRecycleViewAdapter;
import com.example.mylibrary.base.BaseViewHolder;
import com.example.mylibrary.base.EndlessScrollListener;
import java.util.List;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import liubin.com.myapplication.CheeseDetailActivity;
import liubin.com.myapplication.R;
import liubin.com.myapplication.bean.Result;

public class BasicAdapter extends BaseRecycleViewAdapter<Result, RecyclerView.ViewHolder> {
  private static final int ITEM_TYPE_DATA = 1;
  private final EndlessScrollListener.IMore mMore;
  private final LayoutInflater mInflater;
  private int mBackground;

  public BasicAdapter(Context context, List<Result> items, EndlessScrollListener.IMore more) {
    super(items);
    this.mMore = more;
    mInflater = LayoutInflater.from(context);
    TypedValue typedValue = new TypedValue();
    context.getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
    mBackground = typedValue.resourceId;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case ITEM_TYPE_DATA: {
        View view = mInflater.inflate(R.layout.list_item, parent, false);
        view.setBackgroundResource(mBackground);
        return new DataViewHolder(view);
      }
      case ITEM_TYPE_FOOTER: {
        View view = mInflater.inflate(R.layout.recyclerview_item_footer, parent, false);
        view.setBackgroundResource(mBackground);
        return new FootViewHolder(view);
      }
    }
    return null;
  }

  @Override public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
    if (viewHolder instanceof DataViewHolder) {
      final DataViewHolder holder = (DataViewHolder) viewHolder;
      final Result item = getItem(position);
      holder.setText(android.R.id.text1, item.getName());
      ImageView imageView = holder.getView(R.id.avatar);
      Glide.with(imageView.getContext())
          .load(getItem(position).getIcon())
          .bitmapTransform(new CropCircleTransformation(imageView.getContext()))
          .into(imageView);
      holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          Context context = v.getContext();
          Intent intent = new Intent(context, CheeseDetailActivity.class);
          intent.putExtra(CheeseDetailActivity.EXTRA_NAME, item.getName());
          intent.putExtra(CheeseDetailActivity.EXTRA_ICON, item.getIcon());
          context.startActivity(intent);
        }
      });

      /*if (true) return;

      holder.mTextView.setText(item.getName());

      holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          Context context = v.getContext();
          Intent intent = new Intent(context, CheeseDetailActivity.class);
          intent.putExtra(CheeseDetailActivity.EXTRA_NAME, item.getName());
          intent.putExtra(CheeseDetailActivity.EXTRA_ICON, item.getIcon());
          context.startActivity(intent);
        }
      });

      Glide.with(holder.mImageView.getContext())
          .load(getItem(position).getIcon())
          .bitmapTransform(new CropCircleTransformation(holder.mImageView.getContext()))
          .into(holder.mImageView);*/
    } else if (viewHolder instanceof FootViewHolder) {
      ((FootViewHolder) viewHolder).setupFootView(mMore);
    }
  }

  @Override public Result getItem(int position) {
    if (position == mData.size()) {
      return null;
    }
    return mData.get(position);
  }

  @Override public int getItemCount() {
    return mData.size() + 1;
  }

  @Override public int getItemViewType(int position) {
    if (position == mData.size()) return ITEM_TYPE_FOOTER;
    return ITEM_TYPE_DATA;
  }

  /**
   * 继承{@link BaseViewHolder}可以是代码更简洁
   */
  private static class DataViewHolder extends BaseViewHolder {
    //private ImageView mImageView;
    //private TextView mTextView;

    DataViewHolder(View view) {
      super(view);
      //mImageView = (ImageView) view.findViewById(R.id.avatar);
      //mTextView = (TextView) view.findViewById(android.R.id.text1);
    }
  }
}

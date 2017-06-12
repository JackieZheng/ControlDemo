package liubin.com.myapplication.fragments;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.mylibrary.BaseRecycleViewAdapter;
import com.example.mylibrary.EndlessScrollListener;
import java.util.List;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import liubin.com.myapplication.CheeseDetailActivity;
import liubin.com.myapplication.Cheeses;
import liubin.com.myapplication.R;

public class BasicAdapter extends BaseRecycleViewAdapter<String, RecyclerView.ViewHolder> {
  private static final int ITEM_TYPE_DATA = 1;
  private final EndlessScrollListener.IMore mMore;
  private int mBackground;

  public BasicAdapter(Context context, List<String> items, EndlessScrollListener.IMore more) {
    super(items);
    this.mMore = more;
    TypedValue typedValue = new TypedValue();
    context.getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
    mBackground = typedValue.resourceId;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case ITEM_TYPE_DATA: {
        View view =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        view.setBackgroundResource(mBackground);
        return new DataViewHolder(view);
      }
      case ITEM_TYPE_FOOTER: {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.recyclerview_item_footer, parent, false);
        view.setBackgroundResource(mBackground);
        return new FootViewHolder(view);
      }
    }
    return null;
  }

  @Override public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
    if (viewHolder instanceof DataViewHolder) {

      final DataViewHolder holder = (DataViewHolder) viewHolder;
      holder.mBoundString = getItem(position);
      holder.mTextView.setText(getItem(position));

      holder.mView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          Context context = v.getContext();
          Intent intent = new Intent(context, CheeseDetailActivity.class);
          intent.putExtra(CheeseDetailActivity.EXTRA_NAME, holder.mBoundString);
          context.startActivity(intent);
        }
      });

      Glide.with(holder.mImageView.getContext())
          .load(Cheeses.getRandomCheeseDrawable())
          .bitmapTransform(new CropCircleTransformation(holder.mImageView.getContext()))
          .into(holder.mImageView);
    } else if (viewHolder instanceof FootViewHolder) {
      ((FootViewHolder) viewHolder).setupFootView(mMore);
    }
  }

  @Override public String getItem(int position) {
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

  private static class DataViewHolder extends RecyclerView.ViewHolder {
    String mBoundString;
    final View mView;
    final ImageView mImageView;
    final TextView mTextView;

    DataViewHolder(View view) {
      super(view);
      mView = view;
      mImageView = (ImageView) view.findViewById(R.id.avatar);
      mTextView = (TextView) view.findViewById(android.R.id.text1);
    }
  }
}

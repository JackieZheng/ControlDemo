package liubin.com.myapplication.fragments;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.mylibrary.base.ActivityUtils;
import com.example.mylibrary.base.BaseAdapter;
import com.example.mylibrary.base.BaseFragment;
import com.example.mylibrary.base.BaseViewHolder;
import com.example.mylibrary.base.EndlessScrollListener;
import java.util.List;
import liubin.com.myapplication.R;
import liubin.com.myapplication.bean.Result;

public class BasicAdapter extends BaseAdapter<Result, RecyclerView.ViewHolder> {
  private static final int ITEM_TYPE_DATA = 1;
  private final EndlessScrollListener.IMore mMore;
  private final LayoutInflater mInflater;
  private final BaseFragment mFragment;
  private final RequestOptions mOptions;
  private int mBackground;

  public BasicAdapter(BaseFragment context, List<Result> items, EndlessScrollListener.IMore more) {
    super(items);
    this.mMore = more;
    this.mFragment = context;
    mInflater = LayoutInflater.from(context.getContext());
    TypedValue typedValue = new TypedValue();
    context.getContext()
        .getTheme()
        .resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
    mBackground = typedValue.resourceId;
    mOptions = RequestOptions.circleCropTransform()
        .priority(Priority.HIGH)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
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
      DataViewHolder holder = (DataViewHolder) viewHolder;
      final Result item = getItem(position);
      holder.setText(R.id.text1, item.getName());
      Glide.with(mFragment)
          .load(item.getIcon())
          .apply(mOptions)
          .into((ImageView) holder.getView(R.id.avatar));

      holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          Bundle bundle = new Bundle();
          bundle.putString(CollapsingToolbarLayoutFragment.EXTRA_NAME, item.getName());
          bundle.putInt(CollapsingToolbarLayoutFragment.EXTRA_ICON, item.getIcon());
          ActivityUtils.startActivity(mFragment, CollapsingToolbarLayoutFragment.class, bundle, -1);
        }
      });
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
   * 继承{@link BaseViewHolder}可以使代码更简洁
   */
  private static class DataViewHolder extends BaseViewHolder {
    public DataViewHolder(View view) {
      super(view);
    }
  }
}

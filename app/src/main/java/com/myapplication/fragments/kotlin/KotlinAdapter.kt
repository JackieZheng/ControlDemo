package com.myapplication.fragments.kotlin

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mylibrary.base.*
import com.myapplication.R
import com.myapplication.bean.Result
import com.myapplication.fragments.CollapsingToolbarLayoutFragment

class KotlinAdapter(private val mFragment: BaseFragment<*>, items: List<Result>,
    private val mMore: EndlessScrollListener.IMore) : BaseAdapter<Result, RecyclerView.ViewHolder>(
    items) {
  //常量定义
  companion object {
    private val ITEM_TYPE_DATA = 1
  }

  private val mOptions: RequestOptions = RequestOptions.circleCropTransform()
  private val mInflater: LayoutInflater = LayoutInflater.from(mFragment.context)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
    when (viewType) {
      Companion.ITEM_TYPE_DATA -> {
        return DataViewHolder(mInflater.inflate(R.layout.list_item, parent, false))
      }
      ITEM_TYPE_FOOTER -> {
        return FootViewHolder(mInflater.inflate(R.layout.recyclerview_item_footer, parent, false))
      }
    }
    return null
  }

  override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
    if (viewHolder is DataViewHolder) {
      val item = getItem(position)
      viewHolder.setText(R.id.text1, item!!.name)
      Glide.with(mFragment)
          .load(item.icon)
          .apply(mOptions)
          .into(viewHolder.getView<ImageView>(R.id.avatar))

      viewHolder.itemView.setOnClickListener {
        val bundle = Bundle()
        bundle.putString(CollapsingToolbarLayoutFragment.EXTRA_NAME, item.name)
        bundle.putInt(CollapsingToolbarLayoutFragment.EXTRA_ICON, item.icon)
        ActivityUtils.startActivity(mFragment, CollapsingToolbarLayoutFragment::class.java, bundle,
            -1)
      }
    } else if (viewHolder is FootViewHolder) {
      viewHolder.setupFootView(mMore)
    }
  }

  override fun getItem(position: Int): Result? {
    if (position == mData.size) {
      return null
    }
    return mData[position]
  }

  override fun getItemCount(): Int {
    return mData.size + 1
  }

  override fun getItemViewType(position: Int): Int {
    if (position == mData.size) return ITEM_TYPE_FOOTER
    return Companion.ITEM_TYPE_DATA
  }

  /**
   * 继承[BaseViewHolder]可以使代码更简洁
   */
  private class DataViewHolder(view: View) : BaseViewHolder(view)


}

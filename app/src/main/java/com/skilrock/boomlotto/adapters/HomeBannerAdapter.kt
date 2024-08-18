package com.skilrock.boomlotto.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowHomeBannerBinding


class HomeBannerAdapter(val context: Context) :
    RecyclerView.Adapter<HomeBannerAdapter.HomeBannerViewHolder>() {

    private var bannerList: ArrayList<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeBannerViewHolder {
        val binding: RowHomeBannerBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_home_banner, parent, false)
        val params = LinearLayout.LayoutParams((parent.width * 0.8).toInt(), ViewGroup.LayoutParams.MATCH_PARENT)
        params.setMargins(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, context.resources.displayMetrics).toInt(), 0, 0, 10)
        binding.card.layoutParams = params
        return HomeBannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeBannerViewHolder, position: Int) {
        bannerList?.get(position)?.let { url ->
            Glide.with(context).load(url).into(holder.rowHomeBannerBinding.ivBanner)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setBannerListData(list: ArrayList<String>) {
        bannerList = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return bannerList?.size ?: 0
    }

    class HomeBannerViewHolder(val rowHomeBannerBinding: RowHomeBannerBinding) :
        RecyclerView.ViewHolder(rowHomeBannerBinding.root)

}
package com.skilrock.boomlotto.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowCityBinding
import com.skilrock.boomlotto.models.response.CityResponse

class CityAdapter(private val onCitySelected:(CityResponse.Data)->Unit) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    private var cityList: ArrayList<CityResponse.Data?>? = null
    private var cityListCopy: ArrayList<CityResponse.Data?>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val binding: RowCityBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_city, parent, false)
        return CityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.rowCityBinding.model = cityList?.get(position)
        holder.rowCityBinding.llRow.setOnClickListener {
            cityList?.let { list -> list[position]?.let { onCitySelected(it) } }
        }
    }

    override fun getItemCount(): Int {
        return cityList?.size ?: 0
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCityList(list: ArrayList<CityResponse.Data?>?) {
        cityList = list
        cityListCopy?.clear()
        list?.let {
            for (city in it) { cityListCopy?.add(city) }
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun searchFilter(text: String) {
        if (text.isBlank()) {
            cityList = cityListCopy
            notifyDataSetChanged()
        } else {
            val filteredList = ArrayList<CityResponse.Data?>()
            cityListCopy?.let {
                for (city in it) {
                    if ((city?.cityName?.lowercase()?.contains(text.lowercase()) == true) or
                        (city?.cityCode?.lowercase()?.contains(text.lowercase()) == true)) {
                        filteredList.add(city)
                    }
                }
                cityList = filteredList
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return cityList?.get(position)?.cityCode?.toLong() ?: -1
    }

    class CityViewHolder(val rowCityBinding: RowCityBinding) :
        RecyclerView.ViewHolder(rowCityBinding.root)

}
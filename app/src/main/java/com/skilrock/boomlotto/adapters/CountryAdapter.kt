package com.skilrock.boomlotto.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowCountryBinding
import com.skilrock.boomlotto.models.response.CountryListResponse

class CountryAdapter(private val onCountrySelected:(CountryListResponse.Data?)->Unit) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    private var countryList: ArrayList<CountryListResponse.Data?>? = null
    private var countryListCopy: ArrayList<CountryListResponse.Data?>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val binding: RowCountryBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_country, parent, false)
        return CountryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.rowCountryBinding.model = countryList?.get(position)
        holder.rowCountryBinding.llRow.setOnClickListener {
            countryList?.let { list -> onCountrySelected(list[position]) }
        }
    }

    override fun getItemCount(): Int {
        return countryList?.size ?: 0
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setCountryList(list: ArrayList<CountryListResponse.Data?>?) {
        countryList = list
        countryListCopy?.clear()
        list?.let {
            for (country in it) { countryListCopy?.add(country) }
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun searchFilter(text: String) {
        if (text.isBlank()) {
            countryList = countryListCopy
            notifyDataSetChanged()
        } else {
            val filteredList = ArrayList<CountryListResponse.Data?>()
            countryListCopy?.let {
                for (country in it) {
                    if ((country?.countryName?.lowercase()?.contains(text.lowercase()) == true) or
                        (country?.countryCode?.lowercase()?.contains(text.lowercase()) == true) or
                        (country?.isdCode?.lowercase()?.contains(text.lowercase()) == true)) {
                        filteredList.add(country)
                    }
                }
                countryList = filteredList
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return countryList?.get(position)?.countryCode?.toLong() ?: -1
    }

    class CountryViewHolder(val rowCountryBinding: RowCountryBinding) :
        RecyclerView.ViewHolder(rowCountryBinding.root)

}
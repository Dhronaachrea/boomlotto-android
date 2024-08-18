package com.skilrock.boomlotto.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.adapters.CountryAdapter
import com.skilrock.boomlotto.databinding.SheetCountrySelectionBinding
import com.skilrock.boomlotto.models.response.CountryListResponse
import com.skilrock.boomlotto.utility.afterTextChanged
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CountrySelectionSheet(private val countryList: ArrayList<CountryListResponse.Data?>, private val isNationality: Boolean,
                            private val onCountrySelected:(CountryListResponse.Data?, Boolean)->Unit) : BottomSheetDialogFragment() {

    private lateinit var binding: SheetCountrySelectionBinding

    companion object {
        const val TAG = "CountrySelectionSheet"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.attributes?.windowAnimations = R.style.SheetAnimation
        binding = DataBindingUtil.inflate(inflater, R.layout.sheet_country_selection, container, false)
        setSheetExpanded()
        return binding.root
    }

    private fun setSheetExpanded() {
        dialog?.setOnShowListener { dialog ->
            val sheetDialog = dialog as BottomSheetDialog
            val bottomSheetInternal: View? = sheetDialog.findViewById(R.id.design_bottom_sheet)
            bottomSheetInternal?.let { BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = true

        val countryAdapter = CountryAdapter(::onCountryClick)
        binding.rvCountry.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = countryAdapter
        }
        countryAdapter.setCountryList(countryList)

        lifecycleScope.launch {
            delay(300)
            binding.rvCountry.visibility = View.VISIBLE
        }

        binding.etSearch.afterTextChanged {
            countryAdapter.searchFilter(it)
        }
    }

    private fun onCountryClick(country: CountryListResponse.Data?) {
        onCountrySelected(country, isNationality)
        dismiss()
    }

}
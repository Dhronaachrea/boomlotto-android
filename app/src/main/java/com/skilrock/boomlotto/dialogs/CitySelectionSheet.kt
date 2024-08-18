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
import com.skilrock.boomlotto.adapters.CityAdapter
import com.skilrock.boomlotto.databinding.SheetCitySelectionBinding
import com.skilrock.boomlotto.models.response.CityResponse
import com.skilrock.boomlotto.utility.afterTextChanged
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CitySelectionSheet(private val cityList: ArrayList<CityResponse.Data?>,
                         private val onCitySelected:(CityResponse.Data)->Unit) : BottomSheetDialogFragment() {

    private lateinit var binding: SheetCitySelectionBinding

    companion object {
        const val TAG = "CitySelectionSheet"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.attributes?.windowAnimations = R.style.SheetAnimation
        binding = DataBindingUtil.inflate(inflater, R.layout.sheet_city_selection, container, false)
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

        val cityAdapter = CityAdapter(::onCityClick)
        binding.rvCity.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = cityAdapter
        }
        cityAdapter.setCityList(cityList)

        lifecycleScope.launch {
            delay(300)
            binding.rvCity.visibility = View.VISIBLE
        }

        binding.etSearch.afterTextChanged {
            cityAdapter.searchFilter(it)
        }
    }

    private fun onCityClick(city: CityResponse.Data) {
        onCitySelected(city)
        dismiss()
    }

}
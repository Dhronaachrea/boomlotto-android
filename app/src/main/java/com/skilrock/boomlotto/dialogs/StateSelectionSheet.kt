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
import com.skilrock.boomlotto.adapters.StateAdapter
import com.skilrock.boomlotto.databinding.SheetStateSelectionBinding
import com.skilrock.boomlotto.models.response.StateResponse
import com.skilrock.boomlotto.utility.afterTextChanged
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StateSelectionSheet(private val stateList: ArrayList<StateResponse.Data?>,
                          private val onStateSelected:(StateResponse.Data)->Unit) : BottomSheetDialogFragment() {

    private lateinit var binding: SheetStateSelectionBinding

    companion object {
        const val TAG = "StateSelectionSheet"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.attributes?.windowAnimations = R.style.SheetAnimation
        binding = DataBindingUtil.inflate(inflater, R.layout.sheet_state_selection, container, false)
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

        val stateAdapter = StateAdapter(::onStateClick)
        binding.rvState.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = stateAdapter
        }
        stateAdapter.setStateList(stateList)

        lifecycleScope.launch {
            delay(300)
            binding.rvState.visibility = View.VISIBLE
        }

        binding.etSearch.afterTextChanged {
            stateAdapter.searchFilter(it)
        }
    }

    private fun onStateClick(state: StateResponse.Data) {
        onStateSelected(state)
        dismiss()
    }

}
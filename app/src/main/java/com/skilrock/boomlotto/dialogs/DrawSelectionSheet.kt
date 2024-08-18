package com.skilrock.boomlotto.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.adapters.BoomDrawSelectionAdapter
import com.skilrock.boomlotto.databinding.SheetDrawSelectionBinding
import com.skilrock.boomlotto.utility.SharedPrefUtils
import com.skilrock.boomlotto.utility.SpacesItemDecoration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DrawSelectionSheet(private val drawCount: Int,
                         val onDrawSelected:(Int)->Unit) : BottomSheetDialogFragment() {

    private lateinit var binding: SheetDrawSelectionBinding
    private var selectedDrawCount = 1

    companion object {
        const val TAG = "DrawSelectionSheet"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.attributes?.windowAnimations = R.style.SheetAnimation
        binding = DataBindingUtil.inflate(inflater, R.layout.sheet_draw_selection, container, false)
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

        val list = ArrayList<DrawData>()
        for (index in 1..drawCount) {
            val text = context?.getString(R.string.next) + " <b>" + index + "</b> " + context?.getString(R.string.draw)
            list.add(DrawData(index, text, index == 1))
        }

        context?.let { cxt ->
            val drawAdapter = BoomDrawSelectionAdapter(cxt, list) { selectedDrawCount = it }
            binding.rvDraws.apply {
                layoutManager = GridLayoutManager(cxt, 2)
                setHasFixedSize(true)
                addItemDecoration(SpacesItemDecoration(SharedPrefUtils.getAppLanguage(context), 2, 40, false))
                adapter = drawAdapter
            }
        }

        lifecycleScope.launch {
            delay(350)
            binding.rvDraws.visibility = View.VISIBLE
        }

        binding.btnOkay.setOnClickListener {
            dismiss()
            onDrawSelected(selectedDrawCount)
        }

        binding.ivBtnClose.setOnClickListener {
            dismiss()
        }
    }

    data class DrawData(val position: Int, val text: String, var isSelected: Boolean)

}
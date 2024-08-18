package com.skilrock.boomlotto.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.adapters.BoomIncompleteLineAdapter
import com.skilrock.boomlotto.databinding.SheetBoomIncompleteLineBinding

class BoomIncompleteLineSheet(private val listBall: ArrayList<String>, private val maxLimit: Int,
                              val buyTicket:()->Unit) : BottomSheetDialogFragment() {

    private lateinit var binding: SheetBoomIncompleteLineBinding

    companion object {
        const val TAG = "BoomIncompleteLineSheet"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.sheet_boom_incomplete_line, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val text = getString(R.string.you_have_selected_only) + " " + listBall.size + " " + getString(
                    R.string.number_out_of) + " " + maxLimit + ")"

        binding.tvHeading.text = text

        context?.let { cxt ->
            val ballAdapter = BoomIncompleteLineAdapter()
            binding.rvNumbers.apply {
                layoutManager = LinearLayoutManager(cxt, LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
                adapter = ballAdapter
            }
            ballAdapter.setBallsList(listBall)
        }

        binding.btnComplete.setOnClickListener {
            dismiss()
        }

        binding.btnProceed.setOnClickListener {
            dismiss()
            buyTicket()
        }
    }

}
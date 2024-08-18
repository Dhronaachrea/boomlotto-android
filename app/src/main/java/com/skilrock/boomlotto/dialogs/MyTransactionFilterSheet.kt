package com.skilrock.boomlotto.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textview.MaterialTextView
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.SheetTransactionFilterBinding
import com.skilrock.boomlotto.utility.getCurrentDate
import com.skilrock.boomlotto.utility.getPreviousDate
import com.skilrock.boomlotto.viewmodels.MyTransactionFilterViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MyTransactionFilterSheet(val callBackSendingSelectedDay:(startDay: String, endDate: String) -> Unit): BottomSheetDialogFragment(){

    private lateinit var filterViewModel: MyTransactionFilterViewModel
    private lateinit var binding: SheetTransactionFilterBinding
    private val mListTextView = ArrayList<MaterialTextView>()
    private var mStartDate = ""
    private var mEndDate = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.attributes?.windowAnimations = R.style.SheetAnimation
        binding = DataBindingUtil.inflate(inflater, R.layout.sheet_transaction_filter, container, false)
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
        filterViewModel = ViewModelProvider(this)[MyTransactionFilterViewModel::class.java]
        binding.viewModelForFilterDateRangePicker = this.filterViewModel
        binding.lifecycleOwner = this

        onClickListener()
        fillTextViewList()
        setCalenderConstraint()
    }

    private fun setCalenderConstraint(){
        val startCalendar = Calendar.getInstance()
        startCalendar.add(Calendar.MONTH, 0)

        startCalendar.set(Calendar.MONTH, Calendar.JANUARY)
        startCalendar.set(Calendar.DAY_OF_MONTH, 1)
        startCalendar.set(Calendar.YEAR, 2021)

        binding.dateRangePicker.setSelectableDateRange(startCalendar, Calendar.getInstance())

        binding.dateRangePicker.setCalendarListener(object : CalendarListener {

            override fun onFirstDateSelected(startDate: Calendar) {
                filterViewModel.livedataShowPickedCustomDate.value = "Select Date Range"
                binding.llCalenderAndTextForResult.setBackgroundResource(R.drawable.duration_cylinderical_btn_colored)
                context?.let { binding.tvSelectDateRangeTextForResult.setTextColor(ContextCompat.getColor(it, R.color.color_app_pink)) }
                binding.ivCalenderIconForResult.setBackgroundResource(R.drawable.calender_icon_dark_pink)
            }

            override fun onDateRangeSelected(startDate: Calendar, endDate: Calendar) {
                val format1         = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val strStartDate    = format1.format(startDate.time)

                val format2         = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val strEndDate      = format2.format(endDate.time)

                mStartDate  = strStartDate
                mEndDate    = strEndDate

                filterViewModel.livedataShowPickedCustomDate.value = ("$strStartDate ${getString(R.string.to)} $strEndDate")
                context?.let { binding.tvSelectDateRangeTextForResult.setTextColor(ContextCompat.getColor(it, R.color.color_app_base_blue)) }

                binding.ivCalenderIconForResult.setBackgroundResource(R.drawable.violet_outline_calendar_today_24)
                context?.let { binding.llCalenderAndTextForResult.setBackgroundColor(ContextCompat.getColor(it, R.color.color_app_purple)) }

                binding.llCalenderAndTextForResult.setBackgroundResource(R.drawable.duration_cylinderical_btn_colored)
                binding.llCalenderAndTextForResult.setBackgroundResource(R.drawable.duration_after_all_filled_cylindrical_btn_color_)

            }
        })
    }

    private fun fillTextViewList() {
        mListTextView.add(binding.tvTodayTextForResult)
        mListTextView.add(binding.tvLast7TextForResult)
        mListTextView.add(binding.tvLast10TextForResult)
        mListTextView.add(binding.tvLast30TextForResult)
        mListTextView.add(binding.tvLast180TextForResult)
    }

    private fun resetAllTextView() {
        mListTextView.forEach { textView ->
            context?.let { textView.setTextColor(ContextCompat.getColor(it, R.color.color_app_base_blue)) }
            textView.setBackgroundResource(R.drawable.duration_cylindrical_btn)
        }
    }

    private fun onClickListener(){
        binding.llCalenderAndTextForResult.setOnClickListener {
            binding.llCalendar.visibility = View.VISIBLE
            resetAllTextView()
        }

        binding.ivBtnCloseForResult.setOnClickListener {
            dismiss()
        }

        binding.tvTodayTextForResult.setOnClickListener {
            mStartDate = getCurrentDate()
            mEndDate   = getCurrentDate()

            lifecycleScope.launch {
                delay(200)
                binding.btnDoneForResult.performClick()
            }
        }

        binding.tvLast7TextForResult.setOnClickListener {
            mStartDate = getPreviousDate(7)
            mEndDate   = getCurrentDate()//filter_sheet_item_ripple

            lifecycleScope.launch {
                delay(200)
                binding.btnDoneForResult.performClick()
            }
        }

        binding.tvLast10TextForResult.setOnClickListener {
            mStartDate = getPreviousDate(10)
            mEndDate   = getCurrentDate()

            lifecycleScope.launch {
                delay(200)
                binding.btnDoneForResult.performClick()
            }
        }

        binding.tvLast30TextForResult.setOnClickListener {
            mStartDate = getPreviousDate(30)
            mEndDate   = getCurrentDate()

            lifecycleScope.launch {
                delay(200)
                binding.btnDoneForResult.performClick()
            }
        }

        binding.tvLast180TextForResult.setOnClickListener {
            mStartDate = getPreviousDate(180)
            mEndDate   = getCurrentDate()

            lifecycleScope.launch {
                delay(200)
                binding.btnDoneForResult.performClick()
            }
        }

        binding.btnDoneForResult.setOnClickListener {
            callBackSendingSelectedDay(mStartDate, mEndDate)
            dismiss()
        }
    }
}

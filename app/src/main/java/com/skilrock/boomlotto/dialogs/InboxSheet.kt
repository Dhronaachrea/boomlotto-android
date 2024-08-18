package com.skilrock.boomlotto.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.SheetInboxBinding
import com.skilrock.boomlotto.models.response.PlayerInboxResponse
import com.skilrock.boomlotto.utility.showToast

class InboxSheet(private val mailData: PlayerInboxResponse.PlrInbox,
                 val deleteMessage: (ArrayList<Int>) -> Unit) : BottomSheetDialogFragment() {

    private lateinit var binding: SheetInboxBinding

    companion object {
        const val TAG = "InboxSheet"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.attributes?.windowAnimations = R.style.SheetAnimation
        binding = DataBindingUtil.inflate(inflater, R.layout.sheet_inbox, container, false)
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

        binding.tvHeader.text   = mailData.subject ?: "NA"
        binding.tvMessage.text  = mailData.contentIdForUse ?: "NA"
        binding.tvDate.text     = (mailData.getDate() + ", " + mailData.getTime())

        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnDelete.setOnClickListener {
            mailData.inboxId?.let { id ->
                dismiss()
                val list = ArrayList<Int>()
                list.add(id)
                deleteMessage(list)
            } ?: context?.showToast(getString(R.string.some_technical_issue))
        }
    }

}
package com.skilrock.boomlotto.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.FragmentSupportBinding
import com.skilrock.boomlotto.databinding.FragmentTermsAndConditionsBinding

class TermsAndConditionsFragment : BaseFragment() {

    private lateinit var binding: FragmentTermsAndConditionsBinding

    companion object {
        const val TAG = "TermsAndConditionsFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_terms_and_conditions, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarElements()
    }

    override fun hideKeyboard() {

    }

    override fun setToolbarElements() {
        master.setToolbarName(getString(R.string.terms_conditions), View.VISIBLE, View.GONE)
    }

    override fun onStop() {
        super.onStop()
        master.setToolbarName("", View.GONE, View.VISIBLE)
    }
}
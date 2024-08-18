package com.skilrock.boomlotto.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.FragmentCharityBinding
import com.skilrock.boomlotto.databinding.FragmentHowToPlayBinding


class CharityFragment : BaseFragment() {

    private lateinit var binding: FragmentCharityBinding

    companion object {
        const val TAG = "CharityFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_charity, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarElements()
    }

    override fun hideKeyboard() {

    }

    override fun setToolbarElements() {
        master.setToolbarName(getString(R.string.charity), View.VISIBLE, View.GONE)
    }

    override fun onStop() {
        super.onStop()
        master.setToolbarName("", View.GONE, View.VISIBLE)
    }
}
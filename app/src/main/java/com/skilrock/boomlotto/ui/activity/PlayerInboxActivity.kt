package com.skilrock.boomlotto.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.adapters.InboxAdapter
import com.skilrock.boomlotto.databinding.ActivityPlayerInboxBinding
import com.skilrock.boomlotto.dialogs.InboxSheet
import com.skilrock.boomlotto.models.request.PlayerInboxRequest
import com.skilrock.boomlotto.models.response.PlayerInboxReadResponse
import com.skilrock.boomlotto.models.response.PlayerInboxResponse
import com.skilrock.boomlotto.utility.*
import com.skilrock.boomlotto.viewmodels.PlayerInboxViewModel
import kotlinx.android.synthetic.main.activity_player_inbox.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_search.*

class PlayerInboxActivity : BaseActivity(), BaseActivity.HeaderInfoListener {

    private lateinit var binding: ActivityPlayerInboxBinding
    private lateinit var viewModel: PlayerInboxViewModel
    private lateinit var mAdapter: InboxAdapter
    private val listMultipleDelete = ArrayList<Int>()
    private val listOfTabs = ArrayList<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setWindowStyling()
        setDataBindingAndViewModel()
        binding.toolbar.tbNavigationIcon.setImageResource(R.drawable.icon_back)
        setRecyclerView()
        viewModel.callPlayerInboxApi(PlayerInboxRequest())
        setClickListeners()
        addTabsToTabList()
        setTextViewTabActive(binding.tvAllBtn)
    }

    override fun onResume() {
        super.onResume()
        setToolbarElements()
    }

    override fun setDataBindingAndViewModel() {
        binding     = DataBindingUtil.setContentView(this, R.layout.activity_player_inbox)
        viewModel   = ViewModelProvider(this)[PlayerInboxViewModel::class.java]

        binding.lifecycleOwner  = this
        binding.viewModel       = viewModel
        headerInfoListener      = this

        viewModel.liveDataVibrator.observe(this, observerVibrator)
        viewModel.liveDataLoader.observe(this, observerLoader)
        viewModel.liveDataHideKeyboard.observe(this, observerHideKeyboard)
        viewModel.liveDataPlayerInbox.observe(this, observerInbox)
        viewModel.liveDataDeleteMessage.observe(this, observerDeleteMessage)
        viewModel.liveDataReadMessage.observe(this, observerReadMessage)
        viewModel.liveDataNetworkError.observe(this, observerNetworkError)
    }

    private fun addTabsToTabList() {
        listOfTabs.add(binding.tvAllBtn)
        listOfTabs.add(binding.tvOffersBtn)
        listOfTabs.add(binding.tvInboxTransactionBtn)
    }
    private fun setClickListeners() {
        binding.tvSelectAll.setOnClickListener {
            if (binding.tvSelectAll.text.toString().equals(getString(R.string.select_all), true)) {
                binding.tvSelectAll.text = getString(R.string.deselect_all)
                listMultipleDelete.clear()
                listMultipleDelete.addAll(mAdapter.getAllInboxId())
                binding.tvDeleteSelected.text = ("${getString(R.string.delete_selected)} (${listMultipleDelete.size})")
                mAdapter.selectAll(true)
            } else {
                binding.tvSelectAll.text = getString(R.string.select_all)
                listMultipleDelete.clear()
                binding.tvDeleteSelected.text = ("${getString(R.string.delete_selected)} (${listMultipleDelete.size})")
                mAdapter.selectAll(false)
            }
        }

        binding.tvDeleteSelected.setOnClickListener {
            if (listMultipleDelete.isNotEmpty())
                deleteMessage(listMultipleDelete)
            else
                this.showToast(getString(R.string.nothing_to_delete))
        }

        searchInputText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mAdapter.searchFilter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //beforeTextChanged
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //onTextChanged
            }
        })

        binding.toolbar.llDrawerIcon.setOnClickListener { onBackPressed() }

        binding.toolbar.llAddBalance.setOnClickListener {
            if (PlayerInfo.isIdVerified()) {
                showToast("Verified Player")
            } else {
                startActivity(Intent(this, IdVerificationActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }

        binding.tvAllBtn.setOnClickListener {
            setTextViewTabActive(binding.tvAllBtn)
        }

        binding.tvOffersBtn.setOnClickListener {
            setTextViewTabActive(binding.tvOffersBtn)
        }

        binding.tvInboxTransactionBtn.setOnClickListener {
            setTextViewTabActive(binding.tvInboxTransactionBtn)
        }
    }

    private fun setTextViewTabActive(textViewToSet: TextView) {
        listOfTabs.forEach { textView ->
            textView.context?.let { context ->
                textView.setTextColor(ContextCompat.getColor(context, R.color.dark_blue))
                textView.setBackgroundResource(R.drawable.filter_sheet_item_ripple)
            }
        }
        textViewToSet.context?.let { context -> textViewToSet.setTextColor(ContextCompat.getColor(context, R.color.color_app_pink)) }
        textViewToSet.setBackgroundResource(R.drawable.duration_cylinderical_btn_colored)
    }

    private fun setRecyclerView() {
        mAdapter = InboxAdapter(this, ::onClickMail, ::onLongPressMail)
        mAdapter.setHasStableIds(true)
        binding.rvInbox.apply {
            layoutManager = LinearLayoutManager(this@PlayerInboxActivity)
            setHasFixedSize(true)
            adapter = mAdapter
        }

        //binding.rvInbox.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    private fun deleteMessage(list: ArrayList<Int>) {
        if (list.isNotEmpty())
            viewModel.callDeleteMessageApi(list)
    }

    private fun onClickMail(mailData: PlayerInboxResponse.PlrInbox, position: Int) {
        binding.rvInbox.hideKeyboard()
        mailData.status?.let { readStatus ->
            if (!readStatus.equals("READ", true)) {
                mailData.inboxId?.let {
                    viewModel.callReadMessageApi(it, position)
                }
            }
        }

        InboxSheet(mailData, ::deleteMessage).apply {
            show(supportFragmentManager, InboxSheet.TAG)
        }
    }

    private fun onLongPressMail(mailData: PlayerInboxResponse.PlrInbox) {
        binding.rvInbox.hideKeyboard()
        if (!InboxAdapter.IS_LONG_PRESS_ACTIVATED) {
            vibrate(this)
            showDeleteBar(true)
        }
        InboxAdapter.IS_LONG_PRESS_ACTIVATED = true
        mailData.inboxId?.let { id ->
            if (listMultipleDelete.contains(id))
                listMultipleDelete.remove(id)
            else
                listMultipleDelete.add(id)

            binding.tvDeleteSelected.text = ("${getString(R.string.delete_selected)} (${listMultipleDelete.size})")

            if (listMultipleDelete.isEmpty())
                resetLongPress()
        }
    }
    
    private val observerInbox = Observer<ResponseStatus<PlayerInboxResponse>> {
        mAdapter.clearData()
        when(it) {
            is ResponseStatus.Success -> {
                showDeleteBar(false)
                it.response.unreadMsgCount?.let { count ->
                    PlayerInfo.setBadgeCount(this, count)
                    setToolbarElements()
                }
                val inboxList: ArrayList<PlayerInboxResponse.PlrInbox?>? = it.response.plrInboxList
                val isListEmpty: Boolean = inboxList?.isEmpty() ?: true
                if (isListEmpty) {
                    binding.rvInbox.visibility      = View.GONE
                    binding.tvBgText.visibility     = View.VISIBLE
                    binding.tvBgText.text           = getString(R.string.no_mails_found)
                    binding.searchBar.visibility    = View.GONE
                    binding.llDeleteBar.visibility  = View.GONE
                } else {
                    binding.rvInbox.visibility      = View.VISIBLE
                    binding.tvBgText.visibility     = View.GONE
                    mAdapter.setInboxList(inboxList)
                }

                it.response.unreadMsgCount?.let { count ->
                    if (count > 0) {
                        if (PlayerInfo.getBadgeCount(this) < 10) {
                            binding.toolbar.tvBadgeCount.text       = count.toString()

                        } else {
                            binding.toolbar.tvBadgeCount.text       = "9+"
                        }
                        binding.toolbar.tvBadgeCount.visibility     = View.VISIBLE
                    } else {
                        binding.toolbar.tvBadgeCount.visibility     = View.GONE
                    }
                } ?: run { binding.toolbar.tvBadgeCount.visibility  = View.GONE }

            }

            is ResponseStatus.Error -> {
                binding.rvInbox.visibility      = View.GONE
                binding.tvBgText.visibility     = View.VISIBLE
                binding.tvBgText.text           = this.getResponseMessage(this, WEAVER, it.errorCode)
            }

            is ResponseStatus.TechnicalError -> {
                binding.rvInbox.visibility      = View.GONE
                binding.tvBgText.visibility     = View.VISIBLE
                binding.tvBgText.text           = getString(it.errorMessageCode)
            }
        }
    }

    private val observerDeleteMessage = Observer<ResponseStatus<PlayerInboxResponse>> {
        when(it) {
            is ResponseStatus.Success -> {
                showDeleteBar(false)
                this.showToast(getString(R.string.deleted_successfully))
                it.response.unreadMsgCount?.let { count ->
                    PlayerInfo.setBadgeCount(this, count)
                    setToolbarElements()
                }
                val inboxList: ArrayList<PlayerInboxResponse.PlrInbox?>? =
                    it.response.plrInboxList
                val isListEmpty: Boolean = inboxList?.isEmpty() ?: true
                if (isListEmpty) {
                    mAdapter.clearData()
                    binding.rvInbox.visibility      = View.GONE
                    binding.tvBgText.visibility     = View.VISIBLE
                    binding.tvBgText.text           = getString(R.string.no_mails_found)
                    binding.searchBar.visibility    = View.GONE
                    binding.llDeleteBar.visibility  = View.GONE
                } else {
                    mAdapter.clearData()
                    mAdapter.setInboxList(inboxList)
                    binding.searchBar.visibility    = View.VISIBLE
                    binding.llDeleteBar.visibility  = View.VISIBLE
                }

                it.response.unreadMsgCount?.let { count ->
                    if (count > 0) {
                        if (PlayerInfo.getBadgeCount(this) < 10) {
                            binding.toolbar.tvBadgeCount.text       = count.toString()

                        } else {
                            binding.toolbar.tvBadgeCount.text       = "9+"
                        }
                        binding.toolbar.tvBadgeCount.visibility     = View.VISIBLE
                    } else {
                        binding.toolbar.tvBadgeCount.visibility     = View.GONE
                    }
                } ?: run { binding.toolbar.tvBadgeCount.visibility  = View.GONE }

            }

            is ResponseStatus.Error -> this.showToast(this.getResponseMessage(this, WEAVER, it.errorCode))

            is ResponseStatus.TechnicalError -> { this.showToast(getString(it.errorMessageCode)) }
        }
    }

    private val observerReadMessage = Observer<ResponseStatus<PlayerInboxReadResponse>> {
        when(it) {
            is ResponseStatus.Success -> {
                it.response.unreadMsgCount?.let { count ->
                    PlayerInfo.setBadgeCount(this, count)
                    setToolbarElements()
                }

                it.response.inboxId?.let { id ->
                    it.response.position?.let { pos ->
                        mAdapter.readMessage(id, pos)
                    }
                }
            }

            is ResponseStatus.Error -> {
                Log.e("log", "Error: ${this.getResponseMessage(this, WEAVER, it.errorCode)}")
            }

            is ResponseStatus.TechnicalError -> {
                Log.e("log", "Technical Error")
            }
        }
    }

    private fun showDeleteBar(flag: Boolean) {
        listMultipleDelete.clear()
        if (flag) {
            binding.searchBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.flip_in_bottom))
            binding.llDeleteBar.visibility  = View.VISIBLE
            binding.searchBar.visibility    = View.GONE
            binding.llDeleteBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.flip_in_top))
        } else {
            binding.searchBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.flip_in_top))
            binding.llDeleteBar.visibility  = View.GONE
            binding.searchBar.visibility    = View.VISIBLE
            binding.llDeleteBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.flip_in_bottom))
        }
    }

    private fun resetLongPress() {
        showDeleteBar(false)
        mAdapter.resetLongPressRows()
    }

    override fun onBackPressed() {
        when {
            InboxAdapter.IS_LONG_PRESS_ACTIVATED -> { resetLongPress() }
            searchOpenView.visibility == View.VISIBLE -> { close_search_button.performClick() }
            else -> {
                super.onBackPressed()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }
    }

    override fun hideKeyboard() {

    }

    fun setToolbarElements() {
        binding.toolbar.llToolbarNotification.visibility    = View.VISIBLE
        binding.toolbar.tvBalance.text                      = HtmlCompat.fromHtml(PlayerInfo.getPlayerTotalBalanceBold(), HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.toolbar.tvToolbarLabel.text                 = getString(R.string.inbox)
        binding.toolbar.tvToolbarLabel.visibility           = View.VISIBLE
        binding.toolbar.ivToolBarIcon.visibility            = View.GONE

        if (PlayerInfo.getBadgeCount(this) > 0) {
            binding.toolbar.tvBadgeCount.text       = if (PlayerInfo.getBadgeCount(this) < 10) PlayerInfo.getBadgeCount(this).toString() else "9+"
            binding.toolbar.tvBadgeCount.visibility = View.VISIBLE
        } else {
            binding.toolbar.tvBadgeCount.visibility = View.GONE
        }
    }

    override fun onHeaderInfoApiResponseCallback() {
        setToolbarElements()
    }
}
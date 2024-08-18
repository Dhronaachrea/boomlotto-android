package com.skilrock.boomlotto.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowPlayedBallsBinding
import com.skilrock.boomlotto.databinding.SheetTicketDetailBinding
import com.skilrock.boomlotto.models.request.MyTicketDetailRequest
import com.skilrock.boomlotto.models.response.MyTicketDetailResponse
import com.skilrock.boomlotto.models.response.MyTicketDetailResponse.ResponseData.DrawWin
import com.skilrock.boomlotto.models.response.ServerTimeResponse
import com.skilrock.boomlotto.utility.*
import com.skilrock.boomlotto.viewmodels.TicketDetailsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat


class TicketDetailSheet(val mContext: Context, private var ticketDetail: MyTicketDetailResponse.ResponseData,
                        private var drawDetail: DrawWin, private var drawTimeDiff: Long = 0, private val mSelectedDrawId: Int,
                        private val onPlaySameNumbersAgain:(ArrayList<ArrayList<String>>)->Unit) : BottomSheetDialogFragment() {

    private lateinit var binding: SheetTicketDetailBinding
    private lateinit var viewModel: TicketDetailsViewModel
    private val mListPlayedNumbers: ArrayList<ArrayList<String>> = ArrayList()

    companion object {
        const val TAG = "TicketDetailSheet"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.attributes?.windowAnimations = R.style.SheetAnimation
        binding = DataBindingUtil.inflate(inflater, R.layout.sheet_ticket_detail, container, false)
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

        setViewModel()
        showPlayedNumbers()
        setDialogElements()

        binding.btnPlayAgain.setOnClickListener {
            if (mListPlayedNumbers.isNotEmpty()) {
                dismiss()
                onPlaySameNumbersAgain(mListPlayedNumbers)
            } else
                mContext.showToast(getString(R.string.some_problem_occurred))
        }

        binding.ivClose.setOnClickListener { dismiss() }
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this)[TicketDetailsViewModel::class.java]
        binding.lifecycleOwner = this

        viewModel.liveDataLoader.observe(viewLifecycleOwner, observerLoader)
        viewModel.liveDataTicketDetail.observe(viewLifecycleOwner, observerTicketDetail)
        viewModel.liveDataServerTime.observe(viewLifecycleOwner, observerServerTime)
    }

    private fun showPlayedNumbers() {
        lifecycleScope.launch {
            delay(400)
            binding.rvPlayedNumbers.visibility = View.VISIBLE
        }
    }

    private fun setDialogElements() {
        val winResult = drawDetail.winResult
        when {
            winResult == null || winResult.isBlank() || winResult == "RESULT AWAITED" -> awaitedCondition()

            winResult != "RESULT AWAITED" && drawDetail.drawStatusForticket == "CLAIM_ALLOW" && drawDetail.panelWinList?.get(0)?.status != "CANCELLED" -> {
                try {
                    val winAmount = drawDetail.winningAmt?.toDouble()
                    if (winAmount != null && winAmount > 0) {
                        winCondition()
                    } else {
                        loseCondition()
                    }
                } catch (e: Exception) {
                    loseCondition()
                }
            }

            winResult != "RESULT AWAITED" && drawDetail.drawStatusForticket == "CLAIM_HOLD" && drawDetail.panelWinList?.get(0)?.status != "CANCELLED" -> {
                holdCondition()
            }

            drawDetail.panelWinList?.get(0)?.status == "CANCELLED" -> {
                cancelCondition()
            }

            else -> {
                mContext.showToast(mContext.getString(R.string.ticket_information_not_available_6))
                dismiss()
            }
        }
    }

    private fun awaitedCondition() {
        binding.llResultAwaited.visibility      = View.VISIBLE
        binding.llWin.visibility                = View.GONE
        binding.llNonWin.visibility             = View.GONE
        binding.tvCancelled.visibility          = View.GONE
        binding.llDrawResult.visibility         = View.GONE
        binding.tvNonWinMsg.text                = mContext.getString(R.string.try_again_to_get_your_winning_streak_going)

        if (drawTimeDiff > 0) {
            binding.tvAwaitedDate.text = getFormattedDate(drawDetail.drawDateTime ?: "")
            DrawCountDownTimer(drawTimeDiff).start()
        } else {
            binding.tvAwaitedDate.text = getDate(drawDetail.drawDateTime ?: "")
            binding.tvAwaitedTime.text = getTime(drawDetail.drawDateTime ?: "")
        }

        setTransactionDetails()
        setPlayedNumbers()
    }

    private fun holdCondition() {
        binding.llResultAwaited.visibility      = View.GONE
        binding.llWin.visibility                = View.GONE
        binding.llNonWin.visibility             = View.VISIBLE
        binding.tvCancelled.visibility          = View.GONE
        binding.llDrawResult.visibility         = View.GONE
        binding.tvNonWinMsg.text                = mContext.getString(R.string.results_are_on_hold)
        binding.tvAwaitedDate.text              = getDate(drawDetail.drawDateTime ?: "")
        binding.tvAwaitedTime.text              = getTime(drawDetail.drawDateTime ?: "")
        setTransactionDetails()
        setPlayedNumbers(true)
    }

    private fun winCondition() {
        binding.llResultAwaited.visibility      = View.GONE
        binding.llWin.visibility                = View.VISIBLE
        binding.llNonWin.visibility             = View.GONE
        binding.tvCancelled.visibility          = View.GONE
        binding.llDrawResult.visibility         = View.VISIBLE
        binding.tvWinAmount.text = ((ticketDetail.txnCurrency ?: BuildConfig.CURRENCY_CODE) + " " + drawDetail.winningAmt)
        setDrawDetails()
        setTransactionDetails()
        setPlayedNumbers()
    }

    private fun loseCondition() {
        binding.llResultAwaited.visibility      = View.GONE
        binding.llWin.visibility                = View.GONE
        binding.llNonWin.visibility             = View.VISIBLE
        binding.tvCancelled.visibility          = View.GONE
        binding.llDrawResult.visibility         = View.VISIBLE
        setDrawDetails()
        setTransactionDetails()
        setPlayedNumbers()
    }

    private fun cancelCondition() {
        binding.llResultAwaited.visibility      = View.GONE
        binding.llWin.visibility                = View.GONE
        binding.llNonWin.visibility             = View.GONE
        binding.tvCancelled.visibility          = View.VISIBLE
        binding.llDrawResult.visibility         = View.VISIBLE
        setDrawDetails()
        setTransactionDetails()
        setPlayedNumbers()
    }

    private fun setDrawDetails() {
        binding.tvDrawResultDate.text = getFormattedDate(drawDetail.drawDateTime ?: "")
        setDrawBalls()
    }

    private fun setTransactionDetails() {
        ticketDetail.saleDate?.let {
            binding.tvTxnTime.text = getFormattedDate(it)
        } ?: run { binding.tvTxnTime.text = "--" }

        ticketDetail.saleAmt?.let {
            binding.tvTxnAmount.text = ((ticketDetail.txnCurrency ?: "") + " " + getFormattedAmount(it))
        } ?: run { binding.tvTxnAmount.text = "--" }

        ticketDetail.tktNumber?.let {
            binding.tvTicketNumber.text = it
        } ?: run { binding.tvTicketNumber.text = "--" }

        drawDetail.drawId?.let {
            binding.tvDrawId.text = it.toString()
        } ?: run { binding.tvDrawId.text = "--" }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getFormattedDate(drawDate: String) : String {
        return try {
            val parser      = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val formatter   = SimpleDateFormat("MMM dd, yyyy, hh:mm aa")
            parser.parse(drawDate)?.let { formatter.format(it) } ?: mContext.getString(R.string.na)
        } catch (e: Exception) {
            mContext.getString(R.string.na)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate(drawDate: String) : String {
        return try {
            val parser      = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val formatter   = SimpleDateFormat("MMM dd, yyyy")
            parser.parse(drawDate)?.let { formatter.format(it) } ?: mContext.getString(R.string.na)
        } catch (e: Exception) {
            mContext.getString(R.string.na)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTime(drawDate: String) : String {
        return try {
            val parser      = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val formatter   = SimpleDateFormat("hh:mm aa")
            parser.parse(drawDate)?.let { formatter.format(it) } ?: mContext.getString(R.string.na)
        } catch (e: Exception) {
            mContext.getString(R.string.na)
        }
    }

    private fun setPlayedNumbers(isHoldCondition: Boolean = false) {
        val winResult = drawDetail.winResult
        if (winResult != null && winResult.isNotBlank()) {
            val winBalls    = winResult.split(",")
            val winList     = winBalls.map { it.trim() }
            val list = ArrayList<BallData>()
            drawDetail.panelWinList?.forEach { panel ->
                panel?.pickedData?.let { pickedNumbers ->
                    var balls       = pickedNumbers.replace("[", "")
                    balls           = balls.replace("]", "")
                    val listBalls   = balls.split(",").map { it.trim() } as ArrayList<String>

                    if (listBalls.size == 5)
                        mListPlayedNumbers.add(listBalls)

                    if (isHoldCondition) {
                        listBalls.forEach { ballNumber ->
                            list.add(BallData(ballNumber.trim(), false))
                        }
                    } else {
                        listBalls.forEach { ballNumber ->
                            list.add(BallData(ballNumber.trim(), winList.contains(ballNumber.trim())))
                        }
                    }
                }
            }

            if (list.isNotEmpty())
                setPlayedNumberRecyclerView(list)
        }
    }

    private fun setPlayedNumberRecyclerView(list: ArrayList<BallData>) {
        val ballAdapter = PlayedBallAdapter(mContext, list)
        binding.rvPlayedNumbers.apply {
            layoutManager = GridLayoutManager(mContext, 5)
            setHasFixedSize(true)
            adapter = ballAdapter
        }
        binding.rvPlayedNumbers.scheduleLayoutAnimation()

        context?.let { cxt ->
            val horizontalDecoration = DividerItemDecoration(binding.rvPlayedNumbers.context, DividerItemDecoration.VERTICAL)
            val horizontalDivider = ContextCompat.getDrawable(cxt, R.drawable.horizontal_divider)
            if (horizontalDivider != null) {
                horizontalDecoration.setDrawable(horizontalDivider)
                binding.rvPlayedNumbers.addItemDecoration(horizontalDecoration)
            }
        }
    }

    data class BallData(val ballNumber: String, val isSelected: Boolean)

    class PlayedBallAdapter(private val mContext: Context, private val listBalls: ArrayList<BallData>) :
        RecyclerView.Adapter<PlayedBallAdapter.PlayedBallViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayedBallViewHolder {
            val binding: RowPlayedBallsBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_played_balls, parent, false)
            return PlayedBallViewHolder(binding)
        }

        override fun onBindViewHolder(holder: PlayedBallViewHolder, position: Int) {
            val tvBall = holder.rowPlayedBallsBinding.tvBall
            listBalls[position].let {
                tvBall.text = it.ballNumber
                if (it.isSelected) {
                    tvBall.background = ContextCompat.getDrawable(mContext, R.drawable.green_ring_ball)
                    tvBall.setTextColor(Color.parseColor("#00D1A0"))
                } else {
                    tvBall.background = ContextCompat.getDrawable(mContext, R.drawable.white_ball)
                    tvBall.setTextColor(Color.parseColor("#444444"))
                }
            }
        }

        override fun getItemCount(): Int {
            return listBalls.size
        }

        class PlayedBallViewHolder(val rowPlayedBallsBinding: RowPlayedBallsBinding) :
            RecyclerView.ViewHolder(rowPlayedBallsBinding.root)

    }

    inner class DrawCountDownTimer(millisInFuture: Long) :
        CountDownTimer(millisInFuture, 1000) {

        override fun onTick(millisUntilFinished: Long) {
            val totalSeconds: Int   = (millisUntilFinished / 1000).toInt()
            val days: Int           = totalSeconds / (60 * 60 * 24)
            val hours: Int          = (totalSeconds % (60 * 60 * 24)) / 3600
            val totalMinutes: Int   = (totalSeconds % (60 * 60 * 24)) % 3600
            val minutes: Int        = totalMinutes / 60
            val seconds: Int        = totalMinutes % 60

            val drawDay             = if (days < 10) "0$days" else days.toString()
            val drawHour            = if (hours < 10) "0$hours" else hours.toString()
            val drawMinute          = if (minutes < 10) "0$minutes" else minutes.toString()
            val drawSecond          = if (seconds < 10) "0$seconds" else seconds.toString()
            val isDrawDayVisible    = days > 0
            val isDrawHourVisible   = hours > 0

            binding.tvAwaitedTime.text = if (isDrawDayVisible && isDrawHourVisible) {
                "${drawDay}d : ${drawHour}h : ${drawMinute}m : ${drawSecond}s"
            } else if (isDrawHourVisible) {
                "${drawHour}h : ${drawMinute}m : ${drawSecond}s"
            } else {
                "${drawMinute}m : ${drawSecond}s"
            }

        }

        override fun onFinish() {
            vibrate(mContext)
            ticketDetail.tktNumber?.let { ticketNumber ->
                viewModel.callTicketDetailApi(MyTicketDetailRequest(ticketNumber = ticketNumber))
            } ?: run {
                dismiss()
            }
        }
    }

    private fun setDrawBalls() {
        val winResult = drawDetail.winResult
        if (winResult != null && winResult.isNotBlank()) {
            val winBalls = winResult.split(",")
            if (winBalls.size == 5) {
                binding.tvResultBall1.text = winBalls[0]
                binding.tvResultBall2.text = winBalls[1]
                binding.tvResultBall3.text = winBalls[2]
                binding.tvResultBall4.text = winBalls[3]
                binding.tvResultBall5.text = winBalls[4]

                binding.tvResultBall1.animate().scaleX(0f).scaleY(0f).setDuration(1).withEndAction { }
                binding.tvResultBall2.animate().scaleX(0f).scaleY(0f).setDuration(1).withEndAction { }
                binding.tvResultBall3.animate().scaleX(0f).scaleY(0f).setDuration(1).withEndAction { }
                binding.tvResultBall4.animate().scaleX(0f).scaleY(0f).setDuration(1).withEndAction { }
                binding.tvResultBall5.animate().scaleX(0f).scaleY(0f).setDuration(1).withEndAction { }

                binding.tvResultBall1.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(400)
                    .withEndAction {
                        binding.tvResultBall2.animate()
                            .scaleX(1f).scaleY(1f)
                            .setDuration(300)
                            .withEndAction {
                                binding.tvResultBall3.animate()
                                    .scaleX(1f).scaleY(1f)
                                    .setDuration(300)
                                    .withEndAction {
                                        binding.tvResultBall4.animate()
                                            .scaleX(1f).scaleY(1f)
                                            .setDuration(300)
                                            .withEndAction {
                                                binding.tvResultBall5.animate()
                                                    .scaleX(1f).scaleY(1f)
                                                    .setDuration(300)
                                                    .withEndAction {

                                                    }
                                            }
                                    }
                            }
                    }


            } else {
                mContext.showToast(mContext.getString(R.string.ticket_information_not_available_7))
                dismiss()
            }
        } else {
            mContext.showToast(mContext.getString(R.string.ticket_information_not_available_8))
            dismiss()
        }
    }

    private val observerTicketDetail = Observer<ResponseStatus<MyTicketDetailResponse>> {
        when (it) {
            is ResponseStatus.Success -> {
                val ticketData = it.response.responseData
                if (ticketData != null) {
                    val drawData = ticketData.drawWinList?.find { draw -> draw?.drawId == mSelectedDrawId }
                    if (drawData != null) {
                        ticketDetail = ticketData
                        drawDetail   = drawData
                        if (drawData.winResult == null || drawData.winResult.isBlank() || drawData.winResult == "RESULT AWAITED") {
                            viewModel.callServerTimeApi()
                        } else {
                            showPlayedNumbers()
                            setDialogElements()
                        }
                    }
                    else
                        this@TicketDetailSheet.dismiss()
                } else
                    this@TicketDetailSheet.dismiss()
            }

            is ResponseStatus.Error -> this@TicketDetailSheet.dismiss()

            is ResponseStatus.TechnicalError -> this@TicketDetailSheet.dismiss()
        }
    }

    private val observerServerTime = Observer<ResponseStatus<ServerTimeResponse>> {
        when (it) {
            is ResponseStatus.Success -> {
                it.response.data?.date?.let { serverTime ->
                    drawDetail.drawDateTime?.let { drawTime ->
                        drawTimeDiff = viewModel.getTimeDifference(drawTime, serverTime)
                        showPlayedNumbers()
                        setDialogElements()
                    } ?: run {
                        this@TicketDetailSheet.dismiss()
                    }
                } ?: run {
                    this@TicketDetailSheet.dismiss()
                }
            }

            is ResponseStatus.Error -> this@TicketDetailSheet.dismiss()

            is ResponseStatus.TechnicalError -> this@TicketDetailSheet.dismiss()

        }
    }

    private val observerLoader = Observer<Boolean> { binding.progressBar.visibility = if (it) View.VISIBLE else View.INVISIBLE }
}
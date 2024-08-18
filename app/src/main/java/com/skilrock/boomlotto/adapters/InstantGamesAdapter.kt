package com.skilrock.boomlotto.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skilrock.boomlotto.BuildConfig
import com.skilrock.boomlotto.R
import com.skilrock.boomlotto.databinding.RowInstantGamesBinding
import com.skilrock.boomlotto.models.response.InstantGameListResponse.Data.Ige.Engines.DUBAI.Game
import com.skilrock.boomlotto.utility.getFormattedAmount

class InstantGamesAdapter(private val listInstantGames: ArrayList<Game?>, val context: Context, var span: Int, val onClickGame:(Game)->Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 2) {
            val binding: RowInstantGamesBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_instant_games, parent, false)
            InstantGameTwoViewHolder(binding)
        } else {
            val binding: RowInstantGamesBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.row_instant_games, parent, false)
            InstantGameOneViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is InstantGameTwoViewHolder) {
            listInstantGames[position]?.let { gameData: Game ->

                val gameAmount = (gameData.currencyCode ?: BuildConfig.CURRENCY_CODE) + " <b>" + getFormattedAmount(gameData.amount.toString().toDouble()) + "</b>"
                holder.rowInstantGamesBinding.tvGameAmount.text = HtmlCompat.fromHtml(gameAmount, HtmlCompat.FROM_HTML_MODE_LEGACY)

                holder.rowInstantGamesBinding.tvGameName.text = gameData.gameName
                val winUpTo = "<b>" + (gameData.gameWinUpto ?: "--") + "</b>"
                holder.rowInstantGamesBinding.tvWinUpTo.text = HtmlCompat.fromHtml(winUpTo, HtmlCompat.FROM_HTML_MODE_LEGACY)

                Glide.with(context).load(gameData.imagePath).into(holder.rowInstantGamesBinding.ivGameImage)

                gameData.productInfo?.donation?.let { donationList ->
                    if (donationList.isNotEmpty())
                        Glide.with(context).load(donationList[0]?.image).into(holder.rowInstantGamesBinding.ivDonation)
                }
            }

            holder.rowInstantGamesBinding.clRow.setOnClickListener {
                listInstantGames[position]?.let { game -> onClickGame(game)}
            }
        } else if (holder is InstantGameOneViewHolder) {
            listInstantGames[position]?.let { gameData: Game ->

                val gameAmount = (gameData.currencyCode ?: BuildConfig.CURRENCY_CODE) + " <b>" + getFormattedAmount(gameData.amount.toString().toDouble()) + "</b>"
                holder.rowInstantGamesBinding.tvGameAmount.text = HtmlCompat.fromHtml(gameAmount, HtmlCompat.FROM_HTML_MODE_LEGACY)

                holder.rowInstantGamesBinding.tvGameName.text = gameData.gameName
                val winUpTo = "<b>" + (gameData.gameWinUpto ?: "--") + "</b>"
                holder.rowInstantGamesBinding.tvWinUpTo.text = HtmlCompat.fromHtml(winUpTo, HtmlCompat.FROM_HTML_MODE_LEGACY)

                Glide.with(context).load(gameData.imagePathLarge).into(holder.rowInstantGamesBinding.ivGameImage)

                gameData.productInfo?.donation?.let { donationList ->
                    if (donationList.isNotEmpty())
                        Glide.with(context).load(donationList[0]?.image).into(holder.rowInstantGamesBinding.ivDonation)
                }
            }

            holder.rowInstantGamesBinding.clRow.setOnClickListener {
                listInstantGames[position]?.let { game -> onClickGame(game)}
            }
        }

    }

    override fun getItemCount(): Int {
        return if (span == 2)
            if (listInstantGames.size > 4) 4 else listInstantGames.size
        else
            listInstantGames.size
    }

    override fun getItemViewType(position: Int): Int {
        return span
    }

    class InstantGameOneViewHolder(val rowInstantGamesBinding: RowInstantGamesBinding) :
        RecyclerView.ViewHolder(rowInstantGamesBinding.root)

    class InstantGameTwoViewHolder(val rowInstantGamesBinding: RowInstantGamesBinding) :
        RecyclerView.ViewHolder(rowInstantGamesBinding.root)

}
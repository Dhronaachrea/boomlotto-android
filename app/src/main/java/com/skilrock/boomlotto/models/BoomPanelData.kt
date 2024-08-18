package com.skilrock.boomlotto.models

import androidx.lifecycle.MutableLiveData

data class BoomPanelData(
    var indexLiveData: MutableLiveData<String>,
    val listNumbers: ArrayList<BallInfo>,
    var isQuickPick: Boolean,
    var isCardOpen: Boolean,
    val isQuickPickAllowed: Boolean,
    var reachedMaxSelectionCount: Boolean = false
    //val listSelectedNumbers: ArrayList<String> = ArrayList()
    ) {
    data class BallInfo(
        var number: String,
        var isClicked: Boolean = false
    )
}

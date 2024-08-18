package com.skilrock.boomlotto.models

data class BoomPanelDataTrash(
    var index: Int,
    val listNumbers: ArrayList<BallInfo>,
    var isQuickPick: Boolean,

) {
    data class BallInfo(
        var rowIndex: Int,
        var number: String
    ) {
        override fun toString(): String {
            return "\nBallInfo(rowIndex=$rowIndex, number='$number')"
        }
    }

    override fun toString(): String {
        return "BoomPanelData(index=$index,\nlistNumbers=$listNumbers,\nisQuickPick=$isQuickPick)\n\n"
    }
}

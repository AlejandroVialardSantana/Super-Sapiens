package com.avs.supersapiens.models

data class GameCategory(
    val title: String,
    val iconResId: Int,
    val category: String,
    var gamesCompleted: Int = 0,
    val totalGames: Int
)

package com.avs.supersapiens.models

data class GameCategory(
    val id: String,
    val title: String,
    val iconResId: Int,
    val category: String,
    var completedGames: Int = 0,
    val totalGames: Int
)

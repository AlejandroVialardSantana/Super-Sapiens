package com.avs.supersapiens.models

data class Game(
    val id: String,
    val title: String,
    val iconResId: Int,
    val type: String,
    var score: Int = 0,
    var isUnlocked: Boolean = false
)
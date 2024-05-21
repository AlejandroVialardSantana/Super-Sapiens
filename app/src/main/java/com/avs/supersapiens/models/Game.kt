package com.avs.supersapiens.models

data class Game(
    val title: String,
    val iconResId: Int,
    var isUnlocked: Boolean = false,
    var questionsAnswered: Int = 0,
    val totalQuestions: Int = 10
)

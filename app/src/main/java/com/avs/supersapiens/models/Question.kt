package com.avs.supersapiens.models

import com.avs.supersapiens.enums.QuestionType

data class Question(
    val text: String,
    val correctAnswer: Int,
    val type: QuestionType
)
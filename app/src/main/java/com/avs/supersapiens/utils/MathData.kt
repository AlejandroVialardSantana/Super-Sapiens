package com.avs.supersapiens.utils

import com.avs.supersapiens.enums.QuestionType
import com.avs.supersapiens.models.Question
import kotlin.random.Random

object MathData {

    fun generateSumAndSubtractQuestions(): List<Question> {
        val questions = mutableListOf<Question>()
        while (questions.size < 10) {
            val num1 = Random.nextInt(20) + 1
            val num2 = Random.nextInt(20) + 1
            val isAddition = Random.nextBoolean()
            if (isAddition) {
                val correctAnswer = num1 + num2
                questions.add(Question("$num1 + $num2 = ?", correctAnswer, QuestionType.TEXT))
            } else {
                val correctAnswer = num1 - num2
                if (correctAnswer >= 0) {
                    questions.add(Question("$num1 - $num2 = ?", correctAnswer, QuestionType.TEXT))
                }
            }
        }
        return questions
    }

    fun generateMultiplicationQuestions(): List<Question> {
        val questions = mutableListOf<Question>()
        while (questions.size < 10) {
            val num1 = Random.nextInt(12) + 1
            val num2 = Random.nextInt(12) + 1
            val correctAnswer = num1 * num2
            questions.add(Question("$num1 * $num2 = ?", correctAnswer, QuestionType.MULTIPLE_CHOICE))
        }
        return questions
    }

    fun generateMultipleChoiceOptions(correctAnswer: Int): List<Int> {
        val options = mutableSetOf(correctAnswer)
        while (options.size < 4) {
            options.add((1..144).random())
        }
        return options.shuffled()
    }
}

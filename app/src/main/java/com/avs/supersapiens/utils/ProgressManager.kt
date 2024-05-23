package com.avs.supersapiens.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class ProgressManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("game_scores", Context.MODE_PRIVATE)

    fun saveScore(gameId: String, score: Int) {
        val editor = sharedPreferences.edit()
        val currentScore = sharedPreferences.getInt(gameId, 0)
        if (score > currentScore) {
            Log.d("ProgressManager", "Saving score: $score for gameId: $gameId")
            editor.putInt(gameId, score)
        }
        editor.apply()
    }

    fun getScore(gameId: String): Int {
        val score = sharedPreferences.getInt(gameId, 0)
        Log.d("ProgressManager", "Retrieved score: $score for gameId: $gameId")
        return score
    }

    fun getCompletedGames(category: String): Int {
        val completedGames = when (category) {
            "math" -> listOf("1", "2").count { getScore(it) == 10 }
            "english" -> listOf("3", "4").count { getScore(it) == 10 }
            "science" -> listOf("5", "6").count { getScore(it) == 10 }
            else -> 0
        }
        Log.d("ProgressManager", "Completed games for category $category: $completedGames")
        return completedGames
    }

    fun clearAllScores() {
        sharedPreferences.edit().clear().apply()
        Log.d("ProgressManager", "All scores cleared")
    }
}

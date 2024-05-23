package com.avs.supersapiens.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.avs.supersapiens.R
import com.avs.supersapiens.models.Game
import com.avs.supersapiens.utils.ProgressManager

class GameListViewModel(application: Application) : AndroidViewModel(application) {

    private val progressManager = ProgressManager(application)

    private val _games = MutableLiveData<List<Game>>()
    val games: LiveData<List<Game>> get() = _games

    fun loadGames(category: String) {
        val games = when (category) {
            "math" -> listOf(
                Game("1", "Sumas y Restas", R.drawable.ic_math_sum, "sum", progressManager.getScore("1")),
                Game("2", "Multiplicaciones", R.drawable.ic_math_multiply, "multiply", progressManager.getScore("2"))
            )
            "english" -> listOf(
                Game("3", "Formar Palabras", R.drawable.ic_english_words, "word", progressManager.getScore("3")),
                Game("4", "Juego de Vocabulario", R.drawable.ic_english_vocabulary, "vocabulary", progressManager.getScore("4"))
            )
            "science" -> listOf(
                Game("5", "Clasificación de Animales", R.drawable.ic_science_animals, "animals", progressManager.getScore("5")),
                Game("6", "Sistema Solar", R.drawable.ic_science_solar, "solar", progressManager.getScore("6"))
            )
            else -> emptyList()
        }

        _games.value = games
    }
}
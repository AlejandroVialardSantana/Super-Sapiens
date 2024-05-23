package com.avs.supersapiens.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.avs.supersapiens.R
import com.avs.supersapiens.models.GameCategory
import com.avs.supersapiens.utils.ProgressManager

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val progressManager = ProgressManager(application)

    private val _categories = MutableLiveData<List<GameCategory>>()
    val categories: LiveData<List<GameCategory>> get() = _categories

    init {
        loadCategories()
    }

    private fun loadCategories() {
        val categories = listOf(
            GameCategory("1", "Matemáticas", R.drawable.ic_math, "math", 0, 2),
            GameCategory("2", "Inglés", R.drawable.ic_english, "english", 0, 2),
            GameCategory("3", "Ciencias", R.drawable.ic_science, "science", 0, 2)
        )

        categories.forEach { category ->
            category.completedGames = progressManager.getCompletedGames(category.category)
        }

        _categories.value = categories
    }
}

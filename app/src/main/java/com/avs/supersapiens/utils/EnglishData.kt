package com.avs.supersapiens.utils

import com.avs.supersapiens.R
import com.avs.supersapiens.models.Word

object EnglishData {
    val words = listOf(
        Word("apple", R.drawable.ic_apple),
        Word("banana", R.drawable.ic_banana),
        Word("cherry", R.drawable.ic_cherry),
        Word("grape", R.drawable.ic_grape),
        Word("orange", R.drawable.ic_orange),
        Word("pear", R.drawable.ic_pear),
        Word("pineapple", R.drawable.ic_pineapple),
        Word("strawberry", R.drawable.ic_strawberry),
        Word("watermelon", R.drawable.ic_watermelon),
        Word("peach", R.drawable.ic_peach),
    )

    fun getRandomWords(number: Int, exclude: List<Word> = emptyList()): List<Word> {
        return words.filter { it !in exclude }.shuffled().take(number)
    }

    fun getWordByIndex(index: Int): Word {
        return words[index]
    }
}

package com.avs.supersapiens.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.avs.supersapiens.R
import com.avs.supersapiens.adapters.GameAdapter
import com.avs.supersapiens.databinding.FragmentGameListBinding
import com.avs.supersapiens.models.Game
import com.avs.supersapiens.ui.activities.MathGameActivity

class GameListFragment : Fragment() {

    private lateinit var binding: FragmentGameListBinding
    private lateinit var games: MutableList<Game>
    private lateinit var adapter: GameAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGameListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val category = arguments?.getString("category") ?: return

        games = when (category) {
            "math" -> loadGames("math")
            "english" -> loadGames("english")
            "science" -> loadGames("science")
            else -> mutableListOf()
        }

        adapter = GameAdapter(games, category) { game ->
            when (game.title) {
                "Sumas y Restas" -> startActivityForResult(Intent(activity, MathGameActivity::class.java), REQUEST_CODE_GAME)
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GAME && resultCode == Activity.RESULT_OK) {
            val correctAnswers = data?.getIntExtra("correctAnswers", 0) ?: 0
            val game = games.find { it.title == "Sumas y Restas" }
            if (game != null) {
                updateGameProgress(game, correctAnswers)
                updateCategoryProgress("math")
                saveGames("math", games)
            }
        }
    }

    private fun updateGameProgress(game: Game, correctAnswers: Int) {
        game.questionsAnswered = correctAnswers
        if (correctAnswers == game.totalQuestions) {
            val nextIndex = games.indexOf(game) + 1
            if (nextIndex < games.size) {
                games[nextIndex].isUnlocked = true
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun updateCategoryProgress(category: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("game_progress", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gamesCompleted = games.count { it.questionsAnswered == it.totalQuestions }
        editor.putInt("${category}_gamesCompleted", gamesCompleted)
        editor.apply()
    }

    private fun loadGames(category: String): MutableList<Game> {
        val sharedPreferences = requireActivity().getSharedPreferences("game_progress", Context.MODE_PRIVATE)
        val games = when (category) {
            "math" -> mutableListOf(
                Game("Sumas y Restas", R.drawable.ic_math_sum, isUnlocked = true),
                Game("Multiplicaciones", R.drawable.ic_math_multiply)
            )
            "english" -> mutableListOf(
                Game("Formar Palabras", R.drawable.ic_english_words, isUnlocked = true),
                Game("Juego de Vocabulario", R.drawable.ic_english_vocabulary)
            )
            "science" -> mutableListOf(
                Game("Clasificación de Animales", R.drawable.ic_science_animals, isUnlocked = true),
                Game("Sistema Solar", R.drawable.ic_science_solar)
            )
            else -> mutableListOf()
        }

        games.forEach { game ->
            game.questionsAnswered = sharedPreferences.getInt("${category}_${game.title}_progress", 0)
            game.isUnlocked = sharedPreferences.getBoolean("${category}_${game.title}_unlocked", game.isUnlocked)
        }

        return games
    }

    private fun saveGames(category: String, games: List<Game>) {
        val sharedPreferences = requireActivity().getSharedPreferences("game_progress", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        games.forEach { game ->
            editor.putInt("${category}_${game.title}_progress", game.questionsAnswered)
            editor.putBoolean("${category}_${game.title}_unlocked", game.isUnlocked)
        }

        editor.apply()
    }

    companion object {
        private const val REQUEST_CODE_GAME = 1
    }
}

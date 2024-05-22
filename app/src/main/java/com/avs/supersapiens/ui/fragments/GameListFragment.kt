package com.avs.supersapiens.ui.fragments

import android.content.Intent
import android.content.SharedPreferences
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
import com.avs.supersapiens.ui.activities.MathGamePlayActivity

class GameListFragment : Fragment() {

    private lateinit var binding: FragmentGameListBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGameListBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences("game_scores", 0)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val category = arguments?.getString("category") ?: return

        val games = when (category) {
            "math" -> listOf(
                Game("1", "Sumas y Restas", R.drawable.ic_math_sum, "sum"),
                Game("2", "Multiplicaciones", R.drawable.ic_math_multiply, "multiply")
            )
            "english" -> listOf(
                Game("3", "Formar Palabras", R.drawable.ic_english_words, "word"),
                Game("4", "Juego de Vocabulario", R.drawable.ic_english_vocabulary, "vocabulary")
            )
            "science" -> listOf(
                Game("5", "Clasificación de Animales", R.drawable.ic_science_animals, "animals"),
                Game("6", "Sistema Solar", R.drawable.ic_science_solar, "solar")
            )
            else -> emptyList()
        }

        games.forEach { game ->
            game.score = sharedPreferences.getInt(game.id, 0)
        }

        val adapter = GameAdapter(games, category) { game ->
            val intent = Intent(context, MathGamePlayActivity::class.java).apply {
                putExtra("gameId", game.id)
                putExtra("gameTitle", game.title)
                putExtra("gameType", game.type)
                putExtra("gameScore", game.score)
            }
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }
}
package com.avs.supersapiens.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.avs.supersapiens.R
import com.avs.supersapiens.adapters.GameCategoryAdapter
import com.avs.supersapiens.databinding.FragmentHomeBinding
import com.avs.supersapiens.models.GameCategory

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences("game_scores", 0)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categories = listOf(
            GameCategory("1", "Matemáticas", R.drawable.ic_math, "math", 0, 2),
            GameCategory("2", "Inglés", R.drawable.ic_english, "english", 0, 2),
            GameCategory("3", "Ciencias", R.drawable.ic_science, "science", 0, 2)
        )

        // Actualizar el progreso para cada categoría
        for (category in categories) {
            updateCategoryProgress(category)
        }

        val adapter = GameCategoryAdapter(categories) { category ->
            val action = HomeFragmentDirections.actionHomeFragmentToGameListFragment(category.category)
            findNavController().navigate(action)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }

    private fun updateCategoryProgress(category: GameCategory) {
        when (category.category) {
            "math" -> {
                val game1Score = sharedPreferences.getInt("1", 0)
                val game2Score = sharedPreferences.getInt("2", 0)
                category.completedGames = if (game1Score == 10) 1 else 0
                category.completedGames += if (game2Score == 10) 1 else 0
            }
            "english" -> {
                val game3Score = sharedPreferences.getInt("3", 0)
                val game4Score = sharedPreferences.getInt("4", 0)
                category.completedGames = if (game3Score == 10) 1 else 0
                category.completedGames += if (game4Score == 10) 1 else 0
            }
            "science" -> {
                val game5Score = sharedPreferences.getInt("5", 0)
                val game6Score = sharedPreferences.getInt("6", 0)
                category.completedGames = if (game5Score == 10) 1 else 0
                category.completedGames += if (game6Score == 10) 1 else 0
            }
        }
    }
}

package com.avs.supersapiens.ui.fragments

import android.content.Context
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categories = loadCategoriesProgress()

        val adapter = GameCategoryAdapter(categories) { category ->
            val action = HomeFragmentDirections.actionHomeFragmentToGameListFragment(category.category)
            findNavController().navigate(action)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }

    private fun loadCategoriesProgress(): List<GameCategory> {
        val sharedPreferences = requireActivity().getSharedPreferences("game_progress", Context.MODE_PRIVATE)
        val mathCompleted = sharedPreferences.getInt("math_gamesCompleted", 0)
        val englishCompleted = sharedPreferences.getInt("english_gamesCompleted", 0)
        val scienceCompleted = sharedPreferences.getInt("science_gamesCompleted", 0)

        return listOf(
            GameCategory("Matemáticas", R.drawable.ic_math, "math", mathCompleted, 2),
            GameCategory("Inglés", R.drawable.ic_english, "english", englishCompleted, 2),
            GameCategory("Ciencias", R.drawable.ic_science, "science", scienceCompleted, 2)
        )
    }
}

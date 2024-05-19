package com.avs.supersapiens.ui.fragments

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

        val categories = listOf(
            GameCategory("Matemáticas", R.drawable.ic_math, "math"),
            GameCategory("Inglés", R.drawable.ic_english, "english"),
            GameCategory("Ciencias", R.drawable.ic_science, "science")
        )

        val adapter = GameCategoryAdapter(categories) { category ->
            val action = HomeFragmentDirections.actionHomeFragmentToGameListFragment(category.category)
            findNavController().navigate(action)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }
}

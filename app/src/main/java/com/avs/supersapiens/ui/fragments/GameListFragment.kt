package com.avs.supersapiens.ui.fragments

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

class GameListFragment : Fragment() {

    private lateinit var binding: FragmentGameListBinding

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

        val games = when (category) {
            "math" -> listOf(
                Game("Sumas y Restas", R.drawable.ic_math_sum),
                Game("Multiplicaciones", R.drawable.ic_math_multiply)
            )
            "english" -> listOf(
                Game("Formar Palabras", R.drawable.ic_english_words),
                Game("Juego de Vocabulario", R.drawable.ic_english_vocabulary)
            )
            "science" -> listOf(
                Game("Clasificación de Animales", R.drawable.ic_science_animals),
                Game("Sistema Solar", R.drawable.ic_science_solar)
            )
            else -> emptyList()
        }

        val adapter = GameAdapter(games) { game ->
            // Implementar navegación a un detalle de juego si es necesario
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }
}

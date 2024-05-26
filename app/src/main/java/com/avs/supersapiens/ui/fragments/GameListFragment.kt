package com.avs.supersapiens.ui.fragments

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.avs.supersapiens.R
import com.avs.supersapiens.adapters.GameAdapter
import com.avs.supersapiens.databinding.FragmentGameListBinding
import com.avs.supersapiens.ui.activities.EnglishGamePlayActivity
import com.avs.supersapiens.ui.activities.MathGamePlayActivity
import com.avs.supersapiens.ui.activities.AnimalGamePlayActivity
import com.avs.supersapiens.ui.activities.SolarSystemGamePlayActivity
import com.avs.supersapiens.viewmodels.GameListViewModel

class GameListFragment : Fragment() {

    private lateinit var binding: FragmentGameListBinding
    private val viewModel: GameListViewModel by viewModels()

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

        val adapter = GameAdapter(emptyList()) { game ->
            if (game.isUnlocked) {
                val intent = when (game.type) {
                    "sum", "multiply" -> Intent(context, MathGamePlayActivity::class.java)
                    "animals" -> Intent(context, AnimalGamePlayActivity::class.java)
                    "solar" -> Intent(context, SolarSystemGamePlayActivity::class.java)
                    "word", "vocabulary" -> Intent(context, EnglishGamePlayActivity::class.java)
                    else -> Intent(context, MathGamePlayActivity::class.java)
                }.apply {
                    putExtra("gameId", game.id)
                    putExtra("gameType", game.type)
                    putExtra("gameScore", game.score)
                }
                startActivity(intent)
            } else {
                Toast.makeText(context, "Este juego está bloqueado, consigue una puntuación de 5 en el anterior primero", Toast.LENGTH_SHORT).show()
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        viewModel.games.observe(viewLifecycleOwner) { games ->
            adapter.updateGames(games)
        }

        viewModel.loadGames(category)
        viewModel.checkForUnlocks()
    }
}

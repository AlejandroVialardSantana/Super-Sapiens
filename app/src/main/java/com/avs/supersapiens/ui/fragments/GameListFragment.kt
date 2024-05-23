package com.avs.supersapiens.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.avs.supersapiens.adapters.GameAdapter
import com.avs.supersapiens.databinding.FragmentGameListBinding
import com.avs.supersapiens.ui.activities.MathGamePlayActivity
import com.avs.supersapiens.ui.activities.ScienceGamePlayActivity
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
            val intent = when (category) {
                "math" -> Intent(context, MathGamePlayActivity::class.java)
                "science" -> Intent(context, ScienceGamePlayActivity::class.java)
                else -> Intent(context, MathGamePlayActivity::class.java)
            }.apply {
                putExtra("gameId", game.id)
                putExtra("gameType", game.type)
                putExtra("gameScore", game.score)
            }
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        viewModel.games.observe(viewLifecycleOwner) { games ->
            adapter.updateGames(games)
        }

        viewModel.loadGames(category)
    }
}

package com.avs.supersapiens.ui.fragments

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.avs.supersapiens.R
import com.avs.supersapiens.adapters.GameCategoryAdapter
import com.avs.supersapiens.databinding.FragmentHomeBinding
import com.avs.supersapiens.models.GameCategory
import com.avs.supersapiens.ui.viewmodels.HomeViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var progressBar: ProgressBar
    private lateinit var mediaPlayer: MediaPlayer
    private val sharedPrefsFile = "supersapiens_prefs"
    private val achievementShownKey = "achievement_shown"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = binding.progressBar

        val adapter = GameCategoryAdapter(emptyList()) { category ->
            val action = HomeFragmentDirections.actionHomeFragmentToGameListFragment(category.category)
            findNavController().navigate(action)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            adapter.updateCategories(categories)
            updateProgressBar(categories)
        }
    }

    private fun updateProgressBar(categories: List<GameCategory>) {
        val totalGames = categories.sumOf { it.totalGames }
        val completedGames = categories.sumOf { it.completedGames }
        val progress = (completedGames.toFloat() / totalGames * 100).toInt()
        progressBar.progress = progress

        if (progress == 100 && !isAchievementShown()) {
            showAchievementAnimation()
            setAchievementShown(true)
        }
    }

    private fun showAchievementAnimation() {
        val animationView = binding.achievementAnimation
        animationView.visibility = View.VISIBLE
        animationView.playAnimation()

        playSound(R.raw.achievement_unlocked)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            animationView.visibility = View.GONE
        }, animationView.duration + 4000)
    }

    private fun isAchievementShown(): Boolean {
        val sharedPreferences = requireContext().getSharedPreferences(sharedPrefsFile, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(achievementShownKey, false)
    }

    private fun setAchievementShown(shown: Boolean) {
        val sharedPreferences = requireContext().getSharedPreferences(sharedPrefsFile, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean(achievementShownKey, shown)
            apply()
        }
    }

    private fun playSound(resourceId: Int) {
        mediaPlayer = MediaPlayer.create(context, resourceId)
        mediaPlayer.setOnCompletionListener { mp ->
            mp.release()
        }
        mediaPlayer.start()
    }
}

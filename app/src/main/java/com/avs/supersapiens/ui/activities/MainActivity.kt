package com.avs.supersapiens.ui.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.avs.supersapiens.R
import com.avs.supersapiens.utils.ProgressManager
import androidx.appcompat.widget.Toolbar
import android.widget.ImageView

class MainActivity : AppCompatActivity() {

    private lateinit var progressManager: ProgressManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressManager = ProgressManager(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController)

        val userIcon: ImageView = findViewById(R.id.userIcon)
        userIcon.setOnClickListener {
            if (navController.currentDestination?.id == R.id.homeFragment) {
                navController.navigate(R.id.action_homeFragment_to_userFragment)
            } else if (navController.currentDestination?.id == R.id.gameListFragment) {
                navController.navigate(R.id.action_gameListFragment_to_userFragment)
            }
        }

        // Botón para borrar las preferencias
        val clearScoresButton: ImageView = findViewById(R.id.clearScoresButton)
        clearScoresButton.setOnClickListener {
            progressManager.clearAllScores()
            verifyPreferencesCleared()
        }
    }

    private fun verifyPreferencesCleared() {
        val score1 = progressManager.getScore("1")
        val score2 = progressManager.getScore("2")
        Log.d("MainActivity", "Scores after clearing: gameId 1 = $score1, gameId 2 = $score2")
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

package com.avs.supersapiens.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.avs.supersapiens.databinding.ActivityGameResultBinding

class GameResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val correctAnswers = intent.getIntExtra("correctAnswers", 0)
        val totalQuestions = intent.getIntExtra("totalQuestions", 10)
        val incorrectAnswers = totalQuestions - correctAnswers

        binding.correctAnswersText.text = "Respuestas Correctas: $correctAnswers"
        binding.incorrectAnswersText.text = "Respuestas Incorrectas: $incorrectAnswers"

        binding.retryButton.setOnClickListener {
            val retryIntent = Intent(this, MathGamePlayActivity::class.java)
            startActivity(retryIntent)
            finish()
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }
}

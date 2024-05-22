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

        binding.correctAnswersText.text = "Respuestas Correctas: $correctAnswers"
        binding.incorrectAnswersText.text = "Respuestas Incorrectas: ${totalQuestions - correctAnswers}"

        binding.retryButton.setOnClickListener {
            finish()
        }

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
}

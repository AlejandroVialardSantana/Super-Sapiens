package com.avs.supersapiens.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.avs.supersapiens.databinding.ActivityMathGameBinding

class MathGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMathGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMathGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startGameButton.setOnClickListener {
            val intent = Intent(this, MathGamePlayActivity::class.java)
            startActivity(intent)
        }
    }
}

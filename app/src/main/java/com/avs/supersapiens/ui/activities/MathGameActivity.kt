package com.avs.supersapiens.ui.activities

import android.app.Activity
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
            startActivityForResult(intent, REQUEST_CODE_PLAY_GAME)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PLAY_GAME && resultCode == Activity.RESULT_OK) {
            val correctAnswers = data?.getIntExtra("correctAnswers", 0) ?: 0
            val resultIntent = Intent()
            resultIntent.putExtra("correctAnswers", correctAnswers)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    companion object {
        private const val REQUEST_CODE_PLAY_GAME = 1
    }
}

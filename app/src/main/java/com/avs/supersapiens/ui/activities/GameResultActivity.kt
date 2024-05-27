package com.avs.supersapiens.ui.activities

import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.avs.supersapiens.R
import com.avs.supersapiens.databinding.ActivityGameResultBinding
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

/**
 * Actividad que muestra los resultados del juego.
 */
class GameResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameResultBinding
    private lateinit var mediaPlayer: MediaPlayer

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

        binding.shareButton.setOnClickListener {
            shareResults(correctAnswers, totalQuestions)
        }

        // Si el usuario ha respondido correctamente a todas las preguntas, se muestra la animación de confeti.
        if (correctAnswers == 10) {
            showConfetti()
            playSound(R.raw.konfetti)
        }
    }

    /**
     * Comparte los resultados del juego a través de una aplicación de mensajería.
     *
     * @param correctAnswers Número de respuestas correctas.
     * @param totalQuestions Número total de preguntas.
     */
    private fun shareResults(correctAnswers: Int, totalQuestions: Int) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            val message = """
                ¡He obtenido $correctAnswers de $totalQuestions respuestas correctas en el juego de *SuperSapiens*!
                
                ¿Puedes superarme? ¡Descarga la app *SuperSapiens* y juega para mejorar tus habilidades en inglés, matemáticas y más!
                
                [Enlace a la app en la tienda]
            """.trimIndent()
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Compartir resultados a través de"))
    }

    /**
     * Muestra la animación de confeti.
     */
    private fun showConfetti() {
        val konfettiView = binding.konfettiView

        konfettiView.build()
            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
            .setDirection(0.0, 359.0)
            .setSpeed(1f, 5f)
            .setFadeOutEnabled(true)
            .setTimeToLive(2000L)
            .addShapes(Shape.Square, Shape.Circle)
            .addSizes(Size(12, 5f))
            .setPosition(-50f, konfettiView.width + 50f, -50f, -50f)
            .streamFor(300, 5000L)
    }

    /**
     * Reproduce un sonido.
     *
     * @param resourceId ID del recurso de sonido.
     */
    private fun playSound(resourceId: Int) {
        mediaPlayer = MediaPlayer.create(this, resourceId)
        mediaPlayer.setOnCompletionListener { mp ->
            mp.release()
        }
        mediaPlayer.start()
    }
}

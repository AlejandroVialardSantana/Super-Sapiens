package com.avs.supersapiens.ui.activities

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.avs.supersapiens.R
import com.avs.supersapiens.databinding.ActivityMathGamePlayBinding
import com.avs.supersapiens.enums.QuestionType
import com.avs.supersapiens.models.Question
import com.avs.supersapiens.utils.MathData
import com.avs.supersapiens.utils.NumberConverter
import com.avs.supersapiens.utils.ProgressManager
import com.avs.supersapiens.utils.QuestionGenerator
import java.util.*

/**
 * Actividad que muestra las preguntas y respuestas del juego de matemáticas.

 */
class MathGamePlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMathGamePlayBinding
    private lateinit var progressManager: ProgressManager
    private lateinit var mediaPlayer: MediaPlayer

    private var currentQuestionIndex = 0
    private var correctAnswers = 0
    private var questions: List<Question> = emptyList()
    private val REQUEST_CODE_SPEECH_INPUT = 100
    private lateinit var gameId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMathGamePlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressManager = ProgressManager(this)

        gameId = intent.getStringExtra("gameId") ?: return
        val gameType = intent.getStringExtra("gameType") ?: return
        correctAnswers = 0

        questions = QuestionGenerator.generateMathQuestions(gameType)

        binding.submitButton.setOnClickListener { checkAnswer() }
        binding.option1.setOnClickListener { checkMultipleChoiceAnswer(0) }
        binding.option2.setOnClickListener { checkMultipleChoiceAnswer(1) }
        binding.option3.setOnClickListener { checkMultipleChoiceAnswer(2) }
        binding.option4.setOnClickListener { checkMultipleChoiceAnswer(3) }
        binding.voiceButton.setOnClickListener { promptSpeechInput() }

        showQuestion()
    }

    /**
     * Muestra la pregunta actual en la interfaz de usuario.
     */
    private fun showQuestion() {
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]
            binding.questionNumberText.text = "Pregunta ${currentQuestionIndex + 1} de ${questions.size}"
            binding.questionText.text = question.text
            binding.voiceButton.visibility = View.VISIBLE

            when (question.type) {
                QuestionType.TEXT -> {
                    binding.answerInputLayout.visibility = View.VISIBLE
                    binding.multipleChoiceLayout.visibility = View.GONE
                    binding.answerInput.text.clear()
                }
                QuestionType.MULTIPLE_CHOICE -> {
                    binding.answerInputLayout.visibility = View.GONE
                    binding.multipleChoiceLayout.visibility = View.VISIBLE
                    binding.voiceButton.visibility = View.GONE
                    val options = MathData.generateMultipleChoiceOptions(question.correctAnswer)
                    binding.option1.text = options[0].toString()
                    binding.option2.text = options[1].toString()
                    binding.option3.text = options[2].toString()
                    binding.option4.text = options[3].toString()
                }
                else -> return
            }
        } else {
            showResults()
        }
    }

    private fun checkAnswer() {
        val question = questions[currentQuestionIndex]
        val userAnswer = binding.answerInput.text.toString().toIntOrNull()
        val isCorrect = userAnswer == question.correctAnswer

        if (isCorrect) {
            correctAnswers++
            playSound(R.raw.correct_answer)
        }
        else {
            playSound(R.raw.incorrect_answer)
        }

        showFeedbackDialog(isCorrect, question.correctAnswer.toString())
    }

    private fun checkMultipleChoiceAnswer(selectedOptionIndex: Int) {
        val question = questions[currentQuestionIndex]
        val selectedOption = when (selectedOptionIndex) {
            0 -> binding.option1.text.toString().toInt()
            1 -> binding.option2.text.toString().toInt()
            2 -> binding.option3.text.toString().toInt()
            3 -> binding.option4.text.toString().toInt()
            else -> 0
        }

        val isCorrect = selectedOption == question.correctAnswer
        if (isCorrect) {
            correctAnswers++
            playSound(R.raw.correct_answer)
        }
        else {
            playSound(R.raw.incorrect_answer)
        }

        showFeedbackDialog(isCorrect, question.correctAnswer.toString())
    }

    private fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Diga la respuesta")
        }

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(this, "No se pudo acceder a la entrada de voz", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenAnswerText = result?.get(0)?.let { NumberConverter.convertWordToNumber(it) }
            val spokenAnswerNumber = spokenAnswerText?.toIntOrNull()
            if (spokenAnswerNumber != null) {
                val question = questions[currentQuestionIndex]
                val isCorrect = spokenAnswerNumber == question.correctAnswer
                if (isCorrect) {
                    correctAnswers++
                    playSound(R.raw.correct_answer)
                }
                else {
                    playSound(R.raw.incorrect_answer)
                }

                showFeedbackDialog(isCorrect, question.correctAnswer.toString())
            } else {
                Toast.makeText(this, "No se entendió la respuesta. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showResults() {
        progressManager.saveScore(gameId, correctAnswers)
        val intent = Intent(this, GameResultActivity::class.java).apply {
            putExtra("correctAnswers", correctAnswers)
            putExtra("totalQuestions", questions.size)
        }
        startActivity(intent)
        finish()
    }

    private fun showFeedbackDialog(isCorrect: Boolean, correctAnswer: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_feedback, null)
        dialogBuilder.setView(dialogView)

        val feedbackIcon: ImageView = dialogView.findViewById(R.id.feedbackIcon)
        val feedbackText: TextView = dialogView.findViewById(R.id.feedbackText)
        val correctAnswerText: TextView = dialogView.findViewById(R.id.correctAnswerText)

        if (isCorrect) {
            feedbackIcon.setImageResource(R.drawable.ic_correct)
            feedbackText.text = "¡Muy bien!"
            correctAnswerText.visibility = View.GONE
        } else {
            feedbackIcon.setImageResource(R.drawable.ic_incorrect)
            feedbackText.text = "¡Casi! La respuesta correcta es:"
            correctAnswerText.text = correctAnswer
            correctAnswerText.visibility = View.VISIBLE
        }

        dialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            currentQuestionIndex++
            showQuestion()
        }

        val feedbackDialog = dialogBuilder.create()
        feedbackDialog.show()
    }

    private fun playSound(resourceId: Int) {
        mediaPlayer = MediaPlayer.create(this, resourceId)
        mediaPlayer.setOnCompletionListener { mp ->
            mp.release()
        }
        mediaPlayer.start()
    }
}

package com.avs.supersapiens.ui.activities

import android.app.Activity
import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.DragEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.avs.supersapiens.databinding.ActivityMathGamePlayBinding
import kotlin.random.Random

class MathGamePlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMathGamePlayBinding
    private var correctAnswers = 0
    private var currentQuestion = 1
    private var correctAnswer: Int = 0
    private val REQUEST_CODE_SPEECH_INPUT = 100
    private val totalQuestions = 10
    private var currentMode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMathGamePlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDragDrop()
        generateNewQuestion()

        binding.submitButton.setOnClickListener {
            checkAnswer()
        }

        binding.voiceButton.setOnClickListener {
            promptSpeechInput()
        }

        binding.answerInputLayout.visibility = View.VISIBLE
        binding.voiceButton.visibility = View.VISIBLE
    }

    private fun setupDragDrop() {
        binding.answerDropTarget.setOnDragListener { v, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    v.setBackgroundColor(Color.LTGRAY)
                    v.invalidate()
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    v.setBackgroundColor(Color.TRANSPARENT)
                    v.invalidate()
                    true
                }
                DragEvent.ACTION_DROP -> {
                    v.setBackgroundColor(Color.TRANSPARENT)
                    v.invalidate()
                    val item = event.clipData.getItemAt(0)
                    val dragData = item.text.toString()
                    val answer = dragData.toIntOrNull()
                    if (answer != null) {
                        checkAnswer(answer)
                    }
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    v.setBackgroundColor(Color.TRANSPARENT)
                    v.invalidate()
                    true
                }
                else -> false
            }
        }

        val dragListener = View.OnLongClickListener { v ->
            val item = ClipData.Item((v as TextView).text)
            val dragData = ClipData(v.text, arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item)
            val dragShadow = View.DragShadowBuilder(v)
            ViewCompat.startDragAndDrop(v, dragData, dragShadow, v, 0)
            true
        }

        binding.option1.setOnLongClickListener(dragListener)
        binding.option2.setOnLongClickListener(dragListener)
    }

    private fun generateNewQuestion() {
        if (currentQuestion > totalQuestions) {
            finishGame()
            return
        }

        val num1 = Random.nextInt(1, 20)
        val num2 = Random.nextInt(1, 20)
        val isAddition = Random.nextBoolean()

        correctAnswer = if (isAddition || num2 <= num1) {
            binding.questionText.text = "$num1 + $num2 = ?"
            num1 + num2
        } else {
            binding.questionText.text = "$num1 - $num2 = ?"
            num1 - num2
        }

        val incorrectAnswer = correctAnswer + Random.nextInt(1, 10)
        if (Random.nextBoolean()) {
            binding.option1.text = correctAnswer.toString()
            binding.option2.text = incorrectAnswer.toString()
        } else {
            binding.option1.text = incorrectAnswer.toString()
            binding.option2.text = correctAnswer.toString()
        }

        binding.questionNumberText.text = "Pregunta $currentQuestion de $totalQuestions"

        currentMode = (currentMode + 1) % 3
        updateUIForCurrentMode()
    }

    private fun updateUIForCurrentMode() {
        when (currentMode) {
            0 -> {
                binding.answerInputLayout.visibility = View.GONE
                binding.dragDropLayout.visibility = View.VISIBLE
                binding.voiceButton.visibility = View.GONE
            }
            1 -> {
                binding.answerInputLayout.visibility = View.VISIBLE
                binding.dragDropLayout.visibility = View.GONE
                binding.voiceButton.visibility = View.VISIBLE
            }
            2 -> {
                binding.answerInputLayout.visibility = View.GONE
                binding.dragDropLayout.visibility = View.GONE
                binding.voiceButton.visibility = View.VISIBLE
            }
        }
    }

    private fun checkAnswer(userAnswer: Int? = null) {
        val answer = userAnswer ?: binding.answerInput.text.toString().toIntOrNull()

        if (answer == null) {
            Toast.makeText(this, "Por favor, introduce un número válido", Toast.LENGTH_SHORT).show()
            return
        }

        if (answer == correctAnswer) {
            correctAnswers++
        }

        currentQuestion++

        if (currentQuestion <= totalQuestions) {
            generateNewQuestion()
            binding.answerInput.text.clear()
        } else {
            finishGame()
        }
    }

    private fun finishGame() {
        saveGameProgress()
        val intent = Intent(this, GameResultActivity::class.java)
        intent.putExtra("correctAnswers", correctAnswers)
        intent.putExtra("totalQuestions", totalQuestions)
        startActivity(intent)
        finish()
    }

    private fun saveGameProgress() {
        val sharedPreferences = getSharedPreferences("game_progress", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("math_Sumas y Restas_progress", correctAnswers)
        if (correctAnswers == totalQuestions) {
            editor.putBoolean("math_Sumas y Restas_unlocked", true)
        }
        editor.apply()
    }


    private fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Di tu respuesta")
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(this, "Tu dispositivo no soporta entrada de voz", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val userAnswerString = result?.get(0)?.let { convertWordToNumber(it) }
            val userAnswer = userAnswerString?.toIntOrNull()
            if (userAnswer != null) {
                checkAnswer(userAnswer)
            } else {
                Toast.makeText(this, "No se entendió la respuesta. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun convertWordToNumber(word: String): String {
        return when (word.lowercase()) {
            "uno" -> "1"
            "dos" -> "2"
            "tres" -> "3"
            "cuatro" -> "4"
            "cinco" -> "5"
            "seis" -> "6"
            "siete" -> "7"
            "ocho" -> "8"
            "nueve" -> "9"
            "diez" -> "10"
            "once" -> "11"
            "doce" -> "12"
            "trece" -> "13"
            "catorce" -> "14"
            "quince" -> "15"
            "dieciséis" -> "16"
            "diecisiete" -> "17"
            "dieciocho" -> "18"
            "diecinueve" -> "19"
            "veinte" -> "20"
            "veintiuno" -> "21"
            "veintidós" -> "22"
            "veintitrés" -> "23"
            "veinticuatro" -> "24"
            "veinticinco" -> "25"
            "veintiséis" -> "26"
            "veintisiete" -> "27"
            "veintiocho" -> "28"
            "veintinueve" -> "29"
            "treinta" -> "30"
            "treinta y uno" -> "31"
            "treinta y dos" -> "32"
            "treinta y tres" -> "33"
            "treinta y cuatro" -> "34"
            "treinta y cinco" -> "35"
            "treinta y seis" -> "36"
            "treinta y siete" -> "37"
            "cuarenta" -> "40"
            else -> word
        }
    }
}

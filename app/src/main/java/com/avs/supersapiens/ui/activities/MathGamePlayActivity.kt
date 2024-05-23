package com.avs.supersapiens.ui.activities

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.avs.supersapiens.databinding.ActivityMathGamePlayBinding
import com.avs.supersapiens.enums.QuestionType
import com.avs.supersapiens.models.Question
import com.avs.supersapiens.utils.ProgressManager
import java.util.*

class MathGamePlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMathGamePlayBinding
    private lateinit var progressManager: ProgressManager

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

        questions = generateQuestions(gameType)

        binding.submitButton.setOnClickListener { checkAnswer() }
        binding.option1.setOnClickListener { checkMultipleChoiceAnswer(0) }
        binding.option2.setOnClickListener { checkMultipleChoiceAnswer(1) }
        binding.option3.setOnClickListener { checkMultipleChoiceAnswer(2) }
        binding.option4.setOnClickListener { checkMultipleChoiceAnswer(3) }
        binding.voiceButton.setOnClickListener { promptSpeechInput() }

        showQuestion()
    }

    private fun generateQuestions(gameType: String): List<Question> {
        return when (gameType) {
            "sum" -> generateSumAndSubtractQuestions()
            "multiply" -> generateMultiplicationQuestions()
            else -> emptyList()
        }
    }

    private fun generateSumAndSubtractQuestions(): List<Question> {
        val questions = mutableListOf<Question>()
        while (questions.size < 10) {
            val num1 = Random().nextInt(20) + 1
            val num2 = Random().nextInt(20) + 1
            val isAddition = Random().nextBoolean()
            if (isAddition) {
                val correctAnswer = num1 + num2
                questions.add(Question("$num1 + $num2 = ?", correctAnswer, QuestionType.TEXT))
            } else {
                val correctAnswer = num1 - num2
                if (correctAnswer >= 0) {
                    questions.add(Question("$num1 - $num2 = ?", correctAnswer, QuestionType.TEXT))
                }
            }
        }
        return questions
    }

    private fun generateMultiplicationQuestions(): List<Question> {
        val questions = mutableListOf<Question>()
        while (questions.size < 10) {
            val num1 = Random().nextInt(12) + 1
            val num2 = Random().nextInt(12) + 1
            val correctAnswer = num1 * num2
            questions.add(Question("$num1 * $num2 = ?", correctAnswer, QuestionType.MULTIPLE_CHOICE))
        }
        return questions
    }

    private fun showQuestion() {
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]
            binding.questionNumberText.text = "Pregunta ${currentQuestionIndex + 1} de ${questions.size}"
            binding.questionText.text = question.text

            when (question.type) {
                QuestionType.TEXT -> {
                    binding.answerInputLayout.visibility = View.VISIBLE
                    binding.multipleChoiceLayout.visibility = View.GONE
                }
                QuestionType.MULTIPLE_CHOICE -> {
                    binding.answerInputLayout.visibility = View.GONE
                    binding.multipleChoiceLayout.visibility = View.VISIBLE
                    val options = generateMultipleChoiceOptions(question.correctAnswer)
                    binding.option1.text = options[0].toString()
                    binding.option2.text = options[1].toString()
                    binding.option3.text = options[2].toString()
                    binding.option4.text = options[3].toString()
                }
            }
        } else {
            showResults()
        }
    }

    private fun generateMultipleChoiceOptions(correctAnswer: Int): List<Int> {
        val options = mutableSetOf(correctAnswer)
        while (options.size < 4) {
            options.add((1..144).random())
        }
        return options.shuffled()
    }

    private fun checkAnswer() {
        val question = questions[currentQuestionIndex]
        val userAnswer = binding.answerInput.text.toString().toIntOrNull()

        if (userAnswer == question.correctAnswer) {
            correctAnswers++
        }

        currentQuestionIndex++
        binding.answerInput.text.clear()
        showQuestion()
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

        if (selectedOption == question.correctAnswer) {
            correctAnswers++
        }

        currentQuestionIndex++
        showQuestion()
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
            val spokenAnswer = result?.get(0)?.toIntOrNull()
            if (spokenAnswer != null) {
                val question = questions[currentQuestionIndex]
                if (spokenAnswer == question.correctAnswer) {
                    correctAnswers++
                }

                currentQuestionIndex++
                showQuestion()
            } else {
                Toast.makeText(this, "No se entendió la respuesta. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showResults() {
        progressManager.clearAllScores()
        progressManager.saveScore(gameId, correctAnswers)
        val intent = Intent(this, GameResultActivity::class.java).apply {
            putExtra("correctAnswers", correctAnswers)
            putExtra("totalQuestions", questions.size)
        }
        startActivity(intent)
        finish()
    }
}

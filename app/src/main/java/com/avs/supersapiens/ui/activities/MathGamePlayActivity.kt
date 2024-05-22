package com.avs.supersapiens.ui.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.avs.supersapiens.R
import java.util.*

class MathGamePlayActivity : AppCompatActivity() {

    private lateinit var questionNumberText: TextView
    private lateinit var questionText: TextView
    private lateinit var answerInputLayout: View
    private lateinit var answerInput: EditText
    private lateinit var submitButton: Button
    private lateinit var multipleChoiceLayout: View
    private lateinit var option1: Button
    private lateinit var option2: Button
    private lateinit var option3: Button
    private lateinit var option4: Button
    private lateinit var voiceButton: Button

    private var currentQuestionIndex = 0
    private var correctAnswers = 0
    private var questions: List<Question> = emptyList()
    private val REQUEST_CODE_SPEECH_INPUT = 100
    private lateinit var gameId: String
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_math_game_play)

        questionNumberText = findViewById(R.id.questionNumberText)
        questionText = findViewById(R.id.questionText)
        answerInputLayout = findViewById(R.id.answerInputLayout)
        answerInput = findViewById(R.id.answerInput)
        submitButton = findViewById(R.id.submitButton)
        multipleChoiceLayout = findViewById(R.id.multipleChoiceLayout)
        option1 = findViewById(R.id.option1)
        option2 = findViewById(R.id.option2)
        option3 = findViewById(R.id.option3)
        option4 = findViewById(R.id.option4)
        voiceButton = findViewById(R.id.voiceButton)

        gameId = intent.getStringExtra("gameId") ?: return
        val gameTitle = intent.getStringExtra("gameTitle") ?: return
        val gameType = intent.getStringExtra("gameType") ?: return
        correctAnswers = intent.getIntExtra("gameScore", 0)

        sharedPreferences = getSharedPreferences("game_scores", Context.MODE_PRIVATE)

        questions = generateQuestions(gameType)

        submitButton.setOnClickListener { checkAnswer() }
        option1.setOnClickListener { checkMultipleChoiceAnswer(0) }
        option2.setOnClickListener { checkMultipleChoiceAnswer(1) }
        option3.setOnClickListener { checkMultipleChoiceAnswer(2) }
        option4.setOnClickListener { checkMultipleChoiceAnswer(3) }
        voiceButton.setOnClickListener { promptSpeechInput() }

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
        var count = 0
        while (count < 10){
            val num1 = Random().nextInt(20) + 1
            val num2 = Random().nextInt(20) + 1
            if (Random().nextBoolean()) {
                val correctAnswer = num1 + num2
                questions.add(Question("$num1 + $num2 = ?", correctAnswer, QuestionType.TEXT))
            } else {
                val correctAnswer = num1 - num2
                if (correctAnswer >= 0) {
                    questions.add(Question("$num1 - $num2 = ?", correctAnswer, QuestionType.TEXT))
                } else {
                    count++
                }
            }
        }
        return questions
    }

    private fun generateMultiplicationQuestions(): List<Question> {
        val questions = mutableListOf<Question>()
        for (i in 1..10) {
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
            questionNumberText.text = "Pregunta ${currentQuestionIndex + 1} de ${questions.size}"
            questionText.text = question.text

            when (question.type) {
                QuestionType.TEXT -> {
                    answerInputLayout.visibility = View.VISIBLE
                    multipleChoiceLayout.visibility = View.GONE
                }
                QuestionType.MULTIPLE_CHOICE -> {
                    answerInputLayout.visibility = View.GONE
                    multipleChoiceLayout.visibility = View.VISIBLE
                    val options = generateMultipleChoiceOptions(question.correctAnswer)
                    option1.text = options[0].toString()
                    option2.text = options[1].toString()
                    option3.text = options[2].toString()
                    option4.text = options[3].toString()
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
        val userAnswer = answerInput.text.toString().toIntOrNull()

        if (userAnswer == question.correctAnswer) {
            correctAnswers++
        }

        currentQuestionIndex++
        showQuestion()
        answerInput.text.clear()
    }

    private fun checkMultipleChoiceAnswer(selectedOptionIndex: Int) {
        val question = questions[currentQuestionIndex]
        val selectedOption = when (selectedOptionIndex) {
            0 -> option1.text.toString().toInt()
            1 -> option2.text.toString().toInt()
            2 -> option3.text.toString().toInt()
            3 -> option4.text.toString().toInt()
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
        saveProgress(gameId, correctAnswers)
        val intent = Intent(this, GameResultActivity::class.java).apply {
            putExtra("correctAnswers", correctAnswers)
            putExtra("totalQuestions", questions.size)
        }
        startActivity(intent)
        finish()
    }

    private fun saveProgress(gameId: String, score: Int) {
        val editor = sharedPreferences.edit()
        val currentScore = sharedPreferences.getInt(gameId, 0)
        if (score > currentScore) {
            editor.putInt(gameId, score)
        }
        editor.apply()
    }

}

data class Question(
    val text: String,
    val correctAnswer: Int,
    val type: QuestionType
)

enum class QuestionType {
    TEXT,
    MULTIPLE_CHOICE
}

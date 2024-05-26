package com.avs.supersapiens.ui.activities

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.avs.supersapiens.databinding.ActivityAnimalGamePlayBinding
import com.avs.supersapiens.enums.QuestionType
import com.avs.supersapiens.models.Question
import com.avs.supersapiens.utils.ProgressManager
import com.avs.supersapiens.utils.QuestionGenerator
import java.util.Locale

class AnimalGamePlayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnimalGamePlayBinding
    private lateinit var progressManager: ProgressManager

    private var currentQuestionIndex = 0
    private var correctAnswers = 0
    private var questions: List<Question> = emptyList()
    private lateinit var gameId: String
    private val REQUEST_CODE_SPEECH_INPUT = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimalGamePlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressManager = ProgressManager(this)

        gameId = intent.getStringExtra("gameId") ?: return
        correctAnswers = 0

        questions = QuestionGenerator.generateAnimalQuestions()

        binding.submitButton.setOnClickListener { checkAnswer() }
        binding.option1.setOnClickListener { checkMultipleChoiceAnswer(0) }
        binding.option2.setOnClickListener { checkMultipleChoiceAnswer(1) }
        binding.option3.setOnClickListener { checkMultipleChoiceAnswer(2) }
        binding.option4.setOnClickListener { checkMultipleChoiceAnswer(3) }
        binding.voiceButton.setOnClickListener { promptSpeechInput() }

        showQuestion()
    }

    private fun showQuestion() {
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]
            binding.questionNumberText.text = "Pregunta ${currentQuestionIndex + 1} de ${questions.size}"
            binding.questionText.text = question.text

            when (question.type) {
                QuestionType.IMAGE_IDENTIFICATION -> {
                    binding.answerInputLayout.visibility = View.VISIBLE
                    binding.multipleChoiceLayout.visibility = View.GONE
                    binding.voiceButton.visibility = View.VISIBLE
                    binding.answerInput.visibility = View.VISIBLE
                    binding.submitButton.visibility = View.VISIBLE
                    val animal = QuestionGenerator.getAnimalByIndex(question.correctAnswer)
                    binding.questionImage.setImageResource(animal.imageResId)
                }
                QuestionType.IMAGE_MULTIPLE_CHOICE -> {
                    binding.answerInputLayout.visibility = View.GONE
                    binding.multipleChoiceLayout.visibility = View.VISIBLE
                    binding.voiceButton.visibility = View.GONE
                    binding.answerInput.visibility = View.GONE
                    binding.submitButton.visibility = View.GONE

                    val options = QuestionGenerator.questionOptions[currentQuestionIndex]?.map { QuestionGenerator.getAnimalByIndex(it) } ?: emptyList()

                    binding.option1.setImageResource(options[0].imageResId)
                    binding.option2.setImageResource(options[1].imageResId)
                    binding.option3.setImageResource(options[2].imageResId)
                    binding.option4.setImageResource(options[3].imageResId)

                    binding.option1.tag = options[0].imageResId
                    binding.option2.tag = options[1].imageResId
                    binding.option3.tag = options[2].imageResId
                    binding.option4.tag = options[3].imageResId
                }
                else -> {
                    // Handle other types if any
                }
            }
        } else {
            showResults()
        }
    }

    private fun checkAnswer() {
        val question = questions[currentQuestionIndex]
        val userAnswer = binding.answerInput.text.toString()

        if (userAnswer.equals(QuestionGenerator.getAnimalByIndex(question.correctAnswer).name, ignoreCase = true)) {
            correctAnswers++
        }

        currentQuestionIndex++
        binding.answerInput.text.clear()
        showQuestion()
    }

    private fun checkMultipleChoiceAnswer(selectedOptionIndex: Int) {
        val question = questions[currentQuestionIndex]
        val selectedOption = when (selectedOptionIndex) {
            0 -> binding.option1.tag as Int
            1 -> binding.option2.tag as Int
            2 -> binding.option3.tag as Int
            3 -> binding.option4.tag as Int
            else -> -1
        }

        if (selectedOption == QuestionGenerator.getAnimalByIndex(question.correctAnswer).imageResId) {
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
            val spokenAnswerText = result?.get(0)
            if (spokenAnswerText != null) {
                val question = questions[currentQuestionIndex]
                if (spokenAnswerText.equals(QuestionGenerator.getAnimalByIndex(question.correctAnswer).name, ignoreCase = true)) {
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
        progressManager.saveScore(gameId, correctAnswers)
        val intent = Intent(this, GameResultActivity::class.java).apply {
            putExtra("correctAnswers", correctAnswers)
            putExtra("totalQuestions", questions.size)
        }
        startActivity(intent)
        finish()
    }
}

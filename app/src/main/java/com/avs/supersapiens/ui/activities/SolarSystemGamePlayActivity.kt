package com.avs.supersapiens.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.avs.supersapiens.databinding.ActivitySolarSystemGamePlayBinding
import com.avs.supersapiens.enums.QuestionType
import com.avs.supersapiens.models.Question
import com.avs.supersapiens.utils.ProgressManager
import com.avs.supersapiens.utils.QuestionGenerator

class SolarSystemGamePlayActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySolarSystemGamePlayBinding
    private lateinit var progressManager: ProgressManager

    private var currentQuestionIndex = 0
    private var correctAnswers = 0
    private var questions: List<Question> = emptyList()
    private lateinit var gameId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySolarSystemGamePlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressManager = ProgressManager(this)

        gameId = intent.getStringExtra("gameId") ?: return
        correctAnswers = 0

        questions = QuestionGenerator.generateSolarSystemQuestions()

        binding.submitButton.setOnClickListener { checkAnswer() }
        binding.option1.setOnClickListener { checkMultipleChoiceAnswer(0) }
        binding.option2.setOnClickListener { checkMultipleChoiceAnswer(1) }
        binding.option3.setOnClickListener { checkMultipleChoiceAnswer(2) }
        binding.option4.setOnClickListener { checkMultipleChoiceAnswer(3) }

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
                    binding.answerInput.visibility = View.VISIBLE
                    binding.submitButton.visibility = View.VISIBLE
                    binding.option1.visibility = View.GONE
                    binding.option2.visibility = View.GONE
                    binding.option3.visibility = View.GONE
                    binding.option4.visibility = View.GONE
                    val planet = QuestionGenerator.getPlanetByIndex(question.correctAnswer)
                    binding.questionImage.setImageResource(planet.imageResId)
                }
                QuestionType.IMAGE_MULTIPLE_CHOICE -> {
                    binding.answerInputLayout.visibility = View.GONE
                    binding.multipleChoiceLayout.visibility = View.VISIBLE
                    binding.answerInput.visibility = View.GONE
                    binding.submitButton.visibility = View.GONE
                    binding.option1.visibility = View.VISIBLE
                    binding.option2.visibility = View.VISIBLE
                    binding.option3.visibility = View.VISIBLE
                    binding.option4.visibility = View.VISIBLE

                    val options = QuestionGenerator.questionOptions[currentQuestionIndex]?.map { QuestionGenerator.getPlanetByIndex(it) } ?: emptyList()

                    if (options.isNotEmpty()) {
                        binding.option1.setImageResource(options[0].imageResId)
                        binding.option2.setImageResource(options[1].imageResId)
                        binding.option3.setImageResource(options[2].imageResId)
                        binding.option4.setImageResource(options[3].imageResId)

                        binding.option1.tag = options[0].imageResId
                        binding.option2.tag = options[1].imageResId
                        binding.option3.tag = options[2].imageResId
                        binding.option4.tag = options[3].imageResId
                    } else {
                        // Handle the case where options list is empty
                        showResults()
                    }
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
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]
            val userAnswer = binding.answerInput.text.toString()

            if (userAnswer.equals(QuestionGenerator.getPlanetByIndex(question.correctAnswer).name, ignoreCase = true)) {
                correctAnswers++
            }

            currentQuestionIndex++
            binding.answerInput.text.clear()
            showQuestion()
        }
    }

    private fun checkMultipleChoiceAnswer(selectedOptionIndex: Int) {
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]
            val selectedOption = when (selectedOptionIndex) {
                0 -> binding.option1.tag as Int
                1 -> binding.option2.tag as Int
                2 -> binding.option3.tag as Int
                3 -> binding.option4.tag as Int
                else -> -1
            }

            if (selectedOption == QuestionGenerator.getPlanetByIndex(question.correctAnswer).imageResId) {
                correctAnswers++
            }

            currentQuestionIndex++
            showQuestion()
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

package com.avs.supersapiens.ui.activities

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.avs.supersapiens.databinding.ActivityEnglishGamePlayBinding
import com.avs.supersapiens.enums.QuestionType
import com.avs.supersapiens.models.Question
import com.avs.supersapiens.utils.ProgressManager
import com.avs.supersapiens.utils.QuestionGenerator

class EnglishGamePlayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEnglishGamePlayBinding
    private lateinit var progressManager: ProgressManager

    private var currentQuestionIndex = 0
    private var correctAnswers = 0
    private var questions: List<Question> = emptyList()
    private lateinit var gameId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnglishGamePlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressManager = ProgressManager(this)

        gameId = intent.getStringExtra("gameId") ?: return
        correctAnswers = 0

        questions = QuestionGenerator.generateEnglishQuestions()

        binding.confirmButton.setOnClickListener { checkAnswer() }

        showQuestion()
    }

    private fun showQuestion() {
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]
            binding.questionNumberText.text = "Pregunta ${currentQuestionIndex + 1} de ${questions.size}"
            binding.questionText.text = "Ordena las letras para formar la palabra"
            binding.questionImage.setImageResource(QuestionGenerator.getWordByIndex(question.correctAnswer).imageResId)

            when (question.type) {
                QuestionType.DRAG_AND_DROP -> {
                    binding.answerInputLayout.visibility = View.GONE
                    binding.imageMultipleChoiceLayout.visibility = View.GONE
                    binding.dragAndDropLayout.visibility = View.VISIBLE

                    setupDragAndDrop(QuestionGenerator.getShuffledWord(question.correctAnswer))
                }
                QuestionType.IMAGE_MULTIPLE_CHOICE -> {
                    binding.answerInputLayout.visibility = View.GONE
                    binding.imageMultipleChoiceLayout.visibility = View.VISIBLE
                    binding.dragAndDropLayout.visibility = View.GONE

                    val options = QuestionGenerator.questionOptions[currentQuestionIndex]?.map { QuestionGenerator.getWordByIndex(it) } ?: emptyList()

                    if (options.isNotEmpty()) {
                        binding.option1.setImageResource(options[0].imageResId)
                        binding.option2.setImageResource(options[1].imageResId)
                        binding.option3.setImageResource(options[2].imageResId)
                        binding.option4.setImageResource(options[3].imageResId)

                        binding.option1.tag = options[0].name
                        binding.option2.tag = options[1].name
                        binding.option3.tag = options[2].name
                        binding.option4.tag = options[3].name

                        binding.option1.setOnClickListener { checkMultipleChoiceAnswer(0) }
                        binding.option2.setOnClickListener { checkMultipleChoiceAnswer(1) }
                        binding.option3.setOnClickListener { checkMultipleChoiceAnswer(2) }
                        binding.option4.setOnClickListener { checkMultipleChoiceAnswer(3) }
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

    private fun setupDragAndDrop(shuffledWord: String) {
        binding.letterContainer.removeAllViews()
        binding.dropZoneContainer.removeAllViews()

        for (char in shuffledWord) {
            val textView = TextView(this).apply {
                text = char.toString()
                textSize = 24f
                setPadding(16, 16, 16, 16)
                tag = char.toString()
                setBackgroundColor(Color.GREEN)
                setOnLongClickListener { view ->
                    val item = ClipData.Item(view.tag as? CharSequence)
                    val dragData = ClipData(
                        view.tag as? CharSequence,
                        arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                        item
                    )
                    val myShadow = View.DragShadowBuilder(view)
                    view.startDragAndDrop(dragData, myShadow, null, 0)
                    true
                }
            }
            binding.letterContainer.addView(textView)
        }

        for (i in shuffledWord.indices) {
            val textView = TextView(this).apply {
                textSize = 24f
                setPadding(16, 16, 16, 16)
                tag = ""
                setBackgroundColor(Color.BLUE)
                setOnDragListener { view, dragEvent ->
                    when (dragEvent.action) {
                        DragEvent.ACTION_DRAG_STARTED -> true
                        DragEvent.ACTION_DRAG_ENTERED -> {
                            view.invalidate()
                            true
                        }
                        DragEvent.ACTION_DRAG_LOCATION -> true
                        DragEvent.ACTION_DRAG_EXITED -> {
                            view.invalidate()
                            true
                        }
                        DragEvent.ACTION_DROP -> {
                            val item = dragEvent.clipData.getItemAt(0)
                            val dragData = item.text.toString()
                            (view as TextView).text = dragData
                            view.tag = dragData
                            true
                        }
                        DragEvent.ACTION_DRAG_ENDED -> {
                            view.invalidate()
                            true
                        }
                        else -> false
                    }
                }
            }
            binding.dropZoneContainer.addView(textView)
        }
    }

    private fun checkAnswer() {
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]

            if (question.type == QuestionType.DRAG_AND_DROP) {
                val userAnswer = (0 until binding.dropZoneContainer.childCount).joinToString("") { index ->
                    (binding.dropZoneContainer.getChildAt(index) as TextView).text.toString()
                }

                if (userAnswer.equals(QuestionGenerator.getWordByIndex(question.correctAnswer).name, ignoreCase = true)) {
                    correctAnswers++
                }
            }

            currentQuestionIndex++
            showQuestion()
        }
    }

    private fun checkMultipleChoiceAnswer(selectedOptionIndex: Int) {
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]
            val selectedOption = when (selectedOptionIndex) {
                0 -> binding.option1.tag as String
                1 -> binding.option2.tag as String
                2 -> binding.option3.tag as String
                3 -> binding.option4.tag as String
                else -> ""
            }

            if (selectedOption.equals(QuestionGenerator.getWordByIndex(question.correctAnswer).name, ignoreCase = true)) {
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

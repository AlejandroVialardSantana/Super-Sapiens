package com.avs.supersapiens.ui.activities

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.avs.supersapiens.R
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
    private var dropZones: List<TextView> = listOf()

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
            binding.questionText.text = question.text

            when (question.type) {
                QuestionType.DRAG_AND_DROP -> {
                    binding.dragAndDropLayout.visibility = View.VISIBLE
                    setupDragAndDrop(question.correctAnswer)
                }
                else -> {
                    // Handle other types if any
                }
            }
        } else {
            showResults()
        }
    }

    private fun setupDragAndDrop(correctAnswerIndex: Int) {
        val word = QuestionGenerator.getWordByIndex(correctAnswerIndex).name
        val shuffledWord = word.toCharArray().apply { shuffle() }.concatToString()

        binding.letterContainer.removeAllViews()
        binding.dropZoneContainer.removeAllViews()

        for (char in shuffledWord) {
            val letterView = TextView(this).apply {
                text = char.toString()
                textSize = 24f
                setPadding(16, 16, 16, 16)
                setBackgroundColor(resources.getColor(android.R.color.holo_green_dark))
                tag = char.toString()
                setOnClickListener {
                    addLetterToDropZone(this)
                }
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
            binding.letterContainer.addView(letterView)
        }

        dropZones = word.map {
            TextView(this).apply {
                textSize = 24f
                setPadding(16, 16, 16, 16)
                setBackgroundColor(resources.getColor(android.R.color.holo_blue_light))
                tag = ""
                setOnDragListener(dragListener)
            }.also {
                binding.dropZoneContainer.addView(it)
            }
        }
    }

    private fun addLetterToDropZone(letterView: TextView) {
        for (dropZone in dropZones) {
            if (dropZone.tag == "") {
                dropZone.text = letterView.text
                dropZone.tag = letterView.tag
                letterView.visibility = View.INVISIBLE
                break
            }
        }
    }

    private val dragListener = View.OnDragListener { view, dragEvent ->
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
                binding.letterContainer.findViewWithTag<TextView>(dragData).visibility = View.INVISIBLE
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                view.invalidate()
                true
            }
            else -> false
        }
    }

    private fun checkAnswer() {
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]

            if (question.type == QuestionType.DRAG_AND_DROP) {
                val userAnswer = dropZones.joinToString("") { it.text.toString() }

                if (userAnswer.equals(QuestionGenerator.getWordByIndex(question.correctAnswer).name, ignoreCase = true)) {
                    correctAnswers++
                }
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

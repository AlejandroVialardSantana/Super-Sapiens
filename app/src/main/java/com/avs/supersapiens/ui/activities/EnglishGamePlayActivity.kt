package com.avs.supersapiens.ui.activities

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.DragEvent
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.avs.supersapiens.R
import com.avs.supersapiens.databinding.ActivityEnglishGamePlayBinding
import com.avs.supersapiens.enums.QuestionType
import com.avs.supersapiens.models.Question
import com.avs.supersapiens.utils.ProgressManager
import com.avs.supersapiens.utils.QuestionGenerator
import java.util.Locale

/**
 * Actividad para jugar juegos de inglés.
 */
class EnglishGamePlayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEnglishGamePlayBinding
    private lateinit var progressManager: ProgressManager
    private lateinit var mediaPlayer: MediaPlayer

    private var currentQuestionIndex = 0
    private var correctAnswers = 0
    private var questions: List<Question> = emptyList()
    private lateinit var gameId: String
    private var dropZones: List<TextView> = listOf()
    private var letterViews: MutableList<TextView> = mutableListOf()
    private val REQUEST_CODE_SPEECH_INPUT = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnglishGamePlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressManager = ProgressManager(this)

        gameId = intent.getStringExtra("gameId") ?: return
        correctAnswers = 0

        // Obtener preguntas del generador de preguntas según el tipo de juego
        val gameType = intent.getStringExtra("gameType") ?: "word"
        questions = if (gameType == "word") {
            QuestionGenerator.generateEnglishWordFormingQuestions()
        } else {
            QuestionGenerator.generateEnglishVocabularyQuestions()
        }

        binding.confirmButton.setOnClickListener { checkAnswer() }
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

            val imageResId = QuestionGenerator.getWordByIndex(question.correctAnswer).imageResId
            binding.questionImage.setImageResource(imageResId)

            when (question.type) {
                // Mostrar la interfaz de usuario según el tipo de pregunta
                QuestionType.DRAG_AND_DROP -> {
                    binding.dragAndDropLayout.visibility = View.VISIBLE
                    binding.answerInputLayout.visibility = View.GONE
                    binding.imageMultipleChoiceLayout.visibility = View.GONE
                    binding.confirmButton.visibility = View.VISIBLE
                    setupDragAndDrop(question.correctAnswer)
                }
                QuestionType.IMAGE_MULTIPLE_CHOICE -> {
                    binding.dragAndDropLayout.visibility = View.GONE
                    binding.answerInputLayout.visibility = View.GONE
                    binding.imageMultipleChoiceLayout.visibility = View.VISIBLE
                    binding.confirmButton.visibility = View.GONE
                    setupMultipleChoice()
                }
                QuestionType.TEXT -> {
                    binding.dragAndDropLayout.visibility = View.GONE
                    binding.answerInputLayout.visibility = View.VISIBLE
                    binding.imageMultipleChoiceLayout.visibility = View.GONE
                    binding.confirmButton.visibility = View.VISIBLE
                    binding.voiceButton.visibility = View.VISIBLE
                    binding.answerInput.text.clear()
                }
                else -> {
                    // No se implementa
                }
            }
        } else {
            showResults()
        }
    }

    /**
     * Configura la interfaz de usuario para el juego de formación de palabras.
     */
    private fun setupDragAndDrop(correctAnswerIndex: Int) {
        val word = QuestionGenerator.getWordByIndex(correctAnswerIndex).name
        // Mezclar las letras de la palabra
        val shuffledWord = word.toCharArray().apply { shuffle() }.concatToString()

        binding.letterContainer.removeAllViews()
        binding.dropZoneContainer.removeAllViews()
        letterViews.clear()

        val letterParams = LinearLayout.LayoutParams(80, 85).apply {
            setMargins(8, 8, 8, 8)
        }

        // Crear vistas de letras y zonas de caída mediante cuadros
        shuffledWord.forEachIndexed { index, char ->
            val letterView = TextView(this).apply {
                text = char.toString().uppercase(Locale.getDefault())
                textSize = 20f
                gravity = Gravity.CENTER
                setPadding(8, 8, 8, 8)
                setBackgroundResource(R.drawable.letter_background)
                setTextColor(Color.BLACK)
                typeface = Typeface.DEFAULT_BOLD
                tag = "letter_$index"
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
            binding.letterContainer.addView(letterView, letterParams)
            letterViews.add(letterView)
        }

        dropZones = word.mapIndexed { index, _ ->
            TextView(this).apply {
                textSize = 20f
                gravity = Gravity.CENTER
                setPadding(8, 8, 8, 8)
                setBackgroundResource(R.drawable.dropzone_background)
                tag = "drop_$index"
                setOnDragListener(dragListener)
                setOnClickListener {
                    removeLetterFromDropZone(this)
                }
            }.also {
                binding.dropZoneContainer.addView(it, letterParams)
            }
        }
    }

    /**
     * Configura la interfaz de usuario para el juego de opción múltiple.
     */
    private fun setupMultipleChoice() {
        val options = QuestionGenerator.questionOptions[currentQuestionIndex]?.map { QuestionGenerator.getWordByIndex(it) } ?: emptyList()

        if (options.isNotEmpty()) {
            binding.option1.text = options[0].name
            binding.option2.text = options[1].name
            binding.option3.text = options[2].name
            binding.option4.text = options[3].name

            binding.option1.tag = options[0].imageResId
            binding.option2.tag = options[1].imageResId
            binding.option3.tag = options[2].imageResId
            binding.option4.tag = options[3].imageResId
        }
    }

    /**
     * Agrega una letra a la zona de caída.
     */
    private fun addLetterToDropZone(letterView: TextView) {
        for (dropZone in dropZones) {
            if (dropZone.text.isEmpty()) {
                dropZone.text = letterView.text
                dropZone.tag = letterView.tag
                letterView.visibility = View.INVISIBLE
                break
            }
        }
    }

    /**
     * Elimina una letra de la zona de caída.
     */
    private fun removeLetterFromDropZone(dropZone: TextView) {
        if (dropZone.text.isNotEmpty()) {
            val tag = dropZone.tag as String
            dropZone.text = ""
            dropZone.tag = ""
            val letterView = letterViews.firstOrNull { it.tag == tag }
            letterView?.visibility = View.VISIBLE
        }
    }

    /**
     * Listener para el evento de arrastrar y soltar/clickar
     */
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
                val letterView = letterViews.firstOrNull { it.tag == dragData }
                letterView?.visibility = View.INVISIBLE
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                view.invalidate()
                true
            }
            else -> false
        }
    }

    /**
     * Comprueba la respuesta del usuario y muestra el diálogo de retroalimentación.
     */
    private fun checkAnswer() {
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]
            val correctAnswer = QuestionGenerator.getWordByIndex(question.correctAnswer).name
            var isCorrect = false

            if (question.type == QuestionType.DRAG_AND_DROP) {
                val userAnswer = dropZones.joinToString("") { it.text.toString() }
                isCorrect = userAnswer.equals(correctAnswer, ignoreCase = true)
            } else if (question.type == QuestionType.TEXT) {
                val userAnswer = binding.answerInput.text.toString()
                isCorrect = userAnswer.equals(correctAnswer, ignoreCase = true)
                binding.answerInput.text.clear()
            }

            if (isCorrect) {
                correctAnswers++
                playSound(R.raw.correct_answer)
            }
            else {
                playSound(R.raw.incorrect_answer)
            }

            currentQuestionIndex++
            showFeedbackDialog(isCorrect, correctAnswer)
        }
    }

    /**
     * Comprueba la respuesta del usuario en preguntas de opción múltiple y muestra el diálogo de retroalimentación.
     */
    private fun checkMultipleChoiceAnswer(selectedOptionIndex: Int) {
        val question = questions[currentQuestionIndex]
        val selectedOption = when (selectedOptionIndex) {
            0 -> binding.option1.tag as Int
            1 -> binding.option2.tag as Int
            2 -> binding.option3.tag as Int
            3 -> binding.option4.tag as Int
            else -> -1
        }

        val correctAnswer = QuestionGenerator.getWordByIndex(question.correctAnswer).name
        val isCorrect = selectedOption == QuestionGenerator.getWordByIndex(question.correctAnswer).imageResId

        if (isCorrect) {
            correctAnswers++
            playSound(R.raw.correct_answer)
        }
        else {
            playSound(R.raw.incorrect_answer)
        }

        currentQuestionIndex++
        showFeedbackDialog(isCorrect, correctAnswer)
    }

    /**
     * Muestra el diálogo de entrada de voz para responder a la pregunta.
     */
    private fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Diga la respuesta")
        }

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(this, "No se pudo acceder a la entrada de voz", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Procesa la respuesta de voz del usuario, la compara con la respuesta correcta y muestra el diálogo de retroalimentación.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenAnswerText = result?.get(0)
            if (spokenAnswerText != null) {
                val question = questions[currentQuestionIndex]
                val correctAnswer = QuestionGenerator.getWordByIndex(question.correctAnswer).name
                val isCorrect = spokenAnswerText.equals(correctAnswer, ignoreCase = true)
                if (isCorrect) {
                    correctAnswers++
                    playSound(R.raw.correct_answer)
                }
                else {
                    playSound(R.raw.incorrect_answer)
                }

                currentQuestionIndex++
                showFeedbackDialog(isCorrect, correctAnswer)
            } else {
                Toast.makeText(this, "No se entendió la respuesta. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Muestra la pantalla de resultados y guarda la puntuación del usuario.
     */
    private fun showResults() {
        progressManager.saveScore(gameId, correctAnswers)
        val intent = Intent(this, GameResultActivity::class.java).apply {
            putExtra("correctAnswers", correctAnswers)
            putExtra("totalQuestions", questions.size)
        }
        startActivity(intent)
        finish()
    }

    /**
     * Muestra un diálogo de retroalimentación al usuario.
     */
    private fun showFeedbackDialog(isCorrect: Boolean, correctAnswer: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_feedback, null)
        dialogBuilder.setView(dialogView)

        val feedbackIcon: ImageView = dialogView.findViewById(R.id.feedbackIcon)
        val feedbackText: TextView = dialogView.findViewById(R.id.feedbackText)
        val correctAnswerText: TextView = dialogView.findViewById(R.id.correctAnswerText)

        if (isCorrect) {
            feedbackIcon.setImageResource(R.drawable.ic_correct) // Tick icon
            feedbackText.text = "¡Muy bien!"
            correctAnswerText.visibility = View.GONE
        } else {
            feedbackIcon.setImageResource(R.drawable.ic_incorrect) // Cross icon
            feedbackText.text = "¡Casi! La respuesta correcta es:"
            correctAnswerText.text = correctAnswer
            correctAnswerText.visibility = View.VISIBLE
        }

        dialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            showQuestion()
        }

        val feedbackDialog = dialogBuilder.create()
        feedbackDialog.show()
    }

    /**
     * Reproduce un sonido de respuesta correcta o incorrecta.
     */
    private fun playSound(resourceId: Int) {
        mediaPlayer = MediaPlayer.create(this, resourceId)
        mediaPlayer.setOnCompletionListener { mp ->
            mp.release()
        }
        mediaPlayer.start()
    }
}

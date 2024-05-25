package com.avs.supersapiens.utils

import com.avs.supersapiens.enums.QuestionType
import com.avs.supersapiens.models.Question

object QuestionGenerator {
    val questionOptions = mutableMapOf<Int, List<Int>>()

    fun generateAnimalQuestions(): List<Question> {
        val questions = mutableListOf<Question>()
        val animalTypes = AnimalData.animals.map { it.type }.distinct()
        for (type in animalTypes) {
            val correctAnimals = AnimalData.animals.filter { it.type == type }
            val incorrectAnimals = AnimalData.animals.filter { it.type != type }

            if (correctAnimals.isNotEmpty() && incorrectAnimals.size >= 3) {
                val correctAnimal = correctAnimals.random()
                val incorrectOptions = incorrectAnimals.shuffled().take(3)
                val options = (listOf(correctAnimal) + incorrectOptions).shuffled()

                questions.add(
                    Question(
                        "¿Cuál de estos es un $type?",
                        AnimalData.animals.indexOf(correctAnimal),
                        QuestionType.IMAGE_MULTIPLE_CHOICE
                    )
                )
                questionOptions[questions.size - 1] = options.map { AnimalData.animals.indexOf(it) }
            }
        }

        val randomAnimals = AnimalData.getRandomAnimals(10 - questions.size)
        for (animal in randomAnimals) {
            questions.add(
                Question(
                    "¿Qué animal es este?",
                    AnimalData.animals.indexOf(animal),
                    QuestionType.IMAGE_IDENTIFICATION
                )
            )
        }

        return questions.take(10)
    }

    fun generateMathQuestions(gameType: String): List<Question> {
        return when (gameType) {
            "sum" -> MathData.generateSumAndSubtractQuestions()
            "multiply" -> MathData.generateMultiplicationQuestions()
            else -> emptyList()
        }
    }

    fun generateSolarSystemQuestions(): List<Question> {
        val questions = mutableListOf<Question>()
        val planetData = PlanetData.planets

        // Preguntas de identificación de planetas
        val randomPlanets = PlanetData.getRandomPlanets(5)
        for (planet in randomPlanets) {
            questions.add(
                Question(
                    "¿Qué planeta es este?",
                    planetData.indexOf(planet),
                    QuestionType.IMAGE_IDENTIFICATION
                )
            )
        }

        // Preguntas de elección múltiple sobre la posición de los planetas (aleatorias)
        val positions = (1..8).shuffled().take(5)
        for (position in positions) {
            val correctPlanet = planetData.first { it.position == position }
            val incorrectOptions = planetData.filter { it.position != position }.shuffled().take(3)
            val options = (listOf(correctPlanet) + incorrectOptions).shuffled()

            questions.add(
                Question(
                    "¿Cuál de estos planetas ocupa la posición $position en el sistema solar?",
                    planetData.indexOf(correctPlanet),
                    QuestionType.IMAGE_MULTIPLE_CHOICE
                )
            )
            questionOptions[questions.size - 1] = options.map { planetData.indexOf(it) }
        }

        return questions.take(10)
    }

    fun getPlanetByIndex(index: Int) = PlanetData.getPlanetByIndex(index)

    fun getAnimalByIndex(index: Int) = AnimalData.getAnimalByIndex(index)

    fun getWordByIndex(index: Int) = EnglishData.getWordByIndex(index)
    fun generateEnglishWordFormingQuestions(): List<Question> {
        val questions = mutableListOf<Question>()
        val randomWords = EnglishData.getRandomWords(10)
        for (word in randomWords) {
            questions.add(
                Question(
                    "Ordena las letras para formar la palabra",
                    EnglishData.words.indexOf(word),
                    QuestionType.DRAG_AND_DROP
                )
            )
        }
        return questions
    }

    fun generateEnglishVocabularyQuestions(): List<Question> {
        val questions = mutableListOf<Question>()

        // Genera preguntas de elección múltiple
        val wordImages = EnglishData.words.map { it.imageResId }.distinct().shuffled()
        for (image in wordImages.take(5)) {
            val correctWords = EnglishData.words.filter { it.imageResId == image }
            val incorrectWords = EnglishData.words.filter { it.imageResId != image }

            if (correctWords.isNotEmpty() && incorrectWords.size >= 3) {
                val correctWord = correctWords.random()
                val incorrectOptions = incorrectWords.shuffled().take(3)
                val options = (listOf(correctWord) + incorrectOptions).shuffled()

                questions.add(
                    Question(
                        "¿Cuál de estos es la palabra correcta?",
                        EnglishData.words.indexOf(correctWord),
                        QuestionType.IMAGE_MULTIPLE_CHOICE
                    )
                )
                questionOptions[questions.size - 1] = options.map { EnglishData.words.indexOf(it) }
            }
        }

        // Genera preguntas de texto
        val randomWords = EnglishData.getRandomWords(5)
        for (word in randomWords) {
            questions.add(
                Question(
                    "Escribe la palabra para esta imagen",
                    EnglishData.words.indexOf(word),
                    QuestionType.TEXT
                )
            )
        }

        return questions.take(10)
    }
}

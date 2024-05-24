package com.avs.supersapiens.utils

import com.avs.supersapiens.enums.QuestionType
import com.avs.supersapiens.models.Animal
import com.avs.supersapiens.models.Question

object QuestionGenerator {
    val questionOptions = mutableMapOf<Int, List<Int>>()

    fun generateAnimalQuestions(): List<Question> {
        val questions = mutableListOf<Question>()

        // Generar preguntas para tipos de animales
        val animalTypes = AnimalData.animals.map { it.type }.distinct()
        for (type in animalTypes) {
            val correctAnimals = AnimalData.animals.filter { it.type == type }
            val incorrectAnimals = AnimalData.animals.filter { it.type != type }

            if (correctAnimals.isNotEmpty() && incorrectAnimals.size >= 3) {
                val correctAnimal = correctAnimals.random()
                val incorrectOptions = incorrectAnimals.shuffled().take(3)

                // Guardamos las opciones incorrectas en una variable temporal
                val options = (listOf(correctAnimal) + incorrectOptions).shuffled()

                questions.add(
                    Question(
                        "¿Cuál de estos es un $type?",
                        AnimalData.animals.indexOf(correctAnimal),
                        QuestionType.IMAGE_MULTIPLE_CHOICE
                    )
                )

                // Aquí guardamos las opciones incorrectas para usarlas en la actividad
                questionOptions[questions.size - 1] = options.map { AnimalData.animals.indexOf(it) }
            }
        }

        // Generar preguntas de identificación
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

    fun getAnimalByIndex(index: Int): Animal {
        return AnimalData.getAnimalByIndex(index)
    }
}

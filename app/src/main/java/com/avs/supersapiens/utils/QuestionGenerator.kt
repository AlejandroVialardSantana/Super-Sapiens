package com.avs.supersapiens.utils

import com.avs.supersapiens.enums.QuestionType
import com.avs.supersapiens.models.Animal
import com.avs.supersapiens.models.Planet
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

        // Preguntas de elección múltiple sobre la posición de los planetas
        for (planet in planetData) {
            val correctPlanet = planet
            val incorrectOptions = planetData.filter { it != correctPlanet }.shuffled().take(3)
            val options = (listOf(correctPlanet) + incorrectOptions).shuffled()

            questions.add(
                Question(
                    "¿Cuál de estos planetas ocupa la posición ${correctPlanet.position} en el sistema solar?",
                    planetData.indexOf(correctPlanet),
                    QuestionType.IMAGE_MULTIPLE_CHOICE
                )
            )
            questionOptions[questions.size - 1] = options.map { planetData.indexOf(it) }
        }

        return questions.take(10)
    }

    fun getPlanetByIndex(index: Int) : Planet = PlanetData.getPlanetByIndex(index)

    fun getAnimalByIndex(index: Int) : Animal = AnimalData.animals[index]
}

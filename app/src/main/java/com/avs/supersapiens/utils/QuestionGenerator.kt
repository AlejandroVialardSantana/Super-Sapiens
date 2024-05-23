package com.avs.supersapiens.utils

import com.avs.supersapiens.R
import com.avs.supersapiens.enums.QuestionType
import com.avs.supersapiens.models.Animal
import com.avs.supersapiens.models.Question

object QuestionGenerator {

    private val animals = listOf(
        Animal("serpiente", R.drawable.ic_animal_0, "reptil"),
        Animal("águila", R.drawable.ic_animal_1, "ave"),
        Animal("león", R.drawable.ic_animal_2, "mamífero"),
        Animal("tortuga", R.drawable.ic_animal_3, "reptil"),
        Animal("elefante", R.drawable.ic_animal_4, "mamífero"),
        Animal("pingüino", R.drawable.ic_animal_5, "ave"),
        Animal("iguana", R.drawable.ic_animal_6, "reptil"),
        Animal("cocodrilo", R.drawable.ic_animal_7, "reptil"),
        Animal("caballo", R.drawable.ic_animal_8, "mamífero"),
        Animal("pájaro", R.drawable.ic_animal_9, "ave"),
        Animal("hipopótamo", R.drawable.ic_animal_10, "mamífero"),
        Animal("pato", R.drawable.ic_animal_11, "ave")
    )

    fun getRandomAnimals(number: Int, exclude: List<Animal> = emptyList()): List<Animal> {
        return animals.filter { it !in exclude }.shuffled().take(number)
    }

    fun generateAnimalQuestions(): List<Question> {
        val questions = mutableListOf<Question>()

        // Generar preguntas para tipos de animales
        val animalTypes = animals.map { it.type }.distinct()
        for (type in animalTypes) {
            val correctAnimals = animals.filter { it.type == type }
            val incorrectAnimals = animals.filter { it.type != type }

            if (correctAnimals.isNotEmpty() && incorrectAnimals.size >= 3) {
                val correctAnimal = correctAnimals.random()
                val incorrectOptions = incorrectAnimals.shuffled().take(3)

                // Guardamos las opciones incorrectas en una variable temporal
                val options = (listOf(correctAnimal) + incorrectOptions).shuffled()

                questions.add(
                    Question(
                        "¿Cuál de estos es un $type?",
                        animals.indexOf(correctAnimal),
                        QuestionType.IMAGE_MULTIPLE_CHOICE
                    )
                )

                // Aquí guardamos las opciones incorrectas para usarlas en la actividad
                questionOptions[questions.size - 1] = options.map { animals.indexOf(it) }
            }
        }

        // Generar preguntas de identificación
        val randomAnimals = getRandomAnimals(10 - questions.size)
        for (animal in randomAnimals) {
            questions.add(
                Question(
                    "¿Qué animal es este?",
                    animals.indexOf(animal),
                    QuestionType.IMAGE_IDENTIFICATION
                )
            )
        }

        return questions.take(10)
    }

    fun getAnimalByIndex(index: Int): Animal {
        return animals[index]
    }

    // Variable temporal para guardar las opciones incorrectas
    val questionOptions = mutableMapOf<Int, List<Int>>()
}

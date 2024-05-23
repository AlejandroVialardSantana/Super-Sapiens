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

    fun getRandomAnimals(number: Int): List<Animal> {
        return animals.shuffled().take(number)
    }

    fun generateAnimalQuestions(): List<Question> {
        val questions = mutableListOf<Question>()

        // Generar preguntas para tipos de animales
        val animalTypes = animals.map { it.type }.distinct()
        for (type in animalTypes) {
            val correctAnimals = animals.filter { it.type == type }
            val incorrectAnimals = animals.filter { it.type != type }

            if (correctAnimals.isNotEmpty() && incorrectAnimals.isNotEmpty()) {
                val correctAnimal = correctAnimals.random()
                val incorrectAnimal = incorrectAnimals.random()

                questions.add(
                    Question(
                        "¿Cuál de estos es un $type?",
                        animals.indexOf(correctAnimal),
                        QuestionType.IMAGE_MULTIPLE_CHOICE
                    )
                )
            }
        }

        // Generar preguntas de identificación
        val randomAnimals = getRandomAnimals(10)
        for (animal in randomAnimals) {
            questions.add(
                Question(
                    "¿Qué animal es este?",
                    animals.indexOf(animal),
                    QuestionType.IMAGE_IDENTIFICATION
                )
            )
        }

        return questions
    }

    fun getAnimalByIndex(index: Int): Animal {
        return animals[index]
    }
}

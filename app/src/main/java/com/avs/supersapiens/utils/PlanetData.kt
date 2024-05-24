package com.avs.supersapiens.utils

import com.avs.supersapiens.R
import com.avs.supersapiens.models.Planet

object PlanetData {
    val planets = listOf(
        Planet("Mercurio", R.drawable.ic_mercury, 1),
        Planet("Venus", R.drawable.ic_venus, 2),
        Planet("Tierra", R.drawable.ic_earth, 3),
        Planet("Marte", R.drawable.ic_mars, 4),
        Planet("Júpiter", R.drawable.ic_jupiter, 5),
        Planet("Saturno", R.drawable.ic_saturn, 6),
        Planet("Urano", R.drawable.ic_uranus, 7),
        Planet("Neptuno", R.drawable.ic_neptune, 8)
    )

    fun getRandomPlanets(number: Int, exclude: List<Planet> = emptyList()): List<Planet> {
        return planets.filter { it !in exclude }.shuffled().take(number)
    }

    fun getPlanetByIndex(index: Int): Planet {
        return planets[index]
    }
}

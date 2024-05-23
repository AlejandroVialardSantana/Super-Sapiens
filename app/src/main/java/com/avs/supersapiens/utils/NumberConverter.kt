package com.avs.supersapiens.utils

object NumberConverter {

    fun convertWordToNumber(word: String): String {
        return when (word.lowercase()) {
            "uno" -> "1"
            "dos" -> "2"
            "tres" -> "3"
            "cuatro" -> "4"
            "cinco" -> "5"
            "seis" -> "6"
            "siete" -> "7"
            "ocho" -> "8"
            "nueve" -> "9"
            "diez" -> "10"
            "once" -> "11"
            "doce" -> "12"
            "trece" -> "13"
            "catorce" -> "14"
            "quince" -> "15"
            "dieciséis" -> "16"
            "diecisiete" -> "17"
            "dieciocho" -> "18"
            "diecinueve" -> "19"
            "veinte" -> "20"
            "veintiuno" -> "21"
            "veintidós" -> "22"
            "veintitrés" -> "23"
            "veinticuatro" -> "24"
            "veinticinco" -> "25"
            "veintiséis" -> "26"
            "veintisiete" -> "27"
            "veintiocho" -> "28"
            "veintinueve" -> "29"
            "treinta" -> "30"
            "treinta y uno" -> "31"
            "treinta y dos" -> "32"
            "treinta y tres" -> "33"
            "treinta y cuatro" -> "34"
            "treinta y cinco" -> "35"
            "treinta y seis" -> "36"
            "treinta y siete" -> "37"
            "cuarenta" -> "40"
            else -> word
        }
    }
}
package com.example.pmdm1

import java.util.Locale

fun main() {
    printFinalTemperature(27.0, "Celsius", "Fahrenheit") { c ->
        (9.0 * c / 5.0) + 32.0
    }
    printFinalTemperature(350.0, "Kelvin", "Celsius") { k ->
        k - 273.15
    }
    printFinalTemperature(10.0, "Celsius", "Fahrenheit") {
        ((it - 32.0) * 5.0 / 9.0) + 273.15
    }
}

fun printFinalTemperature(
    initialMeasurement: Double,
    initialUnit: String,
    finalUnit: String,
    conversionFormula: (Double) -> Double
) {
    val finalMeasurement = String.format(Locale.ENGLISH,"%.2f", conversionFormula(initialMeasurement)) // two decimal places
    println("$initialMeasurement degrees $initialUnit is $finalMeasurement degrees $finalUnit.")
}
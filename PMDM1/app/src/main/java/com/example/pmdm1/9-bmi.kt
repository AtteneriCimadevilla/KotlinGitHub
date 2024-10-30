package com.example.pmdm1

fun calcBMI(height: Double = 0.0, weight: Double = 0.0): Double {
    val dividend = height * height
    val bmi = weight / (height * height)
    return bmi
}

fun printBMI(bmi: Double) {
    println("BMI: $bmi")
    when (bmi) {
        in 0.0..18.4 -> println("UNDERWEIGHT")
        in 18.5..24.9 -> println("HEALTHY RANGE")
        in 25.0..29.9 -> println("OVERWEIGHT")
        in 30.0..39.9 -> println("OBESITY")
        else -> println("SEVERE OBESITY")
    }
}

fun main() {
    println("Give me your height in centimeters.")
    val heightInCentimeters: Double = readln().toDouble()
    val heightInMeters: Double = heightInCentimeters / 100.0
    println("Give me your weight in kilograms.")
    val weight: Double = readln().toDouble()
    val bmi: Double = calcBMI(heightInMeters, weight)
    printBMI(bmi)
}
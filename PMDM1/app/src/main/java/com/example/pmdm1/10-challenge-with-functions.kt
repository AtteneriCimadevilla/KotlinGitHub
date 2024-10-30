package com.example.pmdm1

import kotlin.math.ceil

fun howManyTins(performance: Double, height: Double, width: Double, coats: Int): Int {
    val tinsDouble: Double = ceil(((height * width) * coats) / performance)
    return tinsDouble.toInt()
}

fun readDouble(message: String): Double {
    println(message)
    return readln().toDouble()
}

fun readInt(message: String): Int {
    println(message)
    return readln().toInt()
}

fun main() {
    val performance: Double = readDouble("Give me the performance of the paint in m2/tin.")
    val height: Double = readDouble("Give me the height of the wall in meters.")
    val width: Double = readDouble("Give me the width of the wall in meters.")
    val coats: Int = readInt("How many coats of paint are you doing?")

    println("You need ${howManyTins(performance, height, width, coats)} tins to paint your wall.")
}
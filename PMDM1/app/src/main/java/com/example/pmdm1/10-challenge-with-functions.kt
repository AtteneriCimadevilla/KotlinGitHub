package com.example.pmdm1

import kotlin.math.ceil

fun howManyTins(performance: Double, height: Double, width: Double, coats: Int): Int {
    val tinsDouble: Double = ceil(((height * width) * coats) / performance)
    return tinsDouble.toInt()
}

fun main() {
    println("Give me the performance of the paint in m2/tin.")
    val performance: Double = readln().toDouble()
    println("Give me the height of the wall in meters.")
    val height: Double = readln().toDouble()
    println("Give me the width of the wall in meters.")
    val width: Double = readln().toDouble()
    println("How many coats of paint are you doing?")
    val coats: Int = readln().toInt()
    val tinsInt: Int = howManyTins(performance, height, width, coats)
    println("You need $tinsInt tins to paint your wall.")
}
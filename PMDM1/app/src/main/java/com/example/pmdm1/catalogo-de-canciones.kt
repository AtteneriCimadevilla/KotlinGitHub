package com.example.pmdm1

class Song(
    private val title: String,
    private val artist: String,
    private val yearPublished: Int,
    private var playCount: Int
) {
    var popular = (playCount >= 1000)

    fun printSong() {
        println("$title, performed by $artist, was released in ${this.yearPublished}.")
        if (popular) {
            println("It's popular.")
        }
    }
}

fun main() {
    val imagine = Song(
        "Imagine",
        "John Lennon and Yoko Ono",
        1971,
        1000000
    )
    imagine.printSong()
}
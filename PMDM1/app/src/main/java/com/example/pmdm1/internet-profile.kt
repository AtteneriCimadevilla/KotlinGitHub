package com.example.pmdm1

fun main() {
    val amanda = Person("Amanda", 33, "play tennis", null)
    val atiqah = Person("Atiqah", 28, "climb", amanda)

    amanda.showProfile()
    atiqah.showProfile()
}


class Person(val name: String, val age: Int, val hobby: String?, val referrer: Person?) {
    fun showProfile() {
        val message: String
        if (referrer == null) {
            message = "Doesn't have a referrer."
        } else {
            val referrerName = referrer.name
            val referrerHobby = referrer.hobby
            message = "Has a referrer named $referrerName, who likes to play $referrerHobby."
        }
        println("Name: $name")
        println("Age: $age")
        println("Likes to $hobby. $message\n")
    }
}
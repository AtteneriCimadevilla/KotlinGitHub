package com.example.pmdm1

fun main() {
    val amanda = Person("Amanda", 33, "play tennis", null)
    val atiqah = Person("Atiqah", 28, "climb", amanda)

    amanda.showProfile()
    atiqah.showProfile()
}


class Person(val name: String, val age: Int, val hobby: String?, val referrer: Person?) {
    fun showProfile() {
        println("Name: $name")
        println("Age: $age")
        if (hobby != null) print("Likes to $hobby.")
        val message: String
        if (referrer == null) message = "Doesn't have a referrer."
        else {
            val referrerName = referrer.name
            if (referrer.hobby == null) message = "Has a referrer named $referrerName."
            else {
                val referrerHobby = referrer.hobby
                message = "Has a referrer named $referrerName, who likes to play $referrerHobby."
            }
        }
        println(message + "\n")
    }
}
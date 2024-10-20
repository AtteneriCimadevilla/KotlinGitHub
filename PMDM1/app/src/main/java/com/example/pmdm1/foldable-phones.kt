package com.example.pmdm1

fun main () {
    val myPhone = FoldablePhone(false, true)
    myPhone.showInfo()
    printInRed("Let's switch it on.")
    myPhone.switchOn()
    printInRed("Oops, sorry, we need to unfold it first.")
    myPhone.unfold()
    printInRed("Now we can switch it on.")
    myPhone.switchOn()
    myPhone.showInfo()
}

open class Phone(open var isScreenLightOn: Boolean = false){
    open fun switchOn() {
        isScreenLightOn = true
    }

    fun switchOff() {
        isScreenLightOn = false
    }

    fun checkPhoneScreenLight() {
        val phoneScreenLight = if (isScreenLightOn) "on" else "off"
        println("The phone screen's light is $phoneScreenLight.")
    }
}

class FoldablePhone(
    isScreenLightOn: Boolean,
    var isFolded: Boolean = true
): Phone(isScreenLightOn) {
    override fun switchOn() {
        if (!isFolded) super.switchOn()
        else println("I can't switch on the phone, it's folded.")
    }

    fun fold() {
        isFolded = true
    }

    fun unfold() {
        isFolded = false
    }

    fun showInfo() {
        var foldedMessage = if (this.isFolded) "folded" else "unfolded"
        println ("My phone es $foldedMessage.")
        checkPhoneScreenLight()
    }

}

fun printInRed(message: String) {
    val red = "\u001b[31m"
    val reset = "\u001b[0m"
    println(red + message + reset)
}
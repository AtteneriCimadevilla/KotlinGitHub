package com.example.challengewithfunctions

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.ceil

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        val button: Button = findViewById( R.id.button)

        val errorMessage: TextView = findViewById(R.id.textViewError)
        val tins: TextView = findViewById(R.id.textViewTins)

        button.setOnClickListener {

            hideKeyboard()

            try {
                errorMessage.text = ""

                val performanceText: EditText = findViewById(R.id.editTextPerformance)
                val heightText: EditText = findViewById(R.id.editTextHeight)
                val widthText: EditText = findViewById(R.id.editTextWidth)
                val coatsText: EditText = findViewById(R.id.editTextCoats)

                val performanceString: String = performanceText.text.toString()
                val heightString: String = heightText.text.toString()
                val widthString: String = widthText.text.toString()
                val coatsString: String = coatsText.text.toString()

                val performanceDouble: Double = performanceString.toDouble()
                val heightDouble: Double = heightString.toDouble()
                val widthDouble: Double = widthString.toDouble()
                val coatsInt: Int = coatsString.toInt()

                if (performanceDouble == 0.0) {
                    errorMessage.text = getString(R.string.performance_cannot_be_zero)
                } else {
                    val howMany = howManyTins(performanceDouble, heightDouble, widthDouble, coatsInt)
                    tins.text = if (howMany == 1) "$howMany tin" else "$howMany tins"
                }

            } catch (e: Exception) {
                errorMessage.text = getString(R.string.all_fields_must_be_filled_in)
            }


        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun howManyTins(performance: Double, height: Double, width: Double, coats: Int): Int {
        val tinsDouble: Double = ceil(((height * width) * coats) / performance)
        return tinsDouble.toInt()
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
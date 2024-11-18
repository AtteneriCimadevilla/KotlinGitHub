package com.example.challengewithfunctions

import android.os.Bundle
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
                    errorMessage.text = "Performance cannot be zero!"
                } else {
                    val howMany = howManyTins(performanceDouble, heightDouble, widthDouble, coatsInt)

                    if (tins != null) {
                        tins.text = if (howMany == 1) howMany.toString() + " tin" else howMany.toString() + " tins"
                    }
                }

            } catch (e: Exception) {
                errorMessage.text = "All fields must be filled in!"
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
}
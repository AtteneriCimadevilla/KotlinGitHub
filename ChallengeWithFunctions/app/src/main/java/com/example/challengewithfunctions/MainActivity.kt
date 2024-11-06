package com.example.challengewithfunctions

import android.os.Bundle
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //MAIN FUNCTIONALITY
        val performanceText: EditText? = findViewById(R.id.editTextPerformance)
        val performanceDouble: Double = performanceText.toString().toDouble()

        val heightText: EditText? = findViewById(R.id.editTextHeight)
        val heightDouble: Double = heightText.toString().toDouble()

        val widthText: EditText? = findViewById(R.id.editTextWidth)
        val widthDouble: Double = widthText.toString().toDouble()

        val coatsText: EditText? = findViewById(R.id.editTextCoats)
        val coatsInt: Int = coatsText.toString().toInt()

        val tins: TextView? = findViewById(R.id.textViewTins)
        if (tins != null) {
            tins.text = howManyTins(performanceDouble, heightDouble, widthDouble, coatsInt).toString()
        }
    }

    private fun howManyTins(performance: Double, height: Double, width: Double, coats: Int): Int {
        val tinsDouble: Double = ceil(((height * width) * coats) / performance)
        return tinsDouble.toInt()
    }
}
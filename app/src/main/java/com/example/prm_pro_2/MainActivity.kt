package com.example.prm_pro_2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    companion object{
        var red = 0
        var green = 0
        var blue = 0
        var size = 10
        var km = 1
    }

    fun locationNotifiaction(){

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val aparat = findViewById<Button>(R.id.aparat)
        val gallery = findViewById<Button>(R.id.gallery)
        val settings = findViewById<Button>(R.id.settings)

        aparat.setOnClickListener {
            val intent = Intent(this, PhotoActivity::class.java).apply {
                putExtra("R",""+red)
                putExtra("G",""+green)
                putExtra("B",""+blue)
                putExtra("S",""+size)
            }
            startActivity(intent)
        }

        gallery.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java).apply {
                putExtra("msg","add")
            }
            startActivity(intent)
        }

        settings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java).apply {
                putExtra("R",""+red)
                putExtra("G",""+green)
                putExtra("B",""+blue)
                putExtra("S",""+size)
                putExtra("KM",""+km)
            }
            startActivity(intent)
        }

    }
}

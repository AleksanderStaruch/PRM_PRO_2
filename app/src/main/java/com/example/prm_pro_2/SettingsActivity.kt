package com.example.prm_pro_2

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class SettingsActivity : AppCompatActivity() {

    private var oldRed = 0
    private var oldGreen = 0
    private var oldBlue = 0
    private var oldSize = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val size = findViewById<EditText>(R.id.s_fontSize)

        val red = findViewById<SeekBar>(R.id.s_seekBar1)
        val green = findViewById<SeekBar>(R.id.s_seekBar2)
        val blue = findViewById<SeekBar>(R.id.s_seekBar3)

        val color = findViewById<ImageView>(R.id.s_imageView)

        oldRed = intent.getStringExtra("R").toInt()
        oldGreen = intent.getStringExtra("G").toInt()
        oldBlue = intent.getStringExtra("B").toInt()
        oldSize = intent.getStringExtra("S").toInt()
        red.progress = oldRed
        green.progress = oldGreen
        blue.progress = oldBlue
        size.setText(oldSize.toString())
        color.setBackgroundColor(Color.rgb(oldRed,oldGreen,oldBlue))

        var newRed = oldRed
        var newGreen = oldGreen
        var newBlue = oldBlue

        val save = findViewById<Button>(R.id.s_save)
        val cancel = findViewById<Button>(R.id.s_cancel)

        red.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val c = Color.rgb(progress,green.progress,blue.progress)
                newRed = progress
                color.setBackgroundColor(c)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        green.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val c = Color.rgb(red.progress,progress,blue.progress)
                newGreen = progress
                color.setBackgroundColor(c)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        blue.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val c = Color.rgb(red.progress,green.progress,progress)
                newBlue = progress
                color.setBackgroundColor(c)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        save.setOnClickListener {
            MainActivity.red = newRed
            MainActivity.green = newGreen
            MainActivity.blue = newBlue
            MainActivity.size = size.text.toString().toInt()
            finish()
        }

        cancel.setOnClickListener {
            MainActivity.red = oldRed
            MainActivity.green = oldGreen
            MainActivity.blue = oldBlue
            MainActivity.size = oldSize
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        MainActivity.red = oldRed
        MainActivity.green = oldGreen
        MainActivity.blue = oldBlue
        MainActivity.size = oldSize
    }
}

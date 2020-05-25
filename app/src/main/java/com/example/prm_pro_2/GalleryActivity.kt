package com.example.prm_pro_2

import android.content.Context
import android.content.ContextWrapper
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.room.Room
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.lang.Exception
import kotlin.concurrent.thread

class GalleryActivity : AppCompatActivity() {

    private fun readDataBase(): List<Photo> {
        var list = listOf<Photo>()
        Room.databaseBuilder(this, PhotoDb::class.java, "photos.db").build().let {
            val t =thread{ list = it.getPhotoDao().selectAll() }
            while (t.isAlive){ }
            return list
        }

    }

    private fun readImages(dbList: Int): List<ImageView> {
        val list= mutableListOf<ImageView>()
        try {
            for(id in 1..dbList){
                try{
                    var path = ContextWrapper(applicationContext).getDir("Images", Context.MODE_PRIVATE)
                    path = File(path,"${id}.jpeg")
                    val bitmapFactory = BitmapFactory.decodeStream(FileInputStream(path))
                    val imageView = ImageView(applicationContext)
                    imageView.setImageBitmap(bitmapFactory)
                    list.add(imageView)
                }catch (e: IOException){
                    val imageView = ImageView(applicationContext)
                    list.add(imageView)
                    Log.println(Log.ERROR,"@@@@@@readImages1", "$e")
                }
            }
            return list
        }catch (e: Exception){
            Log.println(Log.ERROR,"@@@@@@readImages2", "$e")
            return  list
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        val dbList = readDataBase()
        val list = readImages(dbList.size)
//        Log.println(Log.ERROR,"@@@@@@onCreate", "@${dbList.size}")
        val imageView = findViewById<ImageView>(R.id.g_imageView)
        val next = findViewById<Button>(R.id.g_next)
        val prev = findViewById<Button>(R.id.g_prev)
        val text = findViewById<TextView>(R.id.g_text)
        var i = 0
        try {
            imageView.setImageBitmap(list[i].drawable?.toBitmap())
            text.text = dbList[i].toString()
        }catch (e: IndexOutOfBoundsException){
            imageView.setImageBitmap(ImageView(applicationContext).drawable?.toBitmap())
            text.text = "No photos"
        }
        next.setOnClickListener {
            try{
                i++
                imageView.setImageBitmap(list[i%(list.size)].drawable?.toBitmap())
                text.text = dbList[i%(dbList.size)].toString()
            }catch (e:ArithmeticException){
                e.printStackTrace()
            }
        }

        prev.setOnClickListener {
            try{
                i--
                if(i<0){ i = dbList.size-1 }
                imageView.setImageBitmap(list[i%(list.size)].drawable?.toBitmap())
                text.text = dbList[i%(dbList.size)].toString()
            }catch (e:ArithmeticException){
                e.printStackTrace()
            }
        }

    }

    override fun onBackPressed() {
        finish()
    }

}

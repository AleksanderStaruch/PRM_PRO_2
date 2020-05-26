package com.example.prm_pro_2

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.CameraManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_photo.*
import java.io.*
import java.lang.Exception
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.thread


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class PhotoActivity : AppCompatActivity(), SurfaceHolder.Callback {
    private val cam by lazy { MyCamera(getSystemService(Context.CAMERA_SERVICE) as CameraManager) }
    private var red = 0
    private var green = 0
    private var blue = 0
    private var fontSize = 0

    private fun writeToDataBase(photo: Photo){
        try {
            Room.databaseBuilder(this, PhotoDb::class.java, "photos.db").build().let {
                thread { it.getPhotoDao().insert(photo) }
            }
        }catch (e:Exception){
            Log.println(Log.ERROR,"######weadDataBase","DB null1")
        }
    }

    private fun readDataBase(): List<Photo> {
        var list = listOf<Photo>()
        Room.databaseBuilder(this, PhotoDb::class.java, "photos.db").build().let {
            val t =thread{ list = it.getPhotoDao().selectAll() }
            while (t.isAlive){ }
            return list
        }
    }

    @SuppressLint("InflateParams")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun savePhoto(bitmap: Bitmap) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Description")
        val dialogLayout: View = inflater.inflate(R.layout.description_dialog, null)
        val descField = dialogLayout.findViewById<EditText>(R.id.editText)

        builder.setView(dialogLayout)
        builder.setPositiveButton("Save") { _, _ ->
            val geoPosition = getGeoPosition()
            val time = LocalDateTime.now()
            val tmpBitmap = drawTextToBitmap(bitmap,geoPosition.country+" "+geoPosition.city,time.toString())
            val photo = Photo(descField.text.toString(),geoPosition.toString(),time.toString())
            val id = readDataBase().size+1
            bitmapToFile(tmpBitmap,id)
            writeToDataBase(photo)
        }
        builder.setNegativeButton("Cancel"){ _, _ -> }
        builder.show()
    }

    private fun getGeoPosition(): GeoPosition {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 12)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 13)
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        val location = locationManager.getLastKnownLocation(locationManager.allProviders[0])
        val geocoder = Geocoder(this, Locale.getDefault())
        val data = geocoder.getFromLocation(location.latitude, location.longitude, 1)[0].getAddressLine(0).split(",")
        return GeoPosition(data[3],data[1],location.latitude,location.longitude)
    }

    private fun drawTextToBitmap(bitmap: Bitmap, geoText: String, timeText: String): Bitmap {
        var tmpBitmap = bitmap
        val bitmapConfig = tmpBitmap.config
        tmpBitmap = tmpBitmap.copy(bitmapConfig, true)
        val canvas = Canvas(tmpBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.rgb(red, green, blue)
        paint.textSize = (fontSize * 1F)
        paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY)

        val bounds = Rect()
        paint.getTextBounds(geoText, 0, geoText.length, bounds)
        val x = (tmpBitmap.width - bounds.width()) / 5F
        val y = (tmpBitmap.height + bounds.height()) / 5F

        canvas.drawText(geoText,x, y, paint)
        canvas.drawText(timeText,x, y+y, paint)
//        Log.println(Log.ERROR,"###########", "&&&&&&&&& is XY: $x $y")
        canvas.drawBitmap(tmpBitmap, Rect(0,0,200,200),bounds,null)
        return tmpBitmap
    }

    private fun bitmapToFile(bitmap:Bitmap, id: Int): Uri {
        var path = ContextWrapper(applicationContext).getDir("Images",Context.MODE_PRIVATE)
        path = File(path,"${id}.jpeg")
        try{
            val outputStream: OutputStream = FileOutputStream(path)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream)
            outputStream.flush()
            outputStream.close()
//            Log.println(Log.ERROR,"####bitmapToFile",""+id)
        }catch (e: IOException){
            e.printStackTrace()
            Log.println(Log.ERROR,"#####bitmapToFile", "ERROR $e")
        }
        return Uri.parse(path.absolutePath)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        red = intent.getStringExtra("R").toInt()
        green = intent.getStringExtra("G").toInt()
        blue = intent.getStringExtra("B").toInt()
        fontSize = intent.getStringExtra("S").toInt()

        if (ContextCompat.checkSelfPermission(this, "CAMERA") == -1) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 666)
        }
        surfaceView.holder.addCallback(this)
        save.isEnabled = false
        save.isClickable = false
        takePhoto.setOnClickListener {
            imageView.rotation = 270f
            cam.acquire(imageView as ImageView)
            save.isEnabled = true
            save.isClickable = true
        }
        save.setOnClickListener {
            val bitmap1:Bitmap = imageView.drawable.toBitmap()
            savePhoto(bitmap1)
        }
    }


    override fun onResume() { super.onResume(); cam.openCamera() }
    override fun onPause() { cam.closeCamera(); super.onPause() }
    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {}
    override fun surfaceDestroyed(holder: SurfaceHolder?) {}
    override fun surfaceCreated(holder: SurfaceHolder?) { holder?.surface?.let { cam.setupPreviewSession(it) } }
    override fun onBackPressed() { finish() }
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {}
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }
}

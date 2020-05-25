package com.example.prm_pro_2

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Photo::class], version = 1)
abstract class PhotoDb : RoomDatabase() {
    abstract fun getPhotoDao(): PhotoDao
}
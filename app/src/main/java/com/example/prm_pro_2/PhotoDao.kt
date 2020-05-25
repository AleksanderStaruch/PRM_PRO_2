package com.example.prm_pro_2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PhotoDao {
    @Insert
    fun insert(note: Photo)

    @Query("SELECT * FROM Photo")
    fun selectAll(): List<Photo>

}
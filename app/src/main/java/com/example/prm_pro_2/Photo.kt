package com.example.prm_pro_2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Photo(var desc: String, var geo: String, var dateTime: String){
    @PrimaryKey(autoGenerate = true) var id: Int = 0

    override fun toString(): String {
        return "{\"desc\":$desc,\"geo\":$geo,\"time\":$dateTime,\"id\":$id}"
    }


}
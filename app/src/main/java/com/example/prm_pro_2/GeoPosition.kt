package com.example.prm_pro_2

data class GeoPosition(val country: String, val city: String, val latitude: Double, val longitude: Double){
    override fun toString(): String {
        return "{\"country\":$country,\"city\":$city,\"lat\":$latitude,\"long\":$longitude}"
    }
}
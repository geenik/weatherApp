package com.example.weatherapp

import com.example.weatherapp.model.data
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface getdata {
    @GET("current.json?key=54e4c15cc44e41cdac9103352232705")
    fun getweather(@Query("q") city:String ): Call<data>
}
package com.example.weatherapp

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.model.data
import com.example.weatherapp.model.errorresponse
import com.google.gson.Gson
import retrofit2.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    lateinit var bind:ActivityMainBinding
    lateinit var city:String
    lateinit var pref:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind= ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        pref=getSharedPreferences("cityname", Context.MODE_PRIVATE)
        val savedData = pref.getString("city", null)
        val retrofit = RetrofitClient.getInstance().create<getdata>()
        Log.d(TAG,savedData.toString())
        bind.detail.visibility= View.GONE
        bind.noData.visibility=View.VISIBLE
        if(savedData!=null){
            city=savedData
            getData(retrofit)
        }
        bind.btnViewWeather.setOnClickListener {
            city=bind.entertext.text.toString()
            bind.entertext.text=null
            if(!city.isEmpty())getData(retrofit)
        }
    }
    private fun getData(retrofit:getdata) {
        bind.noData.visibility = View.GONE
        bind.progressBar.visibility = View.VISIBLE
        val result = retrofit.getweather(city)
        result.enqueue(object : Callback<data> {
            override fun onResponse(call: Call<data>, response: Response<data>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        showdata(data)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    bind.progressBar.visibility = View.GONE
                    bind.detail.visibility=View.GONE
                    val error = Gson().fromJson(errorBody, errorresponse::class.java)
                    bind.noData.text = error.error.message
                    bind.noData.visibility=View.VISIBLE
                }
            }

            override fun onFailure(call: Call<data>, t: Throwable) {
                if (t is IOException) {
                    bind.progressBar.visibility = View.GONE
                    bind.detail.visibility=View.GONE
                    bind.noData.visibility=View.VISIBLE
                    Toast.makeText(this@MainActivity, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                } else {

                }
            }
        }
        )
    }
    private fun showdata(data: data) {
        bind.cityNameTextView.text=data.location.name
        Glide.with(this).load("https:"+data.current.condition.icon).into(bind.weatherIconImageView)
        bind.temperatureTextView.text=data.current.temp_c.toString()+"°C"
        bind.weatherDescriptionTextView.text=data.current.condition.text
        bind.windSpeedTextView.text=data.current.wind_kph.toString()+" KM/H"
        bind.humidityTextView.text=data.current.humidity.toString()
        bind.progressBar.visibility=View.GONE
        bind.detail.visibility=View.VISIBLE
        pref.edit().putString("city",city).apply()
    }
}
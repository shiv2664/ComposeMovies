package com.myjar.jarassignment

import com.myjar.jarassignment.data.api.ApiService
import com.myjar.jarassignment.data.moviesapi.MoviesApiInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


fun createRetrofit(): MoviesApiInterface {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://www.omdbapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: MoviesApiInterface = retrofit.create(MoviesApiInterface::class.java)
    return service
}

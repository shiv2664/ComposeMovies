package com.myjar.jarassignment

import com.myjar.jarassignment.data.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


fun createRetrofit(): ApiService {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.restful-api.dev")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: ApiService = retrofit.create(ApiService::class.java)
    return service
}

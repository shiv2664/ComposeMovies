package com.myjar.jarassignment.data.api

import com.myjar.jarassignment.data.model.ComputerItem
import retrofit2.http.GET

interface ApiService {
    @GET("/objects")
    suspend fun fetchResults(): List<ComputerItem>
}
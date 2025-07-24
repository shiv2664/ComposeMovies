package com.myjar.jarassignment.data.moviesapi

import com.myjar.jarassignment.data.model.MovieDetails
import com.myjar.jarassignment.data.model.MoviesListingModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface MoviesApiInterface {

    @GET
    suspend fun getMoviesList(@Url url:String):Response<MoviesListingModel?>

    @GET
    suspend fun getMoviesDetails(@Url url:String):Response<MovieDetails?>

}
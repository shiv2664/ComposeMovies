package com.myjar.jarassignment

sealed class NetworkResult<T> (
    val data: T? = null,
    val message: String? = null,
    val key: String? = null
){

    class Loading<T>(message: String?): NetworkResult<T>(message = message)
    class Success<T>(data: T?): NetworkResult<T>(data)
    class Error<T>(message: String?): NetworkResult<T>(message = message)

}
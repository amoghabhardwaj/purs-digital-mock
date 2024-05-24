package com.example.pursdigital

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("location.json")
    suspend fun getLocationData(): LocationResponse
}
//Using Retrofit object to do the service call to the backend
//Please note that currently the BASE_URL is a hardcoded version, but we can make it dynamic when we would need to search for
// different locations - we can add params to the url to make it dynamic and respective to that location
// we can fetch the location entered in the text box directly
object NetworkService {
    private const val BASE_URL = "https://purs-demo-bucket-test.s3.us-west-2.amazonaws.com/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

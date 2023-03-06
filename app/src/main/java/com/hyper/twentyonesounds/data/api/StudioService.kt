package com.hyper.twentyonesounds.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


interface StudioService {

    @GET("api/21sounds")
    suspend fun getStudio() : Studio

    companion object {
        operator fun invoke() : StudioService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://21sounds-api.vercel.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(StudioService::class.java)
        }
    }
}
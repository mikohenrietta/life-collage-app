package com.example.lifecollage.network

import com.example.lifecollage.model.CollageItem
import retrofit2.Response
import retrofit2.http.*

interface CollageApi {
    @GET("collage")
    suspend fun getAllCollages(): List<CollageItem>

    @POST("collage")
    suspend fun createCollage(@Body item: CollageItem): CollageItem

    @PUT("collage/{id}")
    suspend fun updateCollage(@Path("id") id: Int, @Body item: CollageItem): CollageItem

    @DELETE("collage/{id}")
    suspend fun deleteCollage(@Path("id") id: Int): Response<Unit>
}
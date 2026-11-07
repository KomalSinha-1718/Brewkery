package com.brewkery.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface BrewkeryApi {

    @GET("VivekShah138/Brewkery/main/data.json")
    suspend fun getMenu(): MenuResponseDto

    @GET("VivekShah138/Brewkery/main/api/items/{id}.json")
    suspend fun getItem(@Path("id") id: Int): ItemDto

    companion object {
        const val BASE_URL = "https://raw.githubusercontent.com/"
    }
}

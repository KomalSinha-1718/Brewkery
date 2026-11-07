package com.brewkery.app

import com.brewkery.app.data.CartRepository
import com.brewkery.app.data.MenuRepository
import com.brewkery.app.data.OrderRepository
import com.brewkery.app.data.remote.BrewkeryApi
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class AppContainer {
    private val json = Json {
        ignoreUnknownKeys = true // tolerate additive API changes
        coerceInputValues = true // null / unknown enum -> default instead of crashing
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BrewkeryApi.BASE_URL)
        .client(okHttpClient)
        // raw.githubusercontent.com serves JSON as text/plain; the converter ignores response content-type.
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val api: BrewkeryApi = retrofit.create(BrewkeryApi::class.java)

    val menuRepository = MenuRepository(api)
    val cartRepository = CartRepository()
    val orderRepository = OrderRepository()
}

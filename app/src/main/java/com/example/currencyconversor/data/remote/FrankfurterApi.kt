package com.example.currencyconversor.data.remote

import com.example.currencyconversor.data.model.ConversionRate
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FrankfurterApi {

    @GET("currencies")
    suspend fun getCurrencies(): Map<String, String>

    @GET("latest")
    suspend fun getLatestRates(
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("amount") amount: Double
    ): ConversionRate

    companion object {
        const val BASE_URL = "https://api.frankfurter.app/"
    }
}
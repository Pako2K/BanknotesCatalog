package com.pako2k.banknotescatalog.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.pako2k.banknotescatalog.data.Continent
import com.pako2k.banknotescatalog.data.Currency
import com.pako2k.banknotescatalog.data.Territory
import com.pako2k.banknotescatalog.data.TerritoryType
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.GET
import java.util.concurrent.TimeUnit


interface BanknotesAPIService{
    @GET("continent")
    suspend fun getContinents() : List<Continent>

    @GET("territory-type")
    suspend fun getTerritoryTypes() : List<TerritoryType>

    @GET("territory")
    suspend fun getTerritories() : List<Territory>

    @GET("currency")
    suspend fun getCurrencies() : List<Currency>
}


class BanknotesAPIClient (
    baseURL : String,
    timeout : Int
){
    private val retrofit = Retrofit
        .Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseURL)
        .client(
            OkHttpClient
                .Builder()
                .callTimeout(timeout.toLong(), TimeUnit.SECONDS)
                .connectTimeout(timeout.toLong(), TimeUnit.SECONDS)
                .readTimeout(timeout.toLong(), TimeUnit.SECONDS)
                .build()
        )
        .build()

    private val retrofitService : BanknotesAPIService by lazy {
        retrofit.create(BanknotesAPIService::class.java)
    }

    suspend fun getContinents() = retrofitService.getContinents()
    suspend fun getTerritoryTypes()  = retrofitService.getTerritoryTypes()
    suspend fun getTerritories() = retrofitService.getTerritories()
    suspend fun getCurrencies() = retrofitService.getCurrencies()
}
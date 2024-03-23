package com.pako2k.banknotescatalog.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.pako2k.banknotescatalog.data.Continent
import com.pako2k.banknotescatalog.data.Currency
import com.pako2k.banknotescatalog.data.ItemLinked
import com.pako2k.banknotescatalog.data.Territory
import com.pako2k.banknotescatalog.data.TerritoryType
import com.pako2k.banknotescatalog.data.UserSession
import com.pako2k.banknotescatalog.data.stats.CurrencyStats
import com.pako2k.banknotescatalog.data.stats.DenominationStats
import com.pako2k.banknotescatalog.data.stats.IssueYearStats
import com.pako2k.banknotescatalog.data.stats.TerritoryStats
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


interface BanknotesAPIService{
    @GET("/user/session")
    suspend fun getUserSession(@Header("Authorization") basicAuthorization : String) : UserSession

    @GET("continent")
    suspend fun getContinents() : List<Continent>

    @GET("territory-type")
    suspend fun getTerritoryTypes() : List<TerritoryType>

    @GET("territory")
    suspend fun getTerritories() : List<Territory>

    @GET("territory/stats")
    suspend fun getTerritoryStats() : List<TerritoryStats>

    @GET("currency")
    suspend fun getCurrencies() : List<Currency>

    @GET("currency/stats")
    suspend fun getCurrencyStats() : List<CurrencyStats>

    @GET("denomination/stats")
    suspend fun getDenominationStats(
        @Query("fromYear") fromYear : Int? = null,
        @Query("toYear") toYear : Int? = null) : List<DenominationStats>

    @GET("issue-year/stats")
    suspend fun getIssueYearStats() : List<IssueYearStats>

    @GET("item")
    suspend fun getCollection(@Header("Authorization") bearerToken: String) : List<ItemLinked>
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

    suspend fun getUserSession(username : String, password : String) : UserSession {
        // Generate Basic Authorization string
        val credential = Credentials.basic(username,password)
        return retrofitService.getUserSession(credential)
    }
    suspend fun getContinents() = retrofitService.getContinents()
    suspend fun getTerritoryTypes()  = retrofitService.getTerritoryTypes()
    suspend fun getTerritories() = retrofitService.getTerritories()
    suspend fun getTerritoryStats() = retrofitService.getTerritoryStats()
    suspend fun getCurrencies() = retrofitService.getCurrencies()
    suspend fun getCurrencyStats() = retrofitService.getCurrencyStats()
    suspend fun getDenominationStats(
        fromYear : Int? = null,
        toYear : Int? = null)
     = retrofitService.getDenominationStats(fromYear, toYear)
    suspend fun getIssueYearStats() = retrofitService.getIssueYearStats()
    suspend fun getCollection(sessionId : String) = retrofitService.getCollection("Bearer $sessionId")

}
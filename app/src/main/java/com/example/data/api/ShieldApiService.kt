package com.example.data.api

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class ThreatCheckRequest(
    val type: String, // "url", "sms", "qr"
    val content: String,
    val meta: String? = null
)

data class ThreatAnalysisDto(
    val title: String,
    val description: String,
    val severity: String, // "SAFE", "WARNING", "CRITICAL"
    val matchCount: Int,
    val isBlacklisted: Boolean
)

interface ShieldApiService {
    @POST("api/analyze-threat")
    suspend fun analyzeThreat(@Body request: ThreatCheckRequest): ThreatAnalysisDto

    @GET("api/blacklist-sync")
    suspend fun getLiveDatabaseSignatures(): List<String>

    companion object {
        private const val BASE_URL = "https://calm-sentinel-shield-ai.base44.app/"

        fun create(): ShieldApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
            return retrofit.create(ShieldApiService::class.java)
        }
    }
}

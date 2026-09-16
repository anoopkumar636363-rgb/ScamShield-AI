package com.scamsheild.ai

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ScamShieldApi {

    @POST("analyze")
    fun analyzeMessage(
        @Body request: AnalyzeRequest
    ): Call<AnalyzeResponse>
}
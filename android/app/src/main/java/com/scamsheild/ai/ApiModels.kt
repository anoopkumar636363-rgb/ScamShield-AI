package com.scamsheild.ai

data class AnalyzeRequest(
    val source: String,
    val text: String
)

data class AnalyzeResponse(
    val risk_score: Int,
    val level: String,
    val category: String,
    val confidence: String,
    val reasons: List<String>,
    val explanation: String,
    val recommendation: String
)
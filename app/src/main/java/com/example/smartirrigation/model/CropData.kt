package com.example.smartirrigation.model

data class CropData(
    val cropName: String,
    val category: String,
    val soilType: String,
    val baseWaterMmPerSeason: Float,
    val adjustedWaterMmPerSeason: Float,
    val irrigationFrequencyMultiplier: Float,
    val source: String
)

/**
 * Represents user input for irrigation calculation
 */
data class IrrigationInput(
    val cropData: CropData? = null,
    val soilMoisture: Float = 50f, // Default to 50%
    val selectedSoilType: String = ""
)

/**
 * Represents irrigation recommendation result
 */
data class IrrigationRecommendation(
    val recommendedWaterMm: Float,
    val irrigationDurationMinutes: Int,
    val needsIrrigation: Boolean
)

package com.example.smartirrigation.repository

import android.content.Context
import android.util.Log
import com.example.smartirrigation.R
import com.example.smartirrigation.model.CropData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedReader
import java.io.InputStreamReader

class CropRepository(private val context: Context) {
    
    private val _cropData = mutableListOf<CropData>()
    val cropData: List<CropData> get() = _cropData
    
    // Get unique crop names for dropdown
    val uniqueCrops: List<String>
        get() = _cropData.map { it.cropName }.distinct().sorted()
    
    // Get unique soil types for dropdown
    val uniqueSoilTypes: List<String>
        get() = _cropData.map { it.soilType }.distinct().sorted()
    
    init {
        loadCsvData()
    }
    
    private fun loadCsvData() {
        try {
            val inputStream = context.resources.openRawResource(R.raw.smart_irrigation_crop_soil_dataset)
            val reader = BufferedReader(InputStreamReader(inputStream))
            
            // Skip header
            reader.readLine()
            
            reader.useLines { lines ->
                _cropData.addAll(
                    lines.mapNotNull { line ->
                        val columns = line.split(",")
                        if (columns.size >= 7) {
                            CropData(
                                cropName = columns[0].trim(),
                                category = columns[1].trim(),
                                soilType = columns[2].trim(),
                                baseWaterMmPerSeason = columns[3].trim().toFloatOrNull() ?: 0f,
                                adjustedWaterMmPerSeason = columns[4].trim().toFloatOrNull() ?: 0f,
                                irrigationFrequencyMultiplier = columns[5].trim().toFloatOrNull() ?: 1f,
                                source = columns.getOrNull(6)?.trim() ?: ""
                            )
                        } else null
                    }
                )
            }
            Log.d("CropRepository", "Loaded ${_cropData.size} crop data entries")
        } catch (e: Exception) {
            Log.e("CropRepository", "Error loading CSV data", e)
        }
    }
    
    fun getCropData(cropName: String, soilType: String): CropData? {
        return _cropData.find { it.cropName == cropName && it.soilType == soilType }
    }
    
    // Calculate irrigation recommendation based on crop data and soil moisture
    fun calculateIrrigation(
        cropData: CropData,
        currentSoilMoisture: Float
    ): Triple<Float, Int, Boolean> {
        // Simple irrigation logic - adjust based on your requirements
        val moistureDeficit = 100f - currentSoilMoisture // 0-100%
        val baseWaterNeeded = cropData.adjustedWaterMmPerSeason / 30f // Daily water need (simplified)
        
        // Adjust water need based on soil moisture (more moisture = less water needed)
        val adjustedWaterNeed = baseWaterNeeded * (moistureDeficit / 100f)
        
        // Convert mm to minutes of irrigation (simplified conversion)
        // Assuming 1mm = 1 minute for demonstration
        val irrigationMinutes = (adjustedWaterNeed * cropData.irrigationFrequencyMultiplier).toInt()
        
        // Only recommend irrigation if soil moisture is below 70%
        val needsIrrigation = currentSoilMoisture < 70f
        
        return Triple(adjustedWaterNeed, irrigationMinutes, needsIrrigation)
    }
}

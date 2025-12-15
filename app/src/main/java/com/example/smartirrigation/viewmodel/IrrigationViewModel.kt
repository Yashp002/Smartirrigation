package com.example.smartirrigation.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartirrigation.model.CropData
import com.example.smartirrigation.model.IrrigationInput
import com.example.smartirrigation.model.IrrigationRecommendation
import com.example.smartirrigation.repository.CropRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class IrrigationViewModel @Inject constructor(
    private val repository: CropRepository
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow<IrrigationUiState>(IrrigationUiState.Loading)
    val uiState: StateFlow<IrrigationUiState> = _uiState
    
    // Input state
    private val _selectedCategory = mutableStateOf("")
    val selectedCategory: State<String> = _selectedCategory
    
    private val _selectedCrop = mutableStateOf("")
    val selectedCrop: State<String> = _selectedCrop
    
    private val _selectedSoilType = mutableStateOf("")
    val selectedSoilType: State<String> = _selectedSoilType
    
    private val _soilMoisture = mutableStateOf(50f)
    val soilMoisture: State<Float> = _soilMoisture
    
    init {
        loadInitialData()
    }
    
    private fun loadInitialData() {
        viewModelScope.launch {
            // Simulate data loading
            kotlinx.coroutines.delay(500) // Small delay to show loading state
            
            // Get values for dropdowns
            val categories = repository.cropsByCategory.keys.toList().sorted()
            val soilTypes = repository.uniqueSoilTypes
            
            if (categories.isNotEmpty() && soilTypes.isNotEmpty()) {
                // Set default values
                val defaultCategory = if (categories.contains("Houseplant")) "Houseplant" else categories.first()
                val defaultCrop = repository.defaultCrop
                val defaultSoilType = repository.defaultSoilType
                
                // Get crops for the default category
                val cropsForCategory = repository.getCropsForCategory(defaultCategory)
                
                // Set initial selections
                _selectedCategory.value = defaultCategory
                _selectedCrop.value = defaultCrop
                _selectedSoilType.value = defaultSoilType
                
                _uiState.value = IrrigationUiState.Success(
                    categories = categories,
                    crops = cropsForCategory,
                    soilTypes = soilTypes,
                    recommendation = null,
                    currentCropData = repository.getCropData(defaultCrop, defaultSoilType)
                )
                
                // Trigger initial recommendation
                updateRecommendation()
            } else {
                _uiState.value = IrrigationUiState.Error("Failed to load crop data")
            }
        }
    }
    
    fun onCropSelected(crop: String) {
        _selectedCrop.value = crop
        updateRecommendation()
    }
    
    fun onSoilTypeSelected(soilType: String) {
        _selectedSoilType.value = soilType
        updateRecommendation()
    }
    
    fun onSoilMoistureChanged(moisture: Float) {
        _soilMoisture.value = moisture
        updateRecommendation()
    }
    
    private fun updateRecommendation() {
        val currentState = _uiState.value
        if (currentState !is IrrigationUiState.Success) return
        
        val crop = _selectedCrop.value
        val soilType = _selectedSoilType.value
        
        if (crop.isNotEmpty() && soilType.isNotEmpty()) {
            val cropData = repository.getCropData(crop, soilType)
            if (cropData != null) {
                val (waterNeeded, minutes, needsIrrigation) = 
                    repository.calculateIrrigation(cropData, _soilMoisture.value)
                
                val recommendation = IrrigationRecommendation(
                    recommendedWaterMm = waterNeeded,
                    irrigationDurationMinutes = minutes,
                    needsIrrigation = needsIrrigation
                )
                
                _uiState.value = currentState.copy(
                    recommendation = recommendation,
                    currentCropData = cropData
                )
                return
            }
        }
        
        // If we get here, clear the recommendation
        _uiState.value = currentState.copy(recommendation = null, currentCropData = null)
    }
}

// UI State
sealed class IrrigationUiState {
    object Loading : IrrigationUiState()
    data class Error(val message: String) : IrrigationUiState()
    data class Success(
        val categories: List<String>,
        val crops: List<String>,
        val soilTypes: List<String>,
        val recommendation: IrrigationRecommendation?,
        val currentCropData: CropData? = null
    ) : IrrigationUiState()
}

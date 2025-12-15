package com.example.smartirrigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartirrigation.ui.theme.SmartIrrigationTheme
import com.example.smartirrigation.viewmodel.IrrigationUiState
import com.example.smartirrigation.viewmodel.IrrigationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartIrrigationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SmartIrrigationApp()
                }
            }
        }
    }
}

@Composable
fun SmartIrrigationApp(
    viewModel: IrrigationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedCrop by viewModel.selectedCrop.collectAsStateWithLifecycle()
    val selectedSoilType by viewModel.selectedSoilType.collectAsStateWithLifecycle()
    val soilMoisture by viewModel.soilMoisture.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Irrigation") }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is IrrigationUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is IrrigationUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is IrrigationUiState.Success -> {
                MainContent(
                    state = state,
                    selectedCategory = selectedCategory,
                    selectedCrop = selectedCrop,
                    selectedSoilType = selectedSoilType,
                    soilMoisture = soilMoisture,
                    onCategorySelected = { viewModel.onCategorySelected(it) },
                    onCropSelected = { viewModel.onCropSelected(it) },
                    onSoilTypeSelected = { viewModel.onSoilTypeSelected(it) },
                    onSoilMoistureChanged = { viewModel.onSoilMoistureChanged(it) },
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    state: IrrigationUiState.Success,
    selectedCategory: String,
    selectedCrop: String,
    selectedSoilType: String,
    soilMoisture: Float,
    onCategorySelected: (String) -> Unit,
    onCropSelected: (String) -> Unit,
    onSoilTypeSelected: (String) -> Unit,
    onSoilMoistureChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedCrop by remember { mutableStateOf(false) }
    var expandedSoilType by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Category Dropdown
        ExposedDropdownMenuBox(
            expanded = expandedCategory,
            onExpandedChange = { expandedCategory = it }
        ) {
            TextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Category") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedCategory,
                onDismissRequest = { expandedCategory = false }
            ) {
                state.categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            onCategorySelected(category)
                            expandedCategory = false
                        }
                    )
                }
            }
        }

        // Crop Dropdown
        ExposedDropdownMenuBox(
            expanded = expandedCrop,
            onExpandedChange = { expandedCrop = it }
        ) {
            TextField(
                value = selectedCrop,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Crop/Plant") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCrop)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedCrop,
                onDismissRequest = { expandedCrop = false }
            ) {
                state.crops.forEach { crop ->
                    DropdownMenuItem(
                        text = { Text(crop) },
                        onClick = {
                            onCropSelected(crop)
                            expandedCrop = false
                        }
                    )
                }
            }
        }

        // Soil Type Dropdown
        ExposedDropdownMenuBox(
            expanded = expandedSoilType,
            onExpandedChange = { expandedSoilType = it }
        ) {
            TextField(
                value = selectedSoilType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Soil Type") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSoilType)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedSoilType,
                onDismissRequest = { expandedSoilType = false }
            ) {
                state.soilTypes.forEach { soilType ->
                    DropdownMenuItem(
                        text = { Text(soilType) },
                        onClick = {
                            onSoilTypeSelected(soilType)
                            expandedSoilType = false
                        }
                    )
                }
            }
        }

        // Soil Moisture Input
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Current Soil Moisture: ${soilMoisture.toInt()}%",
                    style = MaterialTheme.typography.titleMedium
                )
                Slider(
                    value = soilMoisture,
                    onValueChange = onSoilMoistureChanged,
                    valueRange = 0f..100f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Water Requirements Display
        if (selectedCrop.isNotEmpty() && selectedSoilType.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Irrigation Recommendations",
                        style = MaterialTheme.typography.titleLarge
                    )

                    state.selectedCropData?.let { cropData ->
                        Text(
                            text = "Base Water: ${cropData.baseWaterMmPerSeason} mm/season",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Adjusted Water: ${cropData.adjustedWaterMmPerSeason} mm/season",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Irrigation Frequency: ${cropData.irrigationFrequencyMultiplier}x",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

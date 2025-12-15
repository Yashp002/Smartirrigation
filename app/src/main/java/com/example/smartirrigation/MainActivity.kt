package com.example.smartirrigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartirrigation.ui.theme.SmartIrrigationTheme
import com.example.smartirrigation.viewmodel.IrrigationUiState
import com.example.smartirrigation.viewmodel.IrrigationViewModel

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
    viewModel: IrrigationViewModel = viewModel(
        factory = createViewModelFactory()
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            SmartIrrigationTopBar()
        }
    ) { padding ->
        when (val state = uiState) {
            is IrrigationUiState.Loading -> {
                LoadingScreen(modifier = Modifier.padding(padding))
            }

            is IrrigationUiState.Error -> {
                ErrorScreen(
                    message = state.message,
                    modifier = Modifier.padding(padding)
                )
            }

            is IrrigationUiState.Success -> {
                MainContent(
                    state = state,
                    onCropSelected = viewModel::onCropSelected,
                    onSoilTypeSelected = viewModel::onSoilTypeSelected,
                    onSoilMoistureChanged = viewModel::onSoilMoistureChanged,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun createViewModelFactory(): ViewModelProvider.Factory {
    val context = LocalContext.current
    return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(IrrigationViewModel::class.java)) {
                IrrigationViewModel(
                    context.applicationContext as android.app.Application
                ) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SmartIrrigationTopBar() {
    TopAppBar(
        title = { Text("Smart Irrigation") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
private fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorScreen(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Error: $message",
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MainContent(
    state: IrrigationUiState.Success,
    onCropSelected: (String) -> Unit,
    onSoilTypeSelected: (String) -> Unit,
    onSoilMoistureChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentSoilMoisture by remember { mutableStateOf(50f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CropDropdown(
            crops = state.crops,
            selectedCrop = state.currentCropData?.cropName,
            onCropSelected = onCropSelected
        )

        SoilTypeDropdown(
            soilTypes = state.soilTypes,
            selectedSoilType = state.currentCropData?.soilType,
            onSoilTypeSelected = onSoilTypeSelected
        )

        SoilMoistureCard(
            currentSoilMoisture = currentSoilMoisture,
            onSoilMoistureChanged = { newValue ->
                currentSoilMoisture = newValue
                onSoilMoistureChanged(newValue)
            }
        )

        RecommendationCard(
            recommendation = state.recommendation,
            currentCropData = state.currentCropData
        )

        Spacer(modifier = Modifier.weight(1f))

        FooterText()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CropDropdown(
    crops: List<String>,
    selectedCrop: String?,
    onCropSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            value = selectedCrop ?: "Select a crop",
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Crop") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            crops.forEach { crop ->
                DropdownMenuItem(
                    text = { Text(crop) },
                    onClick = {
                        onCropSelected(crop)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SoilTypeDropdown(
    soilTypes: List<String>,
    selectedSoilType: String?,
    onSoilTypeSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            value = selectedSoilType ?: "Select soil type",
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Soil Type") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            soilTypes.forEach { soilType ->
                DropdownMenuItem(
                    text = { Text(soilType) },
                    onClick = {
                        onSoilTypeSelected(soilType)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SoilMoistureCard(
    currentSoilMoisture: Float,
    onSoilMoistureChanged: (Float) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Current Soil Moisture: ${currentSoilMoisture.toInt()}%",
                style = MaterialTheme.typography.titleMedium
            )

            Slider(
                value = currentSoilMoisture,
                onValueChange = onSoilMoistureChanged,
                valueRange = 0f..100f,
                steps = 10,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0%")
                Text("50%")
                Text("100%")
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    recommendation: Any?,
    currentCropData: Any?
) {
    if (recommendation != null) {
        val needsIrrigation = getPropertyValue(recommendation, "needsIrrigation") as? Boolean ?: false

        val cardColor = if (needsIrrigation) {
            MaterialTheme.colorScheme.tertiaryContainer
        } else {
            MaterialTheme.colorScheme.secondaryContainer
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = cardColor)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (needsIrrigation) {
                        "Irrigation Recommended ✅"
                    } else {
                        "No Irrigation Needed ✋"
                    },
                    style = MaterialTheme.typography.headlineSmall
                )

                if (needsIrrigation) {
                    val waterMm = getPropertyValue(recommendation, "recommendedWaterMm") as? Number
                    val duration = getPropertyValue(recommendation, "irrigationDurationMinutes") as? Number

                    waterMm?.let {
                        Text(
                            text = "Water needed: ${"%.1f".format(it.toDouble())} mm",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    duration?.let {
                        Text(
                            text = "Irrigate for: ${it.toInt()} minutes",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    Text(
                        text = "Soil moisture is optimal. No irrigation needed at this time.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                currentCropData?.let { cropData ->
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = "Crop Info",
                        style = MaterialTheme.typography.titleMedium
                    )

                    val category = getPropertyValue(cropData, "category")
                    val baseWater = getPropertyValue(cropData, "baseWaterMmPerSeason")
                    val adjustedWater = getPropertyValue(cropData, "adjustedWaterMmPerSeason")

                    category?.let { Text("Category: $it") }
                    baseWater?.let { Text("Base Water Need: $it mm/season") }
                    adjustedWater?.let { Text("Adjusted Water Need: $it mm/season") }
                }
            }
        }
    } else {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = "Select a crop and soil type to get irrigation recommendations",
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun FooterText() {
    Text(
        text = "Smart Irrigation System\nPowered by Solar IoT",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

private fun getPropertyValue(obj: Any, propertyName: String): Any? {
    return try {
        obj.javaClass.getDeclaredField(propertyName).apply {
            isAccessible = true
        }.get(obj)
    } catch (e: Exception) {
        null
    }
}
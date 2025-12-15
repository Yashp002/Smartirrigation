# Smart Irrigation System

A solar-powered IoT solution for efficient agricultural irrigation management.

## 🌱 App Flow

1. **User Input**
   - Select crop type and soil type from dropdown menus
   - Adjust soil moisture level using the slider (simulated sensor input)

2. **Data Processing**
   - App loads crop water requirements from local CSV database
   - Calculates optimal irrigation based on:
     - Crop water needs
     - Soil type absorption rate
     - Current soil moisture level

3. **Recommendation Engine**
   - Determines if irrigation is needed
   - Calculates required water volume (mm)
   - Converts to pump runtime (minutes)

4. **Solar Integration**
   - Monitors solar power availability
   - Schedules irrigation during peak solar hours
   - Optimizes for energy efficiency

5. **IoT Communication**
   - Sends irrigation commands to controller
   - Receives real-time sensor data
   - Implements fail-safes for low power conditions

6. **User Feedback**
   - Displays clear irrigation recommendations
   - Shows water savings and system status
   - Provides historical data visualization

## 🎙️ 1-Minute Explanation

"This Smart Irrigation System helps farmers optimize water usage through IoT technology. The Android app serves as the control center, where you can select your crop and soil type. It uses simulated soil moisture data to calculate precise irrigation needs. The system is solar-powered, making it perfect for remote fields. When soil moisture drops below optimal levels, it triggers the water pump - but only when there's enough solar energy available. This ensures sustainable farming by conserving both water and energy, while maintaining healthy crop growth."

## 📊 System Architecture

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│                 │     │                 │     │                 │
│   Android App   │◄───►│  IoT Controller │◄───►│  Soil Moisture  │
│  (User Control  │     │ (Raspberry Pi/  │     │     Sensor      │
│    & Monitoring)│     │   ESP32/Arduino)│     │                 │
└────────┬────────┘     └────────┬────────┘     └─────────────────┘
         │                       │
         │                       │
         ▼                       ▼
┌─────────────────┐     ┌─────────────────┐
│                 │     │                 │
│  Cloud Storage  │     │  Water Pump &   │
│ (Optional Remote│     │   Valve Control │
│     Access)     │     │                 │
└─────────────────┘     └────────┬────────┘
                                 │
                                 │
                        ┌────────▼────────┐
                        │                 │
                        │   Solar Panel   │
                        │  & Power System │
                        │                 │
                        └─────────────────┘
```

### Key Components:

1. **Android App**
   - User interface for monitoring and control
   - Local data processing and storage
   - Real-time status updates

2. **IoT Controller**
   - Processes sensor data
   - Controls water pump
   - Manages power from solar panels

3. **Sensors**
   - Soil moisture sensors
   - Optional: Weather station integration
   - Solar power monitoring

4. **Solar Power System**
   - Solar panel array
   - Battery storage
   - Power management

## 🔧 Setup Instructions

1. Clone the repository
2. Open in Android Studio
3. Add your `smart_irrigation_crop_soil_dataset.csv` to `app/src/main/res/raw/`
4. Build and run on an Android device

## 🌍 Environmental Impact

- Reduces water usage by up to 50% compared to traditional irrigation
- Solar-powered operation reduces carbon footprint
- Prevents over-watering and nutrient leaching

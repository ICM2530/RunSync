package com.example.runsyncmockups.model

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TemperatureState(
    val currentTemperature: Float = 0f,
    val isHot: Boolean = false,
    val isCold: Boolean = false,
    val sensorAvailable: Boolean = true
)

class TemperatureViewModel : ViewModel(), SensorEventListener {

    private val _temperatureState = MutableStateFlow(TemperatureState())
    val temperatureState: StateFlow<TemperatureState> = _temperatureState.asStateFlow()

    private var sensorManager: SensorManager? = null
    private var temperatureSensor: Sensor? = null

    // Umbrales de temperatura (en Celsius)
    private val HOT_TEMPERATURE_THRESHOLD = 30f
    private val COLD_TEMPERATURE_THRESHOLD = 10f

    fun initializeSensor(context: Context) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        temperatureSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

        if (temperatureSensor == null) {
            _temperatureState.value = _temperatureState.value.copy(sensorAvailable = false)
        }
    }

    fun startListening() {
        temperatureSensor?.let { sensor ->
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stopListening() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val temperature = event.values[0]
        _temperatureState.value = _temperatureState.value.copy(
            currentTemperature = temperature,
            isHot = temperature > HOT_TEMPERATURE_THRESHOLD,
            isCold = temperature < COLD_TEMPERATURE_THRESHOLD
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No se requiere acción
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}


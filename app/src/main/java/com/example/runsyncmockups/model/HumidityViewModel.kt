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

data class HumidityState(
    val currentHumidity: Float = 0f,
    val averageHumidity: Float = 0f,
    val willLikelyRain: Boolean = false,
    val sensorAvailable: Boolean = true,
    val hasReceivedFirstReading: Boolean = false,
)

class HumidityViewModel : ViewModel(), SensorEventListener {

    private val _humidityState = MutableStateFlow(HumidityState())
    val humidityState: StateFlow<HumidityState> = _humidityState.asStateFlow()
    private var sensorManager: SensorManager? = null
    private var humiditySensor: Sensor? = null
    private var rainLikelyThreshold = 90f

    fun initializeSensor(context: Context) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        humiditySensor = sensorManager?.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)

        if (humiditySensor == null) {
            _humidityState.value = _humidityState.value.copy(sensorAvailable = false)
        }
    }

    fun startListening() {
        humiditySensor?.let { sensor ->
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stopListening() {
        sensorManager?.unregisterListener(this)
    }



    fun evaluateReading(humidity: Float) {

        val willLikelyRain = willItLikelyRain(humidity)

        _humidityState.value = _humidityState.value.copy(
            currentHumidity = humidity,
            averageHumidity = humidity,
            willLikelyRain = willLikelyRain,
            hasReceivedFirstReading = true,
        )
    }

    fun willItLikelyRain(humidity: Float): Boolean = humidity >= rainLikelyThreshold

    override fun onSensorChanged(event: SensorEvent?) {

        if (event == null) return
        if (event.sensor.type != Sensor.TYPE_RELATIVE_HUMIDITY) return
        if (event.values.isEmpty()) return

        val latest = event.values[0]

        evaluateReading(latest)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}

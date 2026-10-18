package com.example.myapplication.classes.Bonus

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

object TiltController {
    var enabled = false
        private set

    // >0: жуков тянет вправо / вниз по экрану
    var ax = 0f
        private set
    var ay = 0f
        private set

    private var sensorManager: SensorManager? = null

    private val listener = object : SensorEventListener {
        override fun onSensorChanged(e: SensorEvent) {
            val g = SensorManager.GRAVITY_EARTH
            val targetX = (-e.values[0] / g).coerceIn(-1f, 1f)
            val targetY = (e.values[1] / g).coerceIn(-1f, 1f)
            // low-pass фильтр, чтобы жуки не дёргались
            ax += 0.2f * (targetX - ax)
            ay += 0.2f * (targetY - ay)
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    fun start(context: Context) {
        if (enabled) return
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val acc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) ?: return
        sm.registerListener(listener, acc, SensorManager.SENSOR_DELAY_GAME)
        sensorManager = sm
        enabled = true
    }

    fun stop() {
        sensorManager?.unregisterListener(listener)
        sensorManager = null
        enabled = false
        ax = 0f
        ay = 0f
    }
}
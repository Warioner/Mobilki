package com.example.myapplication.classes.Bugs

import android.content.Context
import androidx.annotation.DrawableRes
import kotlin.random.Random

class HardBug(
    context: Context,
    initialX: Float,
    initialY: Float,
    size: Int,
    speed: Float,
    initialDead: Boolean,
    points: Int,
    imageRes: Int,
    weight: Int = 1000,
) : Bug(context, initialX, initialY, size, speed, initialDead, points, imageRes, weight) {
    private enum class State{WALK, PAUSE, DASH}
    private var state = State.WALK
    private var stateFramesLeft = randomDuration(State.WALK)
    private var angle = Random.nextDouble() * 2.0 * Math.PI

    init {
        updateVelocity()
    }
    override fun move(
        boardWidth: Int,
        boardHeight: Int,
        speedMultiplier: Float,
    ) {
        if (dead) return
        if (boardWidth <= size || boardHeight <= size) return
        updateState()

        val stateSpeed = when (state) {
            State.WALK -> 1f
            State.PAUSE -> 0f
            State.DASH -> 2.5f
        }
        val effectiveSpeed = speed * speedMultiplier.coerceAtLeast(0f) * stateSpeed

        when (state) {
            State.WALK -> {
                // плавное блуждание: небольшой случайный поворот каждый кадр
                angle += Random.nextDouble(-0.15, 0.15)
                updateVelocity()
            }
            State.PAUSE -> { /* стоим */ }
            State.DASH -> { /* летим по прямой */ }
        }

        x += vx * effectiveSpeed
        y += vy * effectiveSpeed

        applyBoundsAndDraw(boardWidth, boardHeight)

        // после отскока базовый класс мог изменить vx/vy — синхронизируем угол
        angle = Math.atan2(vy.toDouble(), vx.toDouble())
        view.rotation = Math.toDegrees(angle).toFloat() + 90f
    }

    private fun updateState() {
        if (--stateFramesLeft > 0) return

        state = when (state) {
            State.WALK -> if (Random.nextFloat() < 0.5f) State.PAUSE else State.DASH
            State.PAUSE -> State.DASH
            State.DASH -> State.WALK
        }
        if (state == State.DASH) {
            // перед рывком резко выбираем новое направление
            angle = Random.nextDouble() * 2.0 * Math.PI
            updateVelocity()
        }
        stateFramesLeft = randomDuration(state)
    }

    private fun randomDuration(s: State): Int = when (s) {
        State.WALK -> Random.nextInt(40, 100)
        State.PAUSE -> Random.nextInt(15, 40)
        State.DASH -> Random.nextInt(10, 25)
    }

    private fun updateVelocity() {
        vx = Math.cos(angle).toFloat()
        vy = Math.sin(angle).toFloat()
    }
}
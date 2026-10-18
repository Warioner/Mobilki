package com.example.myapplication.classes.Bugs

import android.content.Context
import com.example.myapplication.classes.Bonus.TiltController
import androidx.annotation.DrawableRes
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class EasyBug(
    context: Context,
    initialX: Float,
    initialY: Float,
    size: Int,
    speed: Float,
    initialDead: Boolean,
    points: Int,
    imageRes: Int,
    weight: Int = 100,
) : Bug(context, initialX, initialY, size, speed, initialDead, points, imageRes, weight) {
    init {
        // Задаём случайное начальное направление
        val angle = Random.nextDouble() * 2.0 * Math.PI
        vx = cos(angle).toFloat()
        vy = sin(angle).toFloat()
    }

    override fun move(
        boardWidth: Int,
        boardHeight: Int,
        speedMultiplier: Float,
        activeBonuses : BooleanArray
    ) {
        if (dead) return
        if (boardWidth <= size || boardHeight <= size) return

        if(TiltController.enabled){
            applyTilt()
        }
        val effectiveSpeed = speed * speedMultiplier.coerceAtLeast(0f)

        x += vx * effectiveSpeed
        y += vy * effectiveSpeed

        applyBoundsAndDraw(boardWidth, boardHeight)

        updateRotation()
    }
    private fun updateRotation() {
        val angle = Math.atan2(vy.toDouble(), vx.toDouble())
        view.rotation = Math.toDegrees(angle).toFloat() + 90f
    }
}
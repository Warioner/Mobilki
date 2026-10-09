package com.example.myapplication.classes.Bugs

import android.content.Context
import androidx.annotation.DrawableRes
import kotlin.random.Random

class NormalBug(
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
    private var t = Random.nextFloat() * 6.28f
    init {
        val angle = Random.nextDouble() * 2.0 * Math.PI
        vx = Math.cos(angle).toFloat()
        vy = Math.sin(angle).toFloat()
    }

    override fun move(boardWidth: Int, boardHeight: Int, speedMultiplier: Float) {
        if (dead) return
        if (boardWidth <= size || boardHeight <= size) return

        t += 0.15f
        val s = speed * speedMultiplier.coerceAtLeast(0f)

        // перпендикуляр к направлению (-vy, vx) даёт боковое покачивание
        val wobble = Math.sin(t.toDouble()).toFloat() * 0.8f
        val dx = vx - vy * wobble
        val dy = vy + vx * wobble

        x += dx * s
        y += dy * s

        applyBoundsAndDraw(boardWidth, boardHeight)

        val heading = Math.atan2(dy.toDouble(), dx.toDouble())
        view.rotation = Math.toDegrees(heading).toFloat() + 90f
    }
}
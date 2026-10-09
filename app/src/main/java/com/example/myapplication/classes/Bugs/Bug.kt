package com.example.myapplication.classes.Bugs

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.DrawableRes

open class Bug(
    context: Context,
    initialX: Float,
    initialY: Float,
    size: Int,
    speed: Float,
    initialDead: Boolean,
    points: Int,
    imageRes: Int,
    weight: Int = 100,
) {
    // Свойства с публичным геттером и protected сеттером
    var x: Float = initialX
        protected set
    var y: Float = initialY
        protected set
    var dead: Boolean = initialDead
        protected set

    // Свойства только для чтения
    val size: Int = size
    protected val speed: Float = speed
    val points: Int = points
    val weight: Int = weight

    private var imageRes: Int = imageRes

    val view: ImageView = ImageView(context)

    var rHitbox: Float = size / 2f * 1.5f

    protected var vx = 0f
    protected var vy = 0f

    init {
        view.setImageResource(imageRes)
        view.scaleType = ImageView.ScaleType.FIT_CENTER
        view.layoutParams = FrameLayout.LayoutParams(size, size)
        view.translationX = x
        view.translationY = y
    }

    fun setImage(res: Int) {
        imageRes = res
        view.setImageResource(res)
    }

    fun getImageRes(): Int = imageRes

    fun markAsDead() {
        dead = true
        view.visibility = View.GONE
    }

    protected fun applyBoundsAndDraw(boardWidth: Int, boardHeight: Int) {
        val maxX = (boardWidth - size).toFloat()
        val maxY = (boardHeight - size).toFloat()

        if (x <= 0f) {
            x = 0f
            vx = Math.abs(vx)
        } else if (x >= maxX) {
            x = maxX
            vx = -Math.abs(vx)
        }

        if (y <= 0f) {
            y = 0f
            vy = Math.abs(vy)
        } else if (y >= maxY) {
            y = maxY
            vy = -Math.abs(vy)
        }

        view.translationX = x
        view.translationY = y
    }

    open fun move(
        boardWidth: Int,
        boardHeight: Int,
        speedMultiplier: Float,
    ) {
    }

    fun contains(touchX: Float, touchY: Float): Boolean {
        if (dead) return false

        val centerX = x + size / 2f
        val centerY = y + size / 2f

        val dx = touchX - centerX
        val dy = touchY - centerY

        return dx * dx + dy * dy <= rHitbox * rHitbox
    }
}
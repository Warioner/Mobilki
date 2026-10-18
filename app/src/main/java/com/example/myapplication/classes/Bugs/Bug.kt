package com.example.myapplication.classes.Bugs

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.myapplication.R
import com.example.myapplication.classes.Bonus.TiltController
import com.example.myapplication.classes.SoundManager
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random
import androidx.annotation.DrawableRes

abstract class Bug(
    context: Context,
    initialX: Float,
    initialY: Float,
    size: Int,
    speed: Float,
    initialDead: Boolean,
    points: Int,
    imageRes: Int,
    weight: Int = 100,
    private val screamRes: Int = R.raw.sounds,
) {
    var x: Float = initialX
        protected set
    var y: Float = initialY
        protected set
    var dead: Boolean = initialDead
        protected set

    val size: Int = size
    protected val speed: Float = speed
    var points: Int = points
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

    /** vx, vy - направление (длина ~1), реальную скорость задаёт speed. */
    private var lastScreamTime = 0L

    /** Крик конкретного жука (не чаще раза в 400 мс, чтобы не было каши). */
    fun scream() {
        val now = System.currentTimeMillis()
        if (now - lastScreamTime < 400) return
        lastScreamTime = now
        // немного разная высота тона, чтобы жуки звучали по-разному
        val rate = 0.8f + (weight % 5) * 0.1f
        SoundManager.play(screamRes, volume = 0.6f, rate = rate)
    }

    protected fun applyTilt() {
        if (!TiltController.enabled) return

        vx += TiltController.ax * TILT_ACCEL
        vy += TiltController.ay * TILT_ACCEL

        vx *= 0.99f
        vy *= 0.99f

        val len = hypot(vx, vy)
        if (len > MAX_TILT_SPEED) {
            vx = vx / len * MAX_TILT_SPEED
            vy = vy / len * MAX_TILT_SPEED
        }
    }

    fun restoreNormalSpeed() {
        val len = hypot(vx, vy)
        if (len > 0.001f) {
            vx /= len
            vy /= len
        } else {
            val a = Random.nextDouble() * 2.0 * Math.PI
            vx = cos(a).toFloat()
            vy = sin(a).toFloat()
        }
    }
    protected fun applyBoundsAndDraw(boardWidth: Int, boardHeight: Int) {
        val maxX = (boardWidth - size).toFloat()
        val maxY = (boardHeight - size).toFloat()

        // при наклоне удар о стенку гасит скорость -> жуки скапливаются в углу
        val bounce = if (TiltController.enabled) 0.3f else 1f
        var hardHit = false

        if (x <= 0f) {
            x = 0f
            if (abs(vx) > HIT_THRESHOLD) hardHit = true
            vx = abs(vx) * bounce
        } else if (x >= maxX) {
            x = maxX
            if (abs(vx) > HIT_THRESHOLD) hardHit = true
            vx = -abs(vx) * bounce
        }

        if (y <= 0f) {
            y = 0f
            if (abs(vy) > HIT_THRESHOLD) hardHit = true
            vy = abs(vy) * bounce
        } else if (y >= maxY) {
            y = maxY
            if (abs(vy) > HIT_THRESHOLD) hardHit = true
            vy = -abs(vy) * bounce
        }

        // кричим только при сильном ударе во время бонуса
        if (hardHit && TiltController.enabled) scream()

        view.translationX = x
        view.translationY = y
    }
    open fun move(boardWidth: Int, boardHeight: Int, speedMultiplier: Float, activeBonuses: BooleanArray){
    }

    fun contains(touchX: Float, touchY: Float): Boolean {
        if (dead) return false

        val centerX = x + size / 2f
        val centerY = y + size / 2f

        val dx = touchX - centerX
        val dy = touchY - centerY

        return dx * dx + dy * dy <= rHitbox * rHitbox
    }
    companion object {
        const val TILT_ACCEL = 0.05f      // как быстро жуки разгоняются; подберите
        const val MAX_TILT_SPEED = 3f     // максимум длины вектора направления
        const val HIT_THRESHOLD = 0.8f    // порог "сильного удара" для крика
    }
}
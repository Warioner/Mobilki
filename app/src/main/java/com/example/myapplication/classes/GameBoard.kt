package com.example.myapplication.classes

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import com.example.myapplication.R
import com.example.myapplication.classes.Bonus.Bonus
import com.example.myapplication.classes.Bugs.Bug
import com.example.myapplication.classes.Bugs.EasyBug
import com.example.myapplication.classes.Bugs.NormalBug
import com.example.myapplication.classes.Bugs.HardBug
import kotlin.random.Random

class GameBoard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var onScoreChanged: ((Int) -> Unit)? = null
    var onTimeChanged: ((Int) -> Unit)? = null
    var onGameFinished: (() -> Unit)? = null

    val bugs = mutableListOf<Bug>()
    val spawnedBonuses = mutableListOf<Bonus>()
    val activeBonuses = BooleanArray(2) { false }

    var points = 0
        private set

    var countBugs = 0
        private set

    private var settings: SettingsGame? = null

    private val handler = Handler(Looper.getMainLooper())
    private var running = false
    private var spawned = 0
    private var timeLeft = 0

    private val initialDelayMs = 2500L

    private data class BugConfig(
        val sizeDp: Int,
        val weight: Int,
        val create: (x: Float, y: Float, sizePx: Int) -> Bug
    )

    private val bugConfigs = listOf(
        BugConfig(128, 100) { x, y, s -> EasyBug(context, x, y, s, 15f, false, 15, R.drawable.booooss_niggersov) },
        BugConfig(128, 100) { x, y, s -> EasyBug(context, x, y, s, 10f, false, 15, R.drawable.misha_bug) },
        BugConfig(128, 100) { x, y, s -> NormalBug(context, x, y, s, 50f, false, 30, R.drawable.dimas_ladybug) },
        BugConfig(128, 100) { x, y, s -> HardBug(context, x, y, s, 100f, false, 50, R.drawable.anton_pchelka) },
        BugConfig(128, 100) { x, y, s -> HardBug(context, x, y, s, 100f, false, 50, R.drawable.katya_gus) },
        BugConfig(128, 100) { x, y, s -> HardBug(context, x, y, s, 100f, false, 50, R.drawable.bog) }
    )

    private fun randomBugConfig(): BugConfig {
        val totalWeight = bugConfigs.sumOf { it.weight }
        var random = Random.nextInt(totalWeight)
        for (cfg in bugConfigs) {
            random -= cfg.weight
            if (random < 0) return cfg
        }
        return bugConfigs.last()
    }

    private val bonusConfigs = listOf(
        100 to { x: Float, y: Float, sizePx: Int, onActivate: (Int) -> Unit ->
            Bonus(context, onActivate, x, y, sizePx, R.drawable.anton, 100, timeWork = 15000)
        }
    )

    private fun randomBonusFactory(): (Float, Float, Int, (Int) -> Unit) -> Bonus {
        val totalWeight = bonusConfigs.sumOf { it.first }
        var random = Random.nextInt(totalWeight)
        for ((weight, factory) in bonusConfigs) {
            random -= weight
            if (random < 0) return factory
        }
        return bonusConfigs.last().second
    }
    private fun activateTiltBonus(durationMs: Int) {
        activeBonuses[0] = true

        handler.postDelayed({
            activeBonuses[0] = false
        }, durationMs.toLong())
    }

    private val startGameRunnable = object : Runnable {
        override fun run() {
            if (settings == null) return
            running = true
            handler.post(frameRunnable)
            handler.post(spawnRunnable)
            handler.post(spawnBonusRunnable)
            handler.postDelayed(timerRunnable, 1000L)
        }
    }

    private val frameRunnable = object : Runnable {
        override fun run() {
            if (!running) return

            val speedMultiplier = (settings?.speedGame?.toFloat() ?: 100f) / 100f

            for (bug in bugs) {
                bug.move(width, height, speedMultiplier, activeBonuses)

            }

            handler.postDelayed(this, 10L)
        }
    }

    private val spawnRunnable = object : Runnable {
        override fun run() {
            if (!running) return
            if (spawned < countBugs) {
                spawnBug()
                spawned++
            }
            val delayMs = Random.nextLong(500L, 3000L)
            handler.postDelayed(this, delayMs)
        }
    }

    private val spawnBonusRunnable = object : Runnable {
        override fun run() {
            if (!running) return
            spawnBonus()
            val delayMs = (settings?.bonusTime?.toLong() ?: 15L) * 1000L
            handler.postDelayed(this, delayMs)
        }
    }

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (!running) return
            timeLeft--
            onTimeChanged?.invoke(timeLeft)
            if (timeLeft <= 0) {
                stop()
                onGameFinished?.invoke()
                return
            }
            handler.postDelayed(this, 1000L)
        }
    }

    fun setSettings(settings: SettingsGame) { this.settings = settings }

    fun start(settings: SettingsGame) {
        setSettings(settings)
        start()
    }

    fun start() {
        val s = settings ?: return
        stop()
        removeAllViews()
        bugs.clear()
        spawnedBonuses.clear()

        for (i in activeBonuses.indices) {
            activeBonuses[i] = false
        }

        points = 0
        countBugs = s.countBug.coerceAtLeast(0)
        spawned = 0
        timeLeft = s.timeRound.coerceAtLeast(1)
        running = false

        onScoreChanged?.invoke(points)
        onTimeChanged?.invoke(timeLeft)
        handler.postDelayed(startGameRunnable, initialDelayMs)
    }

    fun stop() {
        running = false
        handler.removeCallbacksAndMessages(null)
    }

    private fun spawnBug() {
        val cfg = randomBugConfig()
        val density = resources.displayMetrics.density
        val sizePx = (cfg.sizeDp * density).toInt()
        val maxX = (width - sizePx).toFloat()
        val maxY = (height - sizePx).toFloat()
        if (maxX <= 0f || maxY <= 0f) return
        val x = Random.nextFloat() * maxX
        val y = Random.nextFloat() * maxY
        val bug = cfg.create(x, y, sizePx)
        bugs.add(bug)
        addView(bug.view)
    }

    private fun spawnBonus() {
        val density = resources.displayMetrics.density
        val sizePx = (64 * density).toInt()
        val maxX = (width - sizePx).toFloat()
        val maxY = (height - sizePx).toFloat()
        if (maxX <= 0f || maxY <= 0f) return

        val x = Random.nextFloat() * maxX
        val y = Random.nextFloat() * maxY

        val factory = randomBonusFactory()
        val bonus = factory(x, y, sizePx) { duration ->
            activateTiltBonus(duration)
        }

        spawnedBonuses.add(bonus)
        addView(bonus.view)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!running) return super.onTouchEvent(event)

        if (event.action == MotionEvent.ACTION_DOWN) {
            var hit = false

            for (i in bugs.indices.reversed()) {
                val bug = bugs[i]
                if (!bug.dead && bug.contains(event.x, event.y)) {
                    points += bug.points
                    bug.markAsDead()
                    spawned--
                    bugs.removeAt(i)
                    removeView(bug.view)
                    onScoreChanged?.invoke(points)
                    hit = true
                    break
                }
            }

            if (!hit) {
                for (i in spawnedBonuses.indices.reversed()) {
                    val bonus = spawnedBonuses[i]
                    val w = bonus.view.width.toFloat()
                    val h = bonus.view.height.toFloat()
                    if (w <= 0f || h <= 0f) continue

                    if (event.x in bonus.x..(bonus.x + w) && event.y in bonus.y..(bonus.y + h)) {
                        spawnedBonuses.removeAt(i)
                        removeView(bonus.view)
                        bonus.activate()
                        hit = true
                        break
                    }
                }
            }

            if (!hit) {
                points -= 100 * (settings?.userDifficulty ?: 1)
                onScoreChanged?.invoke(points)
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    // Добавлено для исправления предупреждения линтера
    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onDetachedFromWindow() {
        stop()
        super.onDetachedFromWindow()
    }
}
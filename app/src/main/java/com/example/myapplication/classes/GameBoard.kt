package com.example.myapplication.classes

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import com.example.myapplication.R
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

    var points = 0
        private set

    var countBugs = 0
        private set

    private var settings: SettingsGame? = null

    // --- Таймеры и состояние ---
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
        BugConfig(sizeDp = 128, weight = 100) { x, y, sizePx ->
            EasyBug(context, x, y, sizePx, 15f, false, 10, R.drawable.booooss_niggersov)
        },
        BugConfig(sizeDp = 128, weight = 100) { x, y, sizePx ->
            EasyBug(context, x, y, sizePx, 10f, false, 15, R.drawable.misha_bug)
        },
        BugConfig(sizeDp = 128, weight = 100) { x, y, sizePx ->
            NormalBug(context, x, y, sizePx, 50f, false, 30, R.drawable.dimas_ladybug)
        },
        BugConfig(sizeDp = 128, weight = 100) { x, y, sizePx ->
            HardBug(context, x, y, sizePx, 100f, false, 50, R.drawable.anton_pchelka)
        },
        BugConfig(sizeDp = 128, weight = 100) { x, y, sizePx ->
            HardBug(context, x, y, sizePx, 100f, false, 50, R.drawable.katya_gus)
        },
        BugConfig(sizeDp = 128, weight = 100) { x, y, sizePx ->
            HardBug(context, x, y, sizePx, 100f, false, 50, R.drawable.bog)
        }

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

    // --- Runnable для игрового цикла ---

    private val startGameRunnable = object : Runnable {
        override fun run() {
            if (settings == null) return

            running = true

            handler.post(frameRunnable)
            handler.post(spawnRunnable)
            handler.postDelayed(timerRunnable, 1000L)
        }
    }

    private val frameRunnable = object : Runnable {
        override fun run() {
            if (!running) return

            val speedMultiplier = (settings?.speedGame?.toFloat() ?: 100f) / 100f

            for (bug in bugs) {
                bug.move(width, height, speedMultiplier)
            }

            handler.postDelayed(this, 10L)
        }
    }

    private val spawnRunnable = object : Runnable {
        override fun run() {
            if (!running) return

            if (spawned < countBugs)
            {
                spawnBug()
                spawned++

            }

            val randomDelay = Random.nextLong(300L, 1500L)
            handler.postDelayed(this, randomDelay)
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

    // --- Публичные методы ---

    fun setSettings(settings: SettingsGame) {
        this.settings = settings
    }

    fun start(settings: SettingsGame) {
        setSettings(settings)
        start()
    }

    fun start() {
        val s = settings ?: return

        stop()

        removeAllViews()
        bugs.clear()

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

    // --- Спавн жуков ---

    private fun spawnBug() {
        // 1. Выбираем тип жука с учётом весов
        val cfg = randomBugConfig()

        // 2. Конвертируем dp в пиксели
        val density = resources.displayMetrics.density
        val sizePx = (cfg.sizeDp * density).toInt()

        // 3. Случайные координаты внутри поля
        val maxX = (width - sizePx).toFloat()
        val maxY = (height - sizePx).toFloat()

        if (maxX <= 0f || maxY <= 0f) return

        val x = Random.nextFloat() * maxX
        val y = Random.nextFloat() * maxY

        // 4. Лямбда из конфига создаёт жука нужного класса
        val bug = cfg.create(x, y, sizePx)

        bugs.add(bug)
        addView(bug.view)
    }


    // --- Обработка касаний ---

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!running) {
            return super.onTouchEvent(event)
        }

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
                points -= 100*(settings?.userDifficulty?:1)
                onScoreChanged?.invoke(points)
            }

            return true
        }

        return super.onTouchEvent(event)
    }

    // --- Очистка при уничтожении ---

    override fun onDetachedFromWindow() {
        stop()
        super.onDetachedFromWindow()
    }
}
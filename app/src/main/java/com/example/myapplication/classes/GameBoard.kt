package com.example.myapplication.classes

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import com.example.myapplication.R
import com.example.myapplication.classes.Bonus.Bonus
import com.example.myapplication.classes.Bonus.TiltController
import com.example.myapplication.classes.Bugs.Bug
import com.example.myapplication.classes.Bugs.EasyBug
import com.example.myapplication.classes.Bugs.HardBug
import com.example.myapplication.classes.Bugs.NormalBug
import com.example.myapplication.widget.GoldRateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
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
    val specialBugs = mutableListOf<Bug>()
    val spawnedBonuses = mutableListOf<Bonus>()
    val activeBonuses = BooleanArray(1) { false }

    var points = 0
        private set

    var countBugs = 0
        private set

    private lateinit var settings: SettingsGame

    private val viewScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val handler = Handler(Looper.getMainLooper())

    private var running = false
    private var spawned = 0
    private var timeLeft = 0

    private val initialDelayMs = 1500L
    private var currentGoldPoints: Int = 1000

    private data class BugConfig(
        val sizeDp: Int,
        val weight: Int,
        val create: (x: Float, y: Float, sizePx: Int) -> Bug
    )

    private val bugConfigs = listOf(
        BugConfig(128, 100) { x, y, s -> EasyBug(context, x, y, s, 15f, false, 15, R.drawable.booooss_niggersov, R.raw.scream) },
        BugConfig(128, 100) { x, y, s -> EasyBug(context, x, y, s, 10f, false, 15, R.drawable.misha_bug, R.raw.scream) },
        BugConfig(128, 100) { x, y, s -> NormalBug(context, x, y, s, 50f, false, 30, R.drawable.dimas_ladybug, R.raw.scream) },
        BugConfig(128, 100) { x, y, s -> HardBug(context, x, y, s, 100f, false, 50, R.drawable.anton_pchelka, R.raw.scream) },
        BugConfig(128, 100) { x, y, s -> HardBug(context, x, y, s, 100f, false, 50, R.drawable.katya_gus, R.raw.scream) },
        BugConfig(128, 100) { x, y, s -> HardBug(context, x, y, s, 100f, false, 50, R.drawable.bog, R.raw.scream) }
    )
    private val goldBugConfig = BugConfig(128, 0) { x, y, s ->
        HardBug(context, x, y, s, 10f, false, currentGoldPoints, R.drawable.gold, R.raw.scream)
    }

    private fun randomBugConfig(): BugConfig {
        val totalWeight = bugConfigs.sumOf { it.weight }
        if (totalWeight <= 0) return bugConfigs.last()

        var random = Random.nextInt(totalWeight)
        for (cfg in bugConfigs) {
            random -= cfg.weight
            if (random < 0) return cfg
        }
        return bugConfigs.last()
    }

    private data class BonusConfig(
        val sizeDp: Int,
        val weight: Int,
        val create: (x: Float, y: Float, sizePx: Int) -> Bonus
    )

    private val bonusConfigs = listOf(
        BonusConfig(16, 100) { x, y, size -> Bonus(context, { dur -> activateTiltBonus(dur) }, x, y, size, 15, R.drawable.anton) }
    )

    private fun randomBonusFactory(): BonusConfig {
        val totalWeight = bonusConfigs.sumOf { it.weight }
        if (totalWeight <= 0) return bonusConfigs.last()

        var random = Random.nextInt(totalWeight)
        for (bonus in bonusConfigs) {
            random -= bonus.weight
            if (random < 0) return bonus
        }
        return bonusConfigs.last()
    }

    private fun getRandomPosition(sizePx: Int): Pair<Float, Float>? {
        val maxX = (width - sizePx).toFloat()
        val maxY = (height - sizePx).toFloat()
        if (maxX <= 0f || maxY <= 0f) return null
        return Random.nextFloat() * maxX to Random.nextFloat() * maxY
    }

    private val tiltEndRunnable = Runnable { endTiltBonus() }

    private fun activateTiltBonus(duration: Int) {
        activeBonuses[0] = true
        TiltController.start(context)
        handler.removeCallbacks(tiltEndRunnable)
        handler.postDelayed(tiltEndRunnable, duration * 1000L)
    }

    private fun endTiltBonus() {
        activeBonuses[0] = false
        TiltController.stop()
        bugs.forEach { it.restoreNormalSpeed() }
        specialBugs.forEach { it.restoreNormalSpeed() }
    }

    private val startGameRunnable = object : Runnable {
        override fun run() {
            if (!::settings.isInitialized) return
            running = true
            viewScope.launch {
                val rate = GoldRateRepository().loadRate().toInt()
                if (rate > 0) currentGoldPoints = rate
            }

            handler.post(frameRunnable)
            handler.post(spawnRunnable)
            handler.post(spawnBonusRunnable)
            handler.postDelayed(spawnGoldBugRunnable, 10_000L)
            handler.postDelayed(timerRunnable, 1000L)
        }
    }

    private val frameRunnable = object : Runnable {
        override fun run() {
            if (!running || !::settings.isInitialized) return

            val speedMultiplier = settings.speedGame.toFloat() / 100f

            for (bug in bugs) {
                bug.move(width, height, speedMultiplier, activeBonuses)
            }
            for (bug in specialBugs) {
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
    private val spawnGoldBugRunnable = object : Runnable {
        override fun run() {
            if (!running) return
            spawnGoldBug()
            handler.postDelayed(this, 20_000L)
        }
    }

    private val spawnBonusRunnable = object : Runnable {
        override fun run() {
            if (!running || !::settings.isInitialized) return
            spawnBonus()
            val delayMs = settings.bonusTime.toLong() * 1000L
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

    fun setSettings(settings: SettingsGame) {
        this.settings = settings
    }

    fun start(settings: SettingsGame) {
        setSettings(settings)
        stop()
        removeAllViews()
        bugs.clear()
        specialBugs.clear()
        spawnedBonuses.clear()

        for (i in activeBonuses.indices) {
            activeBonuses[i] = false
        }

        points = 0
        countBugs = settings.countBug.coerceAtLeast(0)
        spawned = 0
        timeLeft = settings.timeRound.coerceAtLeast(1)
        running = false

        onScoreChanged?.invoke(points)
        onTimeChanged?.invoke(timeLeft)
        handler.postDelayed(startGameRunnable, initialDelayMs)
    }

    fun stop() {
        running = false
        handler.removeCallbacksAndMessages(null)
        activeBonuses[0] = false
        TiltController.stop()
    }

    private fun spawnBug() {
        val cfg = randomBugConfig()
        val density = resources.displayMetrics.density
        val sizePx = (cfg.sizeDp * density).toInt()

        val pos = getRandomPosition(sizePx) ?: return
        val bug = cfg.create(pos.first, pos.second, sizePx)

        bugs.add(bug)
        addView(bug.view)
    }

    private fun spawnGoldBug() {
        val density = resources.displayMetrics.density
        val sizePx = (goldBugConfig.sizeDp * density).toInt()

        val pos = getRandomPosition(sizePx) ?: return
        val bug = goldBugConfig.create(pos.first, pos.second, sizePx)

        specialBugs.add(bug)
        addView(bug.view)
    }

    private fun spawnBonus() {
        val factoryB = randomBonusFactory()
        val density = resources.displayMetrics.density
        val sizePx = (64 * density).toInt()

        val pos = getRandomPosition(sizePx) ?: return
        val bonus = factoryB.create(pos.first, pos.second, sizePx)

        spawnedBonuses.add(bonus)
        addView(bonus.view)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!running) return super.onTouchEvent(event)

        if (event.action == MotionEvent.ACTION_DOWN) {
            var hit = false

            for (i in specialBugs.indices.reversed()) {
                val bug = specialBugs[i]
                if (!bug.dead && bug.contains(event.x, event.y)) {
                    points += bug.points
                    bug.markAsDead()
                    specialBugs.removeAt(i)
                    removeView(bug.view)
                    onScoreChanged?.invoke(points)
                    hit = true
                    break
                }
            }

            if (!hit) {
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
                points -= 100 * (settings.userDifficulty + 1)
                onScoreChanged?.invoke(points)
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onDetachedFromWindow() {
        viewScope.cancel()
        stop()
        super.onDetachedFromWindow()
    }
}
package com.example.myapplication.classes

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

object SoundManager {
    private var pool: SoundPool? = null
    private val ids = mutableMapOf<Int, Int>()   // resId -> soundId
    private val loaded = mutableSetOf<Int>()     // soundId, которые уже загрузились

    fun init(context: Context, vararg resIds: Int) {
        if (pool != null) return
        pool = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            ).build().also { p ->
                p.setOnLoadCompleteListener { _, soundId, status ->
                    if (status == 0) loaded.add(soundId)
                }
            }
        resIds.forEach { ids[it] = pool!!.load(context, it, 1) }
    }

    fun play(resId: Int, volume: Float = 1f, rate: Float = 1f) {
        val soundId = ids[resId] ?: return
        if (soundId !in loaded) return
        pool?.play(soundId, volume, volume, 1, 0, rate)
    }

    fun release() {
        pool?.release()
        pool = null
        ids.clear()
        loaded.clear()
    }
}
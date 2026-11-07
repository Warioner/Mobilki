package com.example.myapplication.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.myapplication.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

class GoldRateWidgetProvider : AppWidgetProvider() {

    private val repository = GoldRateRepository()

    companion object {
        const val ACTION_REFRESH = "com.example.myapplication.widget.REFRESH_GOLD"

        fun updateViews(context: Context, manager: AppWidgetManager, appWidgetId: Int, textToShow: String) {
            val views = RemoteViews(context.packageName, R.layout.new_app_widget)
            views.setTextViewText(R.id.tv_gold_rate, textToShow)

            val refreshIntent = Intent(context, GoldRateWidgetProvider::class.java).apply {
                action = ACTION_REFRESH
            }
            val pending = PendingIntent.getBroadcast(
                context, appWidgetId, refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.tv_gold_rate, pending)
            manager.updateAppWidget(appWidgetId, views)
        }

        private fun formatRate(value: Double): String {
            return NumberFormat.getNumberInstance(Locale.forLanguageTag("ru-RU")).format(value)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val pending = goAsync()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        scope.launch {
            try {
                withContext(Dispatchers.Main) {
                    appWidgetIds.forEach { id -> updateViews(context, appWidgetManager, id, "Загрузка...") }
                }

                val rate = repository.loadRate()

                withContext(Dispatchers.Main) {
                    if (rate > 0) {
                        // СОХРАНЯЕМ ДАННЫЕ ДЛЯ MAINACTIVITY
                        val prefs = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
                        prefs.edit().putString("gold_rate", rate.toString()).apply()

                        val textToShow = formatRate(rate) + " ₽"
                        appWidgetIds.forEach { id -> updateViews(context, appWidgetManager, id, textToShow) }
                    } else {
                        appWidgetIds.forEach { id -> updateViews(context, appWidgetManager, id, "0 ₽") }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    appWidgetIds.forEach { id -> updateViews(context, appWidgetManager, id, "Сбой") }
                }
            } finally {
                pending.finish()
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, GoldRateWidgetProvider::class.java))
            if (ids.isNotEmpty()) onUpdate(context, manager, ids)
        }
    }
}
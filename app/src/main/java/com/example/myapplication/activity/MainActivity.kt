package com.example.myapplication.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import com.example.myapplication.R
import com.example.myapplication.classes.SettingsGame
import com.example.myapplication.classes.User
import com.example.myapplication.fragment.Game
import com.example.myapplication.fragment.Menu
import com.example.myapplication.fragment.Login
import com.example.myapplication.fragment.Registration
import com.example.myapplication.fragment.Records

class MainActivity : AppCompatActivity() {
    lateinit var user: User
    var settings = SettingsGame(countBug = 10, speedGame = 100, timeRound = 30, 1, 1)
    private lateinit var mainLayout: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        mainLayout = findViewById(R.id.fragment_container)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (savedInstanceState == null) {
            showLogin()   // вместо Registration() первым открывается экран входа
        }

//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, Registration())
//                .commit()
//        }

    }
    fun showLogin() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, Login())
            .commit()
    }
    fun showRecords() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, Records())
            .addToBackStack(null)   // «Назад» вернёт на экран результатов
            .commit()
    }

    fun showRegistration() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, Registration())
            .addToBackStack(null)   // кнопка «Назад» вернёт на экран входа
            .commit()
    }
    fun showMenu() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, Menu())
            .commit()
    }
    fun showGame() {
        val gameFragment = Game()
        gameFragment.settings = settings
        gameFragment.user = user;

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, gameFragment)
            .commit()
    }

    fun updateSettingsCountBug(
        countBug: Int = settings.countBug

    ) {
        settings = settings.copy(
            countBug = countBug
        )
    }
    fun updateSettingsSpeedGame(
        speedGame: Int = settings.speedGame

        ) {
        settings = settings.copy(
            speedGame = speedGame
        )
    }

    fun updateSettingsBonusTime(
        bonusTime: Int = settings.bonusTime

    ) {
        settings = settings.copy(
            bonusTime = bonusTime
        )
    }

    fun updateSettingsTimeRound(
        timeRound: Int = settings.timeRound

    ) {
        settings = settings.copy(
            timeRound = timeRound
        )
    }
}
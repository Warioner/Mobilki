package com.example.myapplication.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.R
import com.example.myapplication.classes.SettingsGame
import com.example.myapplication.classes.User
import com.example.myapplication.fragment.Game
import com.example.myapplication.fragment.Menu
import com.example.myapplication.fragment.Registration

class MainActivity : AppCompatActivity() {
    var user: User? = null
    var settings = SettingsGame(countBug = 10, speedGame = 100, timeRound = 30, 1)
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
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, Registration())
                .commit()
        }
    }
    fun showMenu() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, Menu())
            .commit()
    }
    fun showGame() {
        val gameFragment = Game()
        gameFragment.settings = settings

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, gameFragment)
            .commit()
    }
}
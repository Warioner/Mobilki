package com.example.myapplication.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.R
import com.example.myapplication.fragment.Game
import com.example.myapplication.fragment.PagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var startGameButton: Button
    private lateinit var mainLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        mainLayout = findViewById(R.id.main)
        tabLayout = findViewById(R.id.tab1)
        viewPager = findViewById(R.id.view1)
        startGameButton = findViewById(R.id.button2)

        viewPager.adapter = PagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.registration)
                1 -> getString(R.string.settings)
                2 -> getString(R.string.rules)
                3 -> getString(R.string.autors)
                else -> ""
            }
        }.attach()

        startGameButton.setOnClickListener {
            val game = Game.newInstance(countBug = 15, speedGame = 100, timeRound = 30)

            tabLayout.isVisible = false;
            startGameButton.isVisible = false;
            viewPager.isVisible = false;

            supportFragmentManager.beginTransaction()
                .replace(R.id.main, game)
                .commit()
        }

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
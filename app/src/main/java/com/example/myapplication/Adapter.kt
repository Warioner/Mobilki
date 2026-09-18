package com.example.myapplication

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication.fragment.Authors
import com.example.myapplication.fragment.Registration
import com.example.myapplication.fragment.Rules
import com.example.myapplication.fragment.Settings

class PagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Registration()
            1 -> Settings()
            2 -> Rules()
            3 -> Authors()
            else -> throw IllegalStateException("Неверная позиция")
        }
    }
}
package com.example.myapplication.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

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
package com.example.myapplication.fragment

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Settings()
            1 -> Rules()
            2 -> Authors()
            3 -> Authors()
            else -> throw IllegalStateException("Неверная позиция")
        }
    }
}
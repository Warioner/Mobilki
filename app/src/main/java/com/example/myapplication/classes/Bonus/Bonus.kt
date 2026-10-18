package com.example.myapplication.classes.Bonus

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView

class Bonus(
    val context: Context,
    val setActive: (Int) -> Unit,
    val initialX: Float,
    val initialY: Float,
    val size: Int,
    val imageRes: Int,
    val weight: Int = 100,
    val timeWork: Int
) {

    var x: Float = initialX
        protected set
    var y: Float = initialY
        protected set

    val view: ImageView = ImageView(context)

    init {
        view.setImageResource(imageRes)
        view.scaleType = ImageView.ScaleType.FIT_CENTER
        view.layoutParams = FrameLayout.LayoutParams(size, size)
        view.translationX = x
        view.translationY = y
    }

    fun activate(){
        setActive(timeWork)
    }


}
package com.example.myapplication.fragment
import android.os.Bundle
import androidx.core.os.bundleOf
import android.view.LayoutInflater
import android.view.View
import android.widget.ListView
import android.widget.Button
import android.widget.TextView
import android.content.Intent
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.activity.MainActivity
import androidx.activity.addCallback
class Results : Fragment(R.layout.results) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nickname = arguments?.getString(ARG_NICKNAME).orEmpty()
        val score = arguments?.getInt(ARG_SCORE) ?: 0
        val difficulty = arguments?.getString(ARG_DIFFICULTY).orEmpty()

        view.findViewById<TextView>(R.id.tvPlayer).text = "Игрок: $nickname"
        view.findViewById<TextView>(R.id.tvDifficulty).text = "Сложность: $difficulty"
        view.findViewById<TextView>(R.id.tvScore).text = "Счёт: $score"

        view.findViewById<Button>(R.id.btnExit).setOnClickListener {
            (requireActivity() as MainActivity).showMenu()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            view.findViewById<Button>(R.id.btnExit).performClick()
        }
    }

    companion object {
        private const val ARG_NICKNAME = "nickname"
        private const val ARG_SCORE = "score"
        private const val ARG_DIFFICULTY = "difficulty"

        fun newInstance(nickname: String, score: Int, difficulty: String) = Results().apply {
            arguments = bundleOf(
                ARG_NICKNAME to nickname,
                ARG_SCORE to score,
                ARG_DIFFICULTY to difficulty
            )
        }
    }
}
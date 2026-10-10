package com.example.myapplication.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.classes.GameBoard
import com.example.myapplication.classes.SettingsGame
import androidx.activity.addCallback
import com.example.myapplication.activity.MainActivity
import android.content.Intent

class Game : Fragment() {
    lateinit var settings: SettingsGame
    private lateinit var gameBoard: GameBoard
    private lateinit var timeTextView: TextView
    private lateinit var scoreTextView: TextView
    private lateinit var exitButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gameBoard = view.findViewById(R.id.gameBoard)
        timeTextView = view.findViewById(R.id.timeTextView)
        scoreTextView = view.findViewById(R.id.scoreTextView)
        exitButton = view.findViewById(R.id.exitButton)

        timeTextView.text = "Время: ${settings.timeRound}"
        scoreTextView.text = "Счет: 0"

        gameBoard.onScoreChanged = { score ->
            scoreTextView.text = "Счет: $score"
        }

        gameBoard.onTimeChanged = { time ->
            timeTextView.text = "Время: $time"
        }

        gameBoard.onGameFinished = {
            if (isAdded) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, Results.newInstance(
                        nickname = "—",
                        score = gameBoard.points,
                        difficulty = (0).toString()
                    ))
                    .commit()
            }
        }

        exitButton.setOnClickListener {
            gameBoard.stop()
            onExit()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            exitButton.performClick()
        }

        gameBoard.start(settings)
    }

    private fun onExit() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        if (::gameBoard.isInitialized) {
            gameBoard.stop()
        }
        super.onDestroyView()
    }
}
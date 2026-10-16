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
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.classes.DataBase
import com.example.myapplication.classes.RecordDB
import com.example.myapplication.classes.User
import kotlinx.coroutines.launch

class Game : Fragment() {
    lateinit var user: User
    lateinit var settings: SettingsGame
    private lateinit var gameBoard: GameBoard
    private lateinit var timeTextView: TextView
    private lateinit var scoreTextView: TextView
    private lateinit var exitButton: Button

    private var startedAt = 0L

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

            viewLifecycleOwner.lifecycleScope.launch {
                val db = DataBase.getDatabase(requireContext())
                val dao = db.userDao()

                val record = RecordDB(
                    userName = user.name,
                    difficulty = settings.userDifficulty,
                    score = gameBoard.points,
                    duration = settings.timeRound
                )

                dao.insertRecord(record)
            }

            if (isAdded) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, Results.newInstance(
                        nickname = user.name,
                        score = gameBoard.points,
                        difficulty = settings.userDifficulty.toString()
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

        //startedAt = System.currentTimeMillis()
        gameBoard.start(settings)
    }

    private fun onExit() {
        (requireActivity() as MainActivity).showMenu()
    }

    override fun onDestroyView() {
        if (::gameBoard.isInitialized) {
            gameBoard.stop()
        }
        super.onDestroyView()
    }
}
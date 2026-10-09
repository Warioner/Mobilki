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

class Game : Fragment() {

    companion object {
        const val ARG_COUNT_BUG = "COUNT_BUG"
        const val ARG_SPEED_GAME = "SPEED_GAME"
        const val ARG_TIME_ROUND = "TIME_ROUND"

        // Для передачи настроек из другого фрагмента (Settings)
        fun newInstance(countBug: Int, speedGame: Int, timeRound: Int): Game {
            return Game().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COUNT_BUG, countBug)
                    putInt(ARG_SPEED_GAME, speedGame)
                    putInt(ARG_TIME_ROUND, timeRound)
                }
            }
        }
    }

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

        val args = arguments
        val settings = SettingsGame(
            countBug = args?.getInt(ARG_COUNT_BUG, 10) ?: 10,
            speedGame = args?.getInt(ARG_SPEED_GAME, 1) ?: 1,
            timeRound = args?.getInt(ARG_TIME_ROUND, 30) ?: 30
        )

        timeTextView.text = "Время: ${settings.timeRound}"
        scoreTextView.text = "Счет: 0"

        gameBoard.onScoreChanged = { score ->
            scoreTextView.text = "Счет: $score"
        }

        gameBoard.onTimeChanged = { time ->
            timeTextView.text = "Время: $time"
        }

        gameBoard.onGameFinished = {
            gameBoard.stop()
            onExit()
        }

        exitButton.setOnClickListener {
            gameBoard.stop()
            onExit()
        }

        gameBoard.start(settings)
    }

    private fun onExit() {
        // Фрагмент сам не решает, куда вернуться — это решает тот, кто его запустил.
        // Если есть back stack — пойдём назад. Если нет — закроем Activity.
        if (parentFragmentManager.backStackEntryCount > 0) {
            parentFragmentManager.popBackStack()
        } else {
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        if (::gameBoard.isInitialized) {
            gameBoard.stop()
        }
        super.onDestroyView()
    }
}
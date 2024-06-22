package me.adversing

import com.github.bhlangonijr.chesslib.Board
import me.adversing.mcts.MCTS
import me.adversing.model.state.ChessState

fun main() {
    val initialBoard = Board()
    val initialState = ChessState(initialBoard)

    val mcts = MCTS()
    val bestMove = mcts.search(initialState)
    val confidence = mcts.calculateConfidence(bestMove)

    println("Best move: $bestMove, confidence: $confidence%")
}
package me.adversing.model.state

interface GameState {
    fun getPossibleMoves(): List<Move>
    fun makeMove(move: Move): GameState
    fun getWinner(): Player?
    fun isTerminal(): Boolean
}

typealias Move = Pair<Int, Int>
typealias Player = String
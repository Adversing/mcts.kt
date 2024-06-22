package me.adversing.model.state

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.MoveGenerator

class ChessState(private val board: Board) : GameState {

    override fun getPossibleMoves(): List<Move> {
        val moveList: MutableList<Move> = MoveGenerator.generateLegalMoves(board).map { move ->
            Pair(
                move.from.ordinal,
                move.to.ordinal
            )
        }.toMutableList()

        return moveList.toList()
    }

    override fun makeMove(move: Move): GameState {
        val newBoard = Board()
        newBoard.loadFromFen(board.fen)

        val actualMove = com.github.bhlangonijr.chesslib.move.Move(
            Square.squareAt(move.first),
            Square.squareAt(move.second)
        )

        newBoard.doMove(actualMove)
        return ChessState(newBoard)
    }

    override fun getWinner(): Player? {
        return when {
            board.isMated -> board.sideToMove.flip().name
            board.isDraw -> null
            else -> null
        }
    }

    override fun isTerminal(): Boolean {
        return board.isMated || board.isDraw
    }

    fun getBoard(): Board {
        return board
    }
}
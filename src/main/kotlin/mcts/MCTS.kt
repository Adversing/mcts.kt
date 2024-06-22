package me.adversing.mcts

import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import com.github.bhlangonijr.chesslib.move.MoveGenerator
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.adversing.model.Node
import me.adversing.model.state.ChessState
import me.adversing.model.state.GameState
import java.util.concurrent.ConcurrentHashMap

class MCTS(private val iterations: Int = 1000, private val explorationConstant: Double = 1.41) {
    private val transpositionTable = ConcurrentHashMap<String, Node>()
    private lateinit var root: Node // root node of the search tree

    fun search(initialState: GameState): Move {
        root = Node(initialState)
        transpositionTable[initialState.toString()] = root

        runBlocking {
            repeat(iterations) {
                launch {
                    val selectedNode = select(root)
                    val expandedNode = expand(selectedNode)
                    val simulationResult = simulate(expandedNode)
                    backpropagate(expandedNode, simulationResult)
                }
            }
        }

        return Move(
            Square.squareAt(root.bestChild(0.0).move.first),
            Square.squareAt(root.bestChild(0.0).move.second)
        )
    }

    fun calculateConfidence(move: Move): Int {
        val node = transpositionTable[moveToString(move)] ?: return 0
        val parentVisits = node.parent?.visits ?: return 0

        if (parentVisits == 0) {
            return 0
        }

        return ((node.visits.toDouble() / parentVisits.toDouble()) * 100).toInt()
    }

    private fun moveToString(move: Move): String {
        return "${move.from}${move.to}"
    }

    private fun select(node: Node): Node {
        var currentNode = node
        while (!currentNode.state.isTerminal()) {
            if (!currentNode.isFullyExpanded()) {
                return currentNode
            }
            currentNode = currentNode.bestChild(explorationConstant)
        }
        return currentNode
    }

    private fun expand(node: Node): Node {
        val untriedMoves = node.state.getPossibleMoves().filter { move ->
            node.children.none { it.move == move }
        }

        if (untriedMoves.isEmpty()) {
            return node
        }

        val move = untriedMoves.random()
        val newState = node.state.makeMove(move)

        val newNode = Node(state = newState, parent = node, move = move)

        node.children.add(newNode)
        transpositionTable[moveToString(
            Move(
                Square.squareAt(move.first),
                Square.squareAt(move.second)
            )
        )] = newNode
        return newNode
    }

    private fun simulate(node: Node): Double {
        val currentState = (node.state as ChessState)
        val board = currentState.getBoard()

        while (!board.isMated && !board.isDraw) {
            val possibleMoves = MoveGenerator.generateLegalMoves(board)
            if (possibleMoves.isEmpty()) break
            val move = possibleMoves.random()
            board.doMove(move)
        }

        return when {
            board.isMated -> if (board.sideToMove == Side.WHITE) 0.0 else 1.0 // win for black if white is mated
            board.isDraw -> 0.5 // draw
            else -> 0.0 // if it reaches here, the game is probably still going
        }
    }

    private fun backpropagate(node: Node, result: Double) {
        var currentNode: Node? = node

        while (currentNode != null) {
            currentNode.visits++
            currentNode.wins += result
            currentNode = currentNode.parent
        }
    }
}

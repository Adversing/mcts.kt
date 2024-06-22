package me.adversing.model

import me.adversing.model.state.GameState
import me.adversing.model.state.Move
import kotlin.math.sqrt

data class Node(
    val state: GameState,
    val parent: Node? = null,
    val move: Move = Pair(0, 0),
    val children: MutableList<Node> = mutableListOf(),
    var visits: Int = 0,
    var wins: Double = 0.0
) {
    fun isFullyExpanded(): Boolean {
        return children.size == state.getPossibleMoves().size
    }

    fun bestChild(c: Double = 1.41): Node {
        return children.maxByOrNull {
            it.wins / it.visits + c * sqrt(2 * kotlin.math.log10(visits.toDouble()) / it.visits)
        } ?: throw IllegalStateException("No children found")
    }
}
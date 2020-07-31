import connectFour.Board
import connectFour.Test

fun main() {
    val b = Test().getRandomBoard()

    println(b)
    println(b.getTurn())
    val res = b.evaluateMoves()
    println(res)
}
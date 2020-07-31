package connectFour

interface Game {
    fun isGameOver(): Boolean
    fun symmetricBoard(): Game
    fun makeMove(col: Int): Game
    fun undoMove(): Game
    fun possibleMoves(): List<Int>
    fun makeBestMove(): Game
    fun bestMove(): Pair<Int?,Int>
    override fun toString(): String
}
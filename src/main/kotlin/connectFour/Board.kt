package connectFour

import kotlin.collections.HashMap
import kotlin.math.max
import kotlin.math.min

class Board(
    private val bitboard: Array<Long> = arrayOf(0L,0L),
    private val counter: Int = 0,
    private val height: Array<Int> = arrayOf(0,7,14,21,28,35,42),
    private val moves: List<Int> = listOf()
): Game {
    //specifies number of monte Carlo Iterations
    private val monteCarloIterations = 400

    //getters
    fun getCounter() = counter
    fun getbitboard0() = bitboard[0]
    fun getbitboard1() = bitboard[1]
    fun getMoves() = moves
    fun getHeight() = height
    fun getTurn() = counter and 1
    fun getMonteCarloIterations() = monteCarloIterations

    //Returns Bitboard as Pair
    private fun getBitPair() = bitboard[0] to bitboard[1]

    //functions to check whether game is over and who is winning
    override fun isGameOver() = isWinning(0) || isWinning(1)
    fun isDraw() = bitboard[0] or bitboard[1] == 279258638311359L
    fun isWinning(player: Int): Boolean{
        var bb: Long
        val cb = bitboard[player]
        listOf(1,7,6,8).forEach {
            bb = cb and (cb shr it)
            if ((bb and (bb shr (2*it))) != 0L) return true
        }
        return false
    }

    //returns new Board with swapped colors
    private fun invBoard() = Board(arrayOf(bitboard[1],bitboard[0]),counter-1,height.copyOf(),moves)

    //returns new Board with swapped turn (counter incremented by 1)
    private fun swapTurn() = Board(bitboard.clone(),counter+1,height.copyOf(),moves)

    /*
        - returns Symmetric board
        - shifts bits by column

        - index: numbers for shifting columns
        - l: List of three left columns
        - r: List of three right columns
        - resl/resr: left 3 / right 3 result columns
        - middle: column in the middle
        The resulting bitboard combines left result (resl), right result (resr)
        and middle
     */
    override fun symmetricBoard(): Board {
        val index = listOf(42,28,14)
        val l = listOf(0b111111L,
                0b1111110000000,0b11111100000000000000)
        val r = listOf(0b111111000000000000000000000000000000000000000000,
                       0b11111100000000000000000000000000000000000,
                       0b1111110000000000000000000000000000
        )
        val resl = arrayOf(0L,0L)
        val resr = arrayOf(0L,0L)
        for (i in index.indices) {
            for (j in 0..1) {
                resl[j] = resl[j] or
                        ((bitboard[j] and l[i]) shl index[i])
                resr[j] = resr[j] or
                        ((bitboard[j] and r[i]) shr index[i])
            }
        }
        val middle = 0b111111000000000000000000000L
        val resm = arrayOf(middle and bitboard[0],middle and bitboard[1])
        val res = arrayOf(resl[0] or resr[0] or resm[0],
                resl[1] or resr[1] or resm[1])
        return Board(res.copyOf(),counter+1,height.copyOf(),moves)
    }

    //returns new Board with changed move
    override fun makeMove(col: Int): Board {
        val move = 1L shl height[col]
        val nb = bitboard.copyOf()
        nb[counter and 1] = bitboard[counter and 1] xor move
        val newheight = height.copyOf()
        newheight[col]++
        return Board(nb,counter+1,newheight,moves.plus(col))
    }

    //returns new Board with last move undone
    override fun undoMove(): Board {
        val ncounter = counter - 1
        val col = moves[ncounter]
        val newheight = height.copyOf()
        newheight[col]--
        val move = 1L shl newheight[col]
        val nb = bitboard.copyOf()
        nb[ncounter and 1] = bitboard[ncounter and 1] xor move
        return Board(nb,ncounter,newheight,moves.dropLast(1))
    }

    //lists all possible Moves
    override fun possibleMoves(): List<Int>{
        val moves = mutableListOf<Int>()
        val top = 0b1000000_1000000_1000000_1000000_1000000_1000000_1000000L
        for (col in 0..6) {
            if ((top and (1L shl height[col])) == 0L) moves.add(col)
        }
        return moves
    }

    //returns new Move with best move made
    override fun makeBestMove() = makeMove(bestMove().first!!)

    /*
        returns Alpha-Beta result for current board
        → if turn = player 0: invert board
            (Alha-Beta works with player 1 as maximizing player)
     */
    override fun bestMove(): Pair<Int?,Int>{
        return if (counter and 1 == 1)
            alphaBeta(4)
            else invBoard().alphaBeta(4)
    }

    /*
        Algorithm: Alpha-Beta-Pruning
        → Depth can be chosen in bestMove()
            - algorithm only works for even depths
        → Maximizing player is player 1
        → Results are stored in cache
            - HashMap has key and value
                *key: PairOf( PairOf(bitboard0,bitboard1), IsMaximizingPlayer )
                *value: board evaluation

        → Possible Moves are made in random order
            (possibleMoves().shuffled())
     */
    private fun alphaBeta(depth: Int,
                          maximize: Boolean = true,
                          alpha: Int = Int.MIN_VALUE,
                          beta: Int = Int.MAX_VALUE,
                          cache: HashMap<Pair<Pair<Long,Long>,Boolean>,Int> = HashMap()
                          ): Pair<Int?,Int>{

        val index = getBitPair() to maximize
        val symindex = symmetricBoard().getBitPair() to maximize
        if (cache.containsKey(index)) {
            return null to cache[index]!!
        }
        if (cache.containsKey(symindex)) {
            return null to cache[symindex]!!
        }

        var al = alpha
        var be = beta
        if (depth == 0 || isGameOver())
            return when{
                isWinning(1) -> null to monteCarloIterations
                isWinning(0) -> null to 0
                else -> null to averageBoard()
            }
        var bestRes: Pair<Int?,Int> = null to if (maximize) Int.MIN_VALUE else Int.MAX_VALUE

        if (maximize){
            for (it in possibleMoves().shuffled()) {
                val nextBoard = makeMove(it)
                if (nextBoard.isWinning(1)) {
                    return it to monteCarloIterations
                }
                val next = nextBoard.alphaBeta(depth-1,false,al,be,cache)
                if (next.second > bestRes.second) bestRes = it to next.second
                al = max(al,bestRes.second)
                if (al >= be) break
            }

            if (!makeMove(bestRes.first!!).isWinning(1))
                possibleMoves().forEach{
                    if (swapTurn().makeMove(it).isWinning(0)) bestRes = it to makeMove(it).averageBoard()}
            if(!cache.containsKey(symmetricBoard().getBitPair() to true))
                cache[getBitPair() to true] = bestRes.second
            return bestRes
        }else{
            for (it in possibleMoves().shuffled()){
                val nextBoard = makeMove(it)
                if (nextBoard.isWinning(0)) return it to 0
                val next =  nextBoard.alphaBeta(depth-1,true,al,be,cache)
                if (next.second < bestRes.second) bestRes = null to next.second
                be = min(be,bestRes.second)
                if (be <= al) break
            }
            if(!cache.containsKey(symmetricBoard().getBitPair() to false))
                cache[getBitPair() to false] = bestRes.second
            return bestRes
        }
    }

    //Monte-Carlo-Result(List)
    fun evaluateMoves() = possibleMoves().
        fold(listOf<Pair<Int,Int>>())
        {acc, move -> acc.plus(move to makeMove(move).simulateGames()) }

    //returns evaluation for the best possible Monte-Carlo-Result
    fun getMaxMove() = evaluateMoves().maxBy { i -> i.second }

    //returns average of all values of Monte-Carlo-Result
    private fun averageBoard() = (evaluateMoves().sumBy { i -> i.second })/possibleMoves().count()

    //counts the amount of won games
    private fun simulateGames(): Int = (1..monteCarloIterations).count { playRandomGame() == 1 }

    //plays random moves until game is over
    private fun playRandomGame(): Int{
        var b = Board(bitboard.copyOf(),counter,height.copyOf(),moves)
        if (counter and 1 == 0) b = b.invBoard()
        while (!b.isGameOver()){
            val moves = b.possibleMoves()
            if (moves.isEmpty()) return 0
            val move = moves.random()
            b = b.makeMove(move)
        }
        return if (b.isWinning(1)) -1 else 1
    }

    /*
        returns bitboard as String

        bitboard representation

          6 13 20 27 34 41 48   55 62     Additional row
        +---------------------+
        | 5 12 19 26 33 40 47 | 54 61     top row
        | 4 11 18 25 32 39 46 | 53 60
        | 3 10 17 24 31 38 45 | 52 59
        | 2  9 16 23 30 37 44 | 51 58
        | 1  8 15 22 29 36 43 | 50 57
        | 0  7 14 21 28 35 42 | 49 56 63  bottom row
        +---------------------+

        Original idea and concept by Dominikus Herzberg (GitHub-Account: denkspuren)
        link: https://github.com/denkspuren/BitboardC4/blob/master/BitboardDesign.md
 */
    override fun toString(): String {
        var s = ""
        for (i in 5 downTo 0) {
            for (n in i until i+43 step 7){
                val p1 = (bitboard[0] shr n) and 1
                val p2 = (bitboard[1] shr n) and 1
                s += when {
                    p1 == 1L -> "0 "    //White: bitboard[0] ⬤
                    p2 == 1L -> "X "    //Black: bitboard[1] ◯
                    else -> ". "
                }
            }
            s +="\n"
        }
        return s
    }
}
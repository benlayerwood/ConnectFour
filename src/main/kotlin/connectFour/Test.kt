package connectFour

class Test(var b: Board = Board()) {
    //returns connectFour.Board
    fun getBoard() = b


    /*
        runs 5 critical tests
            → Test Cases are saved in Array "tests"
                tests[0]: Test Case 1 (Engine can win in next move)
                tests[1]: Test Case 2 (Engine can win in two moves)
                tests[2]: Test Case 3 (Engine can win in three moves)
                tests[3]: Test Case 4 (Engine stops opponent from winning)
                tests[4]: Test Case 5 (Engine stops opponent from winning in two moves)
            → Each element in "tests" is tested
                - plays best move until game is over or depth is reached
                - results are printed to console
     */
    fun runTests(){
        val tests =
                arrayOf(
                        Board(arrayOf(18314034168704L,14637254934529L), 21,
                        arrayOf( 1, 10, 17, 25, 29, 41, 45 ),
                        listOf( 4, 6, 1, 6, 2, 0, 5, 2, 1, 5, 5, 5, 5, 3, 1, 3, 3, 2, 3, 5, 6 )
                        ),
                        Board(
                                arrayOf(8831262333322,4605280830981),
                                23,
                                arrayOf( 4, 13, 17, 23, 31, 38, 44 ),
                                listOf( 1, 6, 1, 1, 5, 3, 4, 1, 1, 0, 1, 2, 4, 2, 3, 4, 0, 0, 2, 5, 0, 5, 6 )
                        ),
                        Board(
                        arrayOf(233167350972421,44021045002242),
                        23,
                        arrayOf( 3, 7, 16, 26, 33, 37, 48 ),
                        listOf( 6, 6, 2, 5, 6, 6, 0, 4, 3, 4, 6, 0, 6, 3, 5, 4, 0, 3, 3, 3, 4, 4, 2 )
                        ),
                        Board(
                                arrayOf(13366206660608,68725784577),
                                10,
                                arrayOf( 1, 7, 15, 23, 29, 38, 44 ),
                                listOf( 4, 3, 5, 3, 6, 5, 5, 0, 6, 2 )
                        ),
                        Board(
                                arrayOf(4605018636416,34382807297),
                                13,
                                arrayOf( 1, 9, 14, 25, 30, 38, 43 ),
                                listOf( 1, 3, 4, 5, 6, 3, 3, 0, 4, 3, 5, 1, 5 )
                        ))
        tests.forEachIndexed{index, board ->
            println("Test ${index + 1}:")
            println(board)
            var b = Board(arrayOf(board.getbitboard0(),board.getbitboard1()),
                        board.getCounter(),board.getHeight().copyOf(),board.getMoves())
            var depth = 0
            while (!b.isGameOver()){
                if ((index == 3 && depth == 2))break
                if ((index == 4 && depth == 4))break
                depth++
                val move = b.bestMove()
                b = b.makeMove(move.first!!)
                print("Best Move: ${move.first!!}")
                println(if (b.getTurn()==0) " (black)" else " (white)")
                println("Depth: $depth")
                println(b)
                println()
            }
        }
    }

    /*
        - plays a specific number of random moves
        - prints progress to console
     */
    fun playRandomMoves(n: Int){
        if (n == 1) {
            saveBoard()
            return
        }
        var move: Int
        if (b.possibleMoves().all { b.makeMove(it).isGameOver() }){
            println("Next move is winning move!")
            return
        }
        do {
            move = b.possibleMoves().random()
        }while (b.makeMove(move).isWinning(0) ||
                b.makeMove(move).isWinning(1))
        b = b.makeMove(move)

        println("Player: ${b.getCounter()+1 and 1}")
        println("Move: $move\n")
        println(b.toString())
        playRandomMoves(n - 1)
    }

    /*
        returns random Board after playing a random number of moves
            → random number of moves: Any number between 10 and 26
     */
    fun getRandomBoard(): Board {
        var nb = Board()
        for (i in 0..(10..26).random()) {
            var move = 0
            if (nb.possibleMoves().all {nb.makeMove(it).isGameOver() }) break
            do {
                move = nb.possibleMoves().random()
            } while (nb.makeMove(move).isWinning(0) ||
                    nb.makeMove(move).isWinning(1))
            nb = nb.makeMove(move)
        }
        return nb
    }

    /*
        prints current board to console
            → the console output can be used to recunstruct the shown board
     */
    fun saveBoard(){
        println("val b = connectFour.Board(")
        println("arrayOf(${b.getbitboard0()},${b.getbitboard1()}),")
        println("${b.getCounter()},")
        println("arrayOf(${b.getHeight().contentToString().replace('[',' ').replace(']',' ')}),")
        println("listOf(${b.getMoves().toString().replace('[',' ').replace(']',' ')})")
        println(")")
    }

    /*
        prints 2 versions of best moves
            1) Best move based on Monte-Carlo-Tree-Search
            2) Best move based on Alpha-Beta-Pruning
     */
    fun testBestMove(){
        println(b.toString())
        saveBoard()
        println(b.getTurn())
        println("Monte-Carlo result:")
        println(b.getMaxMove())
        val m = b.bestMove()
        println("BestMove result:")
        println(m)
        println()
    }

    /*
        prints board and symmetric version of board
     */
    fun checkSymmetry(){
        println("This connectFour.Board:")
        println(b.toString())
        println("Symmetric connectFour.Board:")
        println(b.symmetricBoard().toString())
    }

    /*
        shows critical examples for Alpha-Beta-Algorithm
            → starts with random board (getRandomBoard()
            → chooses random board again if
                1) no possible moves in first board
                2) next move in board is a win
                This removes the easy and simple examples!
            → Print board and chosen moves if
                Alpha-Beta result differs from Monte-Carlo result
     */
    fun testBestMoveExamples(){
        var b = listOf<Board>()
        var i = 0
        while (i<10){
            var board: Board
            do {
                board = getRandomBoard()
                if (board.possibleMoves().isEmpty()) continue
                if (board.possibleMoves().any{ board.makeMove(it).isGameOver()}) continue
            }while (board.getMaxMove()?.second!! == board.getMonteCarloIterations() )
            val bestMove = board.bestMove()
            if (bestMove.first != board.getMaxMove()?.first!!) {
                Test(board).testBestMove()
                i++
            }
        }
    }

    /*
        - alpha-beta plays against itself
        - prints progress to console
     */
    fun playAlphaBeta(){
        do {
            println(b.toString())
            val move = b.bestMove().first!!
            b = b.makeMove(move)
            println("Move: $move")
        }while (!b.isGameOver())
        println(b.toString())
    }

    /*
        - alpha-beta plays against human
        - prints progress to console
     */
    fun playAgainstAlphaBeta(){
        do {
            println(b.toString())
            val move: Int =
                    if (b.getTurn() == 1) {
                        b.bestMove().first!!
                    } else {
                        println("Column: ")
                        readLine()!!.toInt()
                    }
            b = b.makeMove(move)
            println("Move: $move")
        }while (!b.isGameOver())
        println(b.toString())
    }
}
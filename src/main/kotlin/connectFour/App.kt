package connectFour

import io.javalin.Javalin
import io.javalin.http.Context
import kotlin.math.pow
import kotlin.system.measureNanoTime

class App (var board: Board = Board()) {
    var p1IsHuman: Boolean = true
    var p2IsHuman: Boolean = false

    init {
        val app = Javalin.create(){
            config -> config.addStaticFiles("/Public")
        }.start(7070)

        app.get("/move"){
            ctx: Context ->

            val s = ctx.queryParam("col")?.toInt()
            if (!board.isGameOver())
                (0..6).forEach{ if (s == it) board = board.makeMove(it)}
            val res = if(board.isGameOver()) "winp1" + board.isWinning(0).toString()[0] + getBitBoardString() else
                getBitBoardString()
            ctx.result(res)
        }

        app.get("/bestMove"){
            val pair = getBestMoveAndTime()
            if (!board.isGameOver())
                board = board.makeMove(pair.first)
            val res = if(board.isGameOver()) "winp1" + board.isWinning(0).toString()[0] + getBitBoardString() else
                getBitBoardString()
            it.result(res+";"+pair.second)
        }

        app.get("/bestMoveReturn"){
            var pair = 0 to ""
            if (!board.isGameOver()){
                if (board.getTurn() == 0 && !p1IsHuman ||
                        board.getTurn() == 1 && !p2IsHuman ||
                        (!p1IsHuman && !p2IsHuman)) {
                    pair = getBestMoveAndTime()
                    board = board.makeMove(pair.first)
                        }
            }
            val res = if(board.isGameOver()) "winp1" + board.isWinning(0).toString()[0] + getBitBoardString() else
                getBitBoardString()
            it.result(res+";"+pair.second)
        }

        app.get("/undoMove"){
            if (board.getCounter() > 0)
                board = board.undoMove()
            it.result(getBitBoardString())
        }

        app.get("/newGame"){
            ctx ->
            board = Board()
            p1IsHuman = ctx.queryParam("first") == "human"
            p2IsHuman = ctx.queryParam("second") == "human"
            if (!p1IsHuman) board = board.makeBestMove()
            ctx.result(getBitBoardString())
        }

        app.get("/runTests"){
            Test().runTests()
        }
    }

    private fun getBitBoardString(): String{
        var b0 = board.getbitboard0().toString(2).reversed()
        b0 += "0".repeat(48 - b0.length)
        var b1 = board.getbitboard1().toString(2).reversed()
        b1 += "0".repeat(48 - b1.length)
        return "board0=$b0;board1=$b1"
    }

    private fun getBestMoveAndTime() :Pair<Int,String>{
        var bestMove = 0
        val t = measureNanoTime {
                    bestMove = board.bestMove().first!! }
        return bestMove to (t.toDouble() * 10.0.pow(-9)).toString().substring(0,5)
    }
}

fun main(args: Array<String>) {
    App()
}

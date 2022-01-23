import model.ChessBoard
import model.GameState
import model.Square

/*
 * MIT License
 *
 * Copyright (c) 2022 coriolang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

// regex for players input
private val regex = Regex("[a-h][1-8][a-h][1-8]")

fun main() {
    val programName = "Pawns-Only Chess"
    println(programName)

    println("First Player's name:")
    val firstPlayersName = readLine()!!
    println("Second Player's name:")
    val secondPlayersName = readLine()!!

    val chessboard = ChessBoard()
    println(getBoard(chessboard))

    var usersInput: String

    while (chessboard.gameState != GameState.END_GAME) {
        when (chessboard.gameState) {
            GameState.FIRST_PLAYERS_TURN -> {
                println("$firstPlayersName's turn:")
            }
            GameState.SECOND_PLAYERS_TURN -> {
                println("$secondPlayersName's turn:")
            }
            else -> continue
        }

        usersInput = readLine()!!
        makeTurn(usersInput, chessboard)

        when (chessboard.gameState) {
            GameState.FIRST_PLAYERS_TURN, GameState.SECOND_PLAYERS_TURN -> {
                println(getBoard(chessboard))
            }
            GameState.INVALID_INPUT -> {
                println("Invalid Input")
                chessboard.undoTurn()
            }
            GameState.NO_WHITE_FIGURE_AT_SQUARE -> {
                println("No white pawn at ${usersInput.substring(0, 2)}")
                chessboard.undoTurn()
            }
            GameState.NO_BLACK_FIGURE_AT_SQUARE -> {
                println("No black pawn at ${usersInput.substring(0, 2)}")
                chessboard.undoTurn()
            }
            GameState.FIRST_PLAYER_WINS -> {
                println("White Wins!")
            }
            GameState.SECOND_PLAYER_WINS -> {
                println("Black Wins!")
            }
            GameState.STALEMATE -> {
                println("Stalemate!")
            }
            else -> continue
        }
    }

    println("Bye!")
}

private fun getBoard(chessboard: ChessBoard): String {
    var board = ""
    val horizontalBorder = "  +---+---+---+---+---+---+---+---+\n"

    for (i in 7 downTo 0) {
        board += horizontalBorder
        board += "${i + 1} |"
        for (j in 0..7) {
            val square = Square(i, j)

            board += if (chessboard.isWhiteFigure(square)) {
                " W |"
            } else if (chessboard.isBlackFigure(square)) {
                " B |"
            } else {
                "   |"
            }
        }
        board += "\n"
    }
    board += horizontalBorder
    board += "    a   b   c   d   e   f   g   h\n"

    return board
}

private fun makeTurn(usersInput: String, chessboard: ChessBoard) {
    if (usersInput.matches(regex)) {
        val start = convertPositionToSquare(usersInput.substring(0, 2))
        val destination = convertPositionToSquare(usersInput.substring(2))

        chessboard.makeTurn(start, destination)
    } else if (usersInput == "exit") {
        chessboard.gameState = GameState.END_GAME
    } else {
        chessboard.gameState = GameState.INVALID_INPUT
    }
}

private fun convertPositionToSquare(position: String): Square {
    return Square(
        convertVerticalDigitToIndex(position[1]),
        convertHorizontalLetterToIndex(position[0])
    )
}

private fun convertHorizontalLetterToIndex(letter: Char): Int {
    return when (letter) {
        'a' -> 0
        'b' -> 1
        'c' -> 2
        'd' -> 3
        'e' -> 4
        'f' -> 5
        'g' -> 6
        'h' -> 7
        else -> -1
    }
}

private fun convertVerticalDigitToIndex(digit: Char) = digit.digitToInt() - 1

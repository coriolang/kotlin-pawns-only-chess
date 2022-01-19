/*
 * MIT License
 *
 * Copyright (c) 2021 coriolang
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

package chess

import kotlin.math.abs

enum class CurrentTurn { FIRST_PLAYERS_TURN, SECOND_PLAYERS_TURN }

class ChessBoard {

    private val chessboard = mutableListOf(
        mutableListOf(" ", " ", " ", " ", " ", " ", " ", " "), // 0
        mutableListOf("W", "W", "W", "W", "W", "W", "W", "W"), // 1
        mutableListOf(" ", " ", " ", " ", " ", " ", " ", " "), // 2
        mutableListOf(" ", " ", " ", " ", " ", " ", " ", " "), // 3
        mutableListOf(" ", " ", " ", " ", " ", " ", " ", " "), // 4
        mutableListOf(" ", " ", " ", " ", " ", " ", " ", " "), // 5
        mutableListOf("B", "B", "B", "B", "B", "B", "B", "B"), // 6
        mutableListOf(" ", " ", " ", " ", " ", " ", " ", " ") // 7
    )

    private val previousChessboardState = mutableListOf(
        mutableListOf(" ", " ", " ", " ", " ", " ", " ", " "), // 0
        mutableListOf("W", "W", "W", "W", "W", "W", "W", "W"), // 1
        mutableListOf(" ", " ", " ", " ", " ", " ", " ", " "), // 2
        mutableListOf(" ", " ", " ", " ", " ", " ", " ", " "), // 3
        mutableListOf(" ", " ", " ", " ", " ", " ", " ", " "), // 4
        mutableListOf(" ", " ", " ", " ", " ", " ", " ", " "), // 5
        mutableListOf("B", "B", "B", "B", "B", "B", "B", "B"), // 6
        mutableListOf(" ", " ", " ", " ", " ", " ", " ", " ") // 7
    )

    private var _currentTurn = CurrentTurn.FIRST_PLAYERS_TURN
    val currentTurn get() = _currentTurn

    fun getBoard(): String {
        var board: String = ""
        val horizontalBorder = "  +---+---+---+---+---+---+---+---+\n"

        for (i in 7 downTo 0) {
            board += horizontalBorder
            board += "${i + 1} |"
            for (j in 0..7) {
                board += " ${chessboard[i][j]} |"
            }
            board += "\n"
        }
        board += horizontalBorder
        board += "    a   b   c   d   e   f   g   h\n"

        return board
    }

    fun makeMove(move: String): Boolean {
        val start = convertPositionToSquare(move.substring(0, 2))
        val destination = convertPositionToSquare(move.substring(2))

        val isCorrectMove = isCorrectMove(start, destination)
        val isCorrectCapture = isCorrectCapture(start, destination)
        val isCorrectEnPassant = isCorrectEnPassant(start, destination)

        return if (isCorrectMove
            || isCorrectCapture
            || isCorrectEnPassant) {

            for (i in 0 until chessboard.size) {
                for (j in 0 until chessboard[i].size) {
                    previousChessboardState[i][j] = chessboard[i][j];
                }
            }

            if (isCorrectEnPassant) {
                if (_currentTurn == CurrentTurn.FIRST_PLAYERS_TURN) {
                    chessboard[destination.horizontal - 1][destination.vertical] = " "
                } else {
                    chessboard[destination.horizontal + 1][destination.vertical] = " "
                }
            }

            chessboard[destination.horizontal][destination.vertical] =
                chessboard[start.horizontal][start.vertical]

            chessboard[start.horizontal][start.vertical] = " "

            _currentTurn = if (_currentTurn == CurrentTurn.FIRST_PLAYERS_TURN) {
                CurrentTurn.SECOND_PLAYERS_TURN
            } else {
                CurrentTurn.FIRST_PLAYERS_TURN
            }

            true
        } else {
            false
        }
    }

    private fun isCorrectMove(start: Square, destination: Square): Boolean {
        // Blank square
        if (isBlankSquare(start)) {
            return false
        }

        // The square is taken by another pawn
        if (!isBlankSquare(destination)) {
            return false
        }

        // Not correct straight move
        if (start.vertical != destination.vertical) {
            return false
        }

        if (_currentTurn == CurrentTurn.FIRST_PLAYERS_TURN) {
            // Not correct color
            if (isBlackPawn(start)) {
                return false
            }

            // The white pawn moving to two squares from starting position or one square from other positions
            if ((start.horizontal - destination.horizontal) != -1) {
                if ((start.horizontal - destination.horizontal) != -2) {
                    return false
                } else if (start.horizontal != 1) {
                    return false
                } else if (!isBlankSquare(Square(start.horizontal + 1, start.vertical))) {
                    return false
                }
            }
        } else {
            // Not correct color
            if (isWhitePawn(start)) {
                return false
            }

            // The black pawn moving to two squares from starting position or one square from other positions
            if ((start.horizontal - destination.horizontal) != 1) {
                if ((start.horizontal - destination.horizontal) != 2) {
                    return false
                } else if (start.horizontal != 6) {
                    return false
                } else if (!isBlankSquare(Square(start.horizontal - 1, start.vertical))) {
                    return false
                }
            }
        }

        return true
    }

    private fun isCorrectCapture(start: Square, destination: Square): Boolean {
        if (_currentTurn == CurrentTurn.FIRST_PLAYERS_TURN) {
            // Not correct color
            if (isBlackPawn(start)) {
                return false
            }

            // Blank square
            if (isBlankSquare(destination) || isWhitePawn(destination)) {
                return false
            }

            // Correct capture
            if ((start.horizontal - destination.horizontal) == -1 &&
                (abs(start.vertical - destination.vertical) == 1)) {

                return true
            }
        } else {
            // Not correct color
            if (isWhitePawn(start)) {
                return false
            }

            // Blank square
            if (isBlankSquare(destination) || isBlackPawn(destination)) {
                return false
            }

            // Correct capture
            if ((start.horizontal - destination.horizontal) == 1 &&
                    (abs(start.vertical - destination.vertical) == 1)) {

                return true
            }
        }

        return false
    }

    private fun isCorrectEnPassant(start: Square, destination: Square): Boolean {
        // Not blank square
        if (!isBlankSquare(destination)) {
            return false
        }

        if (_currentTurn == CurrentTurn.FIRST_PLAYERS_TURN) {
            // Not correct color
            if (isBlackPawn(start)) {
                return false
            }

            // En passant
            if (start.horizontal == 4) {
                if (start.vertical != 0 && isBlackPawn(Square(start.horizontal, start.vertical - 1))) {
                    if ((start.horizontal - destination.horizontal) == -1 &&
                        (start.vertical - destination.vertical) == 1) {
                        if (destination.horizontal != 0
                            && previousChessboardState[destination.horizontal - 1][destination.vertical] == " ") {

                            return true
                        }
                    }
                }
            }
        } else {
            // Not correct color
            if (isWhitePawn(start)) {
                return false
            }

            // En passant
            if (start.horizontal == 3) {
                if (start.vertical != 7 && isWhitePawn(Square(start.horizontal, start.vertical + 1))) {
                    if ((start.horizontal - destination.horizontal) == 1 &&
                        (start.vertical - destination.vertical) == -1) {
                        if (destination.horizontal != 7
                            && previousChessboardState[destination.horizontal + 1][destination.vertical] == " ") {

                            return true
                        }
                    }
                }
            }
        }

        return false
    }

    fun isWhiteWins(): Boolean {
        for (i in 0..7) {
            if (isWhitePawn(Square(7, i))) {
                return true
            }
        }

        for (i in 0..7) {
            for (j in 0..7) {
                if (isBlackPawn(Square(i, j))) {
                    return false
                }
            }
        }

        return true
    }

    fun isBlackWins(): Boolean {
        for (i in 0..7) {
            if (isBlackPawn(Square(0, i))) {
                return true
            }
        }

        for (i in 0..7) {
            for (j in 0..7) {
                if (isWhitePawn(Square(i, j))) {
                    return false
                }
            }
        }

        return true
    }

    fun isStalemate(): Boolean {
        if (_currentTurn == CurrentTurn.FIRST_PLAYERS_TURN) {
            for (i in 0..7) {
                for (j in 0..7) {
                    val start = Square(i, j)

                    if (isWhitePawn(start)) {
                        val destinationMove = Square(i + 1, j)
                        val destinationMove2 = Square(i + 2, j)

                        if (isCorrectMove(start, destinationMove)
                            || isCorrectMove(start, destinationMove2)) {

                            return false
                        }

                        if (j != 0) {
                            val destinationCapture = Square(i + 1, j - 1)
                            val destinationEnPassant = Square(i + 1, j - 1)

                            if (isCorrectCapture(start, destinationCapture)
                                || isCorrectEnPassant(start, destinationEnPassant)) {

                                return false
                            }
                        }

                        if (j != 7) {
                            val destinationCapture = Square(i + 1, j + 1)
                            if (isCorrectCapture(start, destinationCapture)) {
                                return false
                            }
                        }
                    }
                }
            }
        } else {
            for (i in 0..7) {
                for (j in 0..7) {
                    val start = Square(i, j)

                    if (isBlackPawn(start)) {
                        val destinationMove = Square(i - 1, j)
                        val destinationMove2 = Square(i - 2, j)

                        if (isCorrectMove(start, destinationMove)
                            || isCorrectMove(start, destinationMove2)) {

                            return false
                        }

                        if (j != 0) {
                            val destinationCapture = Square(i - 1, j - 1)
                            if (isCorrectCapture(start, destinationCapture)) {
                                return false
                            }
                        }

                        if (j != 7) {
                            val destinationCapture = Square(i - 1, j + 1)
                            val destinationEnPassant = Square(i - 1, j + 1)

                            if (isCorrectCapture(start, destinationCapture)
                                || isCorrectEnPassant(start, destinationEnPassant)) {

                                return false
                            }
                        }
                    }
                }
            }
        }

        return true
    }

    fun isWhitePawn(position: String): Boolean {
        val square = convertPositionToSquare(position)
        return isWhitePawn(square)
    }

    fun isBlackPawn(position: String): Boolean {
        val square = convertPositionToSquare(position)
        return isBlackPawn(square)
    }

    private fun isWhitePawn(square: Square) = chessboard[square.horizontal][square.vertical] == "W"

    private fun isBlackPawn(square: Square) = chessboard[square.horizontal][square.vertical] == "B"

    private fun isBlankSquare(square: Square) = chessboard[square.horizontal][square.vertical] == " "

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
}

class Square(
    val horizontal: Int,
    val vertical: Int
    )

fun main() {
    val programName = "Pawns-Only Chess"
    println(programName)

    println("First Player's name:")
    val firstPlayersName = readLine()!!
    println("Second Player's name:")
    val secondPlayersName = readLine()!!

    val chessboard = ChessBoard()
    println(chessboard.getBoard())

    // regex for players input
    val regex = Regex("[a-h][1-8][a-h][1-8]")

    var usersInput = ""

    while (usersInput != "exit") {

        if (chessboard.isStalemate()) {
            println("Stalemate!\nBye!")
            break
        }

        if (chessboard.currentTurn == CurrentTurn.FIRST_PLAYERS_TURN) {
            println("$firstPlayersName's turn:")
        } else {
            println("$secondPlayersName's turn:")
        }

        usersInput = readLine()!!

        var message = "Invalid Input"

        if (usersInput.matches(regex)) {
            if (chessboard.makeMove(usersInput)) {
                message = chessboard.getBoard()
            } else if (chessboard.currentTurn == CurrentTurn.FIRST_PLAYERS_TURN
                && !chessboard.isWhitePawn(usersInput.substring(0, 2))) {

                message = "No white pawn at ${usersInput.substring(0, 2)}"
            } else if (!chessboard.isBlackPawn(usersInput.substring(0, 2))) {
                message = "No black pawn at ${usersInput.substring(0, 2)}"
            }
        } else if (usersInput == "exit") {
            message = "Bye!"
        }

        println(message)

        if (chessboard.isWhiteWins()) {
            message = "White Wins!\nBye!"
            println(message)
            break
        } else if (chessboard.isBlackWins()) {
            message = "Black Wins!\nBye!"
            println(message)
            break
        }
    }
}

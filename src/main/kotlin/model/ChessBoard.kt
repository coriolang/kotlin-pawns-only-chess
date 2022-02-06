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

package model

import kotlin.math.abs

class ChessBoard : Cloneable {

    private var _gameState = GameState.FIRST_PLAYERS_TURN
    var gameState
        get() = _gameState
        set(value) {
            _gameState = value
        }

    private var _whiteFigures: MutableList<Figure> = mutableListOf(
        Pawn(Square(1, 0)),
        Pawn(Square(1, 1)),
        Pawn(Square(1, 2)),
        Pawn(Square(1, 3)),
        Pawn(Square(1, 4)),
        Pawn(Square(1, 5)),
        Pawn(Square(1, 6)),
        Pawn(Square(1, 7))
    )

    private var _blackFigures: MutableList<Figure> = mutableListOf(
        Pawn(Square(6, 0)),
        Pawn(Square(6, 1)),
        Pawn(Square(6, 2)),
        Pawn(Square(6, 3)),
        Pawn(Square(6, 4)),
        Pawn(Square(6, 5)),
        Pawn(Square(6, 6)),
        Pawn(Square(6, 7))
    )

    companion object {
        private val previousTurns: MutableList<ChessBoard> = mutableListOf()
    }

    fun makeTurn(start: Square, destination: Square) {
        when (_gameState) {
            GameState.FIRST_PLAYERS_TURN -> {
                val isCorrectMove = isCorrectMove(start, destination)
                val isCorrectCapture = isCorrectCapture(start, destination)
                val isCorrectEnPassant = isCorrectEnPassant(start, destination)

                if (isBlankSquare(start) || isBlackFigure(start)) {
                    _gameState = GameState.NO_WHITE_FIGURE_AT_SQUARE
                } else if (isCorrectMove || isCorrectCapture || isCorrectEnPassant) {
                    previousTurns.add(this.clone())

                    if (isCorrectEnPassant) {
                        val capturedFigure = Square(destination.horizontal - 1, destination.vertical)
                        _blackFigures.removeIf { figure ->
                            figure.square == capturedFigure
                        }
                    }

                    if (isCorrectCapture) {
                        _blackFigures.removeIf { figure ->
                            figure.square == destination
                        }
                    }

                    _whiteFigures.find { figure ->
                        figure.square == start
                    }?.square = destination

                    _gameState = GameState.SECOND_PLAYERS_TURN

                    val isStalemate = isStalemate()
                    if (isStalemate) {
                        _gameState = GameState.STALEMATE
                    }

                    val isWhiteWins = isWhiteWins()
                    if (isWhiteWins) {
                        _gameState = GameState.FIRST_PLAYER_WINS
                    }
                } else {
                    _gameState = GameState.INVALID_INPUT
                }
            }
            GameState.SECOND_PLAYERS_TURN -> {
                val isCorrectMove = isCorrectMove(start, destination)
                val isCorrectCapture = isCorrectCapture(start, destination)
                val isCorrectEnPassant = isCorrectEnPassant(start, destination)

                if (isBlankSquare(start) || isWhiteFigure(start)) {
                    _gameState = GameState.NO_BLACK_FIGURE_AT_SQUARE
                } else if (isCorrectMove || isCorrectCapture || isCorrectEnPassant) {
                    previousTurns.add(this.clone())

                    if (isCorrectEnPassant) {
                        val capturedFigure = Square(destination.horizontal + 1, destination.vertical)
                        _whiteFigures.removeIf { figure ->
                            figure.square == capturedFigure
                        }
                    }

                    if (isCorrectCapture) {
                        _whiteFigures.removeIf { figure ->
                            figure.square == destination
                        }
                    }

                    _blackFigures.find { figure ->
                        figure.square == start
                    }?.square = destination

                    _gameState = GameState.FIRST_PLAYERS_TURN

                    val isStalemate = isStalemate()
                    if (isStalemate) {
                        _gameState = GameState.STALEMATE
                    }

                    val isBlackWins = isBlackWins()
                    if (isBlackWins) {
                        _gameState = GameState.SECOND_PLAYER_WINS
                    }
                } else {
                    _gameState = GameState.INVALID_INPUT
                }
            }
            GameState.FIRST_PLAYER_WINS,
            GameState.SECOND_PLAYER_WINS,
            GameState.STALEMATE -> {
                _gameState = GameState.END_GAME
            }
            else -> return
        }
    }

    fun undoTurn() {
        when (_gameState) {
            GameState.INVALID_INPUT -> {
                if (previousTurns.isEmpty()
                    || previousTurns.last()._gameState == GameState.SECOND_PLAYERS_TURN) {

                    _gameState = GameState.FIRST_PLAYERS_TURN
                } else if (previousTurns.last()._gameState == GameState.FIRST_PLAYERS_TURN) {
                    _gameState = GameState.SECOND_PLAYERS_TURN
                }
            }
            GameState.NO_WHITE_FIGURE_AT_SQUARE -> {
                _gameState = GameState.FIRST_PLAYERS_TURN
            }
            GameState.NO_BLACK_FIGURE_AT_SQUARE -> {
                _gameState = GameState.SECOND_PLAYERS_TURN
            }
            else -> return
        }
    }

    private fun isCorrectMove(start: Square, destination: Square): Boolean {
        // The square is taken by another pawn
        if (!isBlankSquare(destination)) {
            return false
        }

        // Not correct straight move
        if (start.vertical != destination.vertical) {
            return false
        }

        // The white pawn moving to two squares from starting position or one square from other positions
        val horizontalMoving = start.horizontal - destination.horizontal

        if (_gameState == GameState.FIRST_PLAYERS_TURN) {
            if (horizontalMoving != -1
                && (horizontalMoving != -2 || start.horizontal != 1
                        || !isBlankSquare(Square(start.horizontal + 1, start.vertical)))) {

                return false
            }
        } else if (_gameState == GameState.SECOND_PLAYERS_TURN) {
            if (horizontalMoving != 1
                && (horizontalMoving != 2 || start.horizontal != 6
                        || !isBlankSquare(Square(start.horizontal - 1, start.vertical)))) {

                return false
            }
        }

        return true
    }

    private fun isCorrectCapture(start: Square, destination: Square): Boolean {
        if (_gameState == GameState.FIRST_PLAYERS_TURN) {
            // Blank square
            if (isBlankSquare(destination) || isWhiteFigure(destination)) {
                return false
            }

            // Correct capture
            if ((start.horizontal - destination.horizontal) == -1 &&
                (abs(start.vertical - destination.vertical) == 1)) {

                return true
            }
        } else if (_gameState == GameState.SECOND_PLAYERS_TURN) {
            // Blank square
            if (isBlankSquare(destination) || isBlackFigure(destination)) {
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

        if (_gameState == GameState.FIRST_PLAYERS_TURN) {
            // En passant
            if (start.horizontal == 4
                && start.vertical != 0 && isBlackFigure(Square(start.horizontal, start.vertical - 1))
                && (start.horizontal - destination.horizontal) == -1 && (start.vertical - destination.vertical) == 1
                && destination.horizontal != 0
                && previousTurns.last().isBlankSquare(
                    Square(destination.horizontal - 1, destination.vertical))
            ) {

                return true
            }
        } else if (_gameState == GameState.SECOND_PLAYERS_TURN) {
            // En passant
            if (start.horizontal == 3
                && start.vertical != 7 && isWhiteFigure(Square(start.horizontal, start.vertical + 1))
                && (start.horizontal - destination.horizontal) == 1 && (start.vertical - destination.vertical) == -1
                && destination.horizontal != 7
                && previousTurns.last().isBlankSquare(
                    Square(destination.horizontal + 1, destination.vertical))
            ) {

                return true
            }
        }

        return false
    }

    private fun isWhiteWins(): Boolean {
        for (i in 0..7) {
            if (isWhiteFigure(Square(7, i))) {
                return true
            }
        }

        if (_blackFigures.isEmpty()) {
            return true
        }

        return false
    }

    private fun isBlackWins(): Boolean {
        for (i in 0..7) {
            if (isBlackFigure(Square(0, i))) {
                return true
            }
        }

        if (_whiteFigures.isEmpty()) {
            return true
        }

        return false
    }

    private fun isStalemate(): Boolean {
        if (_gameState == GameState.FIRST_PLAYERS_TURN) {
            for (figure: Figure in _whiteFigures) {
                val start = figure.square

                if (isCorrectMove(start, Square(start.horizontal + 1, start.vertical))
                    || isCorrectMove(start, Square(start.horizontal + 2, start.vertical))) {

                    return false
                }

                val destinationEnPassant = Square(start.horizontal + 1, start.vertical - 1)

                if (start.vertical != 0
                    && (isCorrectCapture(start, destinationEnPassant)
                            || isCorrectEnPassant(start, destinationEnPassant))) {

                    return false
                }

                if (start.vertical != 7
                    && isCorrectCapture(start, Square(start.horizontal + 1, start.vertical + 1))) {

                    return false
                }
            }
        } else if (_gameState == GameState.SECOND_PLAYERS_TURN) {
            for (figure: Figure in _blackFigures) {
                val start = figure.square

                if (isCorrectMove(start, Square(start.horizontal - 1, start.vertical))
                    || isCorrectMove(start, Square(start.horizontal - 2, start.vertical))) {

                    return false
                }

                if (start.vertical != 0
                    && isCorrectCapture(start, Square(start.horizontal - 1, start.vertical - 1))) {

                    return false
                }

                val destinationEnPassant = Square(start.horizontal - 1, start.vertical + 1)

                if (start.vertical != 7
                    && (isCorrectCapture(start, destinationEnPassant)
                            || isCorrectEnPassant(start, destinationEnPassant))) {

                    return false
                }
            }
        }

        return true
    }

    fun isWhiteFigure(square: Square): Boolean {
        val foundFigure: Figure? = _whiteFigures.find { figure ->
            figure.square == square
        }

        return foundFigure != null
    }

    fun isBlackFigure(square: Square): Boolean {
        val foundFigure: Figure? = _blackFigures.find { figure ->
            figure.square == square
        }

        return foundFigure != null
    }

    private fun isBlankSquare(square: Square): Boolean {
        val hasWhiteFigure = _whiteFigures.any { figure ->
            figure.square == square
        }

        val hasBlackFigure = _blackFigures.any { figure ->
            figure.square == square
        }

        return !hasWhiteFigure && !hasBlackFigure
    }

    override fun clone(): ChessBoard {
        val clonedChessboard = ChessBoard()

        clonedChessboard._gameState = this._gameState

        clonedChessboard._whiteFigures = mutableListOf()
        for (figure in this._whiteFigures) {
            clonedChessboard._whiteFigures.add(figure.clone() as Figure)
        }

        clonedChessboard._blackFigures = mutableListOf()
        for (figure in this._blackFigures) {
            clonedChessboard._blackFigures.add(figure.clone() as Figure)
        }

        return clonedChessboard
    }
}

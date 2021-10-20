package chess

enum class CurrentTurn { FIRST_PLAYERS_TURN, SECOND_PLAYERS_TURN }

fun main() {
    val programName = "Pawns-Only Chess"
    println(programName)

    println("First Player's name:")
    val firstPlayersName = readLine()!!
    println("Second Player's name:")
    val secondPlayersName = readLine()!!

    println(chessboard())

    // regex for "[letter][digit][letter][digit]" \w - alphanumerical characters and \D - not-digit characters
    // val regex = Regex("[\\w\\D]\\d[\\w\\D]\\d")
    // regex for players input
    val regex = Regex("[a-h][1-8][a-h][1-8]")

    var usersInput = ""
    var currentTurn = CurrentTurn.FIRST_PLAYERS_TURN

    while (usersInput != "exit") {
        if (currentTurn == CurrentTurn.FIRST_PLAYERS_TURN) {
            println("$firstPlayersName's turn:")
        } else {
            println("$secondPlayersName's turn:")
        }

        usersInput = readLine()!!

        if (usersInput.matches(regex)) {
            currentTurn = if (currentTurn == CurrentTurn.FIRST_PLAYERS_TURN) {
                CurrentTurn.SECOND_PLAYERS_TURN
            } else {
                CurrentTurn.FIRST_PLAYERS_TURN
            }
        } else if (usersInput == "exit") {
            println("Bye!")
            break
        } else {
            println("Invalid Input")
            continue
        }
    }
}

fun chessboard(): String {
    var chessboard: String = ""
    val horizontalBorder = "  +---+---+---+---+---+---+---+---+\n"

    val blackPawn = "B"
    val whitePawn = "W"
    val blankCell = " "

    for (i in 8 downTo 1) {
        chessboard += horizontalBorder
        chessboard += "$i |"
        for (j in 1..8) {
            chessboard += when (i) {
                7 -> " $blackPawn |"
                2 -> " $whitePawn |"
                else -> " $blankCell |"
            }
        }
        chessboard += "\n"
    }
    chessboard += horizontalBorder
    chessboard += "    a   b   c   d   e   f   g   h"

    return chessboard
}

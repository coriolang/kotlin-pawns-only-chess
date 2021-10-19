package chess

fun main() {
    val programName = "Pawns-Only Chess"

    println(programName)
    println(chessboard())
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

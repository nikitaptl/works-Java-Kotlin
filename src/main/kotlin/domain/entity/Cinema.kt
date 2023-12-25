package domain.entity

import domain.constants.Constants.Companion.LEFT_SIZE
import domain.constants.Constants.Companion.RIGHT_SIZE
import kotlinx.serialization.Serializable

object ConsoleColors {
    const val RESET = "\u001B[0m"
    const val RED = "\u001B[31m"
}

enum class Condition {
    FREE, BOOKED, OCCUPIED
}

const val ASCII_A = 65 // Номер 'A' в ASCII таблице

@Serializable
class Place(
    val number: String,
    var condition: Condition
)

@Serializable
class Cinema {
    val places: Array<Array<Place>> = Array(LEFT_SIZE) { Array(RIGHT_SIZE) { Place("0", Condition.FREE) } }

    init { // Заполняем массив свободными местами
        for (j in 0..(LEFT_SIZE - 1)) {
            for (char in ASCII_A..(ASCII_A + RIGHT_SIZE - 1)) {
                places[j][char - ASCII_A] = Place(char.toChar().toString() + j, Condition.FREE)
            }
        }
    }

    // Вывод на экран всех мест
    // Если место свободно, оно помечается красным
    override fun toString(): String {
        var result = ""
        for (el in places) {
            for (m in el) {
                if (m.condition == Condition.OCCUPIED || m.condition == Condition.BOOKED) {
                    result += ConsoleColors.RED
                    result += m.number + " "
                    result += ConsoleColors.RESET
                } else {
                    result += m.number + " "
                }
            }
            result += "\n"
        }
        return result
    }
}
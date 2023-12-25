package presentation.formatting

fun PrintMenu() {
    println("Введите цифру от 0-8, чтобы: ")
    println("${"\u001B[32m"}0 - Добавить новый фильм в афишу")
    println("1 - Убрать фильм из афиши")
    println("2 - изменить данные о фильме в афише")
    println("3 - продать билет")
    println("4 - вернуть билет")
    println("5 - посмотреть список фильмов")
    println("6 - посмотреть список свободных мест для фильма")
    println("7 - отметить зашедшего человека")
    println("8 - прекратить выполнение программы${"\u001B[0m"}")
}

fun printRed(str: String) {
    println("${"\u001B[31m"}${str}${"\u001B[0m"}")
}

fun getInt(): Int {
    var number: Int? = readLine()?.toIntOrNull()

    while (number == null) {
        printRed("Вы ввели неправильный номер. Попробуйте ещё раз: ")
        number = readLine()?.toIntOrNull()
    }
    return number
}
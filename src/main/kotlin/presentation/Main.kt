package presentation

import data.RuntimeCinemaManagementDao
import domain.CinemaManagementControllerImpl
import domain.CinemaManagementValidatorImpl
import domain.converting.CinemaManagementConverterImpl
import presentation.formatting.PrintMenu
import presentation.formatting.getInt
import presentation.formatting.printRed

fun main() {
    val dao = RuntimeCinemaManagementDao()
    val converter = CinemaManagementConverterImpl()
    val validator = CinemaManagementValidatorImpl(converter)
    val cinema = CinemaManagementControllerImpl(dao, validator)

    var flag = true
    while (flag) {
        PrintMenu()

        val num = getInt()
        if (num == 0) {
            println("Введите имя фильма: ")
            val name = readLine()
            println("Введите цену билета: ")
            val cost = readLine()
            println("Введите дату начала показа в формате Час:Минута День.Месяц.Год")
            val dateStart = readLine()
            println("Введите длительность показа фильма в формате Часы:Минуты")
            val duration = readLine()
            if (name == null || cost == null || dateStart == null || duration == null) {
                printRed("Одно из введённых вами значений является пустым. Попробуйте ещё раз")
            }
            println(cinema.createFilm(name as String, cost as String, dateStart as String, duration as String))
        } else if (num == 1) {
            println("Введите имя фильма: ")
            val name = readLine()
            println(cinema.removeFilm(name))
        } else if (num == 2) {
            println("Введите имя фильма: ")
            val name = readLine()
            println("Введите цену билета: ")
            val cost = readLine()
            println("Введите дату начала показа в формате Час:Минута День.Месяц.Год")
            val dateStart = readLine()
            println("Введите длительность показа фильма в формате Часы:Минуты")
            val duration = readLine()
            if (name == null || cost == null || dateStart == null || duration == null) {
                printRed("Одно из введённых вами значений является пустым. Попробуйте ещё раз")
            }
            println(cinema.editFilm(name as String, cost as String, dateStart as String, duration as String))
        } else if (num == 3) {
            println("Введите имя фильма: ")
            val name = readLine()
            println("Введите название места: ")
            val nameTicket = readLine()
            if (name == null || nameTicket == null) {
                printRed("Одно из введённых вами значений является пустым. Попробуйте ещё раз")
            }
            println(cinema.cellTicket(name as String, nameTicket as String))
        } else if (num == 4) {
            println("Введите имя фильма: ")
            val name = readLine()
            println("Введите название места: ")
            val nameTicket = readLine()
            if (name == null || nameTicket == null) {
                printRed("Одно из введённых вами значений является пустым. Попробуйте ещё раз")
            }
            println(cinema.refundTicket(name as String, nameTicket as String))
        } else if (num == 5) {
            println(cinema.getFilms())
        } else if (num == 6) {
            println("Введите имя фильма: ")
            val name = readLine()
            if (name == null) {
                printRed("Вы ввели пустое значение. Попробуйте ещё раз!")
            }
            cinema.printPlace(name as String)
        } else if (num == 7) {
            println("Введите имя фильма: ")
            val name = readLine()
            println("Введите название места: ")
            val nameTicket = readLine()
            if (name == null || nameTicket == null) {
                printRed("Одно из введённых вами значений является пустым. Попробуйте ещё раз")
            }
            println(cinema.markInList(name as String, nameTicket as String))
        } else if (num == 8) {
            flag = false
        } else {
            printRed("Вы не ввели число в диапозоне!")
        }
    }
}
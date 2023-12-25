package domain.converting

import java.time.Duration
import java.time.LocalDateTime

const val DATE_SIZE = 5 // Формат ввода даты в консоль: Час:Минута День.Месяц.Год, т.е 5 целочисленных чисел
const val MAX_HOURS = 24 // Максимальное количество часов, которое мы задаём в продолжительности сеанса
const val MAX_MINUTES = 60 // Соответствующее максимальное количество минут

interface CinemaManagementConverter {
    fun parseDate(dateString: String): LocalDateTime?
    fun parseDuration(durationString: String): Duration?
}

class CinemaManagementConverterImpl : CinemaManagementConverter {
    override fun parseDate(dateString: String): LocalDateTime? {
        // Проверяем, не пуста ли строка
        if (dateString.isEmpty()) {
            return null
        }

        // Разделяем значения из формата DATE_SIZE и сохраням в массив
        val parts = dateString.split(".", ":", " ")
        if (parts.size != DATE_SIZE) {
            return null
        }

        // Проверяем, конвертируются ли значения в Int
        val integers = IntArray(DATE_SIZE)
        parts.forEachIndexed { index, element ->
            val num = element.toIntOrNull() ?: return null
            integers[index] = num
        }

        // Пробуем создать LocalDateTime переменную из введённых значений
        // Если возникло исключение - значит неверный формат ввода
        val date: LocalDateTime
        try {
            date = LocalDateTime.of(
                integers[DATE_SIZE - 1], // Год
                integers[DATE_SIZE - 2], // Месяц
                integers[2], // День
                integers[0], // Час
                integers[1] // Минута
            )
        } catch (e: Exception) {
            return null
        }
        return date
    }

    override fun parseDuration(durationString: String): Duration? {
        if (durationString.isEmpty()) {
            return null
        }

        // Формат ввода продолжительности сеанса - Кол-во часов/Кол-во минут
        // Следовательно, два Int значения. Проверяем, соответствует ли этому строка
        val parts = durationString.split(":")
        if (parts.size != 2) {
            return null
        }

        // Проверяем, конвертируются ли значения в Int
        val integers = IntArray(2)
        parts.forEachIndexed { index, element ->
            val num = element.toIntOrNull() ?: return null
            integers[index] = num
        }

        // Если пользователь ввёл продолжительность, например 2:100 или 25:20, то это тоже некорректный ввод
        if (integers[0] >= MAX_MINUTES || integers[1] >= MAX_MINUTES) {
            return null
        }

        val duration: Duration
        try {
            duration = Duration.ofHours(integers[0].toLong()).plusMinutes(integers[1].toLong())
        } catch (e: Exception) {
            return null
        }
        return duration
    }
}
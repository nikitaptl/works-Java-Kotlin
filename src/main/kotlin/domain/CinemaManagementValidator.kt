package domain

import data.CinemaManagementDao
import domain.converting.CinemaManagementConverter
import domain.entity.Condition
import domain.entity.FilmSessionEntity
import domain.entity.SessionTime
import presentation.model.OutputModel
import java.time.LocalDateTime

sealed class Result

class SuccessResult<T>(val result: T) : Result()

class Error(val outputModel: OutputModel) : Result()

interface CinemaManagementValidator {
    fun validateAdding(
        cinemaManagementDao: CinemaManagementDao,
        name: String,
        costString: String,
        dateString: String,
        durationString: String,
        parameter: Int = 1
    ): Result

    fun validateCelling(cinemaManagementDao: CinemaManagementDao, filmName: String, placeNumber: String): Result
    fun validateRefund(cinemaManagementDao: CinemaManagementDao, filmName: String, placeNumber: String): Result
    fun validateRemoving(cinemaManagementDao: CinemaManagementDao, filmName: String?): Result
    fun validateMarking(cinemaManagementDao: CinemaManagementDao, filmName: String?, placeNumber: String?): Result
}

class CinemaManagementValidatorImpl(
    private val cinemaManagementConverter: CinemaManagementConverter
) : CinemaManagementValidator {
    fun validateName(name: String): Result {
        return when {
            name.isEmpty() -> Error(OutputModel("Пустое имя"))
            else -> SuccessResult(name)
        }
    }

    fun validateCost(costString: String): Result {
        val cost = costString.toDoubleOrNull() ?: return Error(OutputModel("Введено некорректное значение"))
        return when {
            cost < 0 -> Error(OutputModel("Отрицательная цена билета!"))
            cost == 0.0 -> Error(OutputModel("Нулевая цена билета!"))
            else -> SuccessResult(cost)
        }
    }

    fun validateDate(dateString: String, durationString: String): Result {
        // parse методы возвращают либо null, если в формате введённых строк есть ошибки,
        // либо уже конвертированные переменные
        val date = cinemaManagementConverter.parseDate(dateString)
        val duration = cinemaManagementConverter.parseDuration(durationString)
        if (date == null || duration == null) {
            return Error(OutputModel("Некорректный формат ввода!"))
        }

        // validateData вызывается либо при редактировании даты начала сеанса, либо при добавления нового фильма
        // в обоих случаях если фильм начинается раньше текущего времени, то сообщаем об ошибке
        val dateNow = LocalDateTime.now()
        if (date < dateNow) {
            return Error(OutputModel("Вы ввели дату, которая уже прошла"))
        }
        return SuccessResult(SessionTime(date, duration))
    }

    override fun validateAdding(
        cinemaManagementDao: CinemaManagementDao,
        name: String,
        costString: String,
        dateString: String,
        durationString: String,
        parameter: Int // Параметром, значением по умолчанию которого является 1
        // Если parameter != 1, значит вместо добавления происходит редактирование
        /* Добавление и редактирование - почти один и тот же процесс, однако редактирование ещё требует проверки
        на существование фильма в словаре */
    ): Result {
        // Проверка на то, является ли имя пустым
        val nameValidation = validateName(name)
        if (nameValidation == Error()) {
            return nameValidation;
        }
        // Если редактирование, проверяем, есть ли в словаре такой фильм
        if (parameter != 1) {
            if (cinemaManagementDao.get(name) == null) {
                return Error(OutputModel("В списках нет фильма с таким названием!"))
            }
        }

        // Проверяем стоимость на корректость
        val costValidation = validateCost(costString)
        val cost: Double
        when {
            costValidation is SuccessResult<*> -> cost = costValidation.result as Double
            else -> return costValidation;
        }

        // Проверяем введённые даты на корректность
        val sessionFilmValidation = validateDate(dateString, durationString)
        val sessionTime: SessionTime
        when {
            sessionFilmValidation is SuccessResult<*> -> sessionTime = sessionFilmValidation.result as SessionTime
            else -> return sessionFilmValidation;
        }

        val filmSession = FilmSessionEntity(name, cost, sessionTime)
        // Проверяем, не накладывается ли расписание
        if (!cinemaManagementDao.checkForIntersections(filmSession, parameter)) {
            return Error(OutputModel("Расписание этого фильма накладывается на уже существующие расписания!"))
        }
        return SuccessResult(filmSession)
    }

    override fun validateCelling(
        cinemaManagementDao: CinemaManagementDao,
        filmName: String,
        placeNumber: String
    ): Result {
        val result =
            cinemaManagementDao.get(filmName) ?: return Error(OutputModel("Фильма с таким именем нет в афише!"))
        result.cinema.places.forEach {
            it.forEach {
                if (it.number == placeNumber) {
                    if (it.condition == Condition.FREE) {
                        it.condition = Condition.BOOKED
                        return SuccessResult(result)
                    } else {
                        return Error(OutputModel("Это место занято! Бронирование невозможно!"))
                    }
                }
            }
        }
        return Error(OutputModel("Места с таким номером не существует!"))
    }

    override fun validateRefund(
        cinemaManagementDao: CinemaManagementDao,
        filmName: String,
        placeNumber: String
    ): Result {
        val result =
            cinemaManagementDao.get(filmName) ?: return Error(OutputModel("Фильма с таким именем нет в афише!"))
        if (result.sessionTime.dateStart <= LocalDateTime.now()) {
            return Error(OutputModel("Сеанс уже начался, возврат невозможен!"))
        }
        result.cinema.places.forEach {
            it.forEach {
                if (it.number == placeNumber) {
                    if (it.condition == Condition.BOOKED) {
                        it.condition = Condition.FREE
                        return SuccessResult(result)
                    } else if (it.condition == Condition.FREE) {
                        return Error(OutputModel("Это место не было забронировано! Возврат невозможен!"))
                    } else {
                        return Error(OutputModel("Это место занято! Возврат невозможен!"))
                    }
                }
            }
        }
        return Error(OutputModel("Места с таким номером не существует!"))
    }

    override fun validateRemoving(cinemaManagementDao: CinemaManagementDao, filmName: String?): Result {
        if (filmName == null) {
            return Error(OutputModel("Пустая строка!"))
        }
        if (cinemaManagementDao.get(filmName) == null) {
            return Error(OutputModel("Фильма с таким именем не существует!"))
        }
        return SuccessResult("")
    }

    override fun validateMarking(
        cinemaManagementDao: CinemaManagementDao,
        filmName: String?,
        placeNumber: String?
    ): Result {
        if (filmName == null || placeNumber == null) {
            return Error(OutputModel("Введены пустые значения!"))
        }
        val result =
            cinemaManagementDao.get(filmName) ?: return Error(OutputModel("Фильма с таким именем нет в афише!"))
        result.cinema.places.forEach {
            it.forEach {
                if (it.number == placeNumber) {
                    if (it.condition == Condition.BOOKED) {
                        it.condition = Condition.OCCUPIED
                        return SuccessResult(result)
                    } else {
                        return Error(OutputModel("Это место не забронировано!"))
                    }
                }
            }
        }
        return Error(OutputModel("Места с таким номером не существует!"))
    }
}
// 11:20 22/12/2023
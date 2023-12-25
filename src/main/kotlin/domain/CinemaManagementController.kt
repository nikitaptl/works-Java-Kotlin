package domain

import data.CinemaManagementDao
import data.RuntimeCinemaManagementDao
import domain.entity.FilmSessionEntity
import presentation.model.OutputModel

interface CinemaManagemenController {
    fun createFilm(name: String, costString: String, dateString: String, durationString: String): OutputModel
    fun editFilm(name: String, costString: String, dateString: String, durationString: String): OutputModel
    fun getFilms(): OutputModel
    fun cellTicket(filmName: String, placeName: String): OutputModel
    fun refundTicket(filmName: String, placeName: String): OutputModel
    fun markInList(filmName: String?, placeName: String?): OutputModel
    fun removeFilm(name: String?): OutputModel
    fun printPlace(name: String)
}

class CinemaManagementControllerImpl(
    private val cinemaManagementDao: CinemaManagementDao,
    private val cinemaManagementValidator: CinemaManagementValidator
) : CinemaManagemenController {

    override fun getFilms(): OutputModel {
        val accountUI = cinemaManagementDao.getAll().joinToString()
        return OutputModel(accountUI).takeIf { it.message.isNotEmpty() }
            ?: OutputModel("List is Empty")
    }

    override fun createFilm(name: String, costString: String, dateString: String, durationString: String): OutputModel {
        val validationResult =
            cinemaManagementValidator.validateAdding(cinemaManagementDao, name, costString, dateString, durationString)
        when (validationResult) {
            is Error -> return validationResult.outputModel
            is SuccessResult<*> -> {
                cinemaManagementDao.update(validationResult.result as FilmSessionEntity)
                return OutputModel("Success")
            }

            else -> return OutputModel("Неизвестная ошибка")
        }
    }

    override fun editFilm(name: String, costString: String, dateString: String, durationString: String): OutputModel {
        val validationResult =
            cinemaManagementValidator.validateAdding(
                cinemaManagementDao,
                costString,
                name,
                dateString,
                durationString,
                2
            )
        when (validationResult) {
            is Error -> return validationResult.outputModel
            is SuccessResult<*> -> {
                cinemaManagementDao.update(validationResult.result as FilmSessionEntity)
                return OutputModel("Success")
            }
        }
    }

    override fun cellTicket(filmName: String, placeName: String): OutputModel {
        val result = cinemaManagementValidator.validateCelling(cinemaManagementDao, filmName, placeName)
        when (result) {
            is Error -> return result.outputModel
            is SuccessResult<*> -> {
                cinemaManagementDao.update(result.result as FilmSessionEntity)
                return OutputModel("Success")
            }
        }
    }

    override fun refundTicket(filmName: String, placeName: String): OutputModel {
        val result = cinemaManagementValidator.validateRefund(cinemaManagementDao, filmName, placeName)
        when (result) {
            is Error -> return result.outputModel
            is SuccessResult<*> -> {
                cinemaManagementDao.update(result.result as FilmSessionEntity)
                return OutputModel("Success")
            }
        }
    }

    override fun markInList(filmName: String?, placeName: String?): OutputModel {
        val result = cinemaManagementValidator.validateMarking(cinemaManagementDao, filmName, placeName)
        when (result) {
            is Error -> return result.outputModel
            is SuccessResult<*> -> {
                cinemaManagementDao.update(result.result as FilmSessionEntity)
                return OutputModel("Success")
            }
        }
    }

    override fun removeFilm(name: String?): OutputModel {
        val result = cinemaManagementValidator.validateRemoving(cinemaManagementDao, name)
        when (result) {
            is Error -> return result.outputModel
            else -> {
                return OutputModel("Success")
            }
        }
    }

    override fun printPlace(name: String) {
        when {
            cinemaManagementDao is RuntimeCinemaManagementDao -> println(cinemaManagementDao.printPlaces(name))
        }
    }
}
package data

import domain.constants.Constants.Companion.PATH_JSON
import domain.entity.FilmSessionEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

interface CinemaManagementDao {
    fun update(vararg films: FilmSessionEntity)
    fun getAll(): List<FilmSessionEntity>
    fun get(name: String): FilmSessionEntity?
    fun checkForIntersections(film: FilmSessionEntity, parameter: Int = 1): Boolean
    fun printPlaces(name: String): String?
    fun remove(name: String)
}

class RuntimeCinemaManagementDao : CinemaManagementDao {
    private val filmSessions = mutableMapOf<String, FilmSessionEntity>()

    // update отвечает и за добавление, и за редактирование
    override fun update(vararg films: FilmSessionEntity) {
        films.forEach { film ->
            filmSessions[film.name] = film
        }
    }

    override fun getAll(): List<FilmSessionEntity> {
        return filmSessions.values.toList()
    }

    override fun get(name: String): FilmSessionEntity? {
        if (name !in filmSessions) {
            return null;
        }
        return filmSessions[name]
    }

    override fun checkForIntersections(film: FilmSessionEntity, parameter: Int): Boolean {
        for (el in filmSessions) {
            if (parameter != 1 && el.value == film) {
                continue
            }
            if (film.sessionTime.dateStart == el.value.sessionTime.dateStart) {
                return false;
            } else if (film.sessionTime.dateStart < el.value.sessionTime.dateStart) {
                val dateEnd = film.sessionTime.dateStart + film.sessionTime.duration
                if (dateEnd > el.value.sessionTime.dateStart) {
                    return false;
                }
            } else if (film.sessionTime.dateStart > el.value.sessionTime.dateStart) {
                val dateEnd = el.value.sessionTime.dateStart + el.value.sessionTime.duration
                if (dateEnd > film.sessionTime.dateStart) {
                    return false;
                }
            }
        }
        return true;
    }

    override fun printPlaces(name: String): String? {
        return filmSessions[name]?.cinema?.toString()
    }

    override fun remove(name: String) {
        filmSessions.remove(name)
    }

    private fun serializeJson() {
        val file = File(PATH_JSON)
        file.writeText(Json.encodeToString(filmSessions))
    }
}
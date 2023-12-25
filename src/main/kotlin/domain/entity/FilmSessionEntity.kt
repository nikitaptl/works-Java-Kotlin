package domain.entity

import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.LocalDateTime

@Serializable
data class SessionTime(
    val dateStart: LocalDateTime,
    val duration: Duration
)

@Serializable
data class FilmSessionEntity(
    val name: String,
    val ticketPrice: Double,
    val sessionTime: SessionTime
) {
    val cinema = Cinema()
    override fun toString(): String {
        return "${name} (${sessionTime.dateStart}, ${sessionTime.duration.toMinutes() / 60} ч. " +
                "${sessionTime.duration.toMinutes() % 60} мин.) - ${ticketPrice} руб."
    }
}
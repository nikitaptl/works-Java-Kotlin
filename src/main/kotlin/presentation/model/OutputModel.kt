package presentation.model

@JvmInline
value class OutputModel(
    val message: String,
) {
    override fun toString(): String {
        return message.toString()
    }
}
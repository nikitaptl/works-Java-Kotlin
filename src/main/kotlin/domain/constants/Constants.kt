package domain.constants

class Constants {
    companion object {
        const val LEFT_SIZE = 7 // Места нумеруются от 0 до 9 включительно
        const val RIGHT_SIZE = 12 // Места нумеруются от A до (A + RIGHT_SIZE) (но не более 26)
        const val PATH_JSON = "src/main/resources/serializedFile.json"
    }
}
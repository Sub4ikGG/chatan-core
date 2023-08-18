package ru.chatan

@kotlinx.serialization.Serializable
data class Response <T> (
    val code: Int,
    val message: String? = null,
    val data: T? = null
) {
    companion object {
        fun <T> success(code: Int = 200, message: String? = null, data: T? = null) = Response(code = code, message = message, data = data)
        fun <T> error(code: Int, message: String? = null, data: T? = null) = Response(code = code, message = message, data = data)
    }
}
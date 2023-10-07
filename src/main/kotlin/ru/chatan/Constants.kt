package ru.chatan

object Constants {

    const val API_HOST = "api.chatan.ru"

    const val DEVICE_ID = "deviceId"
    const val TOKEN = "token"

    val DEVICE_ID_ERROR = Response.error<Nothing>(code = 400, message = "deviceId not found")

}
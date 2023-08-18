package ru.chatan.service

import org.mindrot.jbcrypt.BCrypt

object PasswordService {
    private const val salt = "\$2a\$10\$axSswHeScKRNMKsTjgFHGu"
    fun encryptPassword(password: String): String = BCrypt.hashpw(password, salt)
}
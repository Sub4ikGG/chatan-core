package ru.chatan.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Encoders
import io.jsonwebtoken.security.Keys
import java.security.Key
import java.util.*

object JwtService {

    private const val KEY = "fdsaJI2c8y[b8X/edwANU82GOggle3"
    private const val EXPIRATION = 3_600_000 // 100 minutes / 1.6 hours

    const val DEVICE_ID = "deviceId"
    const val TOKEN = "token"
    const val REFRESH_TOKEN = "refreshToken"

    private val byteArray = KEY.toByteArray()
    private val base64 = Base64.getEncoder().encode(byteArray)
    private val key: Key = Keys.hmacShaKeyFor(base64)

    private val builder = Jwts.builder()
    private val parserBuilder = Jwts.parserBuilder().setSigningKey(key).build()

    fun encryptJson(json: String): String =
        builder.setSubject(json).setExpiration(Date(Date().time + EXPIRATION)).signWith(key).compact()

    fun decryptToken(token: String): String? {
        return try {
            parserBuilder.parseClaimsJws(token).body.subject
        }
        catch (_: Exception) { null }
    }

    fun generateRefreshToken(): String {
        val key = Keys.secretKeyFor(SignatureAlgorithm.HS256)
        return Encoders.BASE64.encode(key.encoded)
    }

    fun isTokenValid(token: String): Boolean {
        return try {
            parserBuilder.parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }

    }

}
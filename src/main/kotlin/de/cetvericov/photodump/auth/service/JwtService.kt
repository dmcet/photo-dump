package de.cetvericov.photodump.auth.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.security.Key
import java.util.Date
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Service
class JwtService(
    @Value("\${app.jwt.secret}")
    private val secret: String,
    @Value("\${app.jwt.expiration}")
    private val expiration: Long
) {
    fun generateToken(userDetails: UserDetails): String = Jwts.builder()
        .setSubject(userDetails.username)
        .setIssuedAt(Date())
        .setExpiration(Date(System.currentTimeMillis() + expiration))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact()

    @OptIn(ExperimentalEncodingApi::class)
    private fun getSigningKey(): Key = Keys.hmacShaKeyFor(Base64.decode(secret))

    fun validateToken(token: String): Boolean = try {
        Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
        true
    } catch (e: Exception) {
        false
    }

    fun getUsernameFromToken(token: String): String = try {
        Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .body
            .subject
    } catch (e: Exception) {
        throw IllegalArgumentException("Invalid JWT token", e)
    }
}
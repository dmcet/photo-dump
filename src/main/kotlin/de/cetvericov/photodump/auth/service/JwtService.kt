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
    /**
         * Generates a JWT token for the specified user.
         *
         * The token includes the user's username as the subject, the current time as the issued date, and an expiration date based on the configured duration.
         *
         * @param userDetails The user details containing the username to include in the token.
         * @return A signed JWT token as a string.
         */
        fun generateToken(userDetails: UserDetails): String = Jwts.builder()
        .setSubject(userDetails.username)
        .setIssuedAt(Date())
        .setExpiration(Date(System.currentTimeMillis() + expiration))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact()

    /**
     * Decodes the Base64-encoded secret and returns the HMAC SHA signing key for JWT operations.
     *
     * @return The signing key derived from the configured secret.
     */
    @OptIn(ExperimentalEncodingApi::class)
    private fun getSigningKey(): Key = Keys.hmacShaKeyFor(Base64.decode(secret))

    /**
     * Checks whether the provided JWT token is valid and properly signed.
     *
     * @param token The JWT token to validate.
     * @return `true` if the token is valid and can be parsed with the signing key, `false` otherwise.
     */
    fun validateToken(token: String): Boolean = try {
        Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
        true
    } catch (e: Exception) {
        false
    }

    /**
         * Extracts the username from the subject claim of a JWT.
         *
         * @param token The JWT from which to extract the username.
         * @return The username contained in the token's subject claim.
         */
        fun getUsernameFromToken(token: String): String = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .body
        .subject
}
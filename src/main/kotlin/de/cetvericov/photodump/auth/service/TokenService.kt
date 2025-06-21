package de.cetvericov.photodump.auth.service

import de.cetvericov.photodump.auth.Token
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TokenService {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    private val tokenMap = mutableMapOf<String, Token>()

    fun issueToken(username: String): Token {
        logger.info("Issuing token for user $username")
        return generateAndStoreToken(username)
    }

    fun validateToken(token: Token): Boolean {
        logger.info("Validating token $token")
        if (!tokenMap.containsValue(token)) {
            logger.warn("No such token")
            return false
        }
        return true
    }

    fun revokeToken(username: String, token: Token) {
        logger.info("Trying to revoke token for user $username")

        if (!tokenMap.contains(username)) {
            logger.warn("No token for user $username")
            return
        }

        if (tokenMap[username] != token) {
            logger.warn("Token mismatch")
            return
        }

        logger.info("Revoking token for user $username")
        tokenMap.remove(username)
    }

    private fun generateAndStoreToken(username: String): Token {
        tokenMap[username] = Token(UUID.randomUUID().toString())
        return tokenMap[username]!!
    }
}
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

    fun validateToken(username: String, token: Token): Boolean {
        logger.info("Validating token for user ${token.token}")
        if (!tokenMap.containsKey(username)) {
            logger.warn("Token for user $username does not exist")
            return false
        }

        return tokenMap[username] == token
    }

    fun revokeToken(username: String, token: Token) {
        if (!validateToken(username, token)) {
            logger.warn("Token for user $username is not valid, nothing to revoke")
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
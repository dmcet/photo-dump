package de.cetvericov.photodump.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class BearerToken(
    private val token: String
) : Authentication {
    /**
 * Returns the name associated with this authentication, which is always an empty string.
 */
override fun getName(): String = ""
    /**
 * Returns an empty list, indicating that no authorities are granted for this authentication.
 *
 * @return An empty collection of granted authorities.
 */
override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()
    /**
 * Returns the bearer token string used as credentials for authentication.
 *
 * @return The bearer token.
 */
override fun getCredentials(): String = token
    /**
 * Returns `null` as this token does not provide additional authentication details.
 */
override fun getDetails(): Any? = null
    /**
 * Returns `null` as this token does not associate with a principal.
 */
override fun getPrincipal(): Any? = null
    /**
 * Indicates whether the authentication is considered authenticated.
 *
 * Always returns false for this bearer token implementation.
 * 
 * @return false, indicating the token is not authenticated.
 */
override fun isAuthenticated(): Boolean = false
    /**
 * Does nothing; the authentication state cannot be changed for this token.
 */
override fun setAuthenticated(isAuthenticated: Boolean) {}
}

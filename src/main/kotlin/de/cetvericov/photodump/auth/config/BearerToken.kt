package de.cetvericov.photodump.auth.config

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class BearerToken(
    private val token: String
) : Authentication {
    override fun getName(): String = ""
    override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()
    override fun getCredentials(): String = token
    override fun getDetails(): Any? = null
    override fun getPrincipal(): Any? = null
    override fun isAuthenticated(): Boolean = false
    override fun setAuthenticated(isAuthenticated: Boolean) {}
}

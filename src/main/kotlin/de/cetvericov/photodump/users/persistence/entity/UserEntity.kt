package de.cetvericov.photodump.users.persistence.entity

import jakarta.validation.constraints.NotBlank
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("users")
data class UserEntity(
    @Id
    val id: Long? = null,
    @NotBlank
    val username: String,
    @NotBlank
    val password: String
)

package de.cetvericov.photodump.persistence.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.Objects

@Table("images")
data class ImageEntity(
    @Id
    var id: Long? = null,
    var data: ByteArray? = null,
    var name: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ImageEntity) return false

        if (id != other.id) return false
        if (name != other.name) return false
        // Intentionally not comparing data array

        return true
    }

    override fun hashCode(): Int = Objects.hash(id, name)
}
package io.numbersprotocol.starlingcapture.util

import androidx.room.TypeConverter
import java.io.File
import java.util.*

enum class MimeType(private val string: String, val extension: String) {
    JPEG("image/jpeg", "jpg"),
    MP4("video/mp4", "mp4");

    override fun toString() = string

    companion object {
        fun fromString(string: String): MimeType {
            values().forEach { if (it.string == string) return it }
            error("Cannot find the MIME type with given string: $string")
        }

        fun fromUrl(url: String): MimeType {
            val extension = File(url).extension
            values().forEach {
                if (it.extension.toLowerCase(Locale.ROOT) == extension.toLowerCase(Locale.ROOT)) return it
            }
            error("Cannot find the MIME type with given extension: $extension")
        }

        fun isSupported(url: String) = values()
            .map { it.extension.toLowerCase(Locale.ROOT) }
            .contains(File(url).extension.toLowerCase(Locale.ROOT))
    }
}

class MimeTypeConverter {

    @TypeConverter
    fun toMimeType(value: String) = MimeType.fromString(value)

    @TypeConverter
    fun fromMimeType(value: MimeType) = value.toString()
}
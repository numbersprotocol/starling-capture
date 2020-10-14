package io.numbersprotocol.starlingcapture.util

import androidx.room.TypeConverter
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.io.File
import java.util.*

enum class MimeType(private val string: String, val extension: String) {
    JPEG("image/jpeg", "jpg"),
    PNG("image/png", "png"),
    MP4("video/mp4", "mp4");

    override fun toString() = string

    class RoomTypeConverter {

        @TypeConverter
        fun toMimeType(value: String) = fromString(value)

        @TypeConverter
        fun fromMimeType(value: MimeType) = value.toString()
    }

    class JsonAdapter {

        @FromJson
        fun fromJson(value: String) = fromString(value)

        @ToJson
        fun toJson(value: MimeType) = value.toString()
    }

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
package io.numbersprotocol.starlingcapture.util

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

class SortedSetAdapter<T>(
    private val elementAdapter: JsonAdapter<T>
) : JsonAdapter<SortedSet<T>>() {

    override fun fromJson(reader: JsonReader): SortedSet<T>? {
        val result = sortedSetOf<T>()
        reader.beginArray()
        while (reader.hasNext()) result.add(elementAdapter.fromJson(reader)!!)
        reader.endArray()
        return result
    }

    override fun toJson(writer: JsonWriter, value: SortedSet<T>?) {
        writer.beginArray()
        value?.forEach { elementAdapter.toJson(writer, it) }
        writer.endArray()
    }
}

class SortedSetAdapterFactory : JsonAdapter.Factory {

    override fun create(
        type: Type,
        annotations: MutableSet<out Annotation>,
        moshi: Moshi
    ): JsonAdapter<*>? {
        if (annotations.isNotEmpty()) {
            return null
        }

        if (type !is ParameterizedType) {
            return null
        }

        if (type.rawType != SortedSet::class.java) {
            return null
        }

        val elementType = type.actualTypeArguments[0]
        val elementAdapter = moshi.adapter<Any>(elementType)
        return SortedSetAdapter(elementAdapter).nullSafe()
    }
}
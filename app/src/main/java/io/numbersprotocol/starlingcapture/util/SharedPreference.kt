@file:Suppress("unused")

package io.numbersprotocol.starlingcapture.util

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import kotlin.reflect.KProperty

abstract class SharedPreferenceLiveData<T>(
    val sharedPrefs: SharedPreferences,
    private val key: String,
    private val defValue: T
) : LiveData<T>() {

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == this.key) value = getValueFromPreferences(key, defValue)
        }

    abstract fun getValueFromPreferences(key: String, defValue: T): T

    override fun onActive() {
        super.onActive()
        value = getValueFromPreferences(key, defValue)
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onInactive() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onInactive()
    }
}

class SharedPreferenceIntLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Int) :
    SharedPreferenceLiveData<Int>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Int) =
        sharedPrefs.getInt(key, defValue)
}

class SharedPreferenceStringLiveData(
    sharedPrefs: SharedPreferences,
    key: String,
    defValue: String
) : SharedPreferenceLiveData<String>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: String) =
        sharedPrefs.getString(key, defValue)!!
}

class SharedPreferenceBooleanLiveData(
    sharedPrefs: SharedPreferences,
    key: String,
    defValue: Boolean
) : SharedPreferenceLiveData<Boolean>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Boolean) =
        sharedPrefs.getBoolean(key, defValue)
}

class SharedPreferenceFloatLiveData(
    sharedPrefs: SharedPreferences,
    key: String,
    defValue: Float
) : SharedPreferenceLiveData<Float>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Float) =
        sharedPrefs.getFloat(key, defValue)
}

class SharedPreferenceLongLiveData(
    sharedPrefs: SharedPreferences,
    key: String,
    defValue: Long
) : SharedPreferenceLiveData<Long>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Long) =
        sharedPrefs.getLong(key, defValue)
}

class SharedPreferenceStringSetLiveData(
    sharedPrefs: SharedPreferences,
    key: String,
    defValue: Set<String>
) : SharedPreferenceLiveData<Set<String>>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Set<String>): Set<String> =
        sharedPrefs.getStringSet(key, defValue)!!
}

fun SharedPreferences.intLiveData(key: String, defValue: Int = 0) =
    SharedPreferenceIntLiveData(this, key, defValue)

fun SharedPreferences.stringLiveData(key: String, defValue: String = "") =
    SharedPreferenceStringLiveData(this, key, defValue)

fun SharedPreferences.booleanLiveData(key: String, defValue: Boolean = false) =
    SharedPreferenceBooleanLiveData(this, key, defValue)

fun SharedPreferences.floatLiveData(key: String, defValue: Float = 0F) =
    SharedPreferenceFloatLiveData(this, key, defValue)

fun SharedPreferences.longLiveData(key: String, defValue: Long = 0L) =
    SharedPreferenceLongLiveData(this, key, defValue)

fun SharedPreferences.stringSetLiveData(key: String, defValue: Set<String> = setOf()) =
    SharedPreferenceStringSetLiveData(this, key, defValue)

abstract class SharedPreferenceDelegate<T>(val sharedPrefs: SharedPreferences, val key: String) {
    abstract operator fun getValue(thisRef: Any?, property: KProperty<*>): T
    abstract operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
}

class SharedPreferenceIntDelegate(
    sharedPref: SharedPreferences,
    key: String,
    private val defaultValue: Int
) : SharedPreferenceDelegate<Int>(sharedPref, key) {
    override fun getValue(thisRef: Any?, property: KProperty<*>) =
        sharedPrefs.getInt(key, defaultValue)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) =
        sharedPrefs.edit { putInt(key, value) }
}

class SharedPreferenceStringDelegate(
    sharedPref: SharedPreferences,
    key: String,
    private val defaultValue: String
) : SharedPreferenceDelegate<String>(sharedPref, key) {
    override fun getValue(thisRef: Any?, property: KProperty<*>) =
        sharedPrefs.getString(key, defaultValue)!!

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) =
        sharedPrefs.edit { putString(key, value) }
}

class SharedPreferenceBooleanDelegate(
    sharedPref: SharedPreferences,
    key: String,
    private val defaultValue: Boolean
) : SharedPreferenceDelegate<Boolean>(sharedPref, key) {
    override fun getValue(thisRef: Any?, property: KProperty<*>) =
        sharedPrefs.getBoolean(key, defaultValue)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) =
        sharedPrefs.edit { putBoolean(key, value) }
}

class SharedPreferenceFloatDelegate(
    sharedPref: SharedPreferences,
    key: String,
    private val defaultValue: Float
) : SharedPreferenceDelegate<Float>(sharedPref, key) {
    override fun getValue(thisRef: Any?, property: KProperty<*>) =
        sharedPrefs.getFloat(key, defaultValue)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) =
        sharedPrefs.edit { putFloat(key, value) }
}

class SharedPreferenceLongDelegate(
    sharedPref: SharedPreferences,
    key: String,
    private val defaultValue: Long
) : SharedPreferenceDelegate<Long>(sharedPref, key) {
    override fun getValue(thisRef: Any?, property: KProperty<*>) =
        sharedPrefs.getLong(key, defaultValue)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) =
        sharedPrefs.edit { putLong(key, value) }
}

class SharedPreferenceStringSetDelegate(
    sharedPref: SharedPreferences,
    key: String,
    private val defaultValue: Set<String>
) : SharedPreferenceDelegate<Set<String>>(sharedPref, key) {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Set<String> =
        sharedPrefs.getStringSet(key, defaultValue)!!

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Set<String>) =
        sharedPrefs.edit { putStringSet(key, value) }
}

fun SharedPreferences.intPref(key: String, defaultValue: Int = 0) =
    SharedPreferenceIntDelegate(this, key, defaultValue)

fun SharedPreferences.stringPref(key: String, defaultValue: String = "") =
    SharedPreferenceStringDelegate(this, key, defaultValue)

fun SharedPreferences.booleanPref(key: String, defaultValue: Boolean = false) =
    SharedPreferenceBooleanDelegate(this, key, defaultValue)

fun SharedPreferences.floatPref(key: String, defaultValue: Float = 0F) =
    SharedPreferenceFloatDelegate(this, key, defaultValue)

fun SharedPreferences.longPref(key: String, defaultValue: Long = 0L) =
    SharedPreferenceLongDelegate(this, key, defaultValue)

fun SharedPreferences.stringSetPref(key: String, defaultValue: Set<String> = setOf()) =
    SharedPreferenceStringSetDelegate(this, key, defaultValue)
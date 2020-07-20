package io.numbersprotocol.starlingcapture.util

import android.text.TextUtils
import android.util.Patterns
import java.util.regex.Pattern

fun CharSequence.isInt() = toString().toIntOrNull() != null

fun CharSequence.isPositiveInteger(): Boolean {
    val integer = toString().toIntOrNull() ?: return false
    return integer > 0
}

private const val IP_ADDRESS_WITH_OPTIONAL_PORT_PATTERN_STRING =
    "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\." +
            "(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\." +
            "(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\." +
            "(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9]))" +
            "(:([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5]))?"

private val IP_ADDRESS_WITH_OPTIONAL_PORT_PATTERN: Pattern =
    Pattern.compile(IP_ADDRESS_WITH_OPTIONAL_PORT_PATTERN_STRING)

fun CharSequence.isIpAddress() = IP_ADDRESS_WITH_OPTIONAL_PORT_PATTERN.matcher(this).matches()

fun CharSequence.isEmail() =
    !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()
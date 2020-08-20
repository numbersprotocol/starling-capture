package io.numbersprotocol.starlingcapture.util

import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigator

fun NavController.navigateSafely(@IdRes resId: Int) {
    (currentDestination ?: graph).getAction(resId) ?: return
    if (currentDestination?.id != resId) navigate(resId)
}

fun NavController.navigateSafely(directions: NavDirections, navigatorExtras: Navigator.Extras) {
    val action = (currentDestination ?: graph).getAction(directions.actionId) ?: return
    if (currentDestination?.id != action.destinationId) {
        navigate(directions, navigatorExtras)
    }
}
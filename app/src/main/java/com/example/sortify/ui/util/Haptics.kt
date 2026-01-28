package com.example.sortify.ui.util

import android.view.HapticFeedbackConstants
import android.view.View

object Haptics {
    fun light(v: View) = v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    fun medium(v: View) = v.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    fun success(v: View) = v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
}

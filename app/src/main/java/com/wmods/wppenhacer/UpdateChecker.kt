package com.wmods.wppenhacer

import android.app.Activity

/**
 * Update checking has been permanently disabled per configuration.
 * All operations are no-ops to eliminate background network pings and update dialogs.
 */
class UpdateChecker(private val mActivity: Activity) : Runnable {
    override fun run() {
        // Disabled: No-op
    }
}


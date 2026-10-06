package com.wmods.wppenhacer.xposed.utils

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.util.Log
import com.wmods.wppenhacer.BuildConfig
import de.robv.android.xposed.XposedBridge
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object HookFailureLogger {

    private const val TAG = "WAE_HookFailure"
    const val ACTION_HOOK_FAILURE = "${BuildConfig.APPLICATION_ID}.ACTION_HOOK_FAILURE"
    const val EXTRA_LOG_ENTRY = "EXTRA_LOG_ENTRY"
    private const val LOG_FILE_NAME = "hook_failures.log"

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)

    @Synchronized
    fun logFailure(
        feature: String,
        target: String,
        error: Throwable,
        pkgName: String? = null,
        wppVersion: String? = null,
        context: Context? = null
    ) {
        val timestamp = synchronized(dateFormat) { dateFormat.format(Date()) }
        val effectivePkg = pkgName ?: context?.packageName ?: "com.whatsapp"
        val effectiveVer = wppVersion ?: "Unknown"

        val filteredStackTrace = error.stackTrace
            .filter { s ->
                !s.className.startsWith("android.") &&
                !s.className.startsWith("com.android.") &&
                !s.className.startsWith("java.lang.reflect.")
            }
            .take(15)
            .joinToString("\n      at ")

        val logEntry = buildString {
            append("==================================================\n")
            append("[$timestamp] HOOK FAILURE DETECTED\n")
            append("Feature:          $feature\n")
            append("Target:           $target\n")
            append("WhatsApp Package: $effectivePkg\n")
            append("WhatsApp Version: $effectiveVer\n")
            append("Module Version:   ${BuildConfig.VERSION_NAME}\n")
            append("Error Type:       ${error.javaClass.name}\n")
            append("Message:          ${error.message}\n")
            if (filteredStackTrace.isNotEmpty()) {
                append("Trace:            at $filteredStackTrace\n")
            }
            append("==================================================\n\n")
        }

        try {
            XposedBridge.log("[$TAG] $logEntry")
        } catch (_: Throwable) {}
        Log.e(TAG, logEntry, error)

        // Write directly to accessible log file targets
        writeToLogFiles(logEntry, context)

        // Broadcast to manager app so it saves in WAE internal storage
        broadcastFailure(logEntry, context)
    }

    private fun writeToLogFiles(logEntry: String, context: Context?) {
        val targets = mutableListOf<File>()

        context?.let { ctx ->
            try {
                targets.add(File(ctx.filesDir, LOG_FILE_NAME))
                ctx.getExternalFilesDir(null)?.let { targets.add(File(it, LOG_FILE_NAME)) }
            } catch (_: Throwable) {}
        }

        try {
            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val waeDir = File(downloadDir, "WaEnhancer")
            if (!waeDir.exists()) waeDir.mkdirs()
            targets.add(File(waeDir, LOG_FILE_NAME))
        } catch (_: Throwable) {}

        for (targetFile in targets) {
            try {
                targetFile.parentFile?.mkdirs()
                FileWriter(targetFile, true).use { writer ->
                    writer.write(logEntry)
                }
            } catch (_: Throwable) {}
        }
    }

    private fun broadcastFailure(logEntry: String, context: Context?) {
        val ctx = context ?: try { Utils.application } catch (_: Throwable) { null } ?: return
        try {
            val intent = Intent(ACTION_HOOK_FAILURE).apply {
                putExtra(EXTRA_LOG_ENTRY, logEntry)
                setPackage(BuildConfig.APPLICATION_ID)
            }
            ctx.sendBroadcast(intent)
        } catch (_: Throwable) {}
    }

    fun readLogFile(context: Context): String {
        val candidates = listOf(
            File(context.filesDir, LOG_FILE_NAME),
            File(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "WaEnhancer"), LOG_FILE_NAME),
            File(context.getExternalFilesDir(null), LOG_FILE_NAME)
        )

        for (file in candidates) {
            if (file.exists() && file.length() > 0) {
                return try {
                    file.readText()
                } catch (e: Throwable) {
                    "Error reading log file: ${e.message}"
                }
            }
        }
        return "No hook failures detected! All hooks are functioning normally."
    }

    fun clearLogFile(context: Context): Boolean {
        var cleared = false
        val candidates = listOf(
            File(context.filesDir, LOG_FILE_NAME),
            File(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "WaEnhancer"), LOG_FILE_NAME),
            File(context.getExternalFilesDir(null), LOG_FILE_NAME)
        )

        for (file in candidates) {
            if (file.exists()) {
                try {
                    file.writeText("")
                    cleared = true
                } catch (_: Throwable) {}
            }
        }
        return cleared
    }
}

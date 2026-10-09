package com.wmods.wppenhacer.xposed.core

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Instrumentation
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.crossbowffs.remotepreferences.RemotePreferences
import com.wmods.wppenhacer.App
import com.wmods.wppenhacer.BuildConfig
import com.wmods.wppenhacer.R
import com.wmods.wppenhacer.xposed.utils.HookFailureLogger
import com.wmods.wppenhacer.WppXposed
import com.wmods.wppenhacer.activities.CrashReportActivity
import com.wmods.wppenhacer.xposed.core.components.AlertDialogWpp
import com.wmods.wppenhacer.xposed.core.components.FMessageWpp
import com.wmods.wppenhacer.xposed.core.components.FStatusWpp
import com.wmods.wppenhacer.xposed.core.components.ProtocolTreeNodeWpp
import com.wmods.wppenhacer.xposed.core.components.SharedPreferencesWrapper
import com.wmods.wppenhacer.xposed.core.components.WaContactWpp
import com.wmods.wppenhacer.xposed.core.devkit.Unobfuscator
import com.wmods.wppenhacer.xposed.core.devkit.UnobfuscatorCache
import com.wmods.wppenhacer.xposed.features.customization.BubbleColors
import com.wmods.wppenhacer.xposed.features.customization.ContactVerify
import com.wmods.wppenhacer.xposed.features.customization.CustomThemeV2
import com.wmods.wppenhacer.xposed.features.customization.CustomTime
import com.wmods.wppenhacer.xposed.features.customization.CustomToolbar
import com.wmods.wppenhacer.xposed.features.customization.CustomView
import com.wmods.wppenhacer.xposed.features.customization.DefaultEmoji
import com.wmods.wppenhacer.xposed.features.customization.FilterGroups
import com.wmods.wppenhacer.xposed.features.customization.FloatingBottomBar
import com.wmods.wppenhacer.xposed.features.customization.HideSeenView
import com.wmods.wppenhacer.xposed.features.customization.HideTabs
import com.wmods.wppenhacer.xposed.features.customization.IGStatus
import com.wmods.wppenhacer.xposed.features.customization.SeparateGroup
import com.wmods.wppenhacer.xposed.features.customization.ShowOnline
import com.wmods.wppenhacer.xposed.features.general.AboutContactPicker
import com.wmods.wppenhacer.xposed.features.general.AntiRevoke
import com.wmods.wppenhacer.xposed.features.general.CallType
import com.wmods.wppenhacer.xposed.features.general.CaptureDevice
import com.wmods.wppenhacer.xposed.features.general.ChatLimit
import com.wmods.wppenhacer.xposed.features.general.DeleteStatus
import com.wmods.wppenhacer.xposed.features.general.NewChat
import com.wmods.wppenhacer.xposed.features.general.Others
import com.wmods.wppenhacer.xposed.features.general.PinnedLimit
import com.wmods.wppenhacer.xposed.features.general.SeenTick
import com.wmods.wppenhacer.xposed.features.general.ShareLimit
import com.wmods.wppenhacer.xposed.features.general.ShowEditMessage
import com.wmods.wppenhacer.xposed.features.general.Tasker
import com.wmods.wppenhacer.xposed.features.listeners.ContactItemListener
import com.wmods.wppenhacer.xposed.features.listeners.ConversationItemListener
import com.wmods.wppenhacer.xposed.features.providers.MenuStatusProvider
import com.wmods.wppenhacer.xposed.features.media.CallRecording
import com.wmods.wppenhacer.xposed.features.media.DownloadProfile
import com.wmods.wppenhacer.xposed.features.media.DownloadViewOnce
import com.wmods.wppenhacer.xposed.features.media.MediaPreview
import com.wmods.wppenhacer.xposed.features.media.MediaQuality
import com.wmods.wppenhacer.xposed.features.media.StatusDownload
import com.wmods.wppenhacer.xposed.features.others.ActivityController
import com.wmods.wppenhacer.xposed.features.others.AudioTranscript
import com.wmods.wppenhacer.xposed.features.others.BackupRestore
import com.wmods.wppenhacer.xposed.features.others.Channels
import com.wmods.wppenhacer.xposed.features.others.ChatFilters
import com.wmods.wppenhacer.xposed.features.others.CopySelectionMessage
import com.wmods.wppenhacer.xposed.features.others.CopyStatus
import com.wmods.wppenhacer.xposed.features.others.DebugFeature
import com.wmods.wppenhacer.xposed.features.others.GoogleTranslate
import com.wmods.wppenhacer.xposed.features.others.GroupAdmin
import com.wmods.wppenhacer.xposed.features.others.JumpFirstMessage
import com.wmods.wppenhacer.xposed.features.others.MenuHome
import com.wmods.wppenhacer.xposed.features.others.MinorFixes
import com.wmods.wppenhacer.xposed.features.others.Stickers
import com.wmods.wppenhacer.xposed.features.others.TextStatusComposer
import com.wmods.wppenhacer.xposed.features.others.ToastViewer
import com.wmods.wppenhacer.xposed.features.privacy.AntiWa
import com.wmods.wppenhacer.xposed.features.privacy.CallPrivacy
import com.wmods.wppenhacer.xposed.features.privacy.CustomPrivacy
import com.wmods.wppenhacer.xposed.features.privacy.DndMode
import com.wmods.wppenhacer.xposed.features.privacy.FreezeLastSeen
import com.wmods.wppenhacer.xposed.features.privacy.HideChat
import com.wmods.wppenhacer.xposed.features.privacy.HideSeen
import com.wmods.wppenhacer.xposed.features.privacy.LockedChatsEnhancer
import com.wmods.wppenhacer.xposed.features.privacy.TagMessage
import com.wmods.wppenhacer.xposed.features.privacy.TypingPrivacy
import com.wmods.wppenhacer.xposed.features.privacy.ViewOnce
import com.wmods.wppenhacer.xposed.features.providers.ContextMenuActionProvider
import com.wmods.wppenhacer.xposed.spoofer.HookBL
import com.wmods.wppenhacer.xposed.utils.DesignUtils
import com.wmods.wppenhacer.xposed.utils.ReflectionUtils
import com.wmods.wppenhacer.xposed.utils.Utils
import de.robv.android.xposed.SELinuxHelper
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.jvm.java

class FeatureLoader {

    companion object {
        @JvmField
        var mApp: Application? = null

        lateinit var moduleContext: Context

        const val PACKAGE_WPP = "com.whatsapp"
        const val PACKAGE_BUSINESS = "com.whatsapp.w4b"

        private val list = Collections.synchronizedList(ArrayList<ErrorItem>())
        private var supportedVersions: List<String>? = null
        private var currentVersion: String? = null
        private var crashHandlerInstalled = false

        @JvmStatic
        fun start(loader: ClassLoader, sourceDir: String) {
            if (!Unobfuscator.initWithPath(sourceDir)) {
                XposedBridge.log("Can't init dexkit")
                return
            }

            Utils.appClassLoader = loader

            XposedHelpers.findAndHookMethod(
                Instrumentation::class.java, "callApplicationOnCreate", Application::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        mApp = param.args[0] as Application
                        val application = mApp!!
                        val pref = getPreferences(application)
                        Feature.DEBUG = pref.getBoolean("enablelogs", true)
                        Utils.xprefs = pref

                        if (pref.getBoolean("bootloader_spoofer", false)) {
                            HookBL.hook(loader, pref)
                            XposedBridge.log("Bootloader Spoofer is Injected")
                        }

                        val packageManager = application.packageManager
                        val packageInfo = packageManager.getPackageInfo(application.packageName, 0)
                        XposedBridge.log(packageInfo.versionName)
                        currentVersion = packageInfo.versionName
                        installCrashHandler(application, packageInfo.versionName.orEmpty())

                        val resIdArray = if (application.packageName == PACKAGE_WPP)
                            R.array.supported_versions_wpp
                        else
                            R.array.supported_versions_business

                        supportedVersions =
                            application.resources.getStringArray(resIdArray).toList()
                        application.registerActivityLifecycleCallbacks(WaCallback())
                        registerReceivers()

                        try {
                            initializeModuleContext()
                            val timeMillis = System.currentTimeMillis()
                            UnobfuscatorCache.init(application)
                            SharedPreferencesWrapper.hookInit(application.classLoader)
                            ReflectionUtils.initCache(application)

                            val isSupported = isSupportedVersion(packageInfo.versionName, supportedVersions)

                            try {
                                disableExpirationVersion(application.classLoader)
                            } catch (e: Throwable) {
                                XposedBridge.log("Failed to disable expiration: ${e.message}")
                            }

                            if (!isSupported) {
                                XposedBridge.log("Running on unverified version: ${packageInfo.versionName}. Attempting dynamic feature loading.")
                            }

                            initComponents(loader, pref)
                            plugins(loader, pref, packageInfo.versionName!!)
                            sendEnabledBroadcast(application)

                            val totalTime = System.currentTimeMillis() - timeMillis
                            XposedBridge.log("Loaded Hooks in ${totalTime}ms")

                        } catch (e: Throwable) {
                            XposedBridge.log(e)
                            HookFailureLogger.logFailure(
                                "MainFeatures[Critical]",
                                "callApplicationOnCreate",
                                e,
                                application.packageName,
                                packageInfo.versionName,
                                application
                            )
                            val error = ErrorItem().apply {
                                pluginName = "MainFeatures[Critical]"
                                whatsAppVersion = packageInfo.versionName
                                moduleVersion = BuildConfig.VERSION_NAME
                                message = e.message
                                errorDetail = e.stackTrace
                                    .filter { s ->
                                        !s.className.startsWith("android") && !s.className.startsWith(
                                            "com.android"
                                        )
                                    }
                                    .joinToString(prefix = "[", postfix = "]")
                            }
                            list.add(error)
                        }
                    }
                })

            XposedHelpers.findAndHookMethod(
                Activity::class.java, "onCreate", Bundle::class.java,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (param.thisObject.javaClass.simpleName != "HomeActivity") return
                        val errors = synchronized(list) { list.toList() }
                        if (errors.isNotEmpty()) {
                            val msg = errors.joinToString("\n") { "${it.pluginName} - ${it.message}" }
                            XposedBridge.log("[WaEnhancer] Non-fatal hook issues logged:\n$msg")
                        }
                    }
                })
        }

        private fun getPreferences(context: Context): SharedPreferences {
            val pref = WppXposed.getPref()
            pref.reload()
            try {
                val fileCanRead =
                    SELinuxHelper.getAppDataFileService().checkFileAccess(pref.file.absolutePath, 4)
                if (fileCanRead) {
                    return pref
                }
            } catch (e: Exception) {
                XposedBridge.log(e)
            }
            XposedBridge.log("XSharedPreferences not accessible, using RemotePreferences fallback")
            return RemotePreferences(
                context,
                BuildConfig.APPLICATION_ID + ".preferences",
                BuildConfig.APPLICATION_ID + "_preferences"
            )
        }

        private fun initializeModuleContext() {
            try {
                val context = mApp!!.createPackageContext(
                    BuildConfig.APPLICATION_ID,
                    Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY
                )
                moduleContext = android.view.ContextThemeWrapper(context, R.style.AppTheme)
            } catch (_: PackageManager.NameNotFoundException) {
                throw PackageManager.NameNotFoundException(Utils.application.getString(R.string.alert_module_notfound))
            }
        }

        private fun installCrashHandler(application: Application, whatsAppVersion: String) {
            if (crashHandlerInstalled) return
            crashHandlerInstalled = true

            val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                try {
                    XposedBridge.log(throwable)
                    val isMainThread = Looper.getMainLooper().thread == thread
                    val isFatalSystemError = throwable is Error
                    if (!isMainThread && !isFatalSystemError) {
                        previousHandler?.uncaughtException(thread, throwable)
                        return@setDefaultUncaughtExceptionHandler
                    }
                    val crashInfo = buildCrashInfo(application, whatsAppVersion)
                    val intent = Intent().apply {
                        component = ComponentName(
                            BuildConfig.APPLICATION_ID,
                            CrashReportActivity::class.java.name
                        )
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        putExtra(CrashReportActivity.EXTRA_CRASH_INFO, crashInfo)
                        putExtra(
                            CrashReportActivity.EXTRA_CRASH_TRACE,
                            Log.getStackTraceString(throwable)
                        )
                    }
                    application.startActivity(intent)
                } catch (e: Throwable) {
                    XposedBridge.log(e)
                } finally {
                    if (previousHandler != null) {
                        previousHandler.uncaughtException(thread, throwable)
                    } else {
                        Runtime.getRuntime().exit(2)
                    }
                }
            }
        }

        private fun buildCrashInfo(application: Application, whatsAppVersion: String): String {
            val androidVersion = "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
            val deviceModel = listOf(Build.MANUFACTURER, Build.MODEL)
                .filter { it.isNotBlank() }
                .joinToString(" ")

            return listOf(
                "${application.getString(R.string.whatsapp_version)}: $whatsAppVersion",
                "${application.getString(R.string.whatsapp_package)}: ${application.packageName}",
                "${application.getString(R.string.wae_version)}: ${BuildConfig.VERSION_NAME}",
                "${application.getString(R.string.crash_android_version)}: $androidVersion",
                "${application.getString(R.string.device_model)}: $deviceModel"
            ).joinToString("\n")
        }


        private var isExpirationDisabled = false

        @JvmStatic
        fun isSupportedVersion(version: String?, supportedList: List<String>?): Boolean {
            if (version.isNullOrBlank()) return false
            if (supportedList != null && supportedList.any { version.startsWith(it.replace(".xx", "")) }) {
                return true
            }
            return try {
                val parts = version.split('.').mapNotNull { it.toIntOrNull() }
                if (parts.size >= 2) {
                    val major = parts[0]
                    val minor = parts[1]
                    major > 2 || (major == 2 && minor >= 24)
                } else {
                    false
                }
            } catch (_: Throwable) {
                false
            }
        }

        @JvmStatic
        @Synchronized
        fun disableExpirationVersion(classLoader: ClassLoader) {
            if (isExpirationDisabled) return
            try {
                val expirationClass = Unobfuscator.loadExpirationClass(classLoader)

                // 1. Hook Date methods (returns Year 2099 date)
                val dateMethods =
                    ReflectionUtils.findAllMethodsUsingFilter(expirationClass) { m -> m.returnType == Date::class.java }
                for (method in dateMethods) {
                    XposedBridge.hookMethod(method, object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            val calendar = Calendar.getInstance().apply {
                                set(2099, 11, 31, 23, 59, 59)
                            }
                            param.result = calendar.time
                        }
                    })
                }

                // 2. Hook boolean methods (isExpired, isNearExpiration -> false)
                val booleanMethods =
                    ReflectionUtils.findAllMethodsUsingFilter(expirationClass) { m ->
                        m.returnType == java.lang.Boolean.TYPE || m.returnType == java.lang.Boolean::class.java
                    }
                for (method in booleanMethods) {
                    XposedBridge.hookMethod(method, object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            param.result = false
                        }
                    })
                }

                // 3. Hook Long methods (epoch timestamps > 1000000000000L -> Year 2099)
                val longMethods =
                    ReflectionUtils.findAllMethodsUsingFilter(expirationClass) { m ->
                        m.returnType == java.lang.Long.TYPE || m.returnType == java.lang.Long::class.java
                    }
                for (method in longMethods) {
                    XposedBridge.hookMethod(method, object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            val result = param.result
                            if (result is Long && result > 1000000000000L) {
                                param.result = 4102444800000L // 2099-12-31 23:59:59 UTC
                            }
                        }
                    })
                }

                isExpirationDisabled = true
                XposedBridge.log("[WaEnhancer] Successfully disabled WhatsApp expiration permanently (Year 2099 bypass applied).")
            } catch (e: Throwable) {
                XposedBridge.log("[WaEnhancer] Failed to disable expiration: ${e.message}")
            }
        }

        @Throws(Exception::class)
        private fun initComponents(loader: ClassLoader, pref: SharedPreferences) {
            FMessageWpp.initialize(loader)
            FStatusWpp.initialize(loader)
            ProtocolTreeNodeWpp.initialize(loader)
            AlertDialogWpp.initDialog(loader)
            WaContactWpp.initialize(loader)
            WppCore.initialize(loader, pref)
            DesignUtils.setPrefs(pref)
            Utils.init()

            WppCore.addListenerActivity { activity, type ->
                if (type == WppCore.ActivityChangeState.ChangeType.RESUMED) {
                    checkRestartPrompt(activity)
                }
            }

        }

        private fun checkRestartPrompt(activity: Activity) {
            if (WppCore.getPrivBoolean("need_restart", false)) {
                WppCore.setPrivBoolean("need_restart", false)
                try {
                    AlertDialogWpp(activity)
                        .setMessage(activity.getString(R.string.restart_wpp))
                        .setPositiveButton(activity.getString(R.string.yes)) { _, _ ->
                            if (!Utils.doRestart(activity)) {
                                Toast.makeText(
                                    activity,
                                    "Unable to rebooting activity",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .setNegativeButton(activity.getString(R.string.no), null)
                        .show()
                } catch (_: Throwable) {
                }
            }
        }

        @SuppressLint("WrongConstant")
        private fun registerReceivers() {
            val app = mApp ?: return

            // Reboot receiver
            val restartReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (context.packageName == intent.getStringExtra("PKG")) {
                        val appName =
                            context.packageManager.getApplicationLabel(context.applicationInfo)
                        Toast.makeText(
                            context,
                            "${context.getString(R.string.rebooting)} $appName...",
                            Toast.LENGTH_SHORT
                        ).show()
                        if (!Utils.doRestart(context)) {
                            Toast.makeText(
                                context,
                                "Unable to rebooting $appName",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            ContextCompat.registerReceiver(
                app, restartReceiver,
                IntentFilter("${BuildConfig.APPLICATION_ID}.WHATSAPP.RESTART"),
                ContextCompat.RECEIVER_EXPORTED
            )

            // Wpp receiver
            val wppReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    sendEnabledBroadcast(context)
                }
            }
            ContextCompat.registerReceiver(
                app, wppReceiver,
                IntentFilter("${BuildConfig.APPLICATION_ID}.CHECK_WPP"),
                ContextCompat.RECEIVER_EXPORTED
            )

            // Dialog receiver restart
            val restartManualReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    WppCore.setPrivBoolean("need_restart", true)
                }
            }
            ContextCompat.registerReceiver(
                app, restartManualReceiver,
                IntentFilter("${BuildConfig.APPLICATION_ID}.MANUAL_RESTART"),
                ContextCompat.RECEIVER_EXPORTED
            )
        }

        private fun sendEnabledBroadcast(context: Context) {
            try {
                val wppIntent = Intent("${BuildConfig.APPLICATION_ID}.RECEIVER_WPP").apply {
                    putExtra(
                        "VERSION",
                        context.packageManager.getPackageInfo(context.packageName, 0).versionName
                    )
                    putExtra("PKG", context.packageName)
                    setPackage(BuildConfig.APPLICATION_ID)
                }
                context.sendBroadcast(wppIntent)
            } catch (_: Exception) {
            }
        }

        @Throws(Exception::class)
        private fun plugins(loader: ClassLoader, pref: SharedPreferences, versionWpp: String) {
            val classes = arrayOf(
                DebugFeature::class.java,
                MinorFixes::class.java,
                ContactItemListener::class.java,
                ConversationItemListener::class.java,
                MenuStatusProvider::class.java,
                ShowEditMessage::class.java,
                AntiRevoke::class.java,
                CustomToolbar::class.java,
                CustomView::class.java,
                SeenTick::class.java,
                BubbleColors::class.java,
                CallPrivacy::class.java,
                ActivityController::class.java,
                CustomThemeV2::class.java,
                FloatingBottomBar::class.java,
                ChatLimit::class.java,
                SeparateGroup::class.java,
                ShowOnline::class.java,
                DndMode::class.java,
                FreezeLastSeen::class.java,
                TypingPrivacy::class.java,
                HideChat::class.java,
                HideSeen::class.java,
                HideSeenView::class.java,
                TagMessage::class.java,
                HideTabs::class.java,
                IGStatus::class.java,
                MediaQuality::class.java,
                NewChat::class.java,
                Others::class.java,
                PinnedLimit::class.java,
                CustomTime::class.java,
                ShareLimit::class.java,
                StatusDownload::class.java,
                ViewOnce::class.java,
                CallType::class.java,
                MediaPreview::class.java,
                FilterGroups::class.java,
                Tasker::class.java,
                DeleteStatus::class.java,
                DownloadViewOnce::class.java,
                Channels::class.java,
                DownloadProfile::class.java,
                ChatFilters::class.java,
                GroupAdmin::class.java,
                Stickers::class.java,
                CopyStatus::class.java,
                CopySelectionMessage::class.java,
                TextStatusComposer::class.java,
                ToastViewer::class.java,
                MenuHome::class.java,
                AntiWa::class.java,
                CustomPrivacy::class.java,
                AudioTranscript::class.java,
                GoogleTranslate::class.java,
                ContactVerify::class.java,
                LockedChatsEnhancer::class.java,
                CallRecording::class.java,
                BackupRestore::class.java,
                JumpFirstMessage::class.java,
                AboutContactPicker::class.java,
                DefaultEmoji::class.java,
                CaptureDevice::class.java,
                ContextMenuActionProvider::class.java
            )

            XposedBridge.log("Loading Plugins")
            val executorService = Executors.newSingleThreadExecutor { runnable ->
                Thread(runnable, "WAE-HookInstaller").apply {
                    isDaemon = true
                }
            }
            val times = Collections.synchronizedList(ArrayList<String>())

            for (clazz in classes) {
                CompletableFuture.runAsync({
                    val startTime = System.currentTimeMillis()
                    try {
                        val constructor = clazz.getConstructor(
                            ClassLoader::class.java,
                            SharedPreferences::class.java
                        )
                        val plugin = constructor.newInstance(loader, pref) as Feature
                        plugin.doHook()
                    } catch (e: Throwable) {
                        XposedBridge.log(e)
                        HookFailureLogger.logFailure(
                            clazz.simpleName,
                            "doHook",
                            e,
                            mApp?.packageName,
                            versionWpp,
                            mApp
                        )
                        val error = ErrorItem().apply {
                            pluginName = clazz.simpleName
                            whatsAppVersion = versionWpp
                            moduleVersion = BuildConfig.VERSION_NAME
                            message = e.message
                            errorDetail = e.stackTrace
                                .filter { s ->
                                    !s.className.startsWith("android") && !s.className.startsWith(
                                        "com.android"
                                    )
                                }
                                .joinToString(prefix = "[", postfix = "]")
                        }
                        list.add(error)
                    }
                    val duration = System.currentTimeMillis() - startTime
                    times.add("* Loaded Plugin ${clazz.simpleName} in ${duration}ms")
                }, executorService)
            }

            executorService.shutdown()
            executorService.awaitTermination(15, TimeUnit.SECONDS)

            if (Feature.DEBUG) {
                val loadedTimes = synchronized(times) { times.toList() }
                loadedTimes.forEach { XposedBridge.log(it) }
            }
        }
    }

    private class ErrorItem {
        var pluginName: String? = null
        var whatsAppVersion: String? = null
        var errorDetail: String? = null
        var moduleVersion: String? = null
        var message: String? = null

        override fun toString(): String {
            return """
                pluginName='$pluginName'
                moduleVersion='$moduleVersion'
                whatsAppVersion='$whatsAppVersion'
                Message=$message
                error='$errorDetail'
            """.trimIndent()
        }
    }
}

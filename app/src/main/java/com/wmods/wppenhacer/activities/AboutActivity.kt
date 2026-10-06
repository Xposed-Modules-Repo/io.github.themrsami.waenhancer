package com.wmods.wppenhacer.activities

import android.os.Build
import android.os.Bundle
import com.wmods.wppenhacer.BuildConfig
import com.wmods.wppenhacer.activities.base.BaseActivity
import com.wmods.wppenhacer.databinding.ActivityAboutBinding

class AboutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvDevName.text = "Enhanced Edition • Pure Local & Private"

        val abi = Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown"
        val androidVer = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
        val device = "${Build.MANUFACTURER} ${Build.MODEL}"

        val sysInfo = buildString {
            append("Package: ").append(BuildConfig.APPLICATION_ID).append("\n")
            append("Version: ").append(BuildConfig.VERSION_NAME).append(" (").append(BuildConfig.BUILD_TYPE).append(")\n")
            append("Device:  ").append(device).append("\n")
            append("OS:      ").append(androidVer).append("\n")
            append("ABI:     ").append(abi).append("\n")
            append("Status:  Active & Offline (No Telemetry)")
        }

        binding.tvSystemInfo.text = sysInfo
    }
}

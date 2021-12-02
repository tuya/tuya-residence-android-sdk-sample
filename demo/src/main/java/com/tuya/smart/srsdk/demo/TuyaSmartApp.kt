package com.tuya.smart.srsdk.demo

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Process
import com.tuya.smart.android.common.utils.L
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.optimus.sdk.TuyaOptimusSdk
import com.tuya.smart.sdk.TuyaSdk
import com.tuya.smart.srsdk.demo.config.ApiConfig
import com.tuya.smart.srsdk.demo.account.SigninActivity

class TuyaSmartApp : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this
        L.d(TAG, "TuyaSmartApp:onCreate " + getProcessName(this))
        L.setSendLogOn(true)

        // Product Environment
        // TuyaHomeSdk.init(this)

        // Pre Environment
        TuyaSdk.init(this)

        TuyaOptimusSdk.init(this)
        TuyaHomeSdk.setDebugMode(true)
        TuyaSdk.setOnNeedLoginListener { context ->
            val intent = Intent(context, SigninActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
    }

    companion object {
        fun getProcessName(context: Context): String {
            val pid = Process.myPid()
            val mActivityManager = context
                .getSystemService(ACTIVITY_SERVICE) as ActivityManager
            for (appProcess in mActivityManager
                .runningAppProcesses) {
                if (appProcess.pid == pid) {
                    return appProcess.processName
                }
            }
            return ""
        }

        var appContext: Context? = null
            private set
    }
}
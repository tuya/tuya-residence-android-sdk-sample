package com.tuya.smart.srsdk.demo.account

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tuya.smart.android.base.TuyaSmartSdk
import com.tuya.smart.android.common.utils.L
import com.tuya.smart.android.network.Business
import com.tuya.smart.android.network.http.BusinessResponse
import com.tuya.smart.android.user.bean.User
import com.tuya.smart.srsdk.demo.Global
import com.tuya.smart.srsdk.demo.MainActivity
import com.tuya.smart.srsdk.demo.TAG
import com.tuya.smart.srsdk.sdk.TuyaSmartResidenceSdk

/**
 * @ClassName LoginActivity
 * @description
 * @author SamuraiSong
 * @date 2021/10/30
 */

class SigninActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TuyaSmartSdk.init(application)
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
            return
        }
        setContent {
            SigninView()
        }
    }
}

@Preview
@Composable
private fun SigninView() {
    val context = LocalContext.current
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("User Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            singleLine = true
        )
        Button(onClick = { TuyaSmartResidenceSdk.account().loginByEmail(Global.countryCode,
            username, password, object : Business.ResultListener<User?> {
                override fun onFailure(p0: BusinessResponse?, p1: User?, p2: String?) {
                    L.d(TAG, "username = $username , password = $password")
                    L.d(TAG, "ErrorMsg = ${p0?.errorMsg}, ErrorCode = ${p0?.errorCode}, apiName = $p2")
                }

                override fun onSuccess(p0: BusinessResponse?, p1: User?, p2: String?) {
                    context.startActivity(Intent(context, MainActivity::class.java))
                    (context as Activity).finish()
                }

            }) }, modifier =
        Modifier
            .fillMaxWidth()
            .padding(
                start = 30
                    .dp,
                end = 30.dp, top = 20.dp
            )) {
            Text(text = "Sign in")
        }
        Button(onClick = { context.startActivity(Intent(context, SignupActivity::class.java)) },
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    start = 30.dp,
                    end = 30.dp, top = 20.dp
                )) {
            Text(text = "Sign up")
        }
    }
}
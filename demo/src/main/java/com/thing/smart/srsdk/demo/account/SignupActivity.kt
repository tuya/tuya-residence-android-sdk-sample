package com.thing.smart.srsdk.demo.account

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alibaba.fastjson.util.TypeUtils
import com.tuya.smart.android.common.utils.L
import com.tuya.smart.android.network.Business
import com.tuya.smart.android.network.http.BusinessResponse
import com.tuya.smart.android.user.api.IRegisterCallback
import com.tuya.smart.android.user.bean.User
import com.tuya.smart.sdk.api.IResultCallback
import com.thing.smart.srsdk.demo.Global
import com.thing.smart.srsdk.demo.MainActivity
import com.thing.smart.srsdk.demo.TAG
import com.tuya.smart.srsdk.sdk.TuyaSmartResidenceSdk

/**
 * @ClassName SignupActivity
 * @description
 * @author SamuraiSong
 * @date 2021/11/1
 */
class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignupView()
        }
    }
}

@Preview
@Composable
fun SignupView() {
    val context = LocalContext.current
    var username by rememberSaveable { mutableStateOf("") }
    var contryCode by rememberSaveable { mutableStateOf(Global.countryCode) }
    var authCode by rememberSaveable { mutableStateOf("") }
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
            value = contryCode,
            onValueChange = { contryCode = it },
            label = { Text("CountryCode") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )


        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
            .fillMaxWidth().padding(top = 20.dp)) {
            OutlinedTextField(
                value = authCode,
                onValueChange = { authCode = it },
                label = { Text("Auth Code") },
                singleLine = true,
                modifier = Modifier.padding(end = 10.dp)
            )
            Button(onClick = { TuyaSmartResidenceSdk.account().sendVerifyCode(userName =
            username, Global.region, contryCode, Global.type, object: Business.ResultListener<String?> {
                override fun onFailure(p0: BusinessResponse?, p1: String?, p2: String?) {
                    p0?.errorMsg?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                    L.d(TAG, "BusinessResponse = $p0, result = $p1, msg = $p2")
                }

                override fun onSuccess(p0: BusinessResponse?, p1: String?, p2: String?) {
                    L.d(TAG, "BusinessResponse = $p0, result = $p1, msg = $p2")
                }

            })}, modifier = Modifier.padding(top = 5.dp)) {
                Text(text = "Get Auth Code")
            }
        }

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(onClick = { TuyaSmartResidenceSdk.account().register(username,  Global
                .region,
            contryCode, authCode, password, object: IRegisterCallback {
                override fun onSuccess(user: User?) {
                    Global.countryCode  = contryCode
                    context.startActivity(Intent(context, MainActivity::class.java))
                }

                override fun onError(code: String?, error: String?) {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    L.d(TAG, "register Error: code = $code, error = $error")
                }
            }
        ) },
            modifier =
            Modifier
                .fillMaxWidth()
                .padding
                    (top = 20.dp
                )) {
            Text(text = "Sign up")
        }
    }
}
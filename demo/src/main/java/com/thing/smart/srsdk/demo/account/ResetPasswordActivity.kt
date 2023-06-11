package com.thing.smart.srsdk.demo.account

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.tuya.smart.android.common.utils.L
import com.tuya.smart.sdk.api.IResultCallback
import com.thing.smart.srsdk.demo.TAG
import com.tuya.smart.srsdk.sdk.TuyaSmartResidenceSdk

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ResetPasswordView()
        }
    }
}

@Preview
@Composable
fun ResetPasswordView() {
    val context = LocalContext.current
    var password by rememberSaveable { mutableStateOf("") }
    Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(onClick = { TuyaSmartResidenceSdk.account().setPassword(password, object:
            IResultCallback {

            override fun onError(code: String?, error: String?) {
                L.d(TAG, "setPassword Error: code = $code, error = $error")
            }

            override fun onSuccess() {
                L.d(TAG, "setPassword Success")
                context.startActivity(Intent(context, ResetPasswordActivity::class.java))
            }
        }
        ) },
            modifier =
            Modifier
                .fillMaxWidth()
                .padding
                    (top = 20.dp
                )) {
            Text(text = "Set Password")
        }
    }
}
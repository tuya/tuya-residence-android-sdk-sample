package com.tuya.smart.srsdk.demo.access

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuya.smart.android.network.Business
import com.tuya.smart.android.network.http.BusinessResponse
import com.tuya.smart.srsdk.api.access.bean.DoorPassword
import com.tuya.smart.srsdk.api.access.bean.PasswordDetail
import com.tuya.smart.srsdk.api.access.bean.Schedule
import com.tuya.smart.srsdk.demo.Global
import com.tuya.smart.srsdk.sdk.TuyaSmartResidenceSdk

/**
 *
 * @description:
 * @author: mengzi.deng
 * @since: 2021/11/11 16:07
 */

private var passwordDetail: PasswordDetail? = null

class UpdatePasswordAccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        passwordDetail = intent.getSerializableExtra("passwordDetail") as PasswordDetail?
        setContent {
            UpdatePasswordAccessView()
        }
    }
}

@Preview
@Composable
fun UpdatePasswordAccessView() {
    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf(passwordDetail?.contactsList?.get(0)?.name.toString()) }
    var startTime by rememberSaveable { mutableStateOf(passwordDetail?.doorPassword?.effectiveTime) }
    var endTime by rememberSaveable { mutableStateOf(passwordDetail?.doorPassword?.invalidTime) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(color = Color(242, 242, 242))
        .padding(start = 8.dp, end = 8.dp)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            Text("Update Password Access", fontSize = 18.sp, color = Color.DarkGray)
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(onClick = {
            Global.currentSite?.homeId?.let {
                TuyaSmartResidenceSdk.access().updatePasswordAccessNickname(it.toString(),
                    passwordDetail?.authGroupId!!,
                    name,
                    object : Business.ResultListener<Boolean> {
                        override fun onFailure(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                            Toast.makeText(context,
                                "UpdatePasswordAccessNickname failure:${p0?.errorMsg}",
                                Toast.LENGTH_SHORT)
                                .show()
                        }

                        override fun onSuccess(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                            Toast.makeText(context,
                                "UpdatePasswordAccessNickname success",
                                Toast.LENGTH_SHORT)
                                .show()
                        }

                    })
            }
        }, modifier = Modifier
            .fillMaxWidth()
            .padding(all = 20.dp
            )) {
            Text(text = "Update Password Access Name")
        }

        Button(onClick = {
            Global.currentSite?.homeId?.let {
                val doorPassword = DoorPassword()
                // optional
                doorPassword.authName = ""
                doorPassword.effectiveTime = startTime
                doorPassword.invalidTime = endTime
                doorPassword.authType = passwordDetail?.doorPassword?.authType
                doorPassword.scheduleRepeat = passwordDetail?.doorPassword?.scheduleRepeat
                if (doorPassword.scheduleRepeat == 1) {
                    val schedule = Schedule()
                    // 9:10
                    schedule.startClock = 9 * 60 + 10
                    // 21:30
                    schedule.endClock = 21 * 60 + 30
                    // 0b1111111 -> 127
                    schedule.weekDay = 127
                    doorPassword.schedule = schedule
                }

                TuyaSmartResidenceSdk.access()
                    .updatePasswordAccessValidity(it.toString(), passwordDetail?.authGroupId!!,
                        doorPassword,
                        object : Business.ResultListener<Boolean> {
                            override fun onFailure(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                                Toast.makeText(context,
                                    "UpdatePasswordAccessValidity failure:${p0?.errorMsg}",
                                    Toast.LENGTH_SHORT)
                                    .show()
                            }

                            override fun onSuccess(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                                Toast.makeText(context, "UpdatePasswordAccessValidity success", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        })
            }
        },
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 20.dp
                )) {
            Text(text = "Update Password Access Validity")
        }
    }

}

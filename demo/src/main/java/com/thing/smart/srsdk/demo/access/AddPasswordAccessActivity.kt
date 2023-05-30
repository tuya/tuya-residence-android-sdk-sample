package com.thing.smart.srsdk.demo.access

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuya.smart.android.network.Business
import com.tuya.smart.android.network.http.BusinessResponse
import com.tuya.smart.srsdk.api.access.bean.*
import com.thing.smart.srsdk.demo.Global
import com.thing.smart.srsdk.demo.RowSpaceLine
import com.tuya.smart.srsdk.sdk.TuyaSmartResidenceSdk

/**
 *
 * @description:
 * @author: mengzi.deng
 * @since: 2021/11/11 16:07
 */
class AddPasswordAccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddPasswordAccessView()
        }
    }
}

@Preview
@Composable
fun AddPasswordAccessView(viewModel: DeviceModel = viewModel()) {
    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var startTime by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    var endTime by rememberSaveable { mutableStateOf(System.currentTimeMillis() + 1000 * 60 * 60 * 24) }
    var passwordCreateBean = PasswordCreateBean()

    val tags = arrayListOf("One Time\nPassword", "Periodicity\nPassword")
    var selectedTag by rememberSaveable { mutableStateOf("One Time\nPassword") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(color = Color(242, 242, 242))
                .padding(start = 8.dp, end = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Add Password Access", fontSize = 18.sp, color = Color.DarkGray)
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = startTime.toString(),
                onValueChange = { startTime = it.toLong() },
                label = { Text("starTime") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = endTime.toString(),
                onValueChange = { endTime = it.toLong() },
                label = { Text("endTime") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.weight(1.0F, true),
                    singleLine = true
                )

                tags.forEach {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = it == selectedTag,
                            onClick = {
                                selectedTag = it
                            },
                            modifier = Modifier.padding(2.dp),
                            enabled = true,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(color = 0xFF6200EE),
                                unselectedColor = Color.Black,
                                disabledColor = Color.Green
                            )
                        )
                        Text(text = it, textAlign = TextAlign.Center)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }

            }

            Text(
                "Device List",
                fontSize = 18.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp, start = 10.dp)
            )

            RowSpaceLine()

            AppDeviceListView(viewModel)


        }

        Button(
            onClick = {
                Global.currentSite?.homeId?.let {
                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(
                            password
                        )
                    ) {
                        Toast.makeText(
                            context,
                            "Name or email or password cannot be empty",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    val deviceIdList = getDeviceIdList(viewModel.deviceList)
                    if (deviceIdList.isEmpty()) {
                        Toast.makeText(context, "Select at least one device", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }

                    val isRepeat = selectedTag == "Periodicity\nPassword"

                    passwordCreateBean.ownerId = it.toString()
                    passwordCreateBean.deviceIdList = deviceIdList

                    val doorPassword = DoorPassword()
                    doorPassword.passwordValue = password
                    // optional
                    doorPassword.authName = ""
                    doorPassword.effectiveTime = startTime
                    doorPassword.invalidTime = endTime
                    doorPassword.authType = if (isRepeat) 1 else 0
                    doorPassword.scheduleRepeat = if (isRepeat) 1 else 0
                    if (isRepeat) {
                        val schedule = Schedule()
                        // 9:10
                        schedule.startClock = 9 * 60 + 10
                        // 21:30
                        schedule.endClock = 21 * 60 + 30
                        // 0b1111111 -> 127
                        schedule.weekDay = 127
                        doorPassword.schedule = schedule
                    }
                    passwordCreateBean.doorPassword = doorPassword

                    val contact = Contacts()
                    contact.name = name
                    contact.email = email
                    passwordCreateBean.contactsList.clear()
                    passwordCreateBean.addContact(contact)

                    TuyaSmartResidenceSdk.access().addPasswordAccess(it.toString(),
                        passwordCreateBean,
                        viewModel.deviceList,
                        object : Business.ResultListener<PasswordResult> {
                            override fun onFailure(
                                p0: BusinessResponse?,
                                p1: PasswordResult?,
                                p2: String?
                            ) {
                                Toast.makeText(
                                    context,
                                    "addPasswordAccess failure:${p0?.errorMsg}",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }

                            override fun onSuccess(
                                p0: BusinessResponse?,
                                p1: PasswordResult?,
                                p2: String?
                            ) {
                                Toast.makeText(
                                    context,
                                    "addPasswordAccess success",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                (context as AddPasswordAccessActivity).finish()
                            }

                        })
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(
                    start = 30.dp,
                    end = 30.dp, top = 20.dp,
                    bottom = 20.dp
                )
        ) {
            Text(text = "Add Password Access")
        }
    }


}

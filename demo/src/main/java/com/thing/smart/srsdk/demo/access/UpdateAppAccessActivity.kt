package com.thing.smart.srsdk.demo.access

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuya.smart.android.network.Business
import com.tuya.smart.android.network.http.BusinessResponse
import com.tuya.smart.srsdk.api.access.bean.AccessBean
import com.tuya.smart.srsdk.api.access.bean.AccessUser
import com.thing.smart.srsdk.demo.Global
import com.tuya.smart.srsdk.sdk.TuyaSmartResidenceSdk

/**
 *
 * @description:
 * @author: mengzi.deng
 * @since: 2021/11/9 15:06
 */
private var accessBean: AccessBean? = null

class UpdateAppAccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accessBean = intent.getSerializableExtra("accessBean") as AccessBean?
        setContent {
            UpdateAppAccessView()
        }
    }

}

@Preview
@Composable
private fun UpdateAppAccessView() {

    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf(accessBean?.nickname ?: "") }
    var isAdmin by rememberSaveable { mutableStateOf(accessBean?.userType == 20) }
    var startTime by rememberSaveable { mutableStateOf(accessBean?.startTime) }
    var endTime by rememberSaveable { mutableStateOf(accessBean?.endTime) }
    var accessUser = AccessUser()

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
            Text("Update App Access", fontSize = 18.sp, color = Color.DarkGray)
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = startTime.toString(),
            onValueChange = { startTime = it.toLong() },
            label = { Text("start time") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = endTime.toString(),
            onValueChange = { endTime = it.toLong() },
            label = { Text("end time") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (accessBean?.userType == 20 || accessBean?.userType == 30) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 20.dp, start = 10.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {
                Text("Admin\nAllow authorized app and password",
                    fontSize = 18.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.weight(1.0F, true))
                Checkbox(checked = isAdmin, onCheckedChange = {
                    isAdmin = it
                })
            }
        }

        Button(onClick = {
            Global.currentSite?.let {

                accessUser.ownerId = it.homeId.toString()
                accessUser.accessUserId = accessBean?.accessUserId
                accessUser.startTime = startTime
                accessUser.endTime = endTime
                accessUser.nickname = name
                if (accessBean?.userType == 20 || accessBean?.userType == 30) {
                    accessUser.userType = if (isAdmin) 20 else 30
                }
                TuyaSmartResidenceSdk.access()
                    .updateAppAccess(accessUser, object : Business.ResultListener<Boolean> {
                        override fun onFailure(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                            Toast.makeText(context, p0?.errorMsg, Toast.LENGTH_SHORT).show()
                        }

                        override fun onSuccess(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                            Toast.makeText(context, "Update app access success", Toast.LENGTH_SHORT).show()
                            (context as UpdateAppAccessActivity).finish()
                        }

                    })
            }
        },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp,
                    end = 30.dp, top = 20.dp
                )) {
            Text(text = "Update App Access")
        }
    }
}

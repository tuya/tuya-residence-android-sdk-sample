package com.tuya.smart.srsdk.demo.access

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuya.sdk.core.PluginManager
import com.tuya.smart.android.common.utils.L
import com.tuya.smart.android.network.Business
import com.tuya.smart.android.network.http.BusinessResponse
import com.tuya.smart.interior.api.ITuyaDevicePlugin
import com.tuya.smart.srsdk.api.access.bean.AccessCreateBean
import com.tuya.smart.srsdk.api.access.bean.DeviceData
import com.tuya.smart.srsdk.demo.Global
import com.tuya.smart.srsdk.demo.RowSpaceLine
import com.tuya.smart.srsdk.demo.TAG
import com.tuya.smart.srsdk.sdk.TuyaSmartResidenceSdk

/**
 *
 * @description:
 * @author: mengzi.deng
 * @since: 2021/11/9 15:06
 */

class AddAppAccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddAppAccessView()
        }
    }

}

@Preview
@Composable
private fun AddAppAccessView(viewModel: DeviceModel = viewModel()) {

    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var isAdmin by rememberSaveable { mutableStateOf(false) }
    var startTime by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    var endTime by rememberSaveable { mutableStateOf(System.currentTimeMillis() + 1000 * 60 * 60 * 24) }
    var accessCreateBean = AccessCreateBean()

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
            Text("Add App Access", fontSize = 18.sp, color = Color.DarkGray)
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

        Text("Device List",
            fontSize = 18.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(top = 20.dp, bottom = 10.dp, start = 10.dp))

        RowSpaceLine()

        AppDeviceListView(viewModel)

        Button(onClick = {
            Global.currentSite?.let {

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
                    Toast.makeText(context, "Name or email cannot be empty", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val deviceIdList = getDeviceIdList(viewModel.deviceList)
                if (deviceIdList.isEmpty()) {
                    Toast.makeText(context, "Select at least one device", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                accessCreateBean.ownerId = it.homeId.toString()
                accessCreateBean.username = email
                accessCreateBean.nickname = name
                accessCreateBean.userType = if (isAdmin) 20 else 30
                accessCreateBean.startTime = startTime
                accessCreateBean.endTime = endTime
                accessCreateBean.deviceIdList = deviceIdList

                TuyaSmartResidenceSdk.access()
                    .addAppAccess(accessCreateBean, object : Business.ResultListener<Boolean> {
                        override fun onFailure(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                            Toast.makeText(context, p0?.errorMsg, Toast.LENGTH_SHORT).show()
                        }

                        override fun onSuccess(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                            Toast.makeText(context, "Add app access success", Toast.LENGTH_SHORT).show()
                            (context as AddAppAccessActivity).finish()
                        }

                    })
            } ?: Toast.makeText(context, "Current site is null", Toast.LENGTH_SHORT).show()
        },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp,
                    end = 30.dp, top = 20.dp
                )) {
            Text(text = "Add App Access")
        }
    }
}

fun getDeviceIdList(deviceList: MutableList<DeviceData>): ArrayList<String> {
    val deviceIdList = mutableListOf<String>()
    for (item in deviceList) {
        if (item.checkStatus) {
            item.deviceBean?.let { deviceIdList.add(it.devId) }
        }
    }
    return deviceIdList as ArrayList<String>
}

@Composable
fun AppDeviceListView(viewModel: DeviceModel) {
    L.d(TAG, "AppDeviceListView()")
    LazyColumn {
        itemsIndexed(viewModel.deviceList) { index, deviceData ->
            DeviceItemRow(index, deviceData, viewModel)
        }
    }
}

@Composable
fun DeviceItemRow(index: Int, deviceData: DeviceData, viewModel: DeviceModel) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .padding(start = 10.dp, end = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
        Text(deviceData.deviceBean?.name ?: "",
            fontSize = 18.sp,
            color = Color.DarkGray,
            modifier = Modifier.weight(1.0F, true))
        Checkbox(checked = deviceData.checkStatus, onCheckedChange = {
            deviceData.checkStatus = it
            viewModel.deviceList[index] = deviceData
        })
    }

    RowSpaceLine()
}

class DeviceModel : ViewModel() {
    val deviceList = mutableStateListOf<DeviceData>()

    init {
        L.d(TAG, "DeviceModel init")
        fetchDeviceList()
    }

    private fun fetchDeviceList() {
        L.d(TAG, "fetchDeviceList()")
        Global.currentSite?.homeId?.let {
            TuyaSmartResidenceSdk.access()
                .fetchDeviceListWithSiteId(it.toString(), object : Business.ResultListener<ArrayList<String>> {
                    override fun onFailure(p0: BusinessResponse?, p1: ArrayList<String>?, p2: String?) {
                        L.d(TAG, "Fetch device list failure")
                    }

                    override fun onSuccess(p0: BusinessResponse?, p1: ArrayList<String>?, p2: String?) {
                        p1?.run {
                            val tempDeviceList = mutableListOf<DeviceData>()
                            val iTuyaDevicePlugin = PluginManager.service(ITuyaDevicePlugin::class.java)
                            for (deviceId in this) {
                                val dev = iTuyaDevicePlugin.devListCacheManager.getDev(deviceId) ?: continue
                                L.d(TAG, "deviceName : ${dev.name}")
                                tempDeviceList.add(DeviceData(dev))
                            }
                            deviceList.clear()
                            deviceList.addAll(tempDeviceList)
                        }
                    }

                })
        }
    }
}

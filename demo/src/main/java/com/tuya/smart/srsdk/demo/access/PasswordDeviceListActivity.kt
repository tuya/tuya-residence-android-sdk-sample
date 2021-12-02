package com.tuya.smart.srsdk.demo.access

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuya.sdk.core.PluginManager
import com.tuya.smart.android.common.utils.L
import com.tuya.smart.android.network.Business
import com.tuya.smart.android.network.http.BusinessResponse
import com.tuya.smart.interior.api.ITuyaDevicePlugin
import com.tuya.smart.srsdk.api.access.bean.DeviceData
import com.tuya.smart.srsdk.demo.Global
import com.tuya.smart.srsdk.demo.TAG
import com.tuya.smart.srsdk.sdk.TuyaSmartResidenceSdk

/**
 *
 * @description:
 * @author: mengzi.deng
 * @since: 2021/11/11 20:31
 */

private var authGroupId: String? = null
private var deviceIdList: ArrayList<String>? = null
private val iTuyaDevicePlugin = PluginManager.service(ITuyaDevicePlugin::class.java)

class PasswordDeviceListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authGroupId = intent.getStringExtra("authGroupId")
        deviceIdList = intent.getStringArrayListExtra("deviceIdList")
        L.d(TAG, "deviceIdList size = ${deviceIdList?.size}")
        setContent {
            PasswordDeviceList()
        }
    }
}

@Composable
fun PasswordDeviceList(viewModel: DeviceModel = viewModel()) {

    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(color = Color(242, 242, 242))
        .padding(8.dp)) {

        LazyColumn {
            items(deviceIdList as List<String>) { deviceId ->
                PasswordDeviceItem(deviceId)
            }
        }

        Button(onClick = {

            val selectedDeviceIdList = getSelectedPasswordDeviceIdList(viewModel.deviceList)

            Global.currentSite?.homeId.let {
                TuyaSmartResidenceSdk.access().addPasswordAccessDevice(it.toString(),
                    authGroupId!!, selectedDeviceIdList,
                    object : Business.ResultListener<Boolean> {
                        override fun onFailure(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                            Toast.makeText(context,
                                "addPasswordAccessDevice failure:${p0?.errorMsg}",
                                Toast.LENGTH_SHORT)
                                .show()
                        }

                        override fun onSuccess(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                            Toast.makeText(context, "Add password access device success", Toast.LENGTH_SHORT).show()
                            (context as PasswordDeviceListActivity).finish()
                        }

                    })
            }
        }, modifier =
        Modifier
            .fillMaxWidth()
            .padding(30.dp)) {
            Text(text = "Add Device")
        }

    }
}

// get the left deviceIds
fun getSelectedPasswordDeviceIdList(deviceList: SnapshotStateList<DeviceData>): List<String> {
    var selectedDeviceIdList = mutableListOf<String>()
    for (deviceData in deviceList) {
        val deviceId = deviceData.deviceBean?.devId
        if (deviceIdList?.contains(deviceId) == false) {
            deviceId?.let { selectedDeviceIdList.add(it) }
        }
    }
    return selectedDeviceIdList
}

@Composable
fun PasswordDeviceItem(deviceId: String) {
    val context = LocalContext.current
    val deviceBean = iTuyaDevicePlugin.devListCacheManager.getDev(deviceId)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .height(50.dp)
            .padding(8.dp)
            .clickable(onClick = {

                AlertDialog
                    .Builder(context)
                    .setMessage("Are you sure to remove the password access device?")
                    .setTitle("Delete Password Access Device")
                    .setPositiveButton("Confirm") { _, _ ->
                        deletePasswordDevice(context, deviceId)
                    }
                    .setNeutralButton("Cancel", null)
                    .create()
                    .show()


            }),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(deviceBean?.name ?: "", fontSize = 16.sp, modifier = Modifier.weight(1.0F, true))
        Text("Delete", fontSize = 16.sp)

    }
}

fun deletePasswordDevice(context: Context, deviceId: String) {
    Global.currentSite?.homeId?.let {
        TuyaSmartResidenceSdk
            .access()
            .removePasswordAccessDevice(it.toString(),
                authGroupId!!,
                deviceId,
                object : Business.ResultListener<Boolean> {
                    override fun onFailure(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                        Toast
                            .makeText(context, "removePasswordAccessDevice failure:${p0?.errorMsg}", Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun onSuccess(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                        Toast
                            .makeText(context, "Remove Password Access Device Success", Toast.LENGTH_SHORT)
                            .show()
                        (context as PasswordDeviceListActivity).finish()
                    }

                })
    }
}

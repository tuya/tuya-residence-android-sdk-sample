package com.thing.smart.srsdk.demo.access

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuya.sdk.core.PluginManager
import com.tuya.smart.android.common.utils.L
import com.tuya.smart.android.network.Business
import com.tuya.smart.android.network.http.BusinessResponse
import com.tuya.smart.interior.api.ITuyaDevicePlugin
import com.tuya.smart.srsdk.api.access.bean.AccessBean
import com.tuya.smart.srsdk.api.access.bean.PassRecordBean
import com.tuya.smart.srsdk.api.access.bean.PassRecordPageBean
import com.thing.smart.srsdk.demo.ArrowImage
import com.thing.smart.srsdk.demo.Global
import com.thing.smart.srsdk.demo.RowSpaceLine
import com.thing.smart.srsdk.demo.TAG
import com.thing.smart.srsdk.demo.util.toDateStr
import com.tuya.smart.srsdk.sdk.TuyaSmartResidenceSdk

/**
 *
 * @description:
 * @author: mengzi.deng
 * @since: 2021/11/10 17:05
 */

private var accessBean: AccessBean? = null
private var accessUserId: String? = null
private var homeMember: Int? = null
private var deviceIdList: ArrayList<String> = ArrayList()
private val iTuyaDevicePlugin = PluginManager.service(ITuyaDevicePlugin::class.java)

class AppAccessDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val username = intent.getStringExtra("username")
        accessUserId = intent.getStringExtra("accessUserId")
        setContent {
            AppAccessDetailView(username)
        }
    }
}

@Composable
fun AppAccessDetailView(
    username: String?,
    viewModel: AppPassRecordModel = viewModel(),
    deviceModel: DeviceModel = viewModel(),
) {
    val context = LocalContext.current
    var name: String by remember { mutableStateOf("") }
    var email: String by remember { mutableStateOf("") }

    val myself = TuyaSmartResidenceSdk.account().getUserInfo()?.username.equals(username)

    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(color = Color(242, 242, 242))
        .padding(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(name, fontSize = 16.sp, modifier = Modifier.weight(1.0F, true))
            Text("Update",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp)
                    .clickable(onClick = {
                        context.startActivity(Intent(context,
                            UpdateAppAccessActivity::class.java).putExtra("accessBean", accessBean))
                    }))
            if (!myself) {
                Text("Delete", fontSize = 16.sp, color = Color.Black, modifier = Modifier.clickable(
                    onClick = {
                        AlertDialog.Builder(context)
                            .setMessage("Are you sure to delete the app access?")
                            .setTitle("Delete App Access")
                            .setPositiveButton("Confirm", DialogInterface.OnClickListener { _, _ ->
                                removeAppAccess(context)
                            })
                            .setNeutralButton("Cancel", null)
                            .create()
                            .show()
                    }
                ))
            }
        }
        Text(email, fontSize = 16.sp)

        RowSpaceLine()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
                .height(50.dp)
                .padding(8.dp)
                .clickable(onClick = {

                    // if family member, get all devices
                    if (homeMember == 1) {
                        getAllDeviceIdList(deviceModel)
                    }

                    context.startActivity(Intent(context, AppDeviceListActivity::class.java)
                        .putExtra("accessUserId", accessUserId)
                        .putStringArrayListExtra("deviceIdList", deviceIdList))
                }),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Device List", fontSize = 16.sp, modifier = Modifier.weight(1.0F, true))
            ArrowImage()
        }
        RowSpaceLine()

        Text("Pass Record",
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(top = 30.dp, bottom = 10.dp))

        LazyColumn {
            itemsIndexed(viewModel.appPassRecordList) { _, item ->
                AppPassRecordItemView(item)
            }
        }
    }

    Global.currentSite?.homeId?.let {
        TuyaSmartResidenceSdk.access()
            .fetchAppAccessUserDetail(it.toString(), accessUserId!!, object : Business.ResultListener<AccessBean> {
                override fun onFailure(p0: BusinessResponse?, p1: AccessBean?, p2: String?) {
                    L.d(TAG, "fetchAppAccessUserDetail failure: ${p0?.errorMsg}")
                }

                override fun onSuccess(p0: BusinessResponse?, p1: AccessBean?, p2: String?) {
                    accessBean = p1
                    name = p1?.nickname.toString()
                    email = p1?.username.toString()
                    homeMember = p1?.homeMember
                    p1?.deviceIdList?.run {
                        L.d(TAG, "fetchAppAccessUserDetail deviceIdList size = ${this.size}")
                        deviceIdList.clear()
                        deviceIdList.addAll(this)
                    }
                }

            })
    }
}

fun getAllDeviceIdList(deviceModel: DeviceModel) {
    if (deviceModel.deviceList.isEmpty()) {
        return
    }
    deviceIdList.clear()
    for (item in deviceModel.deviceList) {
        deviceIdList.add(item.deviceBean?.devId ?: "")
    }
}

fun removeAppAccess(context: Context) {
    Global.currentSite?.homeId?.let {
        TuyaSmartResidenceSdk.access()
            .removeAppAccess(it.toString(), accessUserId!!, object : Business.ResultListener<Boolean> {
                override fun onFailure(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                    Toast.makeText(context, "removeAppAccess failure:${p0?.errorMsg}", Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                    Toast.makeText(context, "removeAppAccess success", Toast.LENGTH_SHORT).show()
                    (context as AppAccessDetailActivity).finish()
                }

            })
    }
}

@Composable
fun AppPassRecordItemView(item: PassRecordBean) {
    var deviceName: String? = null
    iTuyaDevicePlugin.devListCacheManager.getDev(item.deviceId)?.let {
        deviceName = it.name
    }
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 5.dp)) {
        Text(item.createTime.toDateStr(), fontSize = 16.sp)
        Text((item.dp ?: "").plus("    ").plus(deviceName),
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 5.dp))
        RowSpaceLine()
    }
}

class AppPassRecordModel : ViewModel() {

    val appPassRecordList = mutableStateListOf<PassRecordBean>()

    init {
        fetchAppAccessPassRecord()
    }

    private fun fetchAppAccessPassRecord() {

        Global.currentSite?.homeId?.let {
            TuyaSmartResidenceSdk.access().fetchAppAccessPassRecord(it.toString(),
                accessUserId!!,
                "1",
                "20",
                object : Business.ResultListener<PassRecordPageBean> {
                    override fun onFailure(p0: BusinessResponse?, p1: PassRecordPageBean?, p2: String?) {
                        L.d(TAG, "fetchAppAccessPassRecord failure: ${p0?.errorMsg}")
                    }

                    override fun onSuccess(p0: BusinessResponse?, p1: PassRecordPageBean?, p2: String?) {
                        appPassRecordList.clear()
                        p1?.data?.run { appPassRecordList.addAll(this) }
                    }

                })
        }
    }
}
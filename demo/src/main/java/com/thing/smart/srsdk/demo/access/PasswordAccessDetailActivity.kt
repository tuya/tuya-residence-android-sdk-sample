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
import com.tuya.smart.srsdk.api.access.bean.PassRecordBean
import com.tuya.smart.srsdk.api.access.bean.PassRecordPageBean
import com.tuya.smart.srsdk.api.access.bean.PasswordDetail
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

private var passwordDetail: PasswordDetail? = null
private var deviceIdList: ArrayList<String> = ArrayList()
private var authGroupId: String? = null
private val iTuyaDevicePlugin = PluginManager.service(ITuyaDevicePlugin::class.java)

class PasswordAccessDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authGroupId = intent.getStringExtra("authGroupId")
        setContent {
            PasswordAccessDetailView()
        }
    }
}

@Composable
fun PasswordAccessDetailView(viewModel: PassRecordModel = viewModel()) {
    val context = LocalContext.current
    var name: String by remember { mutableStateOf("") }
    var email: String by remember { mutableStateOf("") }

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
                            UpdatePasswordAccessActivity::class.java).putExtra("passwordDetail",
                            passwordDetail))
                    }))
            Text("Delete", fontSize = 16.sp, color = Color.Black, modifier = Modifier.clickable(
                onClick = {
                    AlertDialog.Builder(context)
                        .setMessage("Are you sure to delete the password access?")
                        .setTitle("Delete Password Access")
                        .setPositiveButton("Confirm", DialogInterface.OnClickListener { _, _ ->
                            removePasswordAccess(context)
                        })
                        .setNeutralButton("Cancel", null)
                        .create()
                        .show()
                }
            ))

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
                    context.startActivity(Intent(context, PasswordDeviceListActivity::class.java)
                        .putExtra("authGroupId", authGroupId)
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
            itemsIndexed(viewModel.passRecordList) { _, item ->
                PassRecordItemView(item)
            }
        }
    }

    Global.currentSite?.homeId?.let {
        TuyaSmartResidenceSdk.access()
            .fetchPasswordAccessUserDetail(it.toString(),
                authGroupId!!,
                object : Business.ResultListener<PasswordDetail> {
                    override fun onFailure(p0: BusinessResponse?, p1: PasswordDetail?, p2: String?) {
                        L.d(TAG, "fetchPasswordAccessUserDetail failure: ${p0?.errorMsg}")
                    }

                    override fun onSuccess(p0: BusinessResponse?, p1: PasswordDetail?, p2: String?) {
                        passwordDetail = p1
                        name = p1?.contactsList?.get(0)?.name.toString()
                        email = p1?.contactsList?.get(0)?.email.toString()
                        p1?.deviceList?.run {
                            deviceIdList.clear()
                            for (item in this) {
                                deviceIdList.add(item.deviceId ?: "")
                            }
                        }
                    }

                })
    }
}

fun removePasswordAccess(context: Context) {
    Global.currentSite?.homeId?.let {
        TuyaSmartResidenceSdk.access()
            .removePasswordAccess(it.toString(), authGroupId!!, object : Business.ResultListener<Boolean> {
                override fun onFailure(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                    Toast.makeText(context, "removePasswordAccess failure:${p0?.errorMsg}", Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                    Toast.makeText(context, "removePasswordAccess success", Toast.LENGTH_SHORT).show()
                    (context as PasswordAccessDetailActivity).finish()
                }

            })
    }
}

@Composable
fun PassRecordItemView(item: PassRecordBean) {
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

class PassRecordModel : ViewModel() {

    val passRecordList = mutableStateListOf<PassRecordBean>()

    init {
        fetchPasswordAccessPassRecord()
    }

    private fun fetchPasswordAccessPassRecord() {

        Global.currentSite?.homeId?.let {
            TuyaSmartResidenceSdk.access().fetchPasswordAccessPassRecord(it.toString(),
                authGroupId!!,
                "1",
                "20",
                object : Business.ResultListener<PassRecordPageBean> {
                    override fun onFailure(p0: BusinessResponse?, p1: PassRecordPageBean?, p2: String?) {
                        L.d(TAG, "fetchPasswordAccessPassRecord failure: ${p0?.errorMsg}")
                    }

                    override fun onSuccess(p0: BusinessResponse?, p1: PassRecordPageBean?, p2: String?) {
                        passRecordList.clear()
                        p1?.data?.run { passRecordList.addAll(this) }
                    }

                })
        }
    }
}
package com.tuya.smart.srsdk.demo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuya.smart.android.common.utils.L
import com.tuya.smart.android.network.Business
import com.tuya.smart.android.network.http.BusinessResponse
import com.tuya.smart.android.user.api.ILogoutCallback
import com.tuya.smart.srsdk.demo.pater.R
import com.tuya.smart.srsdk.api.site.bean.SiteBean
import com.tuya.smart.srsdk.demo.access.*
import com.tuya.smart.srsdk.demo.personal.PersonalManagerActivity
import com.tuya.smart.srsdk.demo.site.*
import com.tuya.smart.srsdk.sdk.TuyaSmartResidenceSdk
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainView()
        }
    }
}

@Preview
@Composable
fun MainView() {
    val state = rememberScrollState()
    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(color = Color(242, 242, 242))
        .verticalScroll(state)
    ) {

        UserManagementView()

        SiteManagementView()

        AccessManagementView()

        LogOut()

    }

}

val rowModifier = Modifier
    .fillMaxWidth()
    .background(color = Color.White)
    .height(50.dp)
    .padding(8.dp)

@Composable
fun RowSpaceLine() {
    Spacer(modifier = Modifier
        .fillMaxWidth()
        .height(0.3.dp)
        .background(color = Color.Gray))
}

@Composable
fun RowSpacer() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
    )
}

@Composable
fun ArrowImage() {
    Image(painter = painterResource(id = R.drawable.icon_right_arrow),
        contentDescription = "",
        Modifier.size(24.dp))
}

@Composable
fun TypeText(typeName: String) {
    Text(typeName, fontSize = 18.sp, modifier = Modifier.padding(8.dp))
}

@Composable
fun TextItem(text: String, bottomLine: Boolean = false, value: String = "", onClick: () -> Unit) {
    Row(
        modifier = rowModifier
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text, fontSize = 16.sp, modifier = Modifier.weight(1.0F, true))
        Text(value, fontSize = 14.sp)
        ArrowImage()
    }
    if (bottomLine) {
        RowSpaceLine()
    }
}

@Composable
fun UserManagementView() {
    val context = LocalContext.current
    RowSpacer()
    TypeText("User Management")
    TextItem(text = "Personal Information") {
        context.startActivity(Intent(context, PersonalManagerActivity::class.java))
    }
}

@Composable
fun SiteManagementView() {
    RowSpacer()
    TypeText("Site Management")
    CreateASite()
    CurrentSite()
    SiteList()
    SiteDetail()
    ModifySiteInformation()
    AddMember()
    FetchExpiredList()
}

@Composable
fun CreateASite() {
    val context = LocalContext.current
    TextItem(text = "Create A Site", bottomLine = true) {
        context.startActivity(Intent(context, CreateSiteActivity::class.java))
    }
}

@Composable
fun CurrentSite() {
    val context = LocalContext.current
    var siteName by rememberSaveable { mutableStateOf(Global.currentSite?.name ?: "") }
    val registerForActivityResult = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        it.data?.run {
            siteName = getStringExtra("currentSiteName").toString()
        }
    }
    TextItem(text = "Current Site", bottomLine = true, siteName) {
        registerForActivityResult.launch(Intent(context, CurrentSiteActivity::class.java))
    }
}

@Composable
fun SiteList() {
    TextItem(text = "Site List", bottomLine = true) {
        TuyaSmartResidenceSdk
            .siteManager()
            .fetchSiteList(object : Business
            .ResultListener<ArrayList<SiteBean?>?> {
                override fun onFailure(
                    p0: BusinessResponse?,
                    p1: ArrayList<SiteBean?>?,
                    p2: String?,
                ) {
                    L.d(TAG, "onFailure BusinessResponse = $p0, ArrayList<SiteBean?> = $p1, " +
                            "String = $p2")
                }

                override fun onSuccess(
                    p0: BusinessResponse?,
                    siteList: ArrayList<SiteBean?>?,
                    p2: String?,
                ) {
                    L.d(TAG, "onSuccess BusinessResponse = $p0, ArrayList<SiteBean?> =" +
                            "$siteList, String = $p2")
                    if (!siteList.isNullOrEmpty()) {
                        Global.siteMap.clear()
                        Global.currentSite = siteList[0]
                        siteList.forEach {
                            if (it != null) {
                                Global.siteMap[it.homeId] = it
                            }
                        }
                    }
                }

            })
    }
}

@Composable
fun SiteDetail() {
    val context = LocalContext.current
    TextItem(text = "Site Detail", bottomLine = true) {
        if (Global.currentSite == null) {
            Toast.makeText(context, "current site is null!", Toast.LENGTH_LONG).show()
        } else {
            context.startActivity(Intent(context, SiteDetailActivity::class.java))
        }
    }
}

@Composable
fun ModifySiteInformation() {
    val context = LocalContext.current
    TextItem(text = "Modify Site Information", bottomLine = true) {
        context.startActivity(Intent(context, ModifySiteActivity::class.java))
    }
}

@Composable
fun AddMember() {
    val context = LocalContext.current
    TextItem(text = "Add Member", bottomLine = true) {
        context.startActivity(Intent(context, AddMemberActivity::class.java))
    }
}

@Composable
fun FetchExpiredList() {
    val context = LocalContext.current
    TextItem(text = "Fetch Expired List") {
        context.startActivity(Intent(context, ExpiredListActivity::class.java))
    }
}


@Composable
fun AccessManagementView() {
    RowSpacer()
    TypeText("Access Management")
    DeviceList()
    AddAppAccess()
    QueryAppAccess()
    AddPasswordAccess()
    QueryPasswordAccess()
    FetchAppAccessTotal()
    FetchPasswordAccessTotal()
    CheckAccessAccount()
    CheckAccessAuthorization()

}

@Composable
fun DeviceList() {
    val context = LocalContext.current
    TextItem(text = "Device List", bottomLine = true) {
        context.startActivity(Intent(context, DeviceListActivity::class.java))
    }
}

@Composable
fun AddAppAccess() {
    val context = LocalContext.current
    TextItem(text = "Add App Access", bottomLine = true) {
        context.startActivity(Intent(context, AddAppAccessActivity::class.java))
    }
}

@Composable
fun QueryAppAccess() {
    val context = LocalContext.current
    TextItem(text = "Query App Access", bottomLine = true) {
        context.startActivity(Intent(context, QueryAppAccessActivity::class.java))
    }
}

@Composable
fun AddPasswordAccess() {
    val context = LocalContext.current
    TextItem(text = "Add Password Access", bottomLine = true) {
        context.startActivity(Intent(context, AddPasswordAccessActivity::class.java))
    }
}

@Composable
fun QueryPasswordAccess() {
    val context = LocalContext.current
    TextItem(text = "Query Password Access", bottomLine = true) {
        context.startActivity(Intent(context, QueryPasswordAccessActivity::class.java))
    }
}


@Composable
fun FetchAppAccessTotal() {
    var totalCount: String by remember { mutableStateOf("") }
    TextItem(text = "App Access Total Count", bottomLine = true, totalCount) {
        Global.currentSite?.homeId?.let {
            TuyaSmartResidenceSdk
                .access()
                .fetchAppAccessTotal(it.toString(), 1, object : Business.ResultListener<Int> {
                    override fun onFailure(p0: BusinessResponse?, p1: Int?, p2: String?) {
                        L.d(TAG, "fetchAppAccessTotal failure:${p0?.errorMsg}")
                    }

                    override fun onSuccess(p0: BusinessResponse?, p1: Int?, p2: String?) {
                        totalCount = p1
                            ?.toString()
                            .plus("个")
                    }

                })
        }
    }
}

@Composable
fun FetchPasswordAccessTotal() {
    var totalCount: String by remember { mutableStateOf("") }
    TextItem(text = "Password Access Total Count", bottomLine = true, totalCount) {
        Global.currentSite?.homeId?.let {
            TuyaSmartResidenceSdk
                .access()
                .fetchPasswordAccessTotal(it.toString(), 1, object : Business.ResultListener<Int> {
                    override fun onFailure(p0: BusinessResponse?, p1: Int?, p2: String?) {
                        L.d(TAG, "fetchPasswordAccessTotal failure:${p0?.errorMsg}")
                    }

                    override fun onSuccess(p0: BusinessResponse?, p1: Int?, p2: String?) {
                        totalCount = p1
                            ?.toString()
                            .plus("个")
                    }

                })
        }
    }
}

@Composable
fun CheckAccessAccount() {
    var isRegistered: String by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .height(50.dp)
            .padding(start = 8.dp, end = 8.dp)
            .clickable(onClick = {
                TuyaSmartResidenceSdk
                    .access()
                    .checkAccessAccount("", object : Business.ResultListener<Boolean> {
                        override fun onFailure(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                            L.d(TAG, "CheckAccessAccount failure:${p0?.errorMsg}")
                        }

                        override fun onSuccess(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                            isRegistered = if (p1 == true) "Registered" else "Not registered"
                        }

                    })

            }),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Column(modifier = Modifier.weight(1.0F, true)) {
            Text("Check Access Account", fontSize = 16.sp)
            Text("(Check whether the account is registered)", fontSize = 12.sp)
        }
        Text(isRegistered, fontSize = 12.sp)
        ArrowImage()
    }
    RowSpaceLine()
}

@Composable
fun CheckAccessAuthorization() {
    var isAuthorized: String by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .height(50.dp)
            .padding(start = 8.dp, end = 8.dp)
            .clickable(onClick = {
                Global.currentSite?.homeId?.let {
                    TuyaSmartResidenceSdk
                        .access()
                        .checkAccessAuthorization(it.toString(),
                            "",
                            object : Business.ResultListener<Int> {
                                override fun onFailure(p0: BusinessResponse?, p1: Int?, p2: String?) {
                                    L.d(TAG, "CheckAccessAuthorization failure:${p0?.errorMsg}")
                                }

                                override fun onSuccess(p0: BusinessResponse?, p1: Int?, p2: String?) {
                                    isAuthorized = if (p1 == 0) "Authorized" else "Not Authorized"
                                }

                            })
                }
            }),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Column(modifier = Modifier.weight(1.0F, true)) {
            Text("Check Access Authorization", fontSize = 16.sp)
            Text("(Check whether the account is authorized)", fontSize = 12.sp)
        }
        Text(isAuthorized, fontSize = 12.sp)
        ArrowImage()
    }

}

@Composable
fun LogOut() {
    val context = LocalContext.current
    Button(onClick = {
        TuyaSmartResidenceSdk.account().logout(object : ILogoutCallback {
            override fun onSuccess() {
                L.d(TAG, "Logout success")
                (context as MainActivity).finish()
            }

            override fun onError(code: String?, error: String?) {
                L.d(TAG, "Logout error: code = $code, error = $error")
            }

        })
    }, modifier =
    Modifier
        .fillMaxWidth()
        .padding(30.dp)) {
        Text(text = "Logout")
    }
}

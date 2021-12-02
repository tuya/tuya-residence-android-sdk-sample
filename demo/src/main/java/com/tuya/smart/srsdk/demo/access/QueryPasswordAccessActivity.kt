package com.tuya.smart.srsdk.demo.access

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuya.smart.android.common.utils.L
import com.tuya.smart.android.network.Business
import com.tuya.smart.android.network.http.BusinessResponse
import com.tuya.smart.srsdk.R
import com.tuya.smart.srsdk.api.access.bean.AccessPasswordBean
import com.tuya.smart.srsdk.demo.ArrowImage
import com.tuya.smart.srsdk.demo.Global
import com.tuya.smart.srsdk.demo.RowSpaceLine
import com.tuya.smart.srsdk.demo.TAG
import com.tuya.smart.srsdk.sdk.TuyaSmartResidenceSdk

/**
 *
 * @description:
 * @author: mengzi.deng
 * @since: 2021/11/10 17:18
 */
class QueryPasswordAccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PasswordAccessListView()
        }
    }
}

@Preview
@Composable
fun PasswordAccessListView(viewModel: PasswordAccessModel = viewModel()) {
    LazyColumn {
        itemsIndexed(viewModel.passwordAccessList) { _, passwordAccessItem ->
            PasswordAccessRow(passwordAccessItem)
        }
    }
}

@Composable
fun PasswordAccessRow(accessItem: AccessPasswordBean) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .padding(8.dp)
            .clickable(onClick = {
                context.startActivity(Intent(context, PasswordAccessDetailActivity::class.java)
                    .putExtra("authGroupId", accessItem.authGroupId))
            }),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Column(modifier = Modifier.weight(1.0F, true)) {
            Text(accessItem.doorPassword?.authName ?: "", fontSize = 16.sp)
            Text((accessItem.contactsList?.get(0)?.name ?: "").plus("(")
                .plus(accessItem.contactsList?.get(0)?.email ?: "").plus(")"),
                fontSize = 16.sp)
        }
        ArrowImage()
    }
    RowSpaceLine()

}

class PasswordAccessModel : ViewModel() {
    val passwordAccessList = mutableStateListOf<AccessPasswordBean>()

    init {
        fetchPasswordAccessList()
    }

    private fun fetchPasswordAccessList() {
        Global.currentSite?.homeId.let {
            TuyaSmartResidenceSdk.access()
                .fetchPasswordAccessList(it.toString(),
                    1,
                    "1",
                    "20",
                    object : Business.ResultListener<ArrayList<AccessPasswordBean>> {
                        override fun onFailure(p0: BusinessResponse?, p1: ArrayList<AccessPasswordBean>?, p2: String?) {
                            L.d(TAG, "fetchPasswordAccessList failure: ${p0?.errorMsg} ")
                        }

                        override fun onSuccess(p0: BusinessResponse?, p1: ArrayList<AccessPasswordBean>?, p2: String?) {
                            p1?.run {
                                passwordAccessList.clear()
                                passwordAccessList.addAll(this)
                            }
                        }
                    })
        }
    }
}
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
import com.tuya.smart.srsdk.api.access.bean.AccessBean
import com.tuya.smart.srsdk.api.access.bean.AccessPageBean
import com.tuya.smart.srsdk.demo.ArrowImage
import com.tuya.smart.srsdk.demo.Global
import com.tuya.smart.srsdk.demo.RowSpaceLine
import com.tuya.smart.srsdk.demo.TAG
import com.tuya.smart.srsdk.sdk.TuyaSmartResidenceSdk

/**
 *
 * @description:
 * @author: mengzi.deng
 * @since: 2021/11/10 15:55
 */
class QueryAppAccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppAccessListView()
        }
    }
}

@Preview
@Composable
fun AppAccessListView(viewModel: AppAccessModel = viewModel()) {
    LazyColumn {
        itemsIndexed(viewModel.appAccessList) { _, accessItem ->
            AppAccessRow(accessItem)
        }
    }
}

@Composable
fun AppAccessRow(accessItem: AccessBean) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .padding(8.dp)
            .clickable(onClick = {
                context.startActivity(Intent(context, AppAccessDetailActivity::class.java)
                    .putExtra("username", accessItem.username)
                    .putExtra("accessUserId", accessItem.accessUserId))
            }),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Column(modifier = Modifier.weight(1.0F, true)) {
            Text(accessItem.nickname ?: "", fontSize = 16.sp)
            Text(accessItem.username ?: "", fontSize = 16.sp)
        }
        ArrowImage()
    }
    RowSpaceLine()

}

class AppAccessModel : ViewModel() {
    val appAccessList = mutableStateListOf<AccessBean>()

    init {
        fetchAppAccessList()
    }

    private fun fetchAppAccessList() {
        Global.currentSite?.homeId.let {
            TuyaSmartResidenceSdk.access()
                .fetchAppAccessList(it.toString(), 1, "1", "20", object : Business.ResultListener<AccessPageBean> {
                    override fun onFailure(p0: BusinessResponse?, p1: AccessPageBean?, p2: String?) {
                        L.d(TAG, "fetchAppAccessList failure: ${p0?.errorMsg} ")
                    }

                    override fun onSuccess(p0: BusinessResponse?, p1: AccessPageBean?, p2: String?) {
                        p1?.data?.run {
                            appAccessList.clear()
                            appAccessList.addAll(this)
                        }
                    }
                })
        }
    }
}
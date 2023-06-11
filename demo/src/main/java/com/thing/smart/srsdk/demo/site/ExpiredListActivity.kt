package com.thing.smart.srsdk.demo.site

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuya.smart.android.common.utils.L
import com.tuya.smart.android.network.Business
import com.tuya.smart.android.network.http.BusinessResponse
import com.tuya.smart.srsdk.api.site.bean.SiteExpiredAuthBean
import com.tuya.smart.srsdk.api.site.bean.SiteExpiredAuthBeans
import com.thing.smart.srsdk.demo.RowSpaceLine
import com.thing.smart.srsdk.demo.RowSpacer
import com.thing.smart.srsdk.demo.TAG
import com.thing.smart.srsdk.demo.rowModifier
import com.tuya.smart.srsdk.sdk.TuyaSmartResidenceSdk

class ExpiredListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpiredList()
        }
    }
}

@Preview
@Composable
fun ExpiredList(viewModel: ExpiredListModel = viewModel()) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
                .height(50.dp)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "ExpiredList", fontSize = 18.sp)
        }
        RowSpacer()

        LazyColumn(content = {
            itemsIndexed(viewModel.expiredList) { _, item ->
                ExpiredListItemView(item)
            }
        })
    }
}

@Composable
fun ExpiredListItemView(item: SiteExpiredAuthBean) {
    Column {
        Row(
            modifier = rowModifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(item.getProjectName() ?: "", fontSize = 16.sp, modifier = Modifier.weight(1.0F, true))
        }
        RowSpaceLine()
    }
}


class ExpiredListModel : ViewModel() {

    val expiredList = mutableStateListOf<SiteExpiredAuthBean>()

    init {
        fetchExpiredList()
    }

    private fun fetchExpiredList() {
        TuyaSmartResidenceSdk.siteManager().fetchExpiredList(1, 20, object : Business
        .ResultListener<SiteExpiredAuthBeans> {
            override fun onFailure(
                p0: BusinessResponse?,
                p1: SiteExpiredAuthBeans?,
                p2: String?,
            ) {
                L.d(TAG, "fetchExpiredList onFailure: ${p0?.errorMsg}")
            }

            override fun onSuccess(
                p0: BusinessResponse?,
                p1: SiteExpiredAuthBeans?,
                p2: String?,
            ) {
                expiredList.clear()
                p1?.getData()?.run { expiredList.addAll(this) }
            }

        })
    }
}
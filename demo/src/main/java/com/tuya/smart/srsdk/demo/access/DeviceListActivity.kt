package com.tuya.smart.srsdk.demo.access

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuya.smart.srsdk.R
import com.tuya.smart.srsdk.api.access.bean.DeviceData
import com.tuya.smart.srsdk.demo.ArrowImage
import com.tuya.smart.srsdk.demo.RowSpaceLine

/**
 *
 * @description:
 * @author: mengzi.deng
 * @since: 2021/11/10 15:42
 */
class DeviceListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DeviceListView()
        }
    }
}

@Preview
@Composable
fun DeviceListView(viewModel: DeviceModel = viewModel()) {
    LazyColumn {
        itemsIndexed(viewModel.deviceList) { _, deviceData ->
            DeviceRow(deviceData)
        }
    }
}

@Composable
fun DeviceRow(deviceData: DeviceData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .height(50.dp)
            .padding(8.dp)
            .clickable(onClick = {

            }),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Text(deviceData.deviceBean?.name ?: "", fontSize = 16.sp, modifier = Modifier.weight(1.0F, true))
        ArrowImage()
    }
    RowSpaceLine()
}

package com.tuya.smart.srsdk.demo.personal

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuya.smart.android.network.Business
import com.tuya.smart.android.network.http.BusinessResponse
import com.tuya.smart.srsdk.api.account.bean.TimezoneeBean
import com.tuya.smart.srsdk.demo.RowSpaceLine
import com.tuya.smart.srsdk.sdk.TuyaSmartResidenceSdk

/**
 *
 * @description:
 * @author: mengzi.deng
 * @since: 2021/11/8 14:16
 */

private var timeZoneList: ArrayList<TimezoneeBean?>? = null

class TimeZoneActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timeZoneList = intent.getSerializableExtra("timeZoneList") as ArrayList<TimezoneeBean?>?
        setContent {
            TimeZoneView()
        }
    }

}

@Preview
@Composable
fun TimeZoneView() {

    Column {

        Row(modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(color = Color(242, 242, 242)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            Text("Time Zone List", fontSize = 18.sp, color = Color.DarkGray)
        }

        LazyColumn {
            items(timeZoneList as List<TimezoneeBean>) { timeZone ->
                TimeZoneRow(timeZone)
            }
        }

    }

}

@Composable
fun TimeZoneRow(timeZone: TimezoneeBean?) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .height(50.dp)
            .padding(8.dp)
            .clickable(onClick = {
                TuyaSmartResidenceSdk
                    .account()
                    .updateTimeZone(timezoneid = timeZone?.timezoneId, object : Business.ResultListener<Boolean?> {
                        override fun onFailure(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                            Toast
                                .makeText(context, "Update TimeZone failure", Toast.LENGTH_SHORT)
                                .show()
                        }

                        override fun onSuccess(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                            TuyaSmartResidenceSdk
                                .account()
                                .getUserInfo()?.timezoneId = timeZone?.timezoneId
                            (context as TimeZoneActivity).setResult(1, Intent().putExtra("timeZone", timeZone?.display))
                            context.finish()
                        }

                    })
            }),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Text(timeZone!!.display!!, fontSize = 16.sp, modifier = Modifier.weight(1.0F, true))
    }
    RowSpaceLine()
}

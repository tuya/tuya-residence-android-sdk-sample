package com.tuya.smart.srsdk.demo.site

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuya.smart.srsdk.R
import com.tuya.smart.srsdk.demo.ArrowImage
import com.tuya.smart.srsdk.demo.Global
import com.tuya.smart.srsdk.demo.RowSpaceLine

class CurrentSiteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { 
            CurrentSiteList()
        }
    }

}

@Preview
@Composable
fun CurrentSiteList() {
    val context = LocalContext.current
    val listData = Global.siteMap.values.toList()
    Spacer(modifier = Modifier
        .fillMaxWidth()
        .height(30.dp))
    LazyColumn(content = {
        items(listData.size) {
            val itemBean = listData[it]
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.White)
                        .height(50.dp)
                        .padding(8.dp)
                        .clickable(onClick = {
                            Global.currentSite = itemBean
                            if (context is Activity) {
                                context.setResult(Activity.RESULT_OK, Intent().apply {
                                    putExtra("currentSiteName", itemBean.name)
                                })
                                context.finish()
                            } else {

                            }
                        }),
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    Text(itemBean.name, fontSize = 16.sp, modifier = Modifier.weight(1.0F, true))
                    if (Global.currentSite?.homeId == itemBean.homeId) {
                        ArrowImage()
                    }
                }
                RowSpaceLine()
            }
        }
    })
}
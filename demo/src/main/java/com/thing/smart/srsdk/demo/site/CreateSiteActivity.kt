package com.thing.smart.srsdk.demo.site

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.tuya.smart.android.common.utils.L
import com.tuya.smart.android.network.Business
import com.tuya.smart.android.network.http.BusinessResponse
import com.tuya.smart.home.sdk.bean.HomeBean
import com.tuya.smart.srsdk.api.site.bean.HomeResponseBean
import com.thing.smart.srsdk.demo.Global
import com.thing.smart.srsdk.demo.TAG
import com.tuya.smart.srsdk.sdk.TuyaSmartResidenceSdk

class CreateSiteActivity : AppCompatActivity() {
    val create = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CreateSite(create)
        }
    }
}

class TypeProvider : PreviewParameterProvider<Int> {
    override val values = listOf(1, 2).asSequence()
}

@PreviewParameter(TypeProvider::class)
@Composable
fun CreateSite(type: Int) {
    val stateHomeName: String
    val stateRooms: String
    val stateGeoName: String
    val statelat: String
    val statelon: String
    when (type) {
        1 -> {
            stateHomeName = "My Home"
            stateRooms = "bedroom,living room,kitchen"
            stateGeoName = "My Home"
            statelat = "22"
            statelon = "113"
        }
        else -> {
            stateHomeName = Global.currentSite?.name ?: ""
            stateRooms = Global.currentSite?.rooms?.let { list ->
                val sb = StringBuilder()
                list.forEach {
                    sb.append(it.name)
                    sb.append(",")
                }
                if (sb.isNotEmpty()) sb.deleteCharAt(sb.length - 1)
                sb.toString()
            } ?: ""
            stateGeoName = Global.currentSite?.geoName ?: ""
            statelat = Global.currentSite?.lat.toString()
            statelon = Global.currentSite?.lon.toString()
        }
    }
    var homeName by rememberSaveable { mutableStateOf(stateHomeName) }
    var rooms by rememberSaveable { mutableStateOf(stateRooms) }
    var geoName by rememberSaveable { mutableStateOf(stateGeoName) }
    var lat by rememberSaveable { mutableStateOf(statelat) }
    var lon by rememberSaveable { mutableStateOf(statelon) }
    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(color = Color(242, 242, 242))
        .padding(start = 10.dp, end = 10.dp)
    ) {
        OutlinedTextField(
            value = homeName,
            onValueChange = { homeName = it },
            label = { Text("HomeName") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            singleLine = true
        )
        OutlinedTextField(
            value = rooms,
            onValueChange = { rooms = it },
            label = { Text("Rooms, split with ,") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            singleLine = true
        )
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)) {
            OutlinedTextField(
                value = lat,
                onValueChange = { lat = it },
                label = { Text("lat(Double)") },
                modifier = Modifier
                    .weight(1.0f, false)
                    .padding(end = 10.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = lon,
                onValueChange = { lon = it },
                label = { Text("lon(Double)") },
                modifier = Modifier
                    .weight(1.0f, false)
                    .padding(start = 10.dp),
                singleLine = true
            )
        }
        val split = rooms.split(",")
        Button(modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .height(50.dp),
            onClick = {
                if (type == 1) {
                    TuyaSmartResidenceSdk.siteManager()
                        .createSite(homeName, "", lat = lat.toDouble(), lon = lon.toDouble(), split, object :
                            Business
                            .ResultListener<HomeResponseBean?> {
                            override fun onFailure(p0: BusinessResponse?, p1: HomeResponseBean?, p2: String?) {
                                L.d(
                                    TAG, "onFailure BusinessResponse = $p0, HomeResponseBean = $p1, " +
                                            "String = $p2")
                            }

                            override fun onSuccess(p0: BusinessResponse?, p1: HomeResponseBean?, p2: String?) {
                                L.d(TAG, "onSuccess BusinessResponse = $p0, HomeResponseBean = $p1, " +
                                        "String = $p2")

                            }

                        })
                } else {
                    TuyaSmartResidenceSdk.siteManager()
                        .updateSiteInfo(Global.currentSite?.homeId ?: return@Button, homeName, lon.toDouble(), lat
                            .toDouble(), geoName, split, true, object : Business
                        .ResultListener<Boolean?> {
                            override fun onFailure(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                                L.d(
                                    TAG, "onFailure BusinessResponse = $p0, HomeResponseBean = $p1, " +
                                            "String = $p2")
                            }

                            override fun onSuccess(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                                L.d(TAG, "onSuccess BusinessResponse = $p0, HomeResponseBean = $p1, " +
                                        "String = $p2")

                            }

                        })
                }
            }) {
            Text(text = if (type == 1) "Create" else "Modify")
        }
    }
}

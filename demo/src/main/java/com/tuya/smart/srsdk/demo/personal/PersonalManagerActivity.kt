package com.tuya.smart.srsdk.demo.personal

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.tuya.smart.android.common.utils.L
import com.tuya.smart.android.network.Business
import com.tuya.smart.android.network.http.BusinessResponse
import com.tuya.smart.srsdk.R
import com.tuya.smart.srsdk.api.account.bean.TimezoneeBean
import com.tuya.smart.srsdk.demo.ArrowImage
import com.tuya.smart.srsdk.demo.Global
import com.tuya.smart.srsdk.demo.RowSpaceLine
import com.tuya.smart.srsdk.demo.TAG
import com.tuya.smart.srsdk.demo.account.SigninActivity
import com.tuya.smart.srsdk.sdk.TuyaSmartResidenceSdk

/**
 *
 * @description:
 * @author: mengzi.deng
 * @since: 2021/11/8 09:50
 */
private var timeZoneList: ArrayList<TimezoneeBean?>? = null

class PersonalManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PersonalView()
        }
    }
}

@Preview
@Composable
fun PersonalView() {
    Column(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
    ) {
        PersonalTitle()

        PersonalAvatar()

        PersonalName()

        PersonalTimeZone()

        LogOffAccountView()
    }

}

@Composable
fun PersonalTitle() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .background(color = Color(242, 242, 242)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
        Text("Personal Management", fontSize = 18.sp, color = Color.DarkGray)
    }
}

@Composable
fun PersonalAvatar() {

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

        Text("Avatar", fontSize = 16.sp, modifier = Modifier.weight(1.0F, true))
        Image(painter = rememberImagePainter(data = TuyaSmartResidenceSdk.account().getUserInfo()?.headPic,
            builder = {
                transformations(CircleCropTransformation())
                placeholder(drawableResId = R.drawable.personal_user_icon_default)
                error(drawableResId = R.drawable.personal_user_icon_default)
            }),
            contentDescription = "",
            Modifier.size(30.dp))
        ArrowImage()
    }

    RowSpaceLine()

}

@Composable
fun PersonalName() {
    val context = LocalContext.current
    var nickname: String by remember {
        mutableStateOf(TuyaSmartResidenceSdk.account().getUserInfo()?.nickName ?: "")
    }
    L.d(TAG, "nickname = $nickname")
    var editTextDialog: EditTextDialog
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .height(50.dp)
            .padding(8.dp)
            .clickable(onClick = {
                editTextDialog = EditTextDialog("Modify Nickname", object : EditTextDialog.InputListener {
                    override fun onInputComplete(content: String) {
                        if (TextUtils.isEmpty(content)) {
                            Toast
                                .makeText(context, "nickname cannot be empty", Toast.LENGTH_SHORT)
                                .show()
                            return
                        }

                        TuyaSmartResidenceSdk
                            .account()
                            .updateNickName(content, object : Business.ResultListener<Boolean?> {
                                override fun onFailure(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                                    Toast
                                        .makeText(context, p2, Toast.LENGTH_SHORT)
                                        .show()
                                }

                                override fun onSuccess(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                                    nickname = content
                                }


                            })
                    }

                })
                editTextDialog.show((context as PersonalManagerActivity).supportFragmentManager,
                    "NicknameDialogFragment");
            }),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Text("Nickname", fontSize = 16.sp, modifier = Modifier.weight(1.0F, true))
        Text(nickname, fontSize = 16.sp)
        ArrowImage()
    }

    RowSpaceLine()

}

@Composable
fun PersonalTimeZone() {
    val context = LocalContext.current
    var timeZone: String by remember { mutableStateOf("") }
    val registerForActivityResult = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()
    ) {
        it.data?.run {
            timeZone = getStringExtra("timeZone").toString()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .height(50.dp)
            .padding(8.dp)
            .clickable(onClick = {
                if (timeZoneList == null) {
                    Toast
                        .makeText(context, "Time zone list is empty", Toast.LENGTH_SHORT)
                        .show()
                    return@clickable
                }

                registerForActivityResult.launch(Intent(context, TimeZoneActivity::class.java).putExtra("timeZoneList",
                    timeZoneList))
            }),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Text("Time Zone", fontSize = 16.sp, modifier = Modifier.weight(1.0F, true))
        Text(timeZone, fontSize = 16.sp)
        ArrowImage()
    }
    RowSpaceLine()

    TuyaSmartResidenceSdk.account()
        .requestTimeZone(object : Business.ResultListener<ArrayList<TimezoneeBean?>?> {
            override fun onFailure(p0: BusinessResponse?, p1: ArrayList<TimezoneeBean?>?, p2: String?) {
                L.d(TAG, "fetch time zone list failure")
            }

            override fun onSuccess(p0: BusinessResponse?, p1: ArrayList<TimezoneeBean?>?, p2: String?) {
                timeZoneList = p1
                timeZone = getTimeZone(TuyaSmartResidenceSdk.account().getUserInfo()?.timezoneId ?: "")
            }

        })
}


@Composable
fun LogOffAccountView() {
    val context = LocalContext.current
    Button(onClick = {
        AlertDialog.Builder(context)
            .setMessage("Are you sure to logoff your account?")
            .setTitle("Logoff Account")
            .setPositiveButton("Confirm", DialogInterface.OnClickListener { _, _ ->
                logoffAccount(context)
            })
            .setNeutralButton("Cancel", null)
            .create()
            .show()


    }, modifier =
    Modifier
        .fillMaxWidth()
        .padding(30.dp)) {
        Text(text = "Logoff Account")
    }
}

fun logoffAccount(context: Context) {
    TuyaSmartResidenceSdk.account().logOffAccount(object : Business.ResultListener<Boolean?> {
        override fun onFailure(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
            Toast.makeText(context, "Logoff account failure", Toast.LENGTH_SHORT).show()
        }

        override fun onSuccess(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
            Toast.makeText(context, "Logoff account success", Toast.LENGTH_SHORT).show()
            context.startActivity(Intent(context, SigninActivity::class.java))
            (context as PersonalManagerActivity).finish()
        }
    })
}

fun getTimeZone(timeZoneId: String): String {
    timeZoneList?.let {
        if (it.isEmpty()) {
            return ""
        }
        for (item in it) {
            if (timeZoneId == item?.timezoneId ?: "") {
                return item?.display ?: ""
            }
        }
        return ""
    } ?: return ""
}
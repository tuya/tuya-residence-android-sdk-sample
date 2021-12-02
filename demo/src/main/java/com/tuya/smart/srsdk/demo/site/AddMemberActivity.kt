package com.tuya.smart.srsdk.demo.site

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tuya.sdk.home.bean.InviteMessageBean
import com.tuya.smart.android.common.utils.L
import com.tuya.smart.android.network.Business
import com.tuya.smart.android.network.http.BusinessResponse
import com.tuya.smart.home.sdk.bean.MemberWrapperBean
import com.tuya.smart.srsdk.api.site.bean.MemberBean
import com.tuya.smart.srsdk.api.site.interfaces.IFamilyDataCallback
import com.tuya.smart.srsdk.demo.*
import com.tuya.smart.srsdk.sdk.TuyaSmartResidenceSdk

/**
 * @ClassName AddMemberActivity
 * @description
 * @author SamuraiSong
 * @date 2021/11/12
 */
class AddMemberActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddMember()
        }
    }
}

private val buttonModifier = Modifier
    .fillMaxWidth()
    .padding(top = 20.dp)

@Composable
fun AddMember(viewModel: AddMemberModel = viewModel()) {
    val context = LocalContext.current
    var countryCode by rememberSaveable { mutableStateOf(Global.countryCode) }
    var nickname by rememberSaveable { mutableStateOf("") }
    var account by rememberSaveable { mutableStateOf("") }
    var invitationCode by rememberSaveable { viewModel.invitationCode }
    var invitationId by rememberSaveable { mutableStateOf(0L) }
    Column(modifier = Modifier
        .padding(start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text("Nick Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = account,
            onValueChange = { account = it },
            label = { Text("Account") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        OutlinedTextField(
            value = countryCode,
            onValueChange = { countryCode = it },
            label = { Text("Country Code") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(onClick = { viewModel.addMember(countryCode, nickname, account) },
            modifier = buttonModifier
        ) {
            Text(text = "Add Member")
        }

        RowSpacer()

        OutlinedTextField(
            value = invitationCode,
            onValueChange = { invitationCode = it },
            label = { Text("invitation code") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(onClick = { viewModel.createInvitationCode() },
            modifier = buttonModifier
        ) {
            Text(text = "Create invitation code")
        }
        LazyColumn(content = {
            itemsIndexed(viewModel.invitationRecordList) { _, item ->
                Row(
                    modifier = rowModifier
                        .clickable {
                            invitationId = item.invitationId
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Code = ${item.invitationCode} Id = ${item.invitationId}", fontSize = 16.sp, modifier =
                    Modifier.weight(1.0F, true))
                }
                RowSpaceLine()
            }
        })
        Button(
            onClick = {
                if (invitationId == 0L) {
                    Toast.makeText(context, "invitationCode must not be null", Toast.LENGTH_LONG)
                        .show()
                } else {
                    viewModel.inviteAgain(invitationId)
                }
            },
            modifier = buttonModifier
        ) {
            Text(text = "Invite again")
        }
        Button(
            onClick = {
                if (invitationId == 0L) {
                    Toast.makeText(context, "invitationCode must not be null", Toast.LENGTH_LONG)
                        .show()
                } else {
                    viewModel.deleteInvitationCode(invitationId)
                }
            },
            modifier = buttonModifier
        ) {
            Text(text = "Delete invitation code")
        }

        var joinInvitationCode by rememberSaveable { mutableStateOf("") }
        OutlinedTextField(
            value = joinInvitationCode,
            onValueChange = { joinInvitationCode = it },
            label = { Text("Join a Site") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Button(
            onClick = {
                viewModel.joinSite(joinInvitationCode)
            },
            modifier = buttonModifier
        ) {
            Text(text = "Join a Site")
        }
    }
}

class AddMemberModel : ViewModel() {

    var invitationCode = mutableStateOf("")
    var invitationRecordList = mutableListOf<MemberBean>()

    init {
        fetchInvitationRecordList()
    }

    fun addMember(countryCode: String, nickname: String, account: String) {
        val memberWrapperBean = MemberWrapperBean.Builder()
            .setAccount(account)
            .setCountryCode(countryCode)
            .setNickName(nickname)
            .setHomeId(Global.currentSite?.homeId ?: return)
            .build()
        TuyaSmartResidenceSdk.siteManager().addMember(memberWrapperBean,
            object :
            Business.ResultListener<Long?> {
            override fun onFailure(p0: BusinessResponse?, p1: Long?, p2: String?) {
                L.d(TAG, "addMember: onFailure: ${p0?.errorMsg}")
            }

            override fun onSuccess(p0: BusinessResponse?, p1: Long?, p2: String?) {
                L.d(TAG, "addMember: onSuccess: $p1")
            }

        })
    }

    fun createInvitationCode() {
        TuyaSmartResidenceSdk.siteManager().invitationMember(
            Global.currentSite?.homeId?:return,
            object: Business.ResultListener<InviteMessageBean?> {
            override fun onFailure(p0: BusinessResponse?, p1: InviteMessageBean?, p2: String?) {
                L.d(TAG, "createInvitationCode: onFailure: ${p0?.errorMsg}")
            }

            override fun onSuccess(p0: BusinessResponse?, p1: InviteMessageBean?, p2: String?) {
                L.d(TAG, "createInvitationCode: onSuccess: ${p1?.invitationMsgContent}")
                invitationCode.value = p1?.invitationCode?:""
                fetchInvitationRecordList()
            }

        })
    }

    fun deleteInvitationCode(invitationId: Long) {
        TuyaSmartResidenceSdk.siteManager().cancelInvitation(invitationId, object :
            Business.ResultListener<Boolean?> {
            override fun onFailure(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                L.d(TAG, "deleteInvitationCode: onFailure: ${p0?.errorMsg}")
            }

            override fun onSuccess(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                L.d(TAG, "deleteInvitationCode: onSuccess: $p1")
                fetchInvitationRecordList()
            }

        })
    }

    fun inviteAgain(invitationId: Long) {
        TuyaSmartResidenceSdk.siteManager().inviteAgainMember(invitationId, object :
            Business.ResultListener<InviteMessageBean?> {
            override fun onFailure(p0: BusinessResponse?, p1: InviteMessageBean?, p2: String?) {
                L.d(TAG, "inviteAgain: onFailure: ${p0?.errorMsg}")
            }

            override fun onSuccess(p0: BusinessResponse?, p1: InviteMessageBean?, p2: String?) {
                L.d(TAG, "inviteAgain: onSuccess: ${p1?.invitationMsgContent}")
                invitationCode.value = p1?.invitationCode?:""
                fetchInvitationRecordList()
            }
        })
    }

    fun fetchInvitationRecordList() {
        TuyaSmartResidenceSdk.siteManager().fetchInvitationRecordList(
            Global.currentSite?.homeId?:return, object : IFamilyDataCallback<List<MemberBean>?> {
                override fun onSuccess(result: List<MemberBean>?) {
                    L.d(TAG, "inviteAgain: onSuccess: ${result.toString()}")
                    invitationRecordList.clear()
                    result?.run { invitationRecordList.addAll(this) }
                }

                override fun onError(errorCode: String?, errorMessage: String?) {
                    L.d(TAG, "inviteAgain: onError: errorMessage = $errorMessage")
                }

            })
    }

    fun joinSite(code: String?) {
        TuyaSmartResidenceSdk.siteManager().joinSite(code, object :
            Business.ResultListener<Boolean?> {
            override fun onFailure(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                L.d(TAG, "joinSite: onFailure: ${p0?.errorMsg}")
            }

            override fun onSuccess(p0: BusinessResponse?, p1: Boolean?, p2: String?) {
                L.d(TAG, "joinSite: onSuccess: ${p1?.toString()}")
            }

        })
    }

}

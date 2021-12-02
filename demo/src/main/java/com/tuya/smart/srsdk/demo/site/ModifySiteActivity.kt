package com.tuya.smart.srsdk.demo.site

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity

/**
 * @ClassName ModifySiteActivity
 * @description
 * @author SamuraiSong
 * @date 2021/11/11
 */
class ModifySiteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CreateSite(2)
        }
    }
}
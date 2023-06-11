package com.thing.smart.srsdk.demo.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * @description:
 * @author: mengzi.deng
 * @since: 2021/11/11 14:22
 */

@SuppressLint("SimpleDateFormat")
fun Long.toDateStr(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
    val date = Date(this)
    val format = SimpleDateFormat(pattern)
    return format.format(date)
}
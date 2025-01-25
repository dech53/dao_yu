package com.dech53.dao_yu.utils

import android.content.Context
import android.content.Intent
import com.dech53.dao_yu.CookieManage

object ActivityJump {
    fun SettingsJump(context: Context, index: Int, mainIndex: Int = 0): (() -> Unit) {
        if (mainIndex == 0) {
            if (index == 0) {
                return {
                    val intent = Intent(context, CookieManage::class.java)
                    context.startActivity(intent)
                }
            }
        }
        return {}
    }
}
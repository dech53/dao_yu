package com.dech53.dao_yu.views

import android.content.Context
import android.provider.MediaStore.Audio.Radio
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dech53.dao_yu.static.SettingsItems
import com.dech53.dao_yu.static.settingsItems
import com.dech53.dao_yu.ui.theme.Dao_yuTheme
import com.dech53.dao_yu.utils.ActivityJump

@Composable
fun SettingsView(padding: PaddingValues) {
    val context = LocalContext.current
    Dao_yuTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                settingsItems.forEachIndexed { index, item ->
                    SettingsItemCard(
                        settingsItems = item,
                        mainIndex = index,
                        isLastItem = index == settingsItems.size - 1,
                        context = context
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsItemCard(
    settingsItems: SettingsItems,
    mainIndex: Int,
    isLastItem: Boolean,
    context: Context
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = settingsItems.function,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        settingsItems.settingsItems.forEachIndexed { index, item ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Log.d("设置选项点击", item.title)
                            ActivityJump.SettingsJump(context, index, mainIndex)()
                        }
                        .height(100.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.height(0.dp))
                    Row(

                    ){
                        Text(
                            text = item.title,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                    }

                    if (!isLastItem || item != settingsItems.settingsItems.last()) {
                        HorizontalDivider(
                            thickness = 2.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        )
                    }
                }
            }
        }
    }
}
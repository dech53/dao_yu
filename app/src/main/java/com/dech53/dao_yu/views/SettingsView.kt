package com.dech53.dao_yu.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dech53.dao_yu.R
import com.dech53.dao_yu.component.SettingsItem
import com.dech53.dao_yu.ui.theme.Dao_yuTheme

@Composable
fun SettingsView(padding: PaddingValues) {
    Dao_yuTheme {
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(color = Color.White)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_forward),
                contentDescription = "Developer Image",
                modifier = Modifier
                    .clip(
                        CircleShape
                    )
                    .align(Alignment.TopCenter)
                    .width(100.dp)
                    .height(100.dp)
            )
            Column {
                SettingsItem(
                    ImageId = R.drawable.icons8_github_100,
                    IconId = R.drawable.baseline_arrow_forward_24,
                    modifier = Modifier
                )
            }
        }
    }
}
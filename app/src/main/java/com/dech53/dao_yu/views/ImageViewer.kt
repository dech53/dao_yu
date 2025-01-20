package com.dech53.dao_yu.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import coil3.compose.AsyncImage
import com.dech53.dao_yu.static.Url
import com.dech53.dao_yu.ui.theme.Dao_yuTheme

@Composable
fun ImageViewer(img_Location: String = "", paddingValues: PaddingValues) {
    Dao_yuTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)

        ) {
            if (img_Location.isEmpty()) {
                Text("加载中")
            } else {
                AsyncImage(
                    model = Url.IMG_FULL_QA + Regex(pattern = "&").replace(img_Location, "/"),
                    contentDescription = img_Location,
                )
            }
        }
    }
}
package com.dech53.dao_yu.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dech53.dao_yu.ui.theme.Dao_yuTheme

@Composable
fun SearchView(padding: PaddingValues) {
    Dao_yuTheme {
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            Text("搜索", modifier = Modifier.align(Alignment.Center))
        }
    }
}
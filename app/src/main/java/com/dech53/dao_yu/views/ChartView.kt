package com.dech53.dao_yu.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ChartView(
    padding: PaddingValues,
){
    Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center){
        Text("统计view")
    }
}
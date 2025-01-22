package com.dech53.dao_yu.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dech53.dao_yu.viewmodels.ThreadInfoView_ViewModel
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.dech53.dao_yu.component.Forum_card
import com.dech53.dao_yu.component.TRCard

@Composable
fun ThreadInfoView(
    threadId: String,
    paddingValues: PaddingValues,
    viewModel: ThreadInfoView_ViewModel,
    navController: NavController
) {
    val threadInfo by viewModel.threadInfo
    val isRefreshing by remember { viewModel.isRefreshing }
    LaunchedEffect(Unit) {
        if (threadInfo == null) {
            viewModel.clearThreadInfo()
            viewModel.getThreadId(threadId)
        } else {
            if (threadId != threadInfo!!.id.toString()) {
                viewModel.clearThreadInfo()
                viewModel.getThreadId(threadId)
            }
        }
    }
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        if (threadInfo == null) {
            Text("加载中")
        } else {
            TRCard(
                item = threadInfo!!,
                navController = navController,
                lazyListState = rememberLazyListState(),
                onRefresh = { viewModel.refreshData() },
                isRefreshing = isRefreshing
            )
        }
    }
}
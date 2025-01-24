package com.dech53.dao_yu.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dech53.dao_yu.viewmodels.ThreadInfoView_ViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.dech53.dao_yu.component.TRCard

@Composable
fun ThreadInfoView(
    threadId: String,
    viewModel: ThreadInfoView_ViewModel,
) {
    val threadInfo by viewModel.threadInfo
    val isRefreshing by remember { viewModel.isRefreshing }
    var onError by remember { viewModel.onError }
    val interactionSource = remember { MutableInteractionSource() }
    //初始化
    LaunchedEffect(Unit) {
        if (threadInfo == null) {
            viewModel.clearThreadInfo()
            viewModel.getThreadId(threadId)
        } else {
            if (threadId != threadInfo!!.first().id.toString()) {
                viewModel.clearThreadInfo()
                viewModel.getThreadId(threadId)
            }
        }
    }
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (onError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        viewModel.refreshData()
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "加载失败，请点击重试",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        viewModel.refreshData()
                    }
                )
            }
        } else if (threadInfo == null) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(40.dp)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            TRCard(
                item = threadInfo!!,
                lazyListState = rememberLazyListState(),
                onRefresh = { viewModel.refreshData() },
                isRefreshing = isRefreshing,
                loadMore = {
                    viewModel.loadMore()
                },
            )
        }
    }
}
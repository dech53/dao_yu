@file:OptIn(ExperimentalMaterial3Api::class)

package com.dech53.dao_yu.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.dech53.dao_yu.models.Thread
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshLazyColumn(
    items: List<Thread>,
    lazyListState: LazyListState = rememberLazyListState(),
    content: @Composable (Thread) -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    contentPadding: PaddingValues,
    loadMore: (onComplete: () -> Unit) -> Unit,
) {
    val pullToRefreshState = rememberPullToRefreshState()
    var isLoadingMore by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .nestedScroll(pullToRefreshState.nestedScrollConnection)
            .padding(contentPadding)
            .background(Color.White)
    ) {
        Column {
            LazyColumn(
                state = lazyListState,
            ) {
                itemsIndexed(
                    items = items,
                    key = { index, item -> "${index}" }) { index, item ->
                    content(item)
                    var isLoading by remember { mutableStateOf(false) }
                    LaunchedEffect(lazyListState.layoutInfo.totalItemsCount) {
                        if (!isLoading && index == lazyListState.layoutInfo.totalItemsCount - 1) {
                            isLoading = true
                            isLoadingMore = true
                            loadMore{
                                isLoadingMore = false
                            }
                        }
                    }
                }
                if (isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
        if (pullToRefreshState.isRefreshing) {
            LaunchedEffect(true) {
                onRefresh()
            }
        }

        LaunchedEffect(isRefreshing) {
            if (isRefreshing) {
                pullToRefreshState.startRefresh()
            } else {
                pullToRefreshState.endRefresh()
            }
        }

        PullToRefreshContainer(
            state = pullToRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
@file:OptIn(ExperimentalMaterial3Api::class)

package com.dech53.dao_yu.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.dech53.dao_yu.models.Thread
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
    val loadMoreState = remember { mutableStateOf(false) }
    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = layoutInfo.totalItemsCount
            lastVisibleItem?.index == totalItems - 1 && !loadMoreState.value
        }
    }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .distinctUntilChanged()
            .collect {
                if (it) {
                    loadMoreState.value = true
                    delay(200)
                    loadMore {
                        loadMoreState.value = false
                    }
                }
            }
    }

    Box(
        modifier = Modifier
            .nestedScroll(pullToRefreshState.nestedScrollConnection)
            .padding(contentPadding)
    ) {
        CompositionLocalProvider(
            LocalOverscrollConfiguration provides null
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(
                    items = items,
                    key = { index, item -> "${item.id}${index}" }
                ) { index, item ->
                    key("${item.id}${index}") {

                        content(item)
                    }
                }
                if (loadMoreState.value) {
                    item {
                        ShimmerList(
                            isLoading = loadMoreState.value,
                            contentAfterLoading = {},
                            modifier = Modifier
                                .fillMaxSize(),
                            skeletonContent = {
                                SkeletonCard()
                            },
                            amount = 1
                        )
                    }
                }
            }
        }
        if (pullToRefreshState.isRefreshing) {
            LaunchedEffect(true) {
                delay(100)
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
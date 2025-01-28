@file:OptIn(ExperimentalMaterial3Api::class)

package com.dech53.dao_yu

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import com.dech53.dao_yu.component.HtmlTRText
import com.dech53.dao_yu.component.TRCard
import com.dech53.dao_yu.static.Url
import com.dech53.dao_yu.ui.theme.Dao_yuTheme
import com.dech53.dao_yu.viewmodels.ThreadInfoView_ViewModel
import com.dech53.dao_yu.views.ThreadInfoView

class ThreadAndReplyView : ComponentActivity() {
    private val viewModel: ThreadInfoView_ViewModel by viewModels()

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val threadId = intent.getStringExtra("threadId")
        val hash = intent.getStringExtra("hash")

        viewModel.hash.value = hash!!
        setContent {
            Dao_yuTheme {
                val lazyListState = rememberLazyListState()
                val context = LocalContext.current
                Scaffold(
                    topBar = {
                        TopAppBar(
                            modifier = Modifier.shadow(elevation = 10.dp),
                            title = {
                                Text(text = "No." + threadId)
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        onBackPressedDispatcher.onBackPressed()
                                    }
                                ) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(
                                            id = R.drawable.baseline_arrow_back_24
                                        ),
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        contentAlignment = Alignment.TopCenter,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)

                    ) {
                        val threadInfo by viewModel.threadInfo
                        val isRefreshing by remember { viewModel.isRefreshing }
                        var onError by remember { viewModel.onError }
                        val interactionSource = remember { MutableInteractionSource() }
                        //初始化
                        LaunchedEffect(Unit) {
                            if (threadInfo == null) {
                                viewModel.clearThreadInfo()
                                viewModel.getThreadId(threadId!!)
                            } else {
                                if (threadId != threadInfo!!.first().id.toString()) {
                                    viewModel.clearThreadInfo()
                                    viewModel.getThreadId(threadId!!)
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
//                                TRCard(
//                                    item = threadInfo!!,
//                                    lazyListState = rememberLazyListState(),
//                                    loadMore = {
//                                        viewModel.loadMore()
//                                    },
//                                    viewModel
//                                )
                                val poster = threadInfo!![0].user_hash
                                val pullToRefreshState = rememberPullToRefreshState()
                                Box(
                                    contentAlignment = Alignment.TopCenter,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .nestedScroll(pullToRefreshState.nestedScrollConnection)

                                ) {
                                    LazyColumn(
                                        state = lazyListState,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        itemsIndexed(threadInfo!!) { index, reply ->
                                            val interactionSource =
                                                remember { MutableInteractionSource() }
                                            val date_ = Regex(pattern = "-").replace(
                                                Regex(pattern = "[^\\(]*|(?<=\\))[^\\)]*").find(
                                                    reply.now
                                                )!!.value,
                                                "/"
                                            )
                                            val context = LocalContext.current
                                            val imageLoader = remember {
                                                ImageLoader.Builder(context)
                                                    .components {
                                                        if (SDK_INT >= 28) {
                                                            add(AnimatedImageDecoder.Factory())
                                                        } else {
                                                            add(GifDecoder.Factory())
                                                        }
                                                    }
                                                    .build()
                                            }
                                            if (reply.id != 9999999) {
                                                Surface(
                                                    shape = MaterialTheme.shapes.small,
                                                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                                                    shadowElevation = 2.dp,
                                                    border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
                                                    modifier = Modifier
                                                        .padding(horizontal = 13.dp, vertical = 8.dp)
                                                        .fillMaxWidth()
                                                        .combinedClickable(
                                                            onClick = {},
                                                            onLongClick = {
                                                                Log.d(
                                                                    "TR卡片长按",
                                                                    "触发${reply.id}"
                                                                )
                                                            }
                                                        )
                                                ) {
                                                    Column(modifier = Modifier.padding(5.dp)) {
                                                        Row(
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            modifier = Modifier.fillMaxWidth()
                                                        ) {
                                                            Row {
                                                                if ((poster == reply.user_hash))
                                                                    Text(
                                                                        "Po",
                                                                        fontWeight = FontWeight.W500,
                                                                        fontSize = 11.sp,
                                                                        color = Color.Red,
                                                                    )
                                                                Spacer(modifier = Modifier.padding(3.dp))
                                                                Text(
                                                                    text = date_,
                                                                    fontWeight = FontWeight.W500,
                                                                    fontSize = 11.sp
                                                                )
                                                                Spacer(modifier = Modifier.padding(3.dp))
                                                                Text(
                                                                    text = reply.user_hash,
                                                                    fontWeight = FontWeight.Bold,
                                                                    fontSize = 11.sp
                                                                )
                                                            }
                                                            Text(
                                                                text = "No.${reply.id}",
                                                                fontWeight = FontWeight.W500,
                                                                fontSize = 11.sp
                                                            )
                                                        }
                                                        HtmlTRText(
                                                            htmlContent = reply.content,
                                                            maxLines = Int.MAX_VALUE,
                                                            viewModel = viewModel,
                                                            context = context,
                                                            posterName = poster
                                                        )
                                                        if (reply.img != "") {
                                                            AsyncImage(
                                                                imageLoader = imageLoader,
                                                                model = Url.IMG_THUMB_QA + reply.img + reply.ext,
                                                                contentDescription = "img from usr ${reply.user_hash}",
                                                                modifier = Modifier
                                                                    .clickable(
                                                                        indication = null,
                                                                        interactionSource = interactionSource
                                                                    ) {
                                                                        //zoom in the photo
                                                                        val intent = Intent(
                                                                            context,
                                                                            ImageViewer::class.java
                                                                        )
                                                                        intent.putExtra(
                                                                            "imgName",
                                                                            reply.img + reply.ext
                                                                        )
                                                                        context.startActivity(intent)
                                                                    }
                                                                    .clip(MaterialTheme.shapes.small),
                                                                placeholder = painterResource(id = R.drawable.apple_touch_icon)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            //load more data when scroll to the bottom
                                            LaunchedEffect(Unit) {
                                                if (index == lazyListState.layoutInfo.totalItemsCount - 1) {
                                                    viewModel.loadMore()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
package com.dech53.dao_yu.component

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.dech53.dao_yu.models.Reply
import com.dech53.dao_yu.models.Thread
import com.dech53.dao_yu.models.toReply
import com.dech53.dao_yu.static.Url
import com.dech53.dao_yu.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TRCard(
    item: List<Reply>,
    navController: NavController,
    lazyListState: LazyListState = rememberLazyListState(),
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    loadMore: () -> Unit,
    isIndicatorVisible: Boolean
) {
    val poster = item[0].user_hash
    val pullToRefreshState = rememberPullToRefreshState()
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(pullToRefreshState.nestedScrollConnection)
    ) {
        LazyColumn(state = lazyListState) {
            itemsIndexed(item) { index, reply ->
                ReplyCard(reply, poster, imgClickAction = {
                    navController.navigate(
                        "图片浏览/${
                            Regex(pattern = "/").replace(
                                reply.img!!,
                                "&"
                            ) + reply.ext
                        }"
                    )
                })
                //load more data when scroll to the bottom
                LaunchedEffect(Unit) {
                    if (index == lazyListState.layoutInfo.totalItemsCount - 1) {
                        loadMore()
                    }
                }
            }
            //圆形进度条
//            item {
//                if (isIndicatorVisible) {
//                    CircularProgressIndicator(
//                        modifier = Modifier.width(10.dp),
//                        color = MaterialTheme.colorScheme.primary,
//                        trackColor = MaterialTheme.colorScheme.surfaceVariant
//                    )
//                }
//            }
        }
        if (pullToRefreshState.isRefreshing) {
            LaunchedEffect(true) {
                onRefresh()
            }
        }
        //same logistic
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

@Composable
fun ReplyCard(reply: Reply, posterName: String, imgClickAction: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val date_ = Regex(pattern = "-").replace(
        Regex(pattern = "[^\\(]*|(?<=\\))[^\\)]*").find(reply.now)!!.value,
        "/"
    )
    if (reply.id != 9999999) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            shadowElevation = 4.dp,
            modifier = Modifier
                .padding(all = 5.dp)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(5.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row {
                        if ((posterName == reply.user_hash))
                            Text(
                                "Poster",
                                fontWeight = FontWeight.W500,
                                fontSize = 11.sp,
                                color = Color.Red
                            )
                        Spacer(modifier = Modifier.padding(3.dp))
                        Text(text = date_, fontWeight = FontWeight.W500, fontSize = 11.sp)
                        Spacer(modifier = Modifier.padding(3.dp))
                        Text(text = reply.user_hash, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Text(text = "No.${reply.id}", fontWeight = FontWeight.W500, fontSize = 11.sp)
                }
                HtmlText(htmlContent = reply.content, maxLines = Int.MAX_VALUE)
                if (reply.img != "") {
                    AsyncImage(
                        model = Url.IMG_THUMB_QA + reply.img + reply.ext,
                        contentDescription = "img from usr ${reply.user_hash}",
                        modifier = Modifier
                            .clickable(
                                indication = null,
                                interactionSource = interactionSource
                            ) {
                                //zoom in the photo
                                imgClickAction()
                            }
                            .clip(MaterialTheme.shapes.small),
                        placeholder = painterResource(id = R.drawable.apple_touch_icon)
                    )
                }
            }
        }
    }
}
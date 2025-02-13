package com.dech53.dao_yu.component

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dech53.dao_yu.models.Thread
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.DpOffset
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.disk.DiskCache
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.dech53.dao_yu.R
import com.dech53.dao_yu.static.Url
import com.dech53.dao_yu.static.dropDownItemsList
import com.dech53.dao_yu.static.forumMap

@Composable
fun Forum_card(
    thread: Thread,
    imgClickAction: () -> Unit,
    cardClickAction: () -> Unit,
    cardLongClickAction: (String) -> Unit,
    stricted: Boolean,
    mainForumId: String,
    forumId: String,
    forumIdClickAction: () -> Unit,
    forumCategoryId: String
) {
    val dateRegex = Regex(pattern = "[^(]*|(?<=\\))[^)]*")
    val replace_ = Regex(pattern = "-")
    val calculatedDate by remember(thread.now) {
        mutableStateOf(
            replace_.replace(dateRegex.find(thread.now)!!.value, "/")
        )
    }
    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context,0.25)
                    .build()
            }
            .crossfade(true)
            .build()
    }
    val imageModifier = Modifier
        .size(100.dp)
        .clip(RoundedCornerShape(4.dp))
        .clickable(
            indication = rememberRipple(bounded = true),
            interactionSource = remember { MutableInteractionSource() }
        ) { imgClickAction() }
    var isContextVisible by rememberSaveable { mutableStateOf(false) }
    var pressOffset by remember { mutableStateOf(DpOffset.Zero) }
    var itemHeight by remember { mutableStateOf(0.dp) }
    var itemWidth by remember { mutableStateOf(0.dp) }

    Card(
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier
            .padding(horizontal = 13.dp, vertical = 8.dp)
            .fillMaxWidth()
            .pointerInput(true) {
                detectTapGestures(
                    onTap = {
                        cardClickAction()
                    },
                    onLongPress = {
                        itemWidth = it.x.toDp()
                        itemHeight = it.y.toDp()
                        isContextVisible = true
                    }
                )
            }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = calculatedDate,
                        fontWeight = FontWeight.W500,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = thread.user_hash,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (stricted) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_message_24),
                            contentDescription = "messageIcon",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = thread.ReplyCount.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            if (thread.sage == 1) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "SAGE",
                    color = Color.Red,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (thread.title != "无标题") {
                Text(
                    text = thread.title,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
            if (thread.name != "无名氏") {
                Text(
                    text = thread.name,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            CommonHtmlText(
                htmlContent = thread.content,
                maxLines = if (stricted) 6 else Int.MAX_VALUE
            )

            if (thread.img != "") {
                Spacer(modifier = Modifier.height(10.dp))
                val imageRequest = rememberUpdatedState(
                    Url.IMG_THUMB_QA + thread.img + thread.ext
                )
                AsyncImage(
                    imageLoader = imageLoader,
                    model = ImageRequest.Builder(context)
                        .data(imageRequest.value)
                        .crossfade(true)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = "img from usr ${thread.user_hash}",
                    modifier = imageModifier,
                    placeholder = painterResource(id = R.drawable.apple_touch_icon),
                    contentScale = ContentScale.Crop
                )
            }
            if ((mainForumId in listOf("1", "2", "3")) && forumCategoryId == "999") {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clickable {
                            forumIdClickAction()
                        }
                ) {
                    Text(
                        text = forumMap[forumId] ?: "未知版号",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        DropdownMenu(
            expanded = isContextVisible,
            onDismissRequest = { isContextVisible = false },
            offset = pressOffset.copy(
                x = itemWidth,
                y = pressOffset.y - itemHeight,
            )
        ) {
            dropDownItemsList.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        cardLongClickAction(item)
                        isContextVisible = false
                    }
                )
            }
        }
    }
}
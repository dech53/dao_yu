package com.dech53.dao_yu.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.DpOffset
import coil3.ImageLoader
import coil3.compose.SubcomposeAsyncImage
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.toBitmap
import com.dech53.dao_yu.R
import com.dech53.dao_yu.component.button.CircleButton
import com.dech53.dao_yu.models.preLoadImage
import com.dech53.dao_yu.static.Url
import com.dech53.dao_yu.static.dropDownItemsList
import com.dech53.dao_yu.static.forumMap
import com.dech53.dao_yu.ui.theme.shimmerEffect
import com.dech53.dao_yu.viewmodels.MainPage_ViewModel

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
    forumCategoryId: String,
    isFavored: Boolean,
    favClickAction: (Boolean) -> Unit,
    viewModel: MainPage_ViewModel
) {
    var isFaved = remember { mutableStateOf(isFavored) }

    val dateRegex = Regex(pattern = "[^(]*|(?<=\\))[^)]*")
    val replace_ = Regex(pattern = "-")
    val calculatedDate by remember(thread.now) {
        mutableStateOf(
            replace_.replace(dateRegex.find(thread.now)!!.value, "/")
        )
    }

    val time_ by remember(thread.now) {
        mutableStateOf(
            Regex("""\d{2}:\d{2}:\d{2}$""").find(thread.now)!!.value,
        )
    }

    val density = LocalDensity.current
    val key = Url.IMG_THUMB_QA + thread.img + thread.ext
    val imageInfo = viewModel.imgList[key]
    var imageWidth = imageInfo?.width ?: 200
    var imageHeight = imageInfo?.height ?: 200

    val context = LocalContext.current
    val request = remember {
        ImageRequest.Builder(context)
            .data(Url.IMG_THUMB_QA + thread.img + thread.ext)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build()
    }
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.2)
                    .build()
            }
            .crossfade(true)
            .build()
    }

    LaunchedEffect(Unit) {
        if (thread.img != "") {
            if (!viewModel.imgList.containsKey(Url.IMG_THUMB_QA + thread.img + thread.ext)) {
                val bitmap = imageLoader.execute(request).image!!.toBitmap()
                imageWidth = bitmap.width
                imageHeight = bitmap.height
                viewModel.imgList[Url.IMG_THUMB_QA + thread.img + thread.ext] =
                    preLoadImage(height = bitmap.height, width = bitmap.width)
            }
        }
        Log.d("TRA", "wh fetched")
    }

    var isContextVisible by rememberSaveable { mutableStateOf(false) }
    var itemHeight by remember { mutableStateOf(0.dp) }
    var itemWidth by remember { mutableStateOf(0.dp) }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .padding(horizontal = 13.dp, vertical = 8.dp)
            .fillMaxWidth()
            .onGloballyPositioned { layoutCoordinates ->
                itemHeight = with(density) { layoutCoordinates.size.height.toDp() }
            }
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
                Text(
                    text = thread.user_hash,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (thread.user_hash == "Admin") Color.Red else MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "No.${thread.id}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = calculatedDate,
                        fontWeight = FontWeight.W500,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = time_,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
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
                maxLines = if (stricted) 10 else Int.MAX_VALUE
            )

            if (thread.img != "") {
                Spacer(modifier = Modifier.height(10.dp))
                SubcomposeAsyncImage(
                    imageLoader = imageLoader,
                    model = request,
                    contentDescription = null,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .height(imageHeight.dp)
                                .shimmerEffect()
                        ) {
                        }
                    },
                    modifier = Modifier
                        .size(width = imageWidth.dp, height = imageHeight.dp)
                        .clickable {
                            imgClickAction()
                        },
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.CenterStart
                )
            }
            if ((mainForumId in listOf("1", "2", "3")) && forumCategoryId == "999") {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(6.dp)
                        )
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
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row {
                    CircleButton(
                        size = 40.dp,
                        onClick = {
                            isContextVisible = true
                        },
                        backGroundColor = MaterialTheme.colorScheme.primary.copy(0.3f),
                        imageVector = ImageVector.vectorResource(R.drawable.baseline_more_vert_24)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    CircleButton(
                        size = 40.dp,
                        onClick = {
                            favClickAction(isFaved.value)
                            isFaved.value = !isFaved.value
                        },
                        backGroundColor = MaterialTheme.colorScheme.primary.copy(0.3f),
                        imageVector = ImageVector.vectorResource(if (isFaved.value) R.drawable.baseline_favorite_24 else R.drawable.outline_favorite_border_24)
                    )
                }
                Row {
                    BadgedBox(
                        badge = {
                            if (thread.ReplyCount > 0) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    contentColor = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Text(text = thread.ReplyCount.toString())
                                }
                            }
                        },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(0.0f))
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(0.3f))
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_message_24),
                                contentDescription = "message count"
                            )
                        }
                    }
                }
            }
        }
        DropdownMenu(
            expanded = isContextVisible,
            onDismissRequest = { isContextVisible = false },
            offset = DpOffset(
                x = itemWidth,
                y = itemHeight
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
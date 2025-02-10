package com.dech53.dao_yu.views

import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.dech53.dao_yu.ImageViewer
import com.dech53.dao_yu.ThreadAndReplyView
import com.dech53.dao_yu.component.CommonHtmlText
import com.dech53.dao_yu.dao.FavoriteDao
import com.dech53.dao_yu.static.Url
import com.dech53.dao_yu.ui.theme.Dao_yuTheme
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dech53.dao_yu.component.ActionIcon
import com.dech53.dao_yu.viewmodels.MainPage_ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun FavView(padding: PaddingValues, hash: String, viewModel: MainPage_ViewModel) {
    val data = viewModel.favData.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var visible = remember { mutableStateOf(false) }
    Dao_yuTheme {

        LaunchedEffect(Unit) {
            scope.launch {
                delay(100)
                visible.value = true
            }
        }
        AnimatedVisibility(
            visible = visible.value,
            enter = fadeIn() + slideInHorizontally(),
            exit = fadeOut() + slideOutHorizontally()
        ) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                if (data.value.isEmpty()) {
                    Text(
                        text = "收藏夹为空",
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(data.value, key = { item ->
                            item.id
                        }) { item ->
                            var contextMenuWidth by remember {
                                mutableFloatStateOf(0f)
                            }
                            val offset = remember {
                                Animatable(initialValue = 0f)
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min)
                                    .padding(8.dp).animateItem(),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                Row(
                                    modifier = Modifier
                                        .onSizeChanged {
                                            Log.d("宽度", "${it.width.toFloat()}")
                                            contextMenuWidth = it.width.toFloat()
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ActionIcon(
                                        onClick = {
                                            scope.launch {
                                                offset.animateTo(0f)
                                            }
                                            val sendIntent: Intent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(
                                                    Intent.EXTRA_TEXT,
                                                    Url.Thread_Main_URL + item.id
                                                )
                                                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                                type = "text/plain"
                                            }
                                            val shareIntent = Intent.createChooser(sendIntent, null)
                                            context.startActivity(shareIntent)
                                        },
                                        backgroundColor = Color.Blue,
                                        icon = Icons.Default.Share,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape),
                                        contentDescription = "分享"
                                    )
                                    ActionIcon(
                                        onClick = {
                                            scope.launch {
                                                offset.animateTo(0f)
                                                viewModel.deleteFav(item)
                                            }
                                        },
                                        backgroundColor = Color.Red,
                                        icon = Icons.Default.Delete,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape),
                                        contentDescription = "删除"
                                    )
                                }
                                Card(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .offset { IntOffset(offset.value.roundToInt(), 0) }
                                        .clip(MaterialTheme.shapes.small)
                                        //检测拖拽行为
                                        .pointerInput(true) {
                                            detectHorizontalDragGestures(
                                                onHorizontalDrag = { _, dragAmount ->
                                                    scope.launch {
                                                        val newOffset = (offset.value + dragAmount)
                                                            .coerceIn(0f, contextMenuWidth)
                                                        offset.snapTo(newOffset)
                                                    }
                                                },
                                                onDragEnd = {
                                                    when {
                                                        offset.value >= contextMenuWidth / 2f -> {
                                                            scope.launch {
                                                                offset.animateTo(contextMenuWidth)
                                                            }
                                                        }

                                                        else -> {
                                                            scope.launch {
                                                                offset.animateTo(0f)
                                                            }
                                                        }
                                                    }
                                                }
                                            )
                                        },
                                    shape = MaterialTheme.shapes.small,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    onClick = {
                                        val intent = Intent(context, ThreadAndReplyView::class.java)
                                        intent.putExtra("threadId", item.id.toString())
                                        intent.putExtra("hash", hash)
                                        intent.putExtra("hasId", true)
                                        context.startActivity(intent)
                                    }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = "No.${item.id}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        CommonHtmlText(
                                            htmlContent = item.content,
                                            maxLines = 6
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        if (item.img != "") {
                                            AsyncImage(
                                                model = Url.IMG_THUMB_QA + item.img,
                                                contentDescription = "img${item.id}",
                                                modifier = Modifier
                                                    .width(100.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .clickable {
                                                        val intent =
                                                            Intent(context, ImageViewer::class.java)
                                                        intent.putExtra("imgName", item.img)
                                                        context.startActivity(intent)
                                                    },
                                                contentScale = ContentScale.Crop
                                            )
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
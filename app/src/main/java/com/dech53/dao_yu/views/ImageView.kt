package com.dech53.dao_yu.views

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil3.ImageLoader
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.dech53.dao_yu.R
import com.dech53.dao_yu.static.Url
import com.dech53.dao_yu.ui.theme.ColumnCapsuleShape
import com.dech53.dao_yu.ui.theme.capsuleShape
import com.dech53.dao_yu.utils.DownloadImageFromUrl
import com.dech53.dao_yu.viewmodels.ThreadInfoView_ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ImageView(
    imgName: String,
    replyId: Int,
    quitClick: () -> Unit,
    imageLoader: ImageLoader = Injekt.get(),
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope
) {
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val clipBoardManager = LocalClipboardManager.current
    val isExpanded = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded.value) -45f else 0f,
        animationSpec = tween(durationMillis = 300)
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val state = rememberTransformableState { zoomChange, panChange, rotationChange ->
                scale = (scale * zoomChange).coerceIn(1f, 5f)
                val extraWidth = (scale - 1) * constraints.maxWidth
                val extraHeight = (scale - 1) * constraints.maxHeight

                rotation += rotationChange

                val maxX = extraWidth / 2
                val maxY = extraHeight / 2

                offset = Offset(
                    x = (offset.x + panChange.x * scale).coerceIn(-maxX, maxX),
                    y = (offset.y + panChange.y * scale).coerceIn(-maxY, maxY)
                )
            }
            with(sharedTransitionScope) {
                SubcomposeAsyncImage(
                    imageLoader = imageLoader,
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(Url.IMG_FULL_QA + imgName)
                        .placeholderMemoryCacheKey(key = "${replyId}image/${imgName}false")
                        .memoryCacheKey(key = "${replyId}image/${imgName}true")
                        .build(),
                    contentDescription = imgName,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    isExpanded.value = false
                                }
                            )

                        }
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            rotationZ = rotation
                            translationX = offset.x
                            translationY = offset.y
                        }
                        .transformable(state)
                        .sharedElement(
                            state = rememberSharedContentState(
                                key = "${replyId}image/${imgName}",
                            ),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = BoundsTransform { initialBounds, targetBounds ->
                                tween(durationMillis = 200)
                            }
                        )
                ) {
                    val state by painter.state.collectAsState()
                    when (state) {
                        AsyncImagePainter.State.Empty -> {}
                        is AsyncImagePainter.State.Error -> {
                        }

                        is AsyncImagePainter.State.Loading -> {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Image(
                                    painter = this@SubcomposeAsyncImage.painter,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit,
                                )
                                CircularProgressIndicator(
                                    modifier = Modifier.size(32.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        is AsyncImagePainter.State.Success -> {
                            Image(
                                painter = painter,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 16.dp)
                .padding(14.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            AnimatedVisibility(
                isExpanded.value,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .clip(ColumnCapsuleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(5.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    IconButton(onClick = { quitClick() }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "退出",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = {
                        scope.launch {
                            val filepath = DownloadImageFromUrl.downloadImageFromUrl(
                                context,
                                Regex(pattern = "/").replace(imgName, "&")
                            )
                            withContext(Dispatchers.Main) {
                                if (filepath != null) {
                                    Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.outline_save_24),
                            contentDescription = "保存",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = {
                        scope.launch {
                            val filepath = DownloadImageFromUrl.downloadImageFromUrl(
                                context,
                                Regex(pattern = "/").replace(imgName, "&")
                            )
                            withContext(Dispatchers.Main) {
                                if (filepath != null) {
                                    Log.d("文件路径", filepath)
                                    val file = File(filepath)
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        file
                                    )
                                    val clipData =
                                        ClipData.newUri(context.contentResolver, "Copied", uri)
                                    val clipEntry = ClipEntry(clipData)
                                    clipBoardManager.setClip(clipEntry)
                                }
                            }
                        }
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_content_copy_24),
                            contentDescription = "保存",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                Url.IMG_FULL_QA + imgName
                            )
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "分享",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = {
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "分享",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            FloatingActionButton(
                onClick = {
                    isExpanded.value = !isExpanded.value
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.graphicsLayer {
                    rotationZ = rotationAngle
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_add_24),
                    contentDescription = "expand"
                )
            }
        }
    }
}
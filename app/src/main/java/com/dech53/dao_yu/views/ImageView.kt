package com.dech53.dao_yu.views

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.dech53.dao_yu.R
import com.dech53.dao_yu.static.Url
import com.dech53.dao_yu.utils.DownloadImageFromUrl
import com.dech53.dao_yu.viewmodels.ThreadInfoView_ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ImageView(
    imgName: String,
    replyId: Int,
    quitClick: () -> Unit,
    imageLoader: ImageLoader,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
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
                    .fillMaxWidth()
                    .weight(1f)
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                scope.launch {
                    val filepath = DownloadImageFromUrl.downloadImageFromUrl(
                        context,
                        Regex(pattern = "/").replace(imgName, "&")
                    )
                    withContext(Dispatchers.Main) {
                        if (filepath != null) {
                            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_download_24),
                    contentDescription = "下载",
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
            IconButton(onClick = { quitClick() }) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = "退出",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
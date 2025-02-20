@file:OptIn(ExperimentalFoundationApi::class)

package com.dech53.dao_yu

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import com.dech53.dao_yu.component.PopUpDialog
import com.dech53.dao_yu.component.PopUpDialogItem
import com.dech53.dao_yu.static.Url
import com.dech53.dao_yu.ui.theme.Dao_yuTheme
import com.dech53.dao_yu.utils.DownloadImageFromUrl
import kotlinx.coroutines.launch
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class ImageViewer : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val imgName = intent.getStringExtra("imgName")
        setContent {
            Dao_yuTheme {
                ImageViewer(img_Location = imgName!!, onFinish = {
                    Log.d("获取的图片名字", imgName)
                    onBackPressedDispatcher.onBackPressed()
                })
            }
        }
    }
}

@Composable
fun ImageViewer(img_Location: String = "", onFinish: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var isInfoDialogVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var loadSuccess by remember { mutableStateOf(false) }
    val imageLoader = Injekt.get<ImageLoader>()
    val items = listOf(
        PopUpDialogItem(
            title = "保存",
            color = Color.Black,
            clickAction = {
                scope.launch {
                    val filepath = DownloadImageFromUrl.downloadImageFromUrl(
                        context,
                        Regex(pattern = "/").replace(img_Location, "&")
                    )
                    // a toast fit Composable better with no need to work on the main thread
                    snackbarHostState.showSnackbar(
                        message = if (filepath != null) "图片保存成功" else "图片保存失败",
                        duration = SnackbarDuration.Short,
                    )
                }
                isInfoDialogVisible = false
            }),
        PopUpDialogItem(
            title = "分享",
            color = Color.Black,
            clickAction = {
                //TODO Add share method to transport arguments between screens
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        Url.IMG_FULL_QA + Regex(pattern = "&").replace(img_Location, "/")
                    )
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)
                isInfoDialogVisible = false
            }),
    )
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(interactionSource = interactionSource, indication = null) {
                onFinish()
            }
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(1f)
        )
        if (img_Location == null) {
            Text("图片不存在", color = Color.White)
        } else {
            AsyncImage(
                onLoading = {
                    if (!loadSuccess) {
                        isLoading = true
                        Log.d("onloading", "")
                    }
                },
                onSuccess = {
                    isLoading = false
                    loadSuccess = true
                    Log.d("onSuccess", "")
                },
                onError = {
                    isLoading = false
                },
                model = Url.IMG_FULL_QA + img_Location,
                imageLoader = imageLoader,
                contentDescription = img_Location,
                modifier = Modifier
                    .fillMaxSize()
                    .combinedClickable(
                        indication = null,
                        interactionSource = interactionSource,
                        onClick = {
                            onFinish()
                        },
                        onLongClick = {
                            isInfoDialogVisible = true
                        }
                    )
            )
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(40.dp)
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        if (isInfoDialogVisible) {
            PopUpDialog(boardClickAction = {
                isInfoDialogVisible = false
            }, items = items)
        }
    }
}

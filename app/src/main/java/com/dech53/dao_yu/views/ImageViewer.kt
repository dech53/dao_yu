package com.dech53.dao_yu.views

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.dech53.dao_yu.component.PopUpDialog
import com.dech53.dao_yu.component.PopUpDialogItem
import com.dech53.dao_yu.static.Url
import com.dech53.dao_yu.ui.theme.Dao_yuTheme
import com.dech53.dao_yu.utils.DownloadImageFromUrl
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageViewer(img_Location: String = "", paddingValues: PaddingValues, onBack: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    var isInfoDialogVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val items = listOf(
        PopUpDialogItem(
            title = "保存原图",
            color = Color.Black,
            clickAction = {
                scope.launch {
                    val filepath = DownloadImageFromUrl.downloadImageFromUrl(
                        context,
                        img_Location
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
    Dao_yuTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
                .clickable {
                    onBack()
                }
        ) {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .zIndex(1f)
            )
            if (img_Location.isEmpty()) {
                Text("加载中")
            } else {
                AsyncImage(
                    model = Url.IMG_FULL_QA + Regex(pattern = "&").replace(img_Location, "/"),
                    contentDescription = img_Location,
                    modifier = Modifier.combinedClickable(
                        indication = null,
                        interactionSource = interactionSource,
                        onClick = {
                            onBack()
                        },
                        onLongClick = {
                            isInfoDialogVisible = true
                        },
                    ),

                    )
            }
            if (isInfoDialogVisible) {
                PopUpDialog(boardClickAction = {
                    isInfoDialogVisible = false
                }, items = items)
            }
        }
    }
}
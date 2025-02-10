@file:OptIn(ExperimentalFoundationApi::class)

package com.dech53.dao_yu.component

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import com.dech53.dao_yu.ImageViewer
import com.dech53.dao_yu.R
import com.dech53.dao_yu.models.Reply
import com.dech53.dao_yu.static.Url
import com.dech53.dao_yu.viewmodels.ThreadInfoView_ViewModel

@Composable
fun TRCard(
    posterName:String,
    item: Reply,
    lazyListState: LazyListState = rememberLazyListState(),
    loadMore: () -> Unit,
    viewModel: ThreadInfoView_ViewModel
) {
    val date_ = Regex(pattern = "-").replace(
        Regex(pattern = "[^\\(]*|(?<=\\))[^\\)]*").find(
            item.now
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
    val interactionSource =
        remember { MutableInteractionSource() }
    Card(
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier
            .padding(
                horizontal = 13.dp,
                vertical = 8.dp
            )
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                },
                onLongClick = {
                    Log.d(
                        "TR卡片长按",
                        "触发${item.id}"
                    )
                    viewModel.appendToTextField(">>No.${item.id}\n")
                }
            )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    if ((posterName == item.user_hash))
                        Text(
                            "Po",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.Red,
                        )
                    Spacer(
                        modifier = Modifier.padding(
                            3.dp
                        )
                    )
                    Text(
                        text = date_,
                        fontWeight = FontWeight.W500,
                        fontSize = 13.sp
                    )
                    Spacer(
                        modifier = Modifier.padding(
                            3.dp
                        )
                    )
                    Text(
                        text = item.user_hash,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                Text(
                    text = "No.${item.id}",
                    fontWeight = FontWeight.W500,
                    fontSize = 13.sp
                )
            }
            if (item.sage == 1) {
                Text(
                    text = "SAGE",
                    color = Color.Red,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            if (item.title != "无标题") {
                Text(
                    text = item.title,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            if (item.name != "无名氏") {
                Text(
                    text = item.name,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            HtmlTRText(
                htmlContent = item.content,
                maxLines = Int.MAX_VALUE,
                viewModel = viewModel,
                context = context,
                posterName = posterName
            )
            if (item.img != "") {
                AsyncImage(
                    imageLoader = imageLoader,
                    model = Url.IMG_THUMB_QA + item.img + item.ext,
                    contentDescription = "img from usr ${item.user_hash}",
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
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
                                item.img + item.ext
                            )
                            context.startActivity(
                                intent
                            )
                        }
                        .clip(RoundedCornerShape(4.dp)),
                    placeholder = painterResource(id = R.drawable.apple_touch_icon)
                )
            }
        }
    }
}
@file:OptIn(ExperimentalFoundationApi::class)

package com.dech53.dao_yu.component

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.runtime.*

@Composable
fun TRCard(
    posterName: String,
    item: Reply,
    viewModel: ThreadInfoView_ViewModel,
    modifier: Modifier
) {
    Log.d("now属性", item.now)
    val date_ by remember(item.now) {
        mutableStateOf(
            Regex(pattern = "-").replace(
                Regex(pattern = "[^(]*|(?<=\\))[^)]*").find(item.now)!!.value,
                "/"
            )
        )
    }
    val time_ by remember(item.now) {
        mutableStateOf(
            Regex("""\d{2}:\d{2}:\d{2}$""").find(item.now)!!.value,
        )
    }
    Log.d("time正则结果",time_)

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
    val imageModifier = Modifier
        .size(100.dp)
        .clip(RoundedCornerShape(4.dp))
        .clickable(
            indication = rememberRipple(bounded = true),
            interactionSource = remember { MutableInteractionSource() }
        ) {
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
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
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
            .animateContentSize()
    ) {

        Column(modifier = Modifier.padding(11.dp)) {
//            Text(
//                text = time_,
//                fontWeight = FontWeight.W500,
//                fontSize = 11.sp
//            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    if ((posterName == item.user_hash)) {
                        Text(
                            "Po",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.Red,
                        )
                        Spacer(
                            modifier = Modifier.padding(
                                4.dp
                            )
                        )
                    }
                    Text(
                        text = date_,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                    Spacer(
                        modifier = Modifier.padding(
                            3.dp
                        )
                    )
                    Text(
                        text = item.user_hash,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                Row {
                    Text(
                        text = time_,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(
                        modifier = Modifier.padding(
                            5.dp
                        )
                    )
                    Text(
                        text = "No.${item.id}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
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
                posterName = posterName,
            )
            if (item.img != "") {
                AsyncImage(
                    imageLoader = imageLoader,
                    model = Url.IMG_THUMB_QA + item.img + item.ext,
                    contentDescription = "img from usr ${item.user_hash}",
                    modifier = imageModifier,
                    placeholder = painterResource(id = R.drawable.apple_touch_icon)
                )
            }
        }
    }
}
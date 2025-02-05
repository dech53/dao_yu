package com.dech53.dao_yu.component

import android.os.Build.VERSION.SDK_INT
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.core.text.HtmlCompat
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import com.dech53.dao_yu.R
import com.dech53.dao_yu.static.Url
import com.dech53.dao_yu.viewmodels.ThreadInfoView_ViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Forum_card(
    thread: Thread,
    imgClickAction: () -> Unit,
    cardClickAction: () -> Unit,
    cardLongClickAction: () -> Unit,
    stricted: Boolean,
    posterName: String?
) {
    var dateRegex = Regex(pattern = "[^\\(]*|(?<=\\))[^\\)]*")
    var replace_ = Regex(pattern = "-")
    var date_ = replace_.replace(dateRegex.find(thread.now)!!.value, "/")
    var activePhotoUrl by remember { mutableStateOf<String?>(null) }
    val interactionSource = remember { MutableInteractionSource() }
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
            .combinedClickable(
                onClick = { cardClickAction() },
                onLongClick = { cardLongClickAction() }
            )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!stricted && (posterName == thread.name)) {
                        Text(
                            text = "Poster",
                            fontWeight = FontWeight.W500,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = date_,
                        fontWeight = FontWeight.W500,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = thread.user_hash,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
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
                            fontSize = 13.sp
                        )
                    } else {
                        Text(
                            text = "No.${thread.id}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (thread.title != "无标题") {
                Text(
                    text = thread.title,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            CommonHtmlText(
                htmlContent = thread.content,
                maxLines = if (stricted) 6 else Int.MAX_VALUE
            )

            if (thread.img!="") {
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    imageLoader = imageLoader,
                    model = Url.IMG_THUMB_QA + thread.img + thread.ext,
                    contentDescription = "img from usr ${thread.user_hash}",
                    modifier = Modifier
                        .width(100.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                        .clickable(
                            indication = rememberRipple(bounded = true),
                            interactionSource = remember { MutableInteractionSource() }
                        ) { imgClickAction() },
                    placeholder = painterResource(id = R.drawable.apple_touch_icon),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
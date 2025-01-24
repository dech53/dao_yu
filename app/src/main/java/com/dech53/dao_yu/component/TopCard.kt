package com.dech53.dao_yu.component

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import com.dech53.dao_yu.R
import com.dech53.dao_yu.static.Url

@Composable
fun Forum_card(
    thread: Thread,
    imgClickAction: () -> Unit,
    cardClickAction: () -> Unit,
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
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shadowElevation = 4.dp,
        modifier = Modifier
            .padding(all = 5.dp)
            .fillMaxWidth()
            .clickable {
                cardClickAction()
            }
    ) {
        Column(modifier = Modifier.padding(5.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    //format date like "2025-01-16(四)23:25:03" using regex and replace the "-"
                    if (!stricted && (posterName == thread.name)) {
                        Text("Poster", fontWeight = FontWeight.W500, fontSize = 11.sp)
                    }
                    Text(text = date_, fontWeight = FontWeight.W500, fontSize = 11.sp)
                    Spacer(modifier = Modifier.padding(3.dp))
                    //user name hash code
                    Text(text = thread.user_hash, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Row(
                    horizontalArrangement = Arrangement.Absolute.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (stricted) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_message_24),
                            contentDescription = "messageIcon",
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = thread.ReplyCount.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                        )
                    } else {
                        Text(
                            text = "No.${thread.id}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                        )
                    }

                }
            }
            //make the html code show in the card component
            HtmlText(htmlContent = thread.content, maxLines = if (stricted) 6 else Int.MAX_VALUE)
            if (thread.img != "") {
                //TODO Add click action on img
                AsyncImage(
                    imageLoader = imageLoader,
                    model = Url.IMG_THUMB_QA + thread.img + thread.ext,
                    contentDescription = "img from usr ${thread.user_hash}",
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = interactionSource
                        ) {
                            //zoom in the photo
                            imgClickAction()
                        }
                        .clip(MaterialTheme.shapes.small),
                    //placeholder mean phtoto is not completely loaded
                    placeholder = painterResource(id = R.drawable.apple_touch_icon)
                )
                if (activePhotoUrl != null) {

                }
            }
        }
    }
}
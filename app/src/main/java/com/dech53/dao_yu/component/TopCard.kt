package com.dech53.dao_yu.component

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import coil3.compose.AsyncImage
import com.dech53.dao_yu.R

@Composable
fun Top_card(thread: Thread, clickAction: () -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    var dateRegex = Regex(pattern = "[^\\(]*|(?<=\\))[^\\)]*")
    var replace_ = Regex(pattern = "-")
    var date_ = replace_.replace(dateRegex.find(thread.now)!!.value, "/")
    var activePhotoUrl by remember { mutableStateOf<String?>(null) }
    val interactionSource = remember { MutableInteractionSource() }
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shadowElevation = 4.dp,
        modifier = Modifier
            .padding(all = 5.dp)
            .fillMaxWidth()
            .clickable {
                isExpanded = !isExpanded
            }
    ) {
        Column(modifier = Modifier.padding(5.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    //format date like "2025-01-16(四)23:25:03" using regex and replace the "-"
                    Text(text = date_, fontWeight = FontWeight.W500, fontSize = 11.sp)
                    Spacer(modifier = Modifier.padding(3.dp))
                    //user name hash code
                    Text(text = thread.user_hash, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Row(
                    horizontalArrangement = Arrangement.Absolute.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_message_24),
                        contentDescription = "messageIcon",
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = if (thread.RemainReplies == null) "0" else thread.RemainReplies.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                    )
                }

            }
            //make the html code show in the card component
            HtmlText(htmlContent = thread.content, maxLines = 6)
            if (thread.img != "") {
                //TODO Add click action on img
                AsyncImage(
                    model = "https://image.nmb.best/thumb/" + thread.img + thread.ext,
                    contentDescription = "img from usr ${thread.user_hash}",
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = interactionSource
                    ) {
                        //zoom in the photo
                        clickAction()
                    },
                    //placeholder mean phtoto is not completely loaded
                    placeholder = painterResource(id = R.drawable.apple_touch_icon)
                )
                if (activePhotoUrl != null) {

                }
            }
        }
    }
}
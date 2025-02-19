package com.dech53.dao_yu.component

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.dech53.dao_yu.ImageViewer
import com.dech53.dao_yu.ThreadAndReplyView
import com.dech53.dao_yu.models.QuoteRef
import com.dech53.dao_yu.static.Url
import com.dech53.dao_yu.viewmodels.ThreadInfoView_ViewModel

@Composable
fun QuotedComponent(
    quoteRef: QuoteRef,
    viewModel: ThreadInfoView_ViewModel,
    context: Context,
    posterName: String
) {
    var isExpanded = remember { mutableStateOf(false) }
    if (isExpanded.value) {
        Box(
            modifier = Modifier
                .padding(top = 10.dp)

        ) {
            Box(
                modifier = Modifier
                    .padding(all = 5.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(0.08f))

            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .padding(horizontal = 5.dp).fillMaxSize()
                ) {
                    if (quoteRef.user_hash != "") {
                        Row(
                            horizontalArrangement = Arrangement.Start
                        ) {
                            if (quoteRef.user_hash == posterName)
                                Text(
                                    "Po",
                                    fontWeight = FontWeight.W500,
                                    fontSize = 11.sp,
                                    color = Color.Red,
                                )
                            Spacer(modifier = Modifier.padding(3.dp))
                            Text(
                                text = quoteRef.user_hash,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    HtmlTRText(
                        htmlContent = quoteRef.content,
                        viewModel = viewModel,
                        context = context,
                        posterName = posterName
                    )
                    if (quoteRef.img != "") {
                        AsyncImage(
                            model = Url.IMG_THUMB_QA + quoteRef.img + quoteRef.ext,
                            contentDescription = "",
                            modifier = Modifier
                                .width(100.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .clickable(
                                    indication = rememberRipple(bounded = true),
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    val intent = Intent(context, ImageViewer::class.java)
                                    intent.putExtra("imgName", quoteRef.img + quoteRef.ext)
                                    context.startActivity(intent)
                                }
                        )
                    }
                    if (quoteRef.isThread) {
                        TextButton(onClick = {
                            val intent = Intent(context, ThreadAndReplyView::class.java)
                            intent.putExtra("threadId", quoteRef.id.toString())
                            intent.putExtra("hash", viewModel.hash.value)
                            context.startActivity(intent)
                        }) {
                            Text("查看原串")
                        }
                    }
                }
            }
            //floating id
            Text(
                text = ">>No." + quoteRef.id.toString(),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                ),
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .padding(horizontal = 2.dp)
                    .offset(y = (-9).dp)
                    .clickable { isExpanded.value = !isExpanded.value }
            )
        }
    } else {
        Text(
            text = ">>No." + quoteRef.id.toString(),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .clickable {
                    isExpanded.value = !isExpanded.value
                }
        )
    }
}


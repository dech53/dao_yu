package com.dech53.dao_yu.component

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.dech53.dao_yu.ImageViewer
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
                .padding(top = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color(0xFF789922),
                        shape = MaterialTheme.shapes.extraSmall
                    )
                    .padding(all = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp)
                ) {
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

                    HtmlTRText(
                        htmlContent = quoteRef.content,
                        viewModel = viewModel,
                        context = context,
                        posterName = posterName
                    )
                    AsyncImage(
                        model = Url.IMG_THUMB_QA + quoteRef.img + quoteRef.ext,
                        contentDescription = "",
                        modifier = Modifier.clickable {
                            val intent = Intent(context, ImageViewer::class.java)
                            intent.putExtra("imgName", quoteRef.img + quoteRef.ext)
                            context.startActivity(intent)
                        }
                    )
                }
            }

            //floating id
            Text(
                text = ">>No." + quoteRef.id.toString(),
                color = Color(0xFF789922),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF789922),
                    background = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .offset(y = (-9).dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(horizontal = 2.dp)
                    .clickable { isExpanded.value = !isExpanded.value }
            )
        }
    } else {
        Text(
            text = ">>No." + quoteRef.id.toString(),
            fontSize = 14.sp,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF789922),
                background = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            modifier = Modifier.clickable {
                isExpanded.value = !isExpanded.value
            }
        )
    }
}


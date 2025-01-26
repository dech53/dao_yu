package com.dech53.dao_yu.component

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
    isExpanded: Boolean = false,
    context: Context
) {
    var isExpanded = remember { mutableStateOf(isExpanded) }
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
                    Text(
                        text = quoteRef.user_hash,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    HtmlTRText(
                        htmlContent = quoteRef.content,
                        viewModel = viewModel,
                        context = context
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
                text = ">>" + quoteRef.id.toString(),
                color = Color(0xFF789922),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.primary,
                    background = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                modifier = Modifier
                    .padding(start = 12.dp)
                    .offset(y = (-8).dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(horizontal = 2.dp)
                    .clickable { isExpanded.value = !isExpanded.value }
            )
        }
    } else {
        Text(
            text = ">>" + quoteRef.id.toString(),
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.primary,
                background = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            modifier = Modifier.clickable {
                isExpanded.value = !isExpanded.value
            }
        )
    }
}


package com.dech53.dao_yu.views

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.dech53.dao_yu.ImageViewer
import com.dech53.dao_yu.ThreadAndReplyView
import com.dech53.dao_yu.component.CommonHtmlText
import com.dech53.dao_yu.component.HtmlTRText
import com.dech53.dao_yu.dao.FavoriteDao
import com.dech53.dao_yu.static.Url
import com.dech53.dao_yu.ui.theme.Dao_yuTheme
import com.dech53.dao_yu.utils.DataStoreUtil
import kotlinx.coroutines.launch

@Composable
fun FavView(padding: PaddingValues, favDao: FavoriteDao, hash: String) {
    val data = favDao.getAll().collectAsStateWithLifecycle(initialValue = emptyList())
    val context = LocalContext.current
    Dao_yuTheme {
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (data.value.isEmpty()) {
                Text(
                    text = "收藏夹为空",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    data.value.forEach { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clip(MaterialTheme.shapes.small),
                            shape = MaterialTheme.shapes.small,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            onClick = {
                                val intent = Intent(context, ThreadAndReplyView::class.java)
                                intent.putExtra("threadId", item.id.toString())
                                intent.putExtra("hash", hash)
                                context.startActivity(intent)
                            }
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "No.${item.id}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                CommonHtmlText(
                                    htmlContent = item.content,
                                    maxLines = 6
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                if (item.img != "") {
                                    AsyncImage(
                                        model = Url.IMG_THUMB_QA + item.img,
                                        contentDescription = "img${item.id}",
                                        modifier = Modifier
                                            .width(100.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .clickable {
                                                val intent = Intent(context, ImageViewer::class.java)
                                                intent.putExtra("imgName", item.img)
                                                context.startActivity(intent)
                                            },
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
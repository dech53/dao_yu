package com.dech53.dao_yu.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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

@Composable
fun Top_card(thread: Thread) {
    var isExpanded by remember { mutableStateOf(false) }
    Surface(
        shape = MaterialTheme.shapes.extraSmall,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        modifier = Modifier
            .padding(all = 9.dp)
            .fillMaxWidth()
            .clickable {
                isExpanded = !isExpanded
            }
    ) {
        Column(modifier = Modifier.padding(3.dp)) {
            Row {
                Text(text = thread.user_hash, fontWeight = FontWeight.W200, fontSize = 10.sp)
                Spacer(modifier = Modifier.width(5.dp))

            }
            Text(
                text = thread.content,
                fontWeight = FontWeight.W200,
                maxLines = if (isExpanded) Int.MAX_VALUE else 5,
                modifier = Modifier.animateContentSize()
            )
        }
    }
}
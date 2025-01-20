package com.dech53.dao_yu.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class PopUpDialogItem(
    val title: String,
    val color: Color,
    val clickAction: () -> Unit
)

@Composable
fun PopUpDialog(
    items: List<PopUpDialogItem>,
    boardClickAction: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.Black.copy(alpha = 0.7f),
            )
            .clickable(indication = null, interactionSource = interactionSource) {
                boardClickAction()
            }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize(0.3f)
                .width(60.dp)
                .background(Color.White, shape = RoundedCornerShape(8.dp))
        ) {
            Column(
                modifier = Modifier.padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items.forEach { item ->
                    PopUpDialogItem(item)
                }
            }
        }
    }
}

@Composable
fun PopUpDialogItem(item: PopUpDialogItem) {
    Button(
        onClick = {
            item.clickAction()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .padding(vertical = 5.dp)
    ) {
        Text(text = item.title, color = item.color)
    }
}
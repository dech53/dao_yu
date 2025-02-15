package com.dech53.dao_yu.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp

@Composable
fun CircleButton(
    size: Dp,
    onClick: () -> Unit,
    backGroundColor: Color,
    imageVector: ImageVector,
){
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(backGroundColor)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ){
        Icon(
            imageVector = imageVector,
            contentDescription = "moreIcon"
        )
    }
}
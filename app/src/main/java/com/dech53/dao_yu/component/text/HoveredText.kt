package com.dech53.dao_yu.component.text

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit

@Composable
fun HoveredText(
    text: String,
    fontSize: TextUnit,
    maxLines: Int
) {
    val interactionSource = remember { MutableInteractionSource() }
    var hoverState by remember { mutableStateOf(true) }
    val hoverModifier = if(hoverState) Modifier.background(MaterialTheme.colorScheme.primary) else Modifier
    Text(
        text = text.slice(IntRange(3, text.length-5)),
        modifier = hoverModifier.clickable (
            interactionSource = interactionSource,
            indication = null
        ){
            hoverState = !hoverState
        },
        color = MaterialTheme.colorScheme.primary,
        fontSize = fontSize,
        maxLines = maxLines
    )
}
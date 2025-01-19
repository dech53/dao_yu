package com.dech53.dao_yu.component

import androidx.compose.animation.animateContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration


@Composable
fun HtmlText(
    htmlContent: String,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    maxLines: Int = 4,
) {
    Text(
        //display html and <a> into interactive mode
        AnnotatedString.fromHtml(
            htmlContent,
            linkStyles = TextLinkStyles(
                style = SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        ),
        modifier = Modifier.animateContentSize(),
        maxLines = maxLines,
        style = textStyle,
    )
}
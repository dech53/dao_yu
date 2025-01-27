package com.dech53.dao_yu.component

import android.content.Context
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.dech53.dao_yu.models.QuoteRef
import com.dech53.dao_yu.utils.Http_request
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dech53.dao_yu.viewmodels.ThreadInfoView_ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun HtmlTRText(
    htmlContent: String,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    maxLines: Int = Int.MAX_VALUE,
    fontSize: TextUnit = 15.sp,
    viewModel: ThreadInfoView_ViewModel,
    context: Context,
    posterName: String
) {

    //main regex match
    val mainRegex = Regex("<font color=\"#789922\">&gt;&gt;No\\.\\d+</font>")
    val interactionSource = remember { MutableInteractionSource() }
    val parts = remember(htmlContent) {
        val result = mutableListOf<Pair<Boolean, String>>()
        var lastIndex = 0
        mainRegex.findAll(htmlContent).forEach { matchResult ->
            val startIndex = matchResult.range.first
            val endIndex = matchResult.range.last + 1
            if (startIndex > lastIndex) {
                result.add(false to htmlContent.substring(lastIndex, startIndex))
            }
            result.add(true to matchResult.value)
            lastIndex = endIndex
        }
        if (lastIndex < htmlContent.length) {
            result.add(false to htmlContent.substring(lastIndex))
        }
        result
    }
    var scope = rememberCoroutineScope()
    //second regex match
    Column(modifier = Modifier.animateContentSize()) {
        parts.forEach { (isMatch, text) ->
            if (isMatch) {
                // 提取 id
                Log.d("paired text", text)
                val id = Regex("\\d+").find(Regex("&gt;&gt;No.\\d+").find(text)!!.value)!!.value
                viewModel.getRef(id)
                var contentState = remember { viewModel.contentContext }
                contentState[id]?.let { quoteRef ->
                    QuotedComponent(quoteRef, viewModel = viewModel, context, posterName)
//                    OutlinedTextField(
//                        value = TextFieldValue(
//                            annotatedString = AnnotatedString.fromHtml(
//                                quoteRef,
//                                linkStyles = TextLinkStyles(
//                                    style = SpanStyle(
//                                        textDecoration = TextDecoration.Underline,
//                                        color = MaterialTheme.colorScheme.primary
//                                    )
//                                )
//                            )
//                        ),
//                        onValueChange = {},
//                        readOnly = true,
//                        textStyle = textStyle,
//                        modifier = Modifier.animateContentSize(),
//                        label = {
//                            Text(
//                                text = AnnotatedString.fromHtml(
//                                    text,
//                                    linkStyles = TextLinkStyles(
//                                        style = SpanStyle(
//                                            textDecoration = TextDecoration.Underline,
//                                            color = MaterialTheme.colorScheme.primary
//                                        )
//                                    )
//                                ),
//                                style = textStyle,
//                                fontSize = fontSize,
//                                maxLines = maxLines,
//                            )
//                        }
//                    )
                } ?: run {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = ">>No.${id}",
                            color = Color(0xFF789922),
                            fontSize = 14.sp,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF789922),
                                background = MaterialTheme.colorScheme.surfaceContainerLow
                            ),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = Color(0xFF789922)
                        )
                    }
                }
            } else {
                Text(
                    text = AnnotatedString.fromHtml(
                        text,
                        linkStyles = TextLinkStyles(
                            style = SpanStyle(
                                textDecoration = TextDecoration.Underline,
                                color = Color(0xFF789922)
                            )
                        )
                    ),
                    style = textStyle,
                    fontSize = fontSize,
                    maxLines = maxLines,
                    modifier = Modifier.animateContentSize()
                )
            }
        }
    }
}
package com.dech53.dao_yu.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dech53.dao_yu.static.ForumCategory
import com.dech53.dao_yu.viewmodels.MainPage_ViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.dech53.dao_yu.R.drawable

@Composable
fun ForumCategoryDialog(
    forumCategory: List<ForumCategory>,
    viewModel: MainPage_ViewModel,
    changeDrawerState: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        forumCategory.forEach { forumCategory ->
            ExpandableCategory(forumCategory, viewModel, changeDrawerState)
        }
    }
}

@Composable
fun ExpandableCategory(
    category: ForumCategory,
    viewModel: MainPage_ViewModel,
    changeDrawerState: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f, // 旋转角度
        animationSpec = tween(durationMillis = 300) // 动画时长
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium
                )
                .clickable(indication = null, interactionSource = interactionSource) {
                    isExpanded = !isExpanded
                }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = drawable.baseline_keyboard_arrow_right_24),
                contentDescription = "isExpanded",
                modifier = Modifier.graphicsLayer {
                    rotationZ = rotationAngle
                }
            )
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = slideInVertically() + expandVertically(
                expandFrom = Alignment.Top
            ) + fadeIn(
                initialAlpha = 0.3f
            ),
            exit = slideOutVertically() + shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant, // 改变背景颜色
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                category.forums.forEach { forum ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp)
                            .clickable {
                                isExpanded = !isExpanded
                                viewModel.mainForumId.value = category.id
                                viewModel.changeForumId(
                                    forum.id,
                                    true,
                                    category.id
                                )
                                viewModel.changeTitle(forum.name)
                                changeDrawerState()
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(
                            text = forum.name,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

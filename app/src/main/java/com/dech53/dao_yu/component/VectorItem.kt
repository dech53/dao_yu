package com.dech53.dao_yu.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import com.dech53.dao_yu.R

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: Int,
    val unselectedIcon: Int,
)

val MainButtonItems = listOf(
    BottomNavigationItem(
        title = "主页",
        selectedIcon = R.drawable.baseline_home_24,
        unselectedIcon = R.drawable.outline_home_24,
    ),
    BottomNavigationItem(
        title = "收藏",
        selectedIcon = R.drawable.baseline_favorite_24,
        unselectedIcon = R.drawable.outline_favorite_border_24,
    ),
    BottomNavigationItem(
        title = "统计",
        selectedIcon = R.drawable.baseline_insert_chart_24,
        unselectedIcon = R.drawable.baseline_insert_chart_outlined_24,
    ),
    BottomNavigationItem(
        title = "设置",
        selectedIcon = R.drawable.baseline_settings_24,
        unselectedIcon = R.drawable.outline_settings_24,
    ),
)
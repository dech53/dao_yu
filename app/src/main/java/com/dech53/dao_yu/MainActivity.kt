package com.dech53.dao_yu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.dech53.dao_yu.component.Top_card
import com.dech53.dao_yu.models.Thread
import com.dech53.dao_yu.ui.theme.Dao_yuTheme
import com.dech53.dao_yu.utils.Http_request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import com.dech53.dao_yu.component.MainButtonItems

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Dao_yuTheme {
                Main_Screen()
            }
        }
    }
}


@Composable
fun Main_Page(padding: PaddingValues) {
    val dataState = remember { mutableStateOf<List<Thread>?>(null) }
    LaunchedEffect(key1 = Unit) {
        var data = withContext(Dispatchers.IO) {
            Http_request.get<Thread>("showf?id=53")
        }
        dataState.value = data

    }
    LazyColumn(contentPadding = padding) {
        items(dataState.value ?: emptyList()) { thread ->
            Top_card(thread)
        }
    }
}

@Composable
fun Main_Screen() {
    var selectedItemIndex by rememberSaveable { mutableStateOf(0) }
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(text = "岛语") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(onClick = {

                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_sync_alt_24),
                            contentDescription = "Back",
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                MainButtonItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                        },
                        label = {
                            Text(text = item.title)
                        },
                        alwaysShowLabel = true,
                        icon = {
                            BadgedBox(
                                badge = {
                                }
                            ) {
                                Icon(
                                    imageVector = if (selectedItemIndex == index) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title,
                                )
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Main_Page(padding = innerPadding)
    }
}
package com.dech53.dao_yu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dech53.dao_yu.component.MainButtonItems
import com.dech53.dao_yu.component.PullToRefreshLazyColumn
import com.dech53.dao_yu.views.SearchView
import com.dech53.dao_yu.views.SettingsView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = Unit) {
        var data = withContext(Dispatchers.IO) {
            Http_request.get<Thread>("http://192.168.1.4:8080/json")
        }
        dataState.value = data
    }
    if (dataState.value == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "加载中")
        }
    } else {
        PullToRefreshLazyColumn(
            items = dataState.value!!,
            lazyListState = rememberLazyListState(),
            content = { item ->
                Top_card(item)
            },
            isRefreshing = isRefreshing,
            //refreshing method
            onRefresh = {
                scope.launch {
                    isRefreshing = true
                    val newData = withContext(Dispatchers.IO) {
                        Http_request.get<Thread>("http://192.168.1.4:8080/json")
                    }
                    delay(2000L)
                    dataState.value = newData
                    isRefreshing = false
                }
            },
            contentPadding = padding
        )
    }
}

@Composable
fun Main_Screen() {
    var selectedItemIndex by rememberSaveable { mutableStateOf(0) }
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                modifier = Modifier.shadow(elevation = 10.dp),
                title = { Text(text = "島語") },
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
                            navController.navigate(item.title)
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
        //navigation route
        NavHost(navController = navController, startDestination = "主页") {
            composable("主页") { Main_Page(padding = innerPadding) }
            composable("设置") { SettingsView(padding = innerPadding) }
            composable("搜索") { SearchView(padding = innerPadding) }
        }
    }
}
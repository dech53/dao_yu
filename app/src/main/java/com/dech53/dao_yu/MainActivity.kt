package com.dech53.dao_yu

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.dech53.dao_yu.component.Top_card
import com.dech53.dao_yu.ui.theme.Dao_yuTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dech53.dao_yu.component.ForumCategoryDialog
import com.dech53.dao_yu.component.MainButtonItems
import com.dech53.dao_yu.component.PullToRefreshLazyColumn
import com.dech53.dao_yu.static.forumCategories
import com.dech53.dao_yu.viewmodels.MainPage_ViewModel
import com.dech53.dao_yu.views.ImageViewer
import com.dech53.dao_yu.views.SearchView
import com.dech53.dao_yu.views.SettingsView
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: MainPage_ViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Dao_yuTheme {
                Main_Screen(viewModel)
            }
        }
    }
}


@Composable
fun Main_Page(padding: PaddingValues, navController: NavController, viewModel: MainPage_ViewModel) {
    val dataState by viewModel.dataState
    val isRefreshing by remember { viewModel.isRefreshing }
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }
    if (dataState == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "加载中")
        }
    } else {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            PullToRefreshLazyColumn(
                items = dataState!!,
                lazyListState = rememberLazyListState(),
                content = { item ->
                    Top_card(item, clickAction = {
                        navController.navigate(
                            "图片浏览/${
                                Regex(pattern = "/").replace(
                                    item.img!!,
                                    "&"
                                ) + item.ext
                            }"
                        )
                    })
                },
                isRefreshing = isRefreshing,
                //refreshing method
                onRefresh = {
                    viewModel.refreshData()
                },
                contentPadding = padding
            )
//            if (viewModel.isChangeForumIdDialogVisible.value) {
//                ForumCategoryDialog(forumCategory = forumCategories, viewModel = viewModel)
//            }
        }
    }
}

@Composable
fun Main_Screen(viewModel: MainPage_ViewModel) {
    //change bottom Icon
    var selectedItemIndex by rememberSaveable { mutableStateOf(0) }
    //navigation action
    val navController = rememberNavController()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(280.dp)) {
                Text("菜单", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {
                    ForumCategoryDialog(
                        forumCategory = forumCategories,
                        viewModel = viewModel,
                        changeDrawerState = {
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    )
                }

            }
        }
    ) {
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
                            //TODO change the request forum id
//                        viewModel.changeForumIdDialogVisible()
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
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
                                        modifier = Modifier.animateContentSize()
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
                composable("主页") {
                    Main_Page(
                        padding = innerPadding,
                        navController = navController,
                        viewModel = viewModel
                    )
                }
                composable("设置") { SettingsView(padding = innerPadding) }
                composable("搜索") { SearchView(padding = innerPadding) }
                composable("图片浏览/{imgName}") { navBackStackEntry ->
                    val imgName = navBackStackEntry.arguments?.getString("imgName") ?: ""
                    ImageViewer(paddingValues = innerPadding, img_Location = imgName, onBack = {
                        navController.popBackStack()
                    })
                }
            }
        }
    }


}
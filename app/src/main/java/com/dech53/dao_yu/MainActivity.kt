package com.dech53.dao_yu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.dech53.dao_yu.component.Forum_card
import com.dech53.dao_yu.ui.theme.Dao_yuTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
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
import com.dech53.dao_yu.viewmodels.ThreadInfoView_ViewModel
import com.dech53.dao_yu.views.SearchView
import com.dech53.dao_yu.views.SettingsView
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: MainPage_ViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Dao_yuTheme {
                Main_Screen(viewModel)
            }
        }
    }
}


@Composable
fun Main_Page(padding: PaddingValues, viewModel: MainPage_ViewModel) {
    val dataState by viewModel.dataState
    val isRefreshing by remember { viewModel.isRefreshing }
    val interactionSource = remember { MutableInteractionSource() }
    val lazyListState = rememberLazyListState()
    val context = LocalContext.current
    var onError by remember { viewModel.onError }
    val visibilityState = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = dataState) {
        visibilityState.value = true
    }
    if (onError) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    viewModel.loadData()
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "加载失败，请点击重试",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    viewModel.refreshData(false)
                }
            )
        }
    } else if (dataState == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(40.dp)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            PullToRefreshLazyColumn(
                items = dataState!!,
                lazyListState = lazyListState,
                content = { item ->
                    AnimatedVisibility(
                        visible = visibilityState.value,
                        enter = slideInVertically (
                        ) + expandVertically(
                            // Expand from the top.
                            expandFrom = Alignment.Top
                        ) + fadeIn(
                            // Fade in with the initial alpha of 0.3f.
                            initialAlpha = 0.3f
                        )
                    ) {
                        Forum_card(thread = item, imgClickAction = {
                            val intent = Intent(context, ImageViewer::class.java)
                            intent.putExtra("imgName", item.img + item.ext)
                            context.startActivity(intent)
                        }, cardClickAction = {
                            val intent = Intent(context, ThreadAndReplyView::class.java)
                            intent.putExtra("threadId", item.id.toString())
                            context.startActivity(intent)
                        }, stricted = true, posterName = "")
                    }

                },
                isRefreshing = isRefreshing,
                //refreshing method
                onRefresh = {
                    viewModel.refreshData(true)
                },
                contentPadding = padding,
                loadMore = {
                    viewModel.loadMore()
                }
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

    val title by remember { viewModel.title }

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var topBarState by remember { viewModel.topBarState }

    val nowForumId by remember { viewModel.forumId }

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
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = MaterialTheme.shapes.medium
                        )
                        .clip(MaterialTheme.shapes.medium)

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
                    title = { Text(text = title) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    navigationIcon = {
                        IconButton(onClick = {
                            //TODO change the request forum id
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
                                if (index == 0) {
                                    viewModel.refreshData(true)
                                }
                                selectedItemIndex = index
                                navController.navigate(item.title)
                                viewModel.changeTitle(forumCategories.flatMap { it.forums }
                                    .find { it.id == nowForumId }!!.name)
                                viewModel.changeTopBarState(false)
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
            NavHost(navController = navController, enterTransition = {
                fadeIn(
                    tween(durationMillis = 300)
                )
            }, exitTransition = {
                fadeOut(tween(durationMillis = 300))
            }, startDestination = "主页") {
                composable("主页") {
                    Main_Page(
                        padding = innerPadding,
                        viewModel = viewModel
                    )
                }
                composable("设置") { SettingsView(padding = innerPadding) }
                composable("搜索") { SearchView(padding = innerPadding) }
            }
        }
    }
}
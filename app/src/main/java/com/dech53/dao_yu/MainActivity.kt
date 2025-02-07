package com.dech53.dao_yu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.dech53.dao_yu.component.ForumCategoryDialog
import com.dech53.dao_yu.component.MainButtonItems
import com.dech53.dao_yu.component.PullToRefreshLazyColumn
import com.dech53.dao_yu.dao.CookieDatabase
import com.dech53.dao_yu.dao.FavoriteDao
import com.dech53.dao_yu.dao.FavoriteDataBase
import com.dech53.dao_yu.models.Cookie
import com.dech53.dao_yu.models.Favorite
import com.dech53.dao_yu.static.forumCategories
import com.dech53.dao_yu.viewmodels.MainPage_ViewModel
import com.dech53.dao_yu.views.FavView
import com.dech53.dao_yu.views.SettingsView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private val scope = CoroutineScope(Dispatchers.IO)
    val favDbDao by lazy {
        FavoriteDataBase.getDatabase(applicationContext).favoriteDao()
    }
    private val cookieDb by lazy {
        CookieDatabase.getDatabase(applicationContext)
    }
    private val viewModel by viewModels<MainPage_ViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MainPage_ViewModel(cookieDb.cookieDao) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Dao_yuTheme {
                Main_Screen(viewModel, viewModel.cookie.value, favDbDao)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        scope.launch(Dispatchers.IO) {
            viewModel.initHash()
        }
    }
}


@Composable
fun Main_Page(
    padding: PaddingValues,
    viewModel: MainPage_ViewModel,
    cookie: Cookie?,
    favDao: FavoriteDao
) {
    val dataState by viewModel.dataState
    val isRefreshing by remember { viewModel.isRefreshing }
    val interactionSource = remember { MutableInteractionSource() }
    val lazyListState = rememberLazyListState()
    val context = LocalContext.current
    val onError by remember { viewModel.onError }

    val scope = rememberCoroutineScope()
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
            modifier = Modifier
                .fillMaxSize(),
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
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            PullToRefreshLazyColumn(
                items = dataState!!,
                lazyListState = lazyListState,
                content = { item ->
                    Forum_card(thread = item, imgClickAction = {
                        val intent = Intent(context, ImageViewer::class.java)
                        intent.putExtra("imgName", item.img + item.ext)
                        context.startActivity(intent)
                    }, cardClickAction = {
                        val intent = Intent(context, ThreadAndReplyView::class.java)
                        intent.putExtra("threadId", item.id.toString())
                        intent.putExtra("hash", cookie?.cookie ?: "")
                        context.startActivity(intent)
                    }, cardLongClickAction = {//下拉菜单选项判断操作
                            when(it) {
                                "收藏"-> (scope.launch {
                                    withContext(Dispatchers.IO) {
                                        favDao.insert(
                                            Favorite(
                                                item.id.toString(),
                                                item.content,
                                                img = item.img + item.ext
                                            )
                                        )
                                    }
                                    withContext(Dispatchers.Main){
                                        Log.d("Main_Page", "收藏成功 Toast 显示")
                                        Toast.makeText(context,"收藏成功",Toast.LENGTH_SHORT).show()
                                    }
                                })
                                "屏蔽饼干"-> (Toast.makeText(context,"屏蔽饼干",Toast.LENGTH_SHORT).show())
                                "订阅"-> (Toast.makeText(context,"订阅",Toast.LENGTH_SHORT).show())
                            }
                    }, stricted = true, posterName = "")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main_Screen(viewModel: MainPage_ViewModel, cookie: Cookie?, favDao: FavoriteDao) {
    var threadContent by remember { viewModel.threadContent }
    //change bottom Icon
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
    //navigation action
    val navController = rememberNavController()

    val title by remember { viewModel.title }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            viewModel.loadData()
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val nowForumId by remember { viewModel.forumId }
    var isForumChooseExpanded by remember { mutableStateOf(false) }
    var isCookieChooseExpanded by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
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
                            scope.launch(Dispatchers.IO) {
                                drawerState.close()
                            }
                        }
                    )
                }

            }
        }
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        showBottomSheet = !showBottomSheet
                        Log.d("悬浮按钮点击", "触发")
                    },
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_create_24),
                        contentDescription = "Back",
                    )
                }
            },
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
                            scope.launch(Dispatchers.IO) {
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
                                if (index == 0) {
                                    viewModel.changeTitle(forumCategories.flatMap { it.forums }
                                        .find { it.id == nowForumId }!!.name)
                                } else {
                                    viewModel.changeTitle(item.title)
                                }
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
            if (showBottomSheet) {
                ModalBottomSheet(
                    shape = MaterialTheme.shapes.medium,
                    onDismissRequest = { showBottomSheet = false },
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    tonalElevation = 10.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "新建串",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        OutlinedTextField(
                            value = threadContent,
                            onValueChange = {
                                viewModel.changeThreadContent(it)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp),
                            label = {
                                Text(
                                    "正文",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            },
                            placeholder = {
                                Text(
                                    "分享你的想法...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = MaterialTheme.shapes.small,
                            singleLine = false,
                        )
                        Button(
                            onClick = {
                                viewModel.postThread(
                                    content = threadContent,
                                    fid = "117",
                                    cookie = cookie?.cookie ?: ""
                                )
                            }
                        ) {
                            Text(text = "发布")
                        }
                    }
                }
            }
            //navigation route
            NavHost(navController = navController, startDestination = "主页") {
                composable("主页") {
                    Main_Page(
                        padding = innerPadding,
                        viewModel = viewModel,
                        cookie = cookie,
                        favDao = favDao
                    )
                }
                composable("设置") { SettingsView(padding = innerPadding) }
                composable("收藏") {
                    FavView(
                        padding = innerPadding,
                        favDao,
                        viewModel.cookie.value?.cookie ?: ""
                    )
                }
            }
        }
    }
}
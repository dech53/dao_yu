@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.dech53.dao_yu

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.dech53.dao_yu.component.ForumCategoryDialog
import com.dech53.dao_yu.component.MainButtonItems
import com.dech53.dao_yu.component.PullToRefreshLazyColumn
import com.dech53.dao_yu.component.ShimmerList
import com.dech53.dao_yu.component.SkeletonCard
import com.dech53.dao_yu.dao.CookieDatabase
import com.dech53.dao_yu.dao.FavoriteDataBase
import com.dech53.dao_yu.models.Cookie
import com.dech53.dao_yu.models.Favorite
import com.dech53.dao_yu.static.Forum
import com.dech53.dao_yu.static.ForumSort
import com.dech53.dao_yu.static.TimeLine
import com.dech53.dao_yu.static.forumCategories
import com.dech53.dao_yu.static.forumMap
import com.dech53.dao_yu.viewmodels.MainPage_ViewModel
import com.dech53.dao_yu.views.ChartView
import com.dech53.dao_yu.views.FavView
import com.dech53.dao_yu.views.ImageView
import com.dech53.dao_yu.views.SettingsView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class MainActivity : ComponentActivity() {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val viewModel by viewModels<MainPage_ViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MainPage_ViewModel() as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isTaskRoot) {
            finish()
            return
        }

        enableEdgeToEdge()
        setContent {
            Dao_yuTheme {
                Main_Screen(viewModel, viewModel.cookie.value)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.imgList.clear()
        scope.cancel()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.initHash()
        }
    }
}


@Composable
fun Main_Page(
    padding: PaddingValues,
    viewModel: MainPage_ViewModel,
    cookie: Cookie?,
    imageLoader: ImageLoader,
    onImageClick: (String, Int) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope
) {
    val dataState = viewModel.dataState
    val isRefreshing by remember { viewModel.isRefreshing }
    val interactionSource = remember { MutableInteractionSource() }
    val lazyListState = viewModel.mainPageListState
    val favorites by viewModel.favData.collectAsState()
    val context = LocalContext.current
    val onError by remember { viewModel.onError }
    val scope = rememberCoroutineScope()

//    LaunchedEffect(Unit) {
//        snapshotFlow { dataState.size }
//            .drop(1)
//            .distinctUntilChanged()
//            .collectLatest { newSize ->
//                if (newSize > 0 && !viewModel.isInitialLoad.value) {
//                    lazyListState.scrollToItem(0)
//                }
//            }
//    }

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
                    viewModel.refreshData(false, "")
                }
            )
        }
    } else {
        ShimmerList(
            isLoading = isRefreshing,
            contentAfterLoading = {
                var forunCategoryId by viewModel.mainForumId
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    var visible = remember { mutableStateOf(false) }

                    PullToRefreshLazyColumn(
                        items = dataState!!,
                        lazyListState = lazyListState,
                        content = { item ->
                            Forum_card(
                                thread = item,
                                imgClickAction = { name, id ->
                                    onImageClick(name, id)
                                },
                                cardClickAction = {
                                    Log.d("外部单点", "${item}")
                                    val intent = Intent(context, ThreadAndReplyView::class.java)
                                    intent.putExtra("threadId", item.id.toString())
                                    intent.putExtra("hash", cookie?.cookie ?: "")
                                    if (viewModel.hasId(item.id.toString())) {
                                        intent.putExtra("hasId", true)
                                    }
                                    context.startActivity(intent)
                                },
                                cardLongClickAction = {
                                    Log.d("菜单点击", "${item}")
                                    when (it) {
                                        "屏蔽饼干" -> (Toast.makeText(
                                            context,
                                            "屏蔽饼干",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show())

                                        "订阅" -> (Toast.makeText(
                                            context,
                                            "订阅",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show())
                                    }
                                },
                                stricted = true,
                                forumId = item.fid.toString(),
                                forumIdClickAction = {
                                    scope.launch {
                                        withContext(Dispatchers.Main) {
                                            lazyListState.scrollToItem(0)
                                        }
                                    }
                                    viewModel.changeForumId(forumMap[item.fid.toString()]!!, true)
                                    viewModel.mainForumId.value = ""
                                },
                                mainForumId = viewModel.forumId.value,
                                forumCategoryId = forunCategoryId,
                                favClickAction = { isFaved ->
                                    if (!isFaved) {
                                        viewModel.insertFav(
                                            Favorite(
                                                item.id.toString(),
                                                item.content,
                                                img = item.img + item.ext
                                            )
                                        )
                                    } else {
                                        viewModel.deleteFav(
                                            Favorite(
                                                id = item.id.toString(),
                                                content = "",
                                                img = ""
                                            )
                                        )
                                    }
                                },
                                viewModel = viewModel,
                                isFaved = favorites.any {
                                    it.id == item.id.toString()
                                },
                                animatedVisibilityScope = animatedVisibilityScope,
                                sharedTransitionScope = sharedTransitionScope,
                                imageLoader = imageLoader
                            )
                        },
                        isRefreshing = isRefreshing,
                        //refreshing method
                        onRefresh = {
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    viewModel.refreshData(true, "")
                                }
                            }
                        },
                        contentPadding = padding,
                        loadMore = { onComplete ->
                            viewModel.loadMore(onComplete)
                        }
                    )
                }
            },
            skeletonContent = {
                SkeletonCard()
            },
            modifier = Modifier.padding(padding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main_Screen(viewModel: MainPage_ViewModel, cookie: Cookie?) {
    var threadContent by remember { viewModel.threadContent }
    //change bottom Icon
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
    //navigation action
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isImageScreen = currentRoute?.startsWith("image/") == true


    val title by remember { viewModel.title }
    LaunchedEffect(true) {

        withContext(Dispatchers.IO) {
            delay(100)
            viewModel.loadData()
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val topBottomVisible = remember { mutableStateOf(true) }
    val nowForumId by remember { viewModel.forumId }
    var isForumChooseExpanded by remember { mutableStateOf(false) }
    var isCookieChooseExpanded by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val imageLoader = Injekt.get<ImageLoader>()
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = !isImageScreen,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(280.dp)) {
                Text("菜单", modifier = Modifier.padding(22.dp))
                HorizontalDivider()
                Column(
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
                        forumCategory = listOf(
                            ForumSort(
                                id = "999",
                                name = "时间线",
                                sort = "",
                                status = "",
                                forums = viewModel.timeLine.map {
                                    Forum(
                                        id = it.id.toString(),
                                        msg = it.notice,
                                        name = it.name,
                                        showName = it.display_name
                                    )
                                }
                            )
                        ),
                        viewModel = viewModel,
                        changeDrawerState = {
                            scope.launch(Dispatchers.Main) {
                                drawerState.close()
                                viewModel.mainPageListState.scrollToItem(0)
                            }
                        }
                    )
                    ForumCategoryDialog(
                        forumCategory = viewModel.forumList,
                        viewModel = viewModel,
                        changeDrawerState = {
                            scope.launch(Dispatchers.Main) {
                                drawerState.close()
                                viewModel.mainPageListState.scrollToItem(0)
                            }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            floatingActionButton = {
                if (!isImageScreen) {
                    if (!(title in listOf("收藏", "设置", "统计"))) {
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
                    }
                }
            },
            topBar = {
                if (!isImageScreen)
                    @OptIn(ExperimentalMaterial3Api::class)
                    TopAppBar(
                        actions = {
                            IconButton(
                                onClick = {
                                    imageLoader.diskCache?.clear()
                                    Toast.makeText(
                                        context,
                                        "缓存清理成功(っ˘Д˘)ノ",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_clear_all_24),
                                    contentDescription = "清除缓存"
                                )
                            }
                        },
                        modifier = Modifier
                            .shadow(elevation = 10.dp),
                        title = { Text(text = title) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                        navigationIcon = {
                            if (!(title in listOf("收藏", "设置", "统计"))) {
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
                        },
                    )
            },
            bottomBar = {
                if (!isImageScreen)
                    NavigationBar(
                    ) {
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
                                            painter = painterResource(if (selectedItemIndex == index) item.selectedIcon else item.unselectedIcon),
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
            SharedTransitionLayout {
                NavHost(
                    navController = navController,
                    startDestination = "主页",
                ) {
                    composable("主页") {
                        Main_Page(
                            padding = innerPadding,
                            viewModel = viewModel,
                            cookie = cookie,
                            onImageClick = { name, id ->
                                topBottomVisible.value = false
                                navController.navigate("image/$name/$id")
                            },
                            animatedVisibilityScope = this,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            imageLoader = imageLoader
                        )
                    }
                    composable("设置") { SettingsView(padding = innerPadding) }
                    composable("收藏") {
                        FavView(
                            padding = innerPadding,
                            viewModel.cookie.value?.cookie ?: "",
                            viewModel = viewModel,
                            onImageClick = { name, id ->
                                topBottomVisible.value = false
                                navController.navigate("image/$name/$id")
                            },
                            animatedVisibilityScope = this,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            imageLoader = imageLoader
                        )
                    }
                    composable("统计") {
                        ChartView(padding = innerPadding)
                    }
                    composable(
                        "image/{date}/{name}/{id}", arguments = listOf(
                            navArgument("date") {
                                type = NavType.StringType
                            },
                            navArgument("name") {
                                type = NavType.StringType
                            },
                            navArgument("id") {
                                type = NavType.IntType
                            },
                        )
                    ) {
                        val date = it.arguments?.getString("date") ?: ""
                        val name = it.arguments?.getString("name") ?: ""
                        val id = it.arguments?.getInt("id") ?: 0
                        Log.d("name", date + name)
                        ImageView(
                            imgName = date + "/" + name,
                            replyId = id,
                            quitClick = {
                                topBottomVisible.value = true
                                navController.popBackStack()
                            },
                            animatedVisibilityScope = this,
                            sharedTransitionScope = this@SharedTransitionLayout,
                        )
                    }
                }
            }
        }
    }
}
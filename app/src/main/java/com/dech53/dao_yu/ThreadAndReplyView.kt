@file:OptIn(ExperimentalMaterial3Api::class)

package com.dech53.dao_yu

import android.content.Intent
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import com.dech53.dao_yu.component.CustomExposedDropMenu
import com.dech53.dao_yu.component.HtmlTRText
import com.dech53.dao_yu.component.TRCard
import com.dech53.dao_yu.dao.CookieDatabase
import com.dech53.dao_yu.dao.FavoriteDataBase
import com.dech53.dao_yu.models.Favorite
import com.dech53.dao_yu.static.Url
import com.dech53.dao_yu.static.xDaoPhrases
import com.dech53.dao_yu.ui.theme.Dao_yuTheme
import com.dech53.dao_yu.viewmodels.ThreadInfoView_ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ThreadAndReplyView : ComponentActivity() {
    val favDbDao by lazy {
        FavoriteDataBase.getDatabase(applicationContext)
    }
    private val viewModel by viewModels<ThreadInfoView_ViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ThreadInfoView_ViewModel(db.cookieDao, favDbDao.favoriteDao) as T
                }
            }
        }
    )
    private val db by lazy {
        CookieDatabase.getDatabase(applicationContext)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TRA", "进程销毁")
    }

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val threadId = intent.getStringExtra("threadId")
        val hash = intent.getStringExtra("hash")
        viewModel.isFaved.value = intent.getBooleanExtra("hasId", false)
        viewModel.hash.value = hash ?: ""
        setContent {
            Dao_yuTheme {
                var showBottomSheet by remember { mutableStateOf(false) }
                val lazyListState = rememberLazyListState()
                val context = LocalContext.current
                val cookies by viewModel.cookieList.collectAsState()
                var isChangePageVisible = remember { mutableStateOf(false) }
                Scaffold(
                    topBar = {
                        TopAppBar(
                            modifier = Modifier.shadow(elevation = 10.dp),
                            title = {
                                Text(text = "No." + threadId)
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        onBackPressedDispatcher.onBackPressed()
                                    }
                                ) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(
                                            id = R.drawable.baseline_arrow_back_24
                                        ),
                                        contentDescription = "Back"
                                    )
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        //选择回复生成分享图片
                                    }
                                ) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_collections_24),
                                        contentDescription = "generate photo"
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {
                        BottomAppBar(
                            actions = {
                                IconButton(onClick = {
                                    if (viewModel.isFaved.value) {
                                        viewModel.deleteFav(
                                            Favorite(id = threadId!!, content = "", img = "")
                                        )
                                    } else {
                                        viewModel.addFave(
                                            Favorite(
                                                id = threadId!!,
                                                content = viewModel.threadInfo.value?.get(0)?.content
                                                    ?: "",
                                                img = (viewModel.threadInfo.value?.get(0)?.img
                                                    ?: "") + (viewModel.threadInfo.value?.get(0)?.ext
                                                    ?: "")
                                            )
                                        )
                                    }
                                    viewModel.isFaved.value = !viewModel.isFaved.value
                                }) {
                                    Icon(
                                        imageVector = if (!viewModel.isFaved.value) ImageVector.vectorResource(
                                            id = R.drawable.outline_favorite_border_24
                                        ) else ImageVector.vectorResource(
                                            id = R.drawable.baseline_favorite_24
                                        ),
                                        contentDescription = "bottom icon"
                                    )
                                }
                                IconButton(onClick = {
                                    //跳转页数
                                    isChangePageVisible.value = !isChangePageVisible.value
                                }) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.baseline_move_down_24),
                                        contentDescription = "bottom icon"
                                    )
                                }
                                IconButton(onClick = {
                                    val sendIntent: Intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            Url.Thread_Main_URL + threadId
                                        )
                                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, null)
                                    context.startActivity(shareIntent)
                                    Log.d("log thread share link", "${threadId}")
                                }) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.outline_share_24),
                                        contentDescription = "bottom icon"
                                    )
                                }
                            },
                            floatingActionButton = {
                                FloatingActionButton(onClick = {
                                    showBottomSheet = !showBottomSheet
                                }) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.baseline_message_24),
                                        contentDescription = "reply thread",
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    val configuration = LocalConfiguration.current
                    var uri by remember {
                        mutableStateOf<Uri?>(null)
                    }


                    val singlePhotoPicker = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.PickVisualMedia(),
                        onResult = { uri = it }
                    )
                    var textFieldModifier = Modifier.fillMaxSize()
                    LaunchedEffect(uri) {
                        if (uri != null) {
                            textFieldModifier = Modifier.fillMaxHeight()
                        } else {
                            textFieldModifier = Modifier.fillMaxSize()
                        }
                    }

                    if (showBottomSheet) {
                        val isSending by viewModel.IsSending
                        ModalBottomSheet(
                            onDismissRequest = {
                                showBottomSheet = false
                            },
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            windowInsets = WindowInsets(0),
                            modifier = Modifier.heightIn(
                                max = (configuration.screenHeightDp / 1.5).dp
                            ),
                            shape = MaterialTheme.shapes.small
                        ) {
                            val focusRequester = remember { FocusRequester() }
                            val keyboardController = LocalSoftwareKeyboardController.current
                            LaunchedEffect(showBottomSheet) {
                                if (showBottomSheet) {
                                    delay(150)
                                    focusRequester.requestFocus()
                                }
                            }
                            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    text = "回复串",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .padding(bottom = 10.dp)
                                )
                                //disply choosed img
                            }
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp)
                                    .imePadding()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .height((configuration.screenHeightDp / 8).dp)
                                        .fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .nestedScroll(rememberNestedScrollInteropConnection())
                                            .fillMaxHeight()
                                            .weight(1f)
                                    ) {
                                        OutlinedTextField(
                                            value = viewModel.textFieldValue.value,
                                            modifier = textFieldModifier
                                                .focusRequester(focusRequester = focusRequester),
                                            onValueChange = {
                                                viewModel.updateTextFieldValue(it)
                                            },
                                            label = {
                                                Text(
                                                    "正文",
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        )
                                    }
                                    if (uri != null) {
                                        AsyncImage(
                                            model = uri,
                                            contentDescription = "picked photo",
                                            modifier = Modifier
                                                .size(248.dp)
                                                .weight(0.5f),
                                            placeholder = painterResource(id = R.drawable.baseline_image_24)
                                        )
                                    }
                                }
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    //cookie choose option
                                    CustomExposedDropMenu(
                                        itemList = cookies,
                                        onSelect = { selectedHash ->
                                            viewModel.hash.value = selectedHash
                                        }
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        //表情图标
                                        IconButton(
                                            onClick = {
                                                keyboardController?.hide()
                                            },
                                        ) {
                                            Icon(
                                                imageVector = ImageVector.vectorResource(R.drawable.round_tag_faces_24),
                                                contentDescription = "表情",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        //图片picker
                                        IconButton(
                                            onClick = {
                                                singlePhotoPicker.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                            },
                                        ) {
                                            Icon(
                                                imageVector = ImageVector.vectorResource(R.drawable.baseline_image_24),
                                                contentDescription = "图片",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        //发送图标
                                        if (!isSending) {
                                            IconButton(
                                                onClick = {
                                                    if (!viewModel.textFieldValue.value.text.isEmpty() && !viewModel.hash.value.isEmpty()) {
                                                        viewModel.replyThread(
                                                            content = viewModel.textFieldValue.value.text,
                                                            resto = threadId!!,
                                                            cookie = viewModel.hash.value,
                                                            img = uri,
                                                            context = context,
                                                            onSuccess = { bool ->
                                                                showBottomSheet = bool
                                                            }
                                                        )
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            "存在空字段",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                },
                                            ) {
                                                Icon(
                                                    imageVector = ImageVector.vectorResource(R.drawable.round_send_24),
                                                    contentDescription = "发送",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        } else {
                                            Box(
                                                modifier = Modifier.padding(start = 15.dp)
                                            ) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(24.dp),
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            //emoji grid
//                                Box(
//                                    modifier = Modifier
//                                        .height(200.dp)
//                                        .fillMaxWidth()
//                                ) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(xDaoPhrases) { phrase ->
                                    Text(
                                        text = phrase.display,
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .clickable {
                                                viewModel.appendToTextField(phrase.value)
                                            },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
//                                    }
                            }
                        }
                    }

                    Box(
                        contentAlignment = Alignment.TopCenter,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)

                    ) {
                        val threadInfo by viewModel.threadInfo
                        val isRefreshing by remember { viewModel.isRefreshing }
                        var onError by remember { viewModel.onError }
                        val interactionSource = remember { MutableInteractionSource() }
                        //初始化
                        LaunchedEffect(Unit) {
                            if (threadInfo == null) {
                                viewModel.clearThreadInfo()
                                viewModel.getThreadId(threadId!!)
                            } else {
                                if (threadId != threadInfo!!.first().id.toString()) {
                                    viewModel.clearThreadInfo()
                                    viewModel.getThreadId(threadId!!)
                                }
                            }
                        }
                        Box(
                            contentAlignment = Alignment.TopCenter,
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            if (onError) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable(
                                            interactionSource = interactionSource,
                                            indication = null
                                        ) {
                                            viewModel.refreshData()
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
                                            viewModel.refreshData()
                                        }
                                    )
                                }
                            } else if (threadInfo == null) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .width(40.dp)
                                        .align(Alignment.Center),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
//                                TRCard(
//                                    item = threadInfo!!,
//                                    lazyListState = rememberLazyListState(),
//                                    loadMore = {
//                                        viewModel.loadMore()
//                                    },
//                                    viewModel
//                                )
                                val poster = threadInfo!![0].user_hash
                                val pullToRefreshState = rememberPullToRefreshState()
                                var isLoadingMore by remember { mutableStateOf(false) }
                                Box(
                                    contentAlignment = Alignment.TopCenter,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .nestedScroll(pullToRefreshState.nestedScrollConnection)

                                ) {
                                    LazyColumn(
                                        state = lazyListState,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        itemsIndexed(threadInfo!!) { index, reply ->
                                            val context = LocalContext.current
                                            if (reply.id != 9999999) {
                                                TRCard(
                                                    posterName = poster,
                                                    item = reply,
                                                    lazyListState = lazyListState,
                                                    loadMore = {
                                                        viewModel.loadMore("F")
                                                    },
                                                    viewModel
                                                )
                                            } else {
                                                Card(
                                                    shape = MaterialTheme.shapes.small,
                                                    border = BorderStroke(
                                                        1.dp,
                                                        MaterialTheme.colorScheme.primary
                                                    ),
                                                    elevation = CardDefaults.elevatedCardElevation(
                                                        defaultElevation = 4.dp
                                                    ),
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                                        contentColor = MaterialTheme.colorScheme.onSurface
                                                    ),
                                                    modifier = Modifier
                                                        .padding(
                                                            horizontal = 13.dp,
                                                            vertical = 8.dp
                                                        )
                                                        .fillMaxWidth()
                                                ) {
                                                    Column(modifier = Modifier.padding(5.dp)) {
                                                        Row(
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            modifier = Modifier.fillMaxWidth()
                                                        ) {
                                                            Row {
                                                                Text(
                                                                    text = reply.user_hash,
                                                                    fontWeight = FontWeight.Bold,
                                                                    fontSize = 17.sp,
                                                                    color = MaterialTheme.colorScheme.primary
                                                                )
                                                            }
                                                        }
                                                        HtmlTRText(
                                                            htmlContent = reply.content,
                                                            maxLines = Int.MAX_VALUE,
                                                            viewModel = viewModel,
                                                            context = context,
                                                            posterName = poster
                                                        )
                                                    }
                                                }
                                            }
                                            //load more data when scroll to the bottom
                                            LaunchedEffect(Unit) {
                                                if (index == lazyListState.layoutInfo.totalItemsCount - 1 && viewModel.pageId.value <= viewModel.maxPage.value) {
                                                    isLoadingMore = true
                                                    viewModel.loadMore("F", onComplete = {
                                                        isLoadingMore = false
                                                    })
                                                }
                                            }
                                        }
                                        if (isLoadingMore) {
                                            item {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(16.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    CircularProgressIndicator()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    val page = remember { mutableStateOf(1) }
                    if (isChangePageVisible.value) {
                        //切换页数
                        Dialog(onDismissRequest = {
                            isChangePageVisible.value = !isChangePageVisible.value
                        }) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .width(400.dp),
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    IconButton(onClick = {
                                        page.value =
                                            (page.value - 1).coerceAtLeast(1)
                                    }) {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                                            contentDescription = "Previous Page"
                                        )
                                    }

                                    OutlinedTextField(
                                        value = page.value.toString(),
                                        onValueChange = {
                                            val newPage = it
                                            if (newPage != "" && newPage > "0") {
                                                page.value = newPage.toInt()
                                            }
                                        },
                                        modifier = Modifier.width(100.dp),
                                        textStyle = TextStyle(
                                            fontSize = 16.sp,
                                            textAlign = TextAlign.Center
                                        ),
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            keyboardType = KeyboardType.Number,
                                            imeAction = ImeAction.Done
                                        ),
                                        keyboardActions = KeyboardActions(onDone = {
                                            viewModel.pageId.value = page.value
                                            viewModel.loadMore("B", page.value)
                                            isChangePageVisible.value = !isChangePageVisible.value
                                        })
                                    )

                                    IconButton(onClick = {
                                        if (page.value <= viewModel.maxPage.value)
                                            page.value += 1
                                    }) {
                                        Icon(
                                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_arrow_forward_ios_24),
                                            contentDescription = "Next Page"
                                        )
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}
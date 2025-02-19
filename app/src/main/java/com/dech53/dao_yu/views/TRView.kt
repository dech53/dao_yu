@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)

package com.dech53.dao_yu.views

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.dech53.dao_yu.component.ShimmerList
import com.dech53.dao_yu.component.SkeletonCard
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.ImageLoader
import coil3.compose.SubcomposeAsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.toBitmap
import com.dech53.dao_yu.R
import com.dech53.dao_yu.component.HtmlTRText
import com.dech53.dao_yu.models.Favorite
import com.dech53.dao_yu.models.preLoadImage
import com.dech53.dao_yu.static.Url
import com.dech53.dao_yu.static.forumNameMap
import com.dech53.dao_yu.ui.theme.capsuleShape
import com.dech53.dao_yu.ui.theme.shimmerEffect
import com.dech53.dao_yu.viewmodels.ThreadInfoView_ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.dech53.dao_yu.component.CustomExposedDropMenu
import com.dech53.dao_yu.static.xDaoPhrases
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TRView(
    viewModel: ThreadInfoView_ViewModel,
    threadId: String,
    changeState: (Boolean) -> Unit,
    onImageClick: (String,Int) -> Unit,
    backAction:()->Unit,
    imageLoader: ImageLoader,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedTransitionScope: SharedTransitionScope
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = layoutInfo.totalItemsCount
            lastVisibleItem?.index == totalItems - 1 && viewModel.pageId.value <= viewModel.maxPage.value
        }
    }

    val scope = rememberCoroutineScope()

    val fid by viewModel.fid

    var isLoadingMore by remember { mutableStateOf(false) }
    val page = remember { mutableStateOf(1) }
    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .distinctUntilChanged()
            .collect {
                if (it) {
                    Log.d("触发触底刷新", "触发触底刷新${page.value}")
                    page.value =
                        if (page.value >= viewModel.maxPage.value) viewModel.maxPage.value else page.value + 1
                    isLoadingMore = true
                    viewModel.loadMore("F", onComplete = {
                        isLoadingMore = false
                    })
                }
            }
    }

    val context = LocalContext.current
    val cookies by viewModel.cookieList.collectAsState()
    var isChangePageVisible = remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.shadow(elevation = 10.dp),
                title = {
                    Column {
                        Text(
                            text = "No." + threadId,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (forumNameMap[fid] == null) {
                                Text(
                                    text = "",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .height(18.dp)
                                        .width(50.dp)
                                        .clip(capsuleShape)
                                        .shimmerEffect()
                                )
                            } else {
                                Text(
                                    text = forumNameMap[fid] ?: "加载中...",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.padding(4.dp))
                            Text(
                                text = "X岛·nmbxd.com",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            backAction()
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
                    IconButton(onClick = {
                        viewModel.isRaw.value = !viewModel.isRaw.value
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = if (viewModel.isRaw.value) R.drawable.baseline_raw_on_24 else R.drawable.baseline_raw_off_24),
                            contentDescription = "原始图缩略图切换"
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
                                    content = viewModel.threadInfo.value?.firstOrNull()?.content
                                        ?: "",
                                    img = (viewModel.threadInfo.value?.firstOrNull()?.img
                                        ?: "") + (viewModel.threadInfo.value?.firstOrNull()?.ext
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
                                                name = "无名氏",
                                                title = "无标题",
                                                content = viewModel.textFieldValue.value.text,
                                                resto = threadId!!,
                                                cookie = viewModel.hash.value,
                                                img = uri,
                                                context = context,
                                                onSuccess = { bool ->
                                                    if (((viewModel.threadInfo.value.size - 1) % 19 != 0) && (viewModel.pageId.value >= viewModel.maxPage.value)||(viewModel.threadInfo.value.size == 1)) {
                                                        viewModel.loadMore(
                                                            "F",
                                                            addPage = false,
                                                            onComplete = {
                                                                isLoadingMore = false
                                                            }
                                                        )
                                                    } else {
                                                        viewModel.loadMore(
                                                            "F", onComplete = {
                                                                isLoadingMore = false
                                                            }
                                                        )
                                                    }
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
                    modifier = Modifier.fillMaxSize()
                        .padding(8.dp)
                        ,
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
            val context = LocalContext.current
            val threadInfo by viewModel.threadInfo
            var skipPage by remember { viewModel.skipPage }
            var isLoading by viewModel.isLoadingBackward
            val isRefreshing by remember { viewModel.isRefreshing }
            var onError by remember { viewModel.onError }
            val interactionSource = remember { MutableInteractionSource() }
            //初始化
            LaunchedEffect(Unit) {
                if (threadInfo.isEmpty()) {
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
                } else {
                    ShimmerList(
                        isLoading = threadInfo.isEmpty(),
                        contentAfterLoading = {
                            val poster = threadInfo!!.first().user_hash
                            val pullToRefreshState = rememberPullToRefreshState()
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
                                    itemsIndexed(threadInfo!!.toList()) { index, reply ->
                                        Log.d("now属性", reply.now)
                                        val date_ by remember(reply.now) {
                                            mutableStateOf(
                                                Regex(pattern = "-").replace(
                                                    Regex(pattern = "[^(]*|(?<=\\))[^)]*").find(
                                                        reply.now
                                                    )!!.value,
                                                    "/"
                                                )
                                            )
                                        }

                                        val time_ by remember(reply.now) {
                                            mutableStateOf(
                                                Regex("""\d{2}:\d{2}:\d{2}$""").find(reply.now)!!.value,
                                            )
                                        }
                                        Log.d("time正则结果", time_)


                                        val imageUrl = if (viewModel.isRaw.value) {
                                            Url.IMG_FULL_QA + reply.img + reply.ext
                                        } else {
                                            Url.IMG_THUMB_QA + reply.img + reply.ext
                                        }
                                        val imageInfo = viewModel.imgList[imageUrl]
                                        var imageWidth = imageInfo?.width ?: 200
                                        var imageHeight = imageInfo?.height ?: 200


                                        val request = remember(imageUrl) {
                                            ImageRequest.Builder(context)
                                                .data(imageUrl)
                                                .memoryCachePolicy(CachePolicy.ENABLED)
                                                .diskCachePolicy(CachePolicy.ENABLED)
                                                .build()
                                        }

                                        LaunchedEffect(viewModel.isRaw.value) {
                                            if (reply.img != "") {
                                                if (!viewModel.imgList.containsKey(imageUrl)) {
                                                    val bitmap =
                                                        imageLoader.execute(request).image!!.toBitmap()
                                                    imageWidth = bitmap.width
                                                    imageHeight = bitmap.height
                                                    viewModel.imgList[imageUrl] =
                                                        preLoadImage(
                                                            height = bitmap.height,
                                                            width = bitmap.width
                                                        )
                                                }
                                            }
                                        }
                                        val imageModifier = if (viewModel.isRaw.value) {
                                            Modifier.aspectRatio(imageWidth.toFloat() / imageHeight.toFloat())
                                        } else {
                                            Modifier.size(
                                                width = imageWidth.dp,
                                                height = imageHeight.dp
                                            )
                                        }
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                                contentColor = MaterialTheme.colorScheme.onSurface
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                                .clip(RoundedCornerShape(22.dp)).padding(
                                                    horizontal = 13.dp,
                                                    vertical = 8.dp
                                                )
                                                .combinedClickable(
                                                    onClick = {
                                                    },
                                                    onLongClick = {
                                                        Log.d(
                                                            "TR卡片长按",
                                                            "触发${reply.id}"
                                                        )
                                                        viewModel.appendToTextField(">>No.${reply.id}\n")
                                                    }
                                                )
                                        ) {
                                            Log.d(
                                                "是否重组",
                                                "${reply.id},个数${viewModel.tipsCount.value}"
                                            )
                                            Column(modifier = Modifier.padding(11.dp)) {
//            Text(
//                text = time_,
//                fontWeight = FontWeight.W500,
//                fontSize = 11.sp
//            )'
                                                Row(
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Row {
                                                        Text(
                                                            text = reply.user_hash,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = if (reply.id != 9999999) 14.sp else 17.sp,
                                                            color = if (reply.user_hash == "Admin") Color.Red else MaterialTheme.colorScheme.primary
                                                        )
                                                        Spacer(
                                                            modifier = Modifier.padding(
                                                                2.dp
                                                            )
                                                        )
                                                        if ((poster == reply.user_hash)) {
                                                            Box(
                                                                contentAlignment = Alignment.Center,
                                                                modifier = Modifier
                                                                    .width(30.dp)
                                                                    .height(IntrinsicSize.Min)
                                                                    .padding(
                                                                        horizontal = 3.dp,
                                                                        vertical = 1.dp
                                                                    )
                                                                    .background(
                                                                        color = MaterialTheme.colorScheme.primary.copy(
                                                                            alpha = 0.5f
                                                                        ),
                                                                        shape = capsuleShape
                                                                    )

                                                            ) {
                                                                Text(
                                                                    text = "Po",
                                                                    fontWeight = FontWeight.Bold,
                                                                    style = MaterialTheme.typography.bodyMedium,
                                                                    fontSize = 8.sp,
                                                                    color = MaterialTheme.colorScheme.onPrimary
                                                                )
                                                            }
                                                        }
                                                    }
                                                    if (reply.id != 9999999)
                                                        Text(
                                                            text = "No.${reply.id}",
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 14.sp,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                }
                                                if (reply.id != 9999999)
                                                    Row(
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Row {
                                                            Text(
                                                                text = date_,
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 13.sp
                                                            )
                                                            Spacer(
                                                                modifier = Modifier.padding(
                                                                    3.dp
                                                                )
                                                            )
                                                            Text(
                                                                text = time_,
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 13.sp,
                                                                color = MaterialTheme.colorScheme.primary
                                                            )
                                                        }
                                                    }
                                                if (reply.sage == 1) {
                                                    Text(
                                                        text = "SAGE",
                                                        color = Color.Red,
                                                        fontSize = 22.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                if (reply.title != "无标题") {
                                                    Text(
                                                        text = reply.title,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                if (reply.name != "无名氏") {
                                                    Text(
                                                        text = reply.name,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                HtmlTRText(
                                                    htmlContent = reply.content,
                                                    maxLines = Int.MAX_VALUE,
                                                    viewModel = viewModel,
                                                    context = context,
                                                    posterName = poster,
                                                )
                                                with(sharedTransitionScope) {
                                                    key(imageInfo) {
                                                        if (reply.img != "") {
                                                            SubcomposeAsyncImage(
                                                                imageLoader = imageLoader,
                                                                model = ImageRequest.Builder(context)
                                                                    .data(imageUrl)
                                                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                                                    .diskCachePolicy(CachePolicy.ENABLED)
                                                                    .placeholderMemoryCacheKey(key = "${reply.id}image/${reply.img}${reply.ext}${viewModel.isRaw.value}")
                                                                    .memoryCacheKey(key = "${reply.id}image/${reply.img}${reply.ext}${viewModel.isRaw.value}")
                                                                    .build(),
                                                                contentDescription = null,
                                                                contentScale = ContentScale.Fit,
                                                                alignment = Alignment.CenterStart,
                                                                modifier = Modifier
                                                                    .clickable(
                                                                        indication = rememberRipple(
                                                                            bounded = true
                                                                        ),
                                                                        interactionSource = remember { MutableInteractionSource() }
                                                                    ) {
                                                                        onImageClick(
                                                                            reply.img + reply.ext,
                                                                            reply.id
                                                                        )
                                                                    }
                                                            ) {
                                                                val painter =
                                                                    this@SubcomposeAsyncImage.painter
                                                                val state by painter.state.collectAsState()

                                                                when (state) {
                                                                    AsyncImagePainter.State.Empty -> {}
                                                                    is AsyncImagePainter.State.Error -> {
                                                                        Box(
                                                                            modifier = Modifier.fillMaxSize(),
                                                                            contentAlignment = Alignment.Center
                                                                        ) {
                                                                            Text("加载失败")
                                                                        }
                                                                    }
                                                                    is AsyncImagePainter.State.Loading -> {
                                                                        Box(
                                                                            modifier = Modifier.fillMaxSize(),
                                                                            contentAlignment = Alignment.Center
                                                                        ) {
                                                                            CircularProgressIndicator(
                                                                                color = MaterialTheme.colorScheme.primary
                                                                            )
                                                                        }
                                                                    }
                                                                    is AsyncImagePainter.State.Success -> {
                                                                        Image(
                                                                            painter = painter,
                                                                            contentDescription = null,
                                                                            modifier = imageModifier.sharedElement(
                                                                                state = rememberSharedContentState(
                                                                                    key = "${reply.id}image/${reply.img}${reply.ext}"),
                                                                                animatedVisibilityScope = animatedVisibilityScope,
                                                                                boundsTransform = BoundsTransform { initialBounds, targetBounds ->
                                                                                    tween(durationMillis = 200)
                                                                                }
                                                                            ),
                                                                            contentScale = ContentScale.Fit
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (index == 0) {
                                            Row(
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 5.dp)
                                            ) {
                                                if (skipPage > 1 && !isLoading) {
                                                    TextButton(onClick = {
                                                        viewModel.isLoadingBackward.value =
                                                            true
                                                        viewModel.loadMore("B")
                                                    }) {
                                                        Text("加载先前回复")
                                                    }
                                                }
                                                if (isLoading) {
                                                    ShimmerList(
                                                        isLoading = isLoading,
                                                        contentAfterLoading = {},
                                                        skeletonContent = {
                                                            SkeletonCard()
                                                        },
                                                        modifier = Modifier,
                                                        amount = 2
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    if (isLoadingMore) {
                                        item {
                                            ShimmerList(
                                                isLoading = isLoadingMore,
                                                contentAfterLoading = {},
                                                skeletonContent = {
                                                    SkeletonCard()
                                                },
                                                modifier = Modifier,
                                                amount = 1
                                            )
                                        }
                                    } else {
                                        item {
                                            TextButton(
                                                onClick = {
                                                    if ((((viewModel.threadInfo.value.size - 1 - viewModel.tipsCount.value) % 19 != 0) && (viewModel.pageId.value >= viewModel.maxPage.value)) || (viewModel.threadInfo.value.size - viewModel.tipsCount.value == 1)) {
                                                        viewModel.loadMore(
                                                            "F",
                                                            addPage = false,
                                                            onComplete = {
                                                                changeState(false)
                                                            }
                                                        )
                                                    } else {
                                                        viewModel.loadMore(
                                                            "F", onComplete = {
                                                                changeState(false)
                                                            }
                                                        )
                                                    }
                                                    Log.d("按钮刷新", "按钮刷新")
                                                    changeState(true)
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(50.dp)
                                            ) { Text("没有更多了,刷新试试？") }
                                        }
                                    }
                                }
                            }
                        },
                        skeletonContent = {
                            SkeletonCard()
                        },
                        modifier = Modifier,
                    )
                }
            }
        }
        if (isChangePageVisible.value) {
            //切换页数
            Dialog(onDismissRequest = {
                isChangePageVisible.value = !isChangePageVisible.value
            }) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                                page.value = 1
                            }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_keyboard_double_arrow_left_24),
                                    contentDescription = "mini Page"
                                )
                            }
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
                                    viewModel.loadMore("S")
                                    isChangePageVisible.value =
                                        !isChangePageVisible.value
                                })
                            )

                            IconButton(onClick = {
                                if (page.value < viewModel.maxPage.value)
                                    page.value += 1
                            }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_arrow_forward_ios_24),
                                    contentDescription = "Next Page"
                                )
                            }
                            IconButton(onClick = {
                                page.value = viewModel.maxPage.value
                            }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_keyboard_double_arrow_right_24),
                                    contentDescription = "mini Page"
                                )
                            }
                        }
                    }
                    Button(onClick = {
                        scope.launch {
                            withContext(Dispatchers.Main) {
                                lazyListState.scrollToItem(0)
                            }
                        }
                        viewModel.pageId.value = page.value
                        viewModel.skipPage.value = page.value
                        viewModel.loadMore("S")
                        isChangePageVisible.value =
                            !isChangePageVisible.value
                    }) {
                        Text("跳转")
                    }
                }
            }
        }
    }
}
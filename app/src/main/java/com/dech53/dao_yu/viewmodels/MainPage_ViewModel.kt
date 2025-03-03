package com.dech53.dao_yu.viewmodels

import android.content.Context
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.extensions.isNotNull
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.toBitmap
import com.dech53.dao_yu.dao.DataBaseRepository
import com.dech53.dao_yu.utils.Http_request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.dech53.dao_yu.models.*
import com.dech53.dao_yu.static.ForumSort
import com.dech53.dao_yu.static.TimeLine
import com.dech53.dao_yu.static.Url
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class MainPage_ViewModel(
    private val repo: DataBaseRepository = Injekt.get(),
    private val imageLoader: ImageLoader = Injekt.get(),
) :
    ViewModel() {
    private val _dataState = mutableStateListOf<Thread>()
    val dataState: List<Thread> get() = _dataState

    var cookie = mutableStateOf<Cookie?>(null)


    var isThread = mutableStateOf(false)

    var pageId = mutableStateOf(1)
        private set

    var isRefreshing = mutableStateOf(false)
        private set

    //TODO add dialog to change page id
    var forumId = mutableStateOf("1")

    var mainForumId = mutableStateOf("999")

    var title = mutableStateOf("综合线")

    fun insertFav(fav: Favorite) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repo.insertFav(fav)
            }
        }
    }

    fun hasId(id: String): Boolean {
        return _favorites.value.any { it.id == id }
    }


    private val _favorites = MutableStateFlow<List<Favorite>>(emptyList())
    val favData: StateFlow<List<Favorite>> = _favorites

    fun getAllFav() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repo.getFlowOfFav().collect { newFavorites ->
                    _favorites.value = newFavorites
                }
            }
        }
    }

    fun deleteFav(fav: Favorite) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repo.deleteFav(fav)
            }
        }
    }

    var threadContent = mutableStateOf("")

    fun changeThreadContent(content: String) {
        threadContent.value = content
    }

    var onError = mutableStateOf(false)
        private set

    //    var isChangeForumIdDialogVisible = mutableStateOf(false)
//        private set
//    private val db = CookieDatabase.getDatabase(context)
//    private val cookieDao = db.cookieDao
//

    fun initHash() {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.IO) {
                val hash = repo.getHashToVerify()
                withContext(Dispatchers.Main) {
                    cookie.value = hash
                }
            }
        }
    }


    var forumList = mutableStateListOf<ForumSort>()
    var timeLine = mutableStateListOf<TimeLine>()

    init {
        // 初始化数据
        initHash()
        getAllFav()
        initTimeLine()
        initForum()
        isThread.value = mainForumId.value == "999"
    }

    fun initTimeLine() {
        viewModelScope.launch {
            while (true) {
                try {
                    val data = withContext(Dispatchers.IO) {
                        Http_request.get<TimeLine>("getTimelineList", cookie.value?.cookie ?: "")
                    } ?: emptyList()
                    timeLine.addAll(data)
                    break
                } catch (e: Exception) {
                    Log.e("时间线获取", e.toString())
                }
            }
        }
    }

    fun initForum() {
        viewModelScope.launch {
            while (true) {
                try {
                    val data = withContext(Dispatchers.IO) {
                        Http_request.get<ForumSort>("getForumList", cookie.value?.cookie ?: "")
                    } ?: emptyList()
                    forumList.addAll(data)
                    break
                } catch (e: Exception) {
                    Log.e("版块获取", e.toString())
                }
            }
        }
    }

    val imgList = mutableStateMapOf<String, preLoadImage>()

    // initial request
    fun loadData(context: Context) {
        isRefreshing.value = true
        viewModelScope.launch {
            try {
                onError.value = false
                if (_dataState.isEmpty()) {
                    val data = withContext(Dispatchers.IO) {
                        Http_request.get<Thread>(
                            if (!isThread.value) "showf?id=${forumId.value}" else "timeline?id=${forumId.value}",
                            cookie.value?.cookie ?: ""
                        )
                    }
                    data?.forEach {
                        if (it.img.isNotNull()) {
                            val key = Url.IMG_THUMB_QA + it.img + it.ext
                            if (!imgList.containsKey(key)) {
                                val request = ImageRequest.Builder(context)
                                    .data(key)
                                    .build()
                                val bitmap = imageLoader.execute(request).image?.toBitmap()
                                if (bitmap != null) {
                                    imgList[key] = preLoadImage(bitmap.height, bitmap.width)
                                }
                            }
                        }
                    }
                    withContext(Dispatchers.Main) {
                        isInitialLoad.value = false
                        _dataState.addAll(data ?: emptyList())
                    }
                }
            } catch (e: Exception) {
                onError.value = true
                _dataState.clear()
                Log.d("main_page", "加载失败")
            }
            isRefreshing.value = false
        }
    }

    val mainPageListState = LazyListState()


    var isInitialLoad = mutableStateOf(true)

    // refresh data
    fun refreshData(showIcon: Boolean, name: String) {
        viewModelScope.launch {
            try {
                if (showIcon) {
                    isRefreshing.value = true
                }
                onError.value = false
                val newData = withContext(Dispatchers.IO) {
                    Http_request.get<Thread>(
                        if (!isThread.value) "showf?id=${forumId.value}" else "timeline?id=${forumId.value}",
                        cookie.value?.cookie ?: ""
                    )
                }
                withContext(Dispatchers.Main) {
                    isInitialLoad.value = false
                    _dataState.clear()
                    _dataState.addAll(newData ?: emptyList())
                    resetPageId()
                }
                if (showIcon) {
                    isRefreshing.value = false
                }
            } catch (e: Exception) {
                onError.value = true
                _dataState.clear()
                Log.d("main_page", "刷新失败: ${e.message}")
            }
        }
    }


    fun changeForumId(title: String, showIcon: Boolean) {
        if (timeLine.any { it.name == title }) {
            isThread.value = true
            forumId.value = timeLine.find { it.name == title }?.id.toString()
        } else {
            isThread.value = false
            forumId.value = forumList.flatMap { it.forums }
                .find { it.name == title }?.id ?: ""

        }
        Log.d("版块id测试", forumId.value)
        changeTitle(title)
        refreshData(showIcon, title)
    }

    fun changeTitle(t: String) {
        if (title.value != t) {
            title.value = t
        }
    }

//    fun changeForumIdDialogVisible() {
//        isChangeForumIdDialogVisible.value = !isChangeForumIdDialogVisible.value
//    }

    fun loadMore(onComplete: () -> Unit,context: Context) {
        Log.d("main_page加载第${pageId.value}测试", "触发")
        viewModelScope.launch {
            pageId.value++
            try {
                val newData = withContext(Dispatchers.IO) {
                    Http_request.get<Thread>(
                        if (!isThread.value) "showf?id=${forumId.value}&page=${pageId.value}"
                        else "timeline?id=${forumId.value}&page=${pageId.value}",
                        cookie.value?.cookie ?: ""
                    )
                }
                newData?.forEach {
                    if (it.img.isNotNull()) {
                        val key = Url.IMG_THUMB_QA + it.img + it.ext
                        if (!imgList.containsKey(key)) {
                            val request = ImageRequest.Builder(context)
                                .data(key)
                                .build()
                            val bitmap = imageLoader.execute(request).image?.toBitmap()
                            if (bitmap != null) {
                                imgList[key] = preLoadImage(bitmap.height, bitmap.width)
                            }
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    isInitialLoad.value = true
                    _dataState.addAll(newData!!)
                }
            } catch (e: Exception) {
                Log.e("loadMore", "请求失败: ${e.message}", e)
                pageId.value--
            }
            onComplete()
        }
    }


    fun resetPageId() {
        pageId.value = 1
    }


    fun postThread(content: String, fid: String, cookie: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Http_request.postThread(content, fid.toInt(), cookie)
            }
        }
    }
}
package com.dech53.dao_yu.viewmodels

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dech53.dao_yu.dao.CookieDao
import com.dech53.dao_yu.dao.FavoriteDao
import com.dech53.dao_yu.utils.Http_request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.dech53.dao_yu.models.*
import kotlinx.coroutines.flow.MutableStateFlow

class MainPage_ViewModel(private val cookieDao: CookieDao, private val favDao: FavoriteDao) :
    ViewModel() {
    private val _dataState = mutableStateListOf<Thread>()
    val dataState: List<Thread> get() = _dataState


    var isThread = mutableStateOf(false)
        private set

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
            favDao.insert(fav)
        }
    }

    fun hasId(id: Int): Boolean {
        return favData.value.any { it.id == id.toString() }
    }

    var favData = MutableStateFlow(emptyList<Favorite>())

    fun getAllFav() {
        viewModelScope.launch {
            favDao.getAll().collect { favs ->
                favData.value = favs
            }
        }
    }

    fun deleteFav(fav: Favorite) {
        viewModelScope.launch {
            favDao.delete(fav)
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

    var cookie = mutableStateOf<Cookie?>(null)
    fun initHash() {
        viewModelScope.launch(Dispatchers.IO) {
            val hash = cookieDao.getHashToVerify()
            withContext(Dispatchers.Main) {
                cookie.value = hash
            }
        }
    }

    init {
//        initHash()
//        getAllFav()
        isThread.value = mainForumId.value == "999"
    }

    // initial request
    fun loadData() {
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
                    withContext(Dispatchers.Main) {
                        isInitialLoad.value = false
                        _dataState.addAll(data?: emptyList())
                    }
                }
            } catch (e: Exception) {
                onError.value = true
                Log.d("main_page", "加载失败")
            }
            isRefreshing.value = false
        }
    }

    val mainPageListState = LazyListState()


    var isInitialLoad = mutableStateOf(true)

    // refresh data
    fun refreshData(showIcon: Boolean) {
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
                    _dataState.addAll(newData?: emptyList())
                    resetPageId()
                }
                if (showIcon) {
                    isRefreshing.value = false
                }
            } catch (e: Exception) {
                onError.value = true
                Log.d("main_page", "刷新失败: ${e.message}")
            }
        }
    }


    fun changeForumId(id: String, showIcon: Boolean,categoryId:String = "") {
        if (id in setOf("1", "2", "3") && categoryId == "999") isThread.value = true
        else isThread.value = false
        if (forumId.value != id || forumId.value in setOf("2", "3")) {
            forumId.value = id
            refreshData(showIcon)
        }
    }

    fun changeTitle(t: String) {
        if (title.value != t) {
            title.value = t
        }
    }

//    fun changeForumIdDialogVisible() {
//        isChangeForumIdDialogVisible.value = !isChangeForumIdDialogVisible.value
//    }

    fun loadMore(onComplete:()->Unit) {
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
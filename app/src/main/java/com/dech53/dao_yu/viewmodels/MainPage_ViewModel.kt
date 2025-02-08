package com.dech53.dao_yu.viewmodels

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.dech53.dao_yu.dao.CookieDao
import com.dech53.dao_yu.dao.FavoriteDao
import com.dech53.dao_yu.utils.Http_request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.dech53.dao_yu.models.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MainPage_ViewModel(private val cookieDao: CookieDao, private val favDao: FavoriteDao) :
    ViewModel() {
    private val _dataState = mutableStateOf<List<Thread>?>(null)
    val dataState: State<List<Thread>?> = _dataState

    var isThread = mutableStateOf(false)
        private set

    var pageId = mutableStateOf(1)
        private set

    var isRefreshing = mutableStateOf(false)
        private set

    //TODO add dialog to change page id
    var forumId = mutableStateOf("53")

    var title = mutableStateOf("婆罗门一")

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
        initHash()
        getAllFav()
    }

    // initial request
    fun loadData() {
        viewModelScope.launch {
            try {
                onError.value = false
                if (_dataState.value == null) {
                    val data = withContext(Dispatchers.IO) {
                        Http_request.get<Thread>(
                            if (!isThread.value) "showf?id=${forumId.value}" else "timeline?id=${forumId.value}",
                            cookie.value?.cookie ?: ""
                        )
                    }
                    withContext(Dispatchers.Main) {
                        isInitialLoad.value = false
                        _dataState.value = data

                    }
                }
            } catch (e: Exception) {
                onError.value = true
                Log.d("main_page", "加载失败")
                Log.d("main_page datastate", dataState.value.toString())
                Log.d("main_page onError", onError.value.toString())
            }
        }
    }

    var isInitialLoad = mutableStateOf(true)

    // refresh data
    fun refreshData(showIcon: Boolean, lazyListState: LazyListState?) {
        viewModelScope.launch {
            try {
                if (showIcon) {
                    isRefreshing.value = true
                }
                onError.value = false
                delay(100)
                _dataState.value = null
                val newData = withContext(Dispatchers.IO) {
                    Http_request.get<Thread>(
                        if (!isThread.value) "showf?id=${forumId.value}" else "timeline?id=${forumId.value}",
                        cookie.value?.cookie ?: ""
                    )
                }

                withContext(Dispatchers.Main) {
                    isInitialLoad.value = false
                    _dataState.value = newData
                }
                resetPageId()
                if (showIcon) {
                    isRefreshing.value = false
                }
            } catch (e: Exception) {
                onError.value = true
                _dataState.value = null
            }
        }
    }

    fun changeForumId(id: String, showIcon: Boolean) {
        if (id in setOf("1", "2", "3")) isThread.value = true
        else isThread.value = false
        if (forumId.value != id) {
            forumId.value = id
            refreshData(showIcon, null)
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

    fun loadMore() {
        Log.d("main_page加载第${pageId.value}测试", "触发")
        viewModelScope.launch {
            pageId.value++
            val newData = withContext(Dispatchers.IO) {
                Http_request.get<Thread>(
                    if (!isThread.value) "showf?id=${forumId.value}&page=${pageId.value}" else "timeline?id=${forumId.value}&page=${pageId.value}",
                    cookie.value?.cookie ?: ""
                )
            }
            withContext(Dispatchers.Main) {
                isInitialLoad.value = true
                _dataState.value = (_dataState.value.orEmpty() + newData!!)
            }
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
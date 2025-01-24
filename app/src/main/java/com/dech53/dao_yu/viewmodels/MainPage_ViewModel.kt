package com.dech53.dao_yu.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dech53.dao_yu.utils.Http_request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.dech53.dao_yu.models.*

class MainPage_ViewModel : ViewModel() {
    private val _dataState = mutableStateOf<List<Thread>?>(null)
    val dataState: State<List<Thread>?> = _dataState

    var pageId = mutableStateOf(1)
        private set

    var isRefreshing = mutableStateOf(false)
        private set

    //TODO add dialog to change page id
    var forumId = mutableStateOf("53")
        private set

    var title = mutableStateOf("婆罗门一")
        private set

    var topBarState = mutableStateOf(false)
        private set

    var onError = mutableStateOf(false)
        private set

//    var isChangeForumIdDialogVisible = mutableStateOf(false)
//        private set

    fun changeTopBarState(state: Boolean) {
        if (topBarState.value != state)
            topBarState.value = state
    }

    // initial request
    fun loadData() {
        viewModelScope.launch {
            try {
                onError.value = false
                if (_dataState.value == null) {
                    val data = withContext(Dispatchers.IO) {
                        Http_request.get<Thread>("showf?id=${forumId.value}")
                    }
                    _dataState.value = data
                }
            } catch (e: Exception) {
                onError.value = true
                Log.d("main_page", "加载失败")
                Log.d("main_page datastate", dataState.value.toString())
                Log.d("main_page onError", onError.value.toString())
            }
        }
    }

    // refresh data
    fun refreshData(showIcon: Boolean) {
        viewModelScope.launch {
            try {
                if (showIcon){
                    isRefreshing.value = true
                }
                val newData = withContext(Dispatchers.IO) {
                    Http_request.get<Thread>("showf?id=${forumId.value}")
                }
                _dataState.value = newData
                resetPageId()
                if (showIcon){
                    isRefreshing.value = false
                }
            } catch (e: Exception) {
                onError.value = true
                _dataState.value = null
            }
        }
    }

    fun changeForumId(id: String, showIcon: Boolean) {
        if (forumId.value != id) {
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

    fun loadMore() {
        Log.d("main_page加载第${pageId.value}测试", "触发")
        viewModelScope.launch {
            pageId.value++
            val newData = withContext(Dispatchers.IO) {
                Http_request.get<Thread>("showf?id=${forumId.value}&page=${pageId.value}")
            }
            _dataState.value = (_dataState.value.orEmpty() + newData!!)
        }
    }

    fun resetPageId() {
        pageId.value = 1
    }
}
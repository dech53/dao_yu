package com.dech53.dao_yu.viewmodels

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

    var isRefreshing = mutableStateOf(false)
        private set

    //TODO add dialog to change page id
    var forumId = mutableStateOf("53")
        private set

//    var isChangeForumIdDialogVisible = mutableStateOf(false)
//        private set

    // initial request
    fun loadData() {
        viewModelScope.launch {
            if (_dataState.value == null) {
                val data = withContext(Dispatchers.IO) {
                    Http_request.get<Thread>("showf?id=${forumId.value}")
                }
                _dataState.value = data
            }
        }
    }

    // refresh data
    fun refreshData() {
        viewModelScope.launch {
            isRefreshing.value = true
            val newData = withContext(Dispatchers.IO) {
                Http_request.get<Thread>("showf?id=${forumId.value}")
            }
            _dataState.value = newData
            isRefreshing.value = false
        }
    }

    fun changeForumId(id: String) {
        if (forumId.value != id) {
            forumId.value = id
            refreshData()
        }
    }

//    fun changeForumIdDialogVisible() {
//        isChangeForumIdDialogVisible.value = !isChangeForumIdDialogVisible.value
//    }
}
package com.dech53.dao_yu.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.dech53.dao_yu.models.Thread
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dech53.dao_yu.models.Reply
import com.dech53.dao_yu.models.toReplies
import com.dech53.dao_yu.models.toReply
import com.dech53.dao_yu.utils.Http_request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ThreadInfoView_ViewModel : ViewModel() {
    private val _threadInfo = mutableStateOf<List<Reply>?>(null)
    var threadInfo: State<List<Reply>?> = _threadInfo

    var pageId = mutableStateOf(1)
        private set

    var isRefreshing = mutableStateOf(false)
        private set

    var threadId = mutableStateOf("")
        private set

    fun refreshData() {
        viewModelScope.launch {
            isRefreshing.value = true
            resetPageId()
            val newData = withContext(Dispatchers.IO) {
                Http_request.getThreadInfo("thread?id=${threadId.value}")
            }
            _threadInfo.value = newData!!.toReplies()
            isRefreshing.value = false
        }
    }

    fun getThreadId(id: String) {
        threadId.value = id
        refreshData()
    }

    fun clearThreadInfo() {
        _threadInfo.value = null
    }

    fun loadMore() {
        Log.d("加载第${pageId.value}测试", "触发")
        viewModelScope.launch {
            pageId.value++
            val newData = withContext(Dispatchers.IO) {
                Http_request.getThreadInfo("thread?id=${threadId.value}&page=${pageId.value}")
            }

            _threadInfo.value = (_threadInfo.value.orEmpty() + newData!!.toReplies().drop(1))
        }
    }

    fun resetPageId() {
        pageId.value = 1
    }
}
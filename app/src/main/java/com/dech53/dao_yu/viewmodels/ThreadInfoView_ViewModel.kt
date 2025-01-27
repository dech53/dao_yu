package com.dech53.dao_yu.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dech53.dao_yu.models.QuoteRef
import com.dech53.dao_yu.models.Reply
import com.dech53.dao_yu.models.toReplies
import com.dech53.dao_yu.utils.Http_request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

    var isIndicatorVisible = mutableStateOf(false)
        private set

    var canUseRequest = mutableStateOf(true)
        private set

    var onError = mutableStateOf(false)
        private set

    var replyCount = mutableStateOf(0)
        private set

    var maxPage = mutableStateOf(0)
        private set

    var contentContext = mutableStateMapOf<String, QuoteRef>()

    var hash = mutableStateOf("")

    fun refreshData() {
        viewModelScope.launch {
            try {
                onError.value = false
                isRefreshing.value = true
                resetPageId()
                val newData = withContext(Dispatchers.IO) {
                    Http_request.getThreadInfo("thread?id=${threadId.value}", hash.value)
                }
                replyCount.value = newData!!.ReplyCount
                maxPage.value =
                    if (replyCount.value % 20 == 0) replyCount.value / 20 else replyCount.value / 20 + 1
                Log.d("能加载的最多页数", maxPage.value.toString())
                _threadInfo.value = newData!!.toReplies()
                isRefreshing.value = false
            } catch (e: Exception) {
                onError.value = true
            }
        }
    }

    private val requestMutex = Mutex()
    fun getRef(id: String) {
        viewModelScope.launch {
            try {
                Log.d("contains test",contentContext.contains(id).toString())
                if (!contentContext.contains(id)){
                    requestMutex.withLock {
                        contentContext[id] =
                            withContext(Dispatchers.IO) {
                                Http_request.getRef(id, hash.value)!!
                            }
                    }
                }
            } catch (e: Exception) {
                Log.e("ref esception", e.message.toString())
            }
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
        if (pageId.value > maxPage.value) canUseRequest.value = false
        if (canUseRequest.value) {
            Log.d("thread_page加载第${pageId.value}测试", "触发")
            isIndicatorVisible.value = true
            viewModelScope.launch {
                pageId.value++
                val newData = withContext(Dispatchers.IO) {
                    Http_request.getThreadInfo(
                        "thread?id=${threadId.value}&page=${pageId.value}",
                        hash.value
                    )
                }
                Log.d("新获取的数据", newData!!.toReplies().drop(1).size.toString())
                _threadInfo.value = (_threadInfo.value.orEmpty() + newData!!.toReplies().drop(1))
            }
            isIndicatorVisible.value = false
        } else {
            Log.d("thread_page加载第${pageId.value}测试", "未触发")
        }

    }

    fun resetPageId() {
        pageId.value = 1
    }
}
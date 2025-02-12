package com.dech53.dao_yu.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dech53.dao_yu.dao.CookieDao
import com.dech53.dao_yu.dao.FavoriteDao
import com.dech53.dao_yu.models.Cookie
import com.dech53.dao_yu.models.Favorite
import com.dech53.dao_yu.models.QuoteRef
import com.dech53.dao_yu.models.Reply
import com.dech53.dao_yu.models.toReplies
import com.dech53.dao_yu.utils.Http_request
import com.dech53.dao_yu.utils.JudgeHtmlResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class ThreadInfoView_ViewModel(private val cookieDao: CookieDao, private val favDao: FavoriteDao) :
    ViewModel() {
    private val _threadInfo = mutableStateOf<List<Reply>?>(null)
    var threadInfo: State<List<Reply>?> = _threadInfo

    fun deleteFav(fav: Favorite) {
        viewModelScope.launch {
            favDao.delete(fav)
        }
    }

    fun addFave(fav: Favorite) {
        viewModelScope.launch {
            favDao.insert(fav)
        }
    }

    var isFaved = mutableStateOf(false)

    var pageId = mutableStateOf(1)
        private set

    private val _textFieldValue = mutableStateOf(TextFieldValue())
    val textFieldValue: State<TextFieldValue> get() = _textFieldValue

    fun updateTextFieldValue(newTextFieldValue: TextFieldValue) {
        _textFieldValue.value = newTextFieldValue
    }

    fun appendToTextField(emojiOrReference: String) {
        val currentText = _textFieldValue.value.text
        val newText = currentText + emojiOrReference
        _textFieldValue.value = TextFieldValue(newText, TextRange(newText.length, newText.length))
    }

    var cookieList = MutableStateFlow<List<Cookie>>(emptyList())
    fun initCookieList() {
        viewModelScope.launch {
            cookieDao.getAll().collect { cookies ->
                cookieList.value = cookies
            }
        }
    }

    init {
        initCookieList()
    }

    var IsSending = mutableStateOf(false)

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
                _threadInfo.value = newData.toReplies()
                newData.toReplies().map { it.toQuoteRef() }.forEach { quoteRef ->  contentContext[quoteRef.id.toString()] = quoteRef }
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
                Log.d("contains test", contentContext.contains(id).toString())
                if (!contentContext.contains(id)) {
                    requestMutex.withLock {
                        contentContext[id] =
                            withContext(Dispatchers.IO) {
                                Http_request.getRef(id, hash.value)!!
                            }
                    }
                }
            } catch (e: Exception) {

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

    fun loadMore(direction: String, skipPage: Int? = null, onComplete: () -> Unit = {}) {
        if (direction == "F") {
            if (pageId.value > maxPage.value) {
                canUseRequest.value = false
            } else {
                canUseRequest.value = true
            }
            if (canUseRequest.value) {
                Log.d("thread_page加载第${pageId.value}测试", "触发")
                isIndicatorVisible.value = true
                viewModelScope.launch {
                    pageId.value++
                    try {
                        val newData = withContext(Dispatchers.IO) {
                            Http_request.getThreadInfo(
                                "thread?id=${threadId.value}&page=${pageId.value}",
                                hash.value
                            )
                        }
                        newData?.let {
                            Log.d("新获取的数据", it.toReplies().drop(1).size.toString())
                            _threadInfo.value =
                                (_threadInfo.value.orEmpty() + it.toReplies().drop(1))
                            newData.toReplies().map { it.toQuoteRef() }.forEach { quoteRef ->  contentContext[quoteRef.id.toString()] = quoteRef }
                        } ?: run {
                            Log.e("loadMore", "获取数据失败，服务器返回 null")
                            pageId.value--
                        }
                    } catch (e: Exception) {
                        Log.e("loadMore", "请求失败: ${e.message}", e)
                        pageId.value--
                    } finally {
                        isIndicatorVisible.value = false
                        onComplete()
                    }
                }
            } else {
                Log.d("thread_page加载第${pageId.value}测试", "未触发")
            }
        } else if (direction == "B") {
            isIndicatorVisible.value = true
            viewModelScope.launch {
                try {
                    val newData = withContext(Dispatchers.IO) {
                        Http_request.getThreadInfo(
                            "thread?id=${threadId.value}&page=${skipPage}",
                            hash.value
                        )
                    }
                    newData?.let {
                        Log.d("新获取的数据", it.toReplies().drop(1).size.toString())
                        _threadInfo.value =
                            _threadInfo.value.orEmpty().toMutableList().let { list ->
                                listOf(list.first()) + it.toReplies().drop(1)
                            }
                        newData.toReplies().map { it.toQuoteRef() }.forEach { quoteRef ->  contentContext[quoteRef.id.toString()] = quoteRef }
                    } ?: Log.e("loadMore", "获取数据失败，服务器返回 null")
                } catch (e: Exception) {
                    Log.e("loadMore", "请求失败: ${e.message}", e)
                } finally {
                    isIndicatorVisible.value = false
                    onComplete()
                }
            }
        } else {
            Log.d("thread_page加载第${pageId.value}测试", "未触发")
        }
    }


    fun resetPageId() {
        pageId.value = 1
    }


    fun replyThread(
        content: String,
        resto: String,
        cookie: String,
        img: Uri? = null,
        context: Context,
        onSuccess: (Boolean) -> Unit
    ) {
        IsSending.value = true
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                Http_request.replyThread(
                    content, resto, cookie, img, context
                )
            }
            IsSending.value = false
            onSuccess(false)
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
        }
    }
}
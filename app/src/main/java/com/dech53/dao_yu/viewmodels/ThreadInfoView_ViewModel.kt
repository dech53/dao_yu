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
import com.dech53.dao_yu.dao.DataBaseRepository
import com.dech53.dao_yu.models.Cookie
import com.dech53.dao_yu.models.Favorite
import com.dech53.dao_yu.models.QuoteRef
import com.dech53.dao_yu.models.Reply
import com.dech53.dao_yu.models.preLoadImage
import com.dech53.dao_yu.models.toReplies
import com.dech53.dao_yu.utils.Http_request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class ThreadInfoView_ViewModel(private val repo:DataBaseRepository = Injekt.get()) :
    ViewModel() {
    private val _threadInfo = mutableStateOf<Set<Reply>>(LinkedHashSet())
    var threadInfo: State<Set<Reply>> = _threadInfo

    fun deleteFav(fav: Favorite) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repo.deleteFav(fav)
            }
        }
    }


    val imgList = mutableStateMapOf<String, preLoadImage>()

    fun addFave(fav: Favorite) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repo.insertFav(fav)
            }
        }
    }

    var isFaved = mutableStateOf(false)

    var fid = mutableStateOf("")

    var isLoadingBackward = mutableStateOf(false)

    var pageId = mutableStateOf(1)
        private set

    fun getPageId(): Int {
        return pageId.value
    }

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
            withContext(Dispatchers.IO) {
                repo.getFlowOfCookie().collect { cookies ->
                    cookieList.value = cookies
                }
            }
        }
    }
    init{
        initCookieList()
    }

    var tipsCount = mutableStateOf(0)

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

    var skipPage = mutableStateOf(1)
    var isRaw = mutableStateOf(false)

    var contentContext = mutableStateMapOf<String, QuoteRef>()

    var hash = mutableStateOf("")

    fun refreshData() {
        isRefreshing.value = true
        viewModelScope.launch {
            try {
                onError.value = false
                resetPageId()
                val newData = withContext(Dispatchers.IO) {
                    Http_request.getThreadInfo("thread?id=${threadId.value}", hash.value)
                }
                replyCount.value = newData!!.ReplyCount
                maxPage.value =
                    if (replyCount.value % 19 == 0) replyCount.value / 19 else replyCount.value / 19 + 1
                Log.d("能加载的最多页数", maxPage.value.toString())
                Log.d("回复数量", replyCount.value.toString())
                _threadInfo.value = LinkedHashSet(newData.toReplies())
                newData.toReplies().map { it.toQuoteRef() }
                    .forEach { quoteRef ->
                        contentContext[quoteRef.id.toString()] = quoteRef
                        if (quoteRef.id.toInt() == 9999999){
                            tipsCount.value++
                        }
                    }
                fid.value = _threadInfo.value.firstOrNull()?.fid?.toString() ?: ""
            } catch (e: Exception) {
                onError.value = true
            }
            isRefreshing.value = false
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
        _threadInfo.value = LinkedHashSet()
    }

    fun loadMore(direction: String, onComplete: () -> Unit = {}, addPage: Boolean = true) {
        //向下加载更多数据
        if (direction == "F") {
            if (pageId.value > maxPage.value) {
                canUseRequest.value = false
            } else {
                canUseRequest.value = true
            }
            if (!addPage) {
                canUseRequest.value = true
            }
            if (canUseRequest.value) {
                Log.d("thread_page加载第${pageId.value}测试", "触发")
                isIndicatorVisible.value = true
                viewModelScope.launch {
                    try {
                        if (addPage) {
                            pageId.value++
                        } else {
                            pageId.value--
                        }
                        val newData = withContext(Dispatchers.IO) {
                            Http_request.getThreadInfo(
                                "thread?id=${threadId.value}&page=${pageId.value}",
                                hash.value
                            )
                        }
                        newData?.let {
                            Log.d("新获取的数据", it.toReplies().drop(1).size.toString())
                            if (it.toReplies().drop(1).any { reply -> reply.id == 9999999 }) {
                                tipsCount.value++
                            }
                            val updatedThreadInfo = _threadInfo.value.toMutableSet().apply {
                                addAll(it.toReplies().drop(1))
                            }
                            _threadInfo.value = updatedThreadInfo
                            newData.toReplies().map { it.toQuoteRef() }.forEach { quoteRef ->
                                contentContext[quoteRef.id.toString()] = quoteRef
                            }
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
            //向上加载更多数据
        } else if (direction == "S") {
            isIndicatorVisible.value = true
            viewModelScope.launch {
                try {
                    val newData = withContext(Dispatchers.IO) {
                        Http_request.getThreadInfo(
                            "thread?id=${threadId.value}&page=${skipPage.value}",
                            hash.value
                        )
                    }
                    newData?.let {
                        if (it.toReplies().drop(1).any { reply -> reply.id == 9999999 }) {
                            tipsCount.value++
                        }
                        Log.d("新获取的数据", it.toReplies().drop(1).size.toString())
                        _threadInfo.value = _threadInfo.value.orEmpty().let { set ->
                            val firstElement = set.firstOrNull()
                            val newReplies = it.toReplies().drop(1)
                            (listOfNotNull(firstElement) + newReplies).toMutableSet()
                        }
                        newData.toReplies().map { it.toQuoteRef() }.forEach { quoteRef ->
                            contentContext[quoteRef.id.toString()] = quoteRef
                        }
                    } ?: Log.e("loadMore", "获取数据失败，服务器返回 null")
                } catch (e: Exception) {
                    Log.e("loadMore", "请求失败: ${e.message}", e)
                } finally {
                    isIndicatorVisible.value = false
                    onComplete()
                }
            }
        } else if (direction == "B") {
            viewModelScope.launch {
                skipPage.value--
                try {
                    val newData = withContext(Dispatchers.IO) {
                        Http_request.getThreadInfo(
                            "thread?id=${threadId.value}&page=${skipPage.value}",
                            hash.value
                        )
                    }
                    newData?.let {
                        if (it.toReplies().drop(1).any { reply -> reply.id == 9999999 }) {
                            tipsCount.value++
                        }
                        Log.d("新获取的数据", it.toReplies().drop(1).size.toString())
                        val threadInfoList = _threadInfo.value.orEmpty().toMutableList()
                        if (threadInfoList.isNotEmpty()) {
                            threadInfoList.addAll(1, it.toReplies().drop(1))
                        } else {
                            threadInfoList.addAll(it.toReplies().drop(1))
                        }
                        _threadInfo.value = threadInfoList.toMutableSet()
                        newData.toReplies().map { it.toQuoteRef() }.forEach { quoteRef ->
                            contentContext[quoteRef.id.toString()] = quoteRef
                        }
                    } ?: Log.e("loadMore", "获取数据失败，服务器返回 null")
                } catch (e: Exception) {
                    Log.e("loadMore", "请求失败: ${e.message}", e)
                    skipPage.value++
                } finally {
                    onComplete()
                }
                isLoadingBackward.value = false
            }
        } else {
            Log.d("thread_page加载第${pageId.value}测试", "未触发")
        }
    }


    fun resetPageId() {
        pageId.value = 1
    }


    fun replyThread(
        name: String = "无名氏",
        title: String = "无标题",
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
                    name, title, content, resto, cookie, img, context
                )
            }
            IsSending.value = false
            onSuccess(false)
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
        }
    }
}
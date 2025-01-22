package com.dech53.dao_yu.viewmodels

import androidx.compose.runtime.mutableStateOf
import com.dech53.dao_yu.models.Thread
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dech53.dao_yu.utils.Http_request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ThreadInfoView_ViewModel : ViewModel() {
    private val _threadInfo = mutableStateOf<Thread?>(null)
    var threadInfo: State<Thread?> = _threadInfo

    var isRefreshing = mutableStateOf(false)
        private set

    var threadId = mutableStateOf("")
        private set

    fun refreshData() {
        viewModelScope.launch {
            isRefreshing.value = true
            val newData = withContext(Dispatchers.IO) {
                Http_request.getThreadInfo("thread?id=${threadId.value}")
            }
            _threadInfo.value = newData
            isRefreshing.value = false
        }
    }

    fun getThreadId(id: String) {
        threadId.value = id
        refreshData()
    }
    fun clearThreadInfo(){
        _threadInfo.value = null
    }

}
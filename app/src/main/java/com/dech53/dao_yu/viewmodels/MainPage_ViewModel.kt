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
import kotlinx.coroutines.delay

class MainPage_ViewModel:ViewModel(){
    private val _dataState = mutableStateOf<List<Thread>?>(null)
    val dataState: State<List<Thread>?> = _dataState

    var isRefreshing = mutableStateOf(false)
        private set

    // 初始化数据
    fun loadData() {
        viewModelScope.launch {
            if (_dataState.value == null) { // 避免重复请求
                val data = withContext(Dispatchers.IO) {
                    Http_request.get<Thread>("http://192.168.1.4:8080/json")
                }
                _dataState.value = data
            }
        }
    }

    // 刷新数据
    fun refreshData() {
        viewModelScope.launch {
            isRefreshing.value = true
            val newData = withContext(Dispatchers.IO) {
                Http_request.get<Thread>("http://192.168.1.4:8080/json")
            }
            _dataState.value = newData
            isRefreshing.value = false
        }
    }
}
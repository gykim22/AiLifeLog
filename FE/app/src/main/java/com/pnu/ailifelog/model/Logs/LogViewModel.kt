package com.pnu.ailifelog.model.Logs

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateRecordViewModel @Inject constructor(
    private val repository: CreateRecordRepository
) : ViewModel() {

    /* 일기 작성 */
    private val _createResult = mutableStateOf<Result<CreateResponse>?>(null)
    val createResult: State<Result<CreateResponse>?> = _createResult

    /* 일기 조회 */
    private val _logPage = mutableStateOf<Result<LogPageResponse>?>(null)
    val logPage: State<Result<LogPageResponse>?> = _logPage

    /* 일기 삭제 */
    private val _logDeleteResult = mutableStateOf<Result<Unit>?>(null)
    val logDeleteResult: State<Result<Unit>?> = _logDeleteResult

    /* LLM 요약 요청 */
    private val _askResult = mutableStateOf<Result<String>?>(null)
    val askResult: State<Result<String>?> = _askResult

    /* 마이페이지 */
    private val _userInfo = mutableStateOf<Result<UserInfoResponse>?>(null)
    val userInfo: State<Result<UserInfoResponse>?> = _userInfo

    /* 회원 탈퇴 */
    private val _deleteResult = mutableStateOf<Result<Unit>?>(null)
    val deleteResult: State<Result<Unit>?> = _deleteResult


    fun createRecord(title: String, description: String, timestamp: String? = null) {
        viewModelScope.launch {
            val request = CreateRequest(title, description, timestamp)
            val result = repository.createRecord(request)
            _createResult.value = result
        }
    }

    fun clearCreateResult() {
        _createResult.value = null
    }

    fun fetchLogs(page: Int = 0, size: Int = 10, from: String? = null, to: String? = null) {
        viewModelScope.launch {
            _logPage.value = repository.getLogs(page, size, from, to)
        }
    }

    fun deleteLog(id: Long) {
        viewModelScope.launch {
            val result = repository.deleteLog(id)
            _logDeleteResult.value = result

            if (result.isSuccess) {
                fetchLogs()
            }
        }
    }

    fun ask(prompt: String) {
        viewModelScope.launch {
            _askResult.value = repository.ask(prompt)
        }
    }

    fun clearAskResult() {
        _askResult.value = null
    }


    fun fetchUserInfo() {
        viewModelScope.launch {
            _userInfo.value = repository.getUserInfo()
        }
    }

    fun deleteAccount(password: String) {
        viewModelScope.launch {
            _deleteResult.value = repository.deleteAccount(password)
        }
    }

    fun clearDeleteResult() {
        _deleteResult.value = null
    }
}
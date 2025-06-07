package com.pnu.ailifelog.model.SignUp

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _signUpData = mutableStateOf(SignUpData())
    val signUpData: State<SignUpData> = _signUpData


    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _accessToken = mutableStateOf("")
    val accessToken: State<String> = _accessToken

    /*값 업데이트*/

    fun updateloginId(id: String) {
        _signUpData.value = _signUpData.value.copy(username = id)
    }

    fun updatePassword(password: String) {
        _signUpData.value = _signUpData.value.copy(password = password)
    }

    /*값 읽기*/
    // 사용자 ID
    fun getloginId(): String = _signUpData.value.username

    // 비밀번호
    fun getPassword(): String = _signUpData.value.password

    fun logSignUpData(tag: String = "SignUpData") {
        val data = _signUpData.value
        Log.d(tag, """
        - userId: ${data.username}
        - password: ${data.password}
    """.trimIndent())
    }

    fun completeSignUp(
        onSuccess: (token: String) -> Unit,
        onFailure: (Throwable?) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = authRepository.sendSignUp(_signUpData.value)
                if (response != null) {
                    _accessToken.value = response.token
                    onSuccess(response.token)
                } else {
                    onFailure(Exception("회원가입 실패: 서버 응답 없음"))
                }
            } catch (e: Exception) {
                onFailure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(
        username: String,
        password: String,
        onSuccess: (token: String) -> Unit,
        onFailure: (Throwable?) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = authRepository.login(username, password)
                if (response != null) {
                    _accessToken.value = response.token
                    onSuccess(response.token)
                } else {
                    onFailure(Exception("로그인 실패: 서버 응답 없음"))
                }
            } catch (e: Exception) {
                onFailure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}




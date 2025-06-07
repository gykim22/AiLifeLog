package com.pnu.ailifelog.model.SignUp

import android.util.Log
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApi: ServerAuthAPI
) {
    suspend fun sendSignUp(data: SignUpData): SignUpResponse? {
        val request = SignUpRequest(
            username = data.username,
            password = data.password,
        )
        Log.d("SignUp", "Request 객체 생성 완료")

        return try {
            val response = authApi.signUp(request)
            if (response.isSuccessful) {
                response.body()
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("SignUp", "응답 실패 - 코드: ${response.code()}, 바디: $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e("SignUp", "네트워크 오류 발생", e)
            null
        }
    }

    suspend fun login(username: String, password: String): LoginResponse? {
        val request = LoginRequest(username, password)
        Log.d("Login", "로그인 요청 시작")

        return try {
            val response = authApi.login(request)
            if (response.isSuccessful) {
                response.body()
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("Login", "응답 실패 - 코드: ${response.code()}, 바디: $errorBody")
                null
            }
        } catch (e: Exception) {
            Log.e("Login", "네트워크 오류 발생", e)
            null
        }
    }
}
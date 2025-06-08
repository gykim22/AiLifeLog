package com.pnu.ailifelog.model.SignUp

data class SignUpData(
    val username: String = "",
    val password: String = "",
)

data class SignUpRequest(
    val username: String,
    val password: String,

)

data class SignUpResponse(
    val token: String,
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String
)
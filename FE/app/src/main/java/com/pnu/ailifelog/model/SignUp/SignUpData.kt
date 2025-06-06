package com.pnu.ailifelog.model.SignUp

data class SignUpData(
    val loginId: String = "",
    val password: String = "",

)

data class SignUpRequest(
    val loginId: String,
    val password: String,

)

data class SignUpResponse(
    val token: String,
)
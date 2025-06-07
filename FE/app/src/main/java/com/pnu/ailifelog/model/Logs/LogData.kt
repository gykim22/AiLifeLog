package com.pnu.ailifelog.model.Logs

/**
 * 일기 생성 요청
 * */
data class CreateRequest(
    val title: String,
    val description: String,
    val timestamp: String? = null  // 생략 가능
)

data class CreateResponse(
    val id: Long,
    val title: String,
    val description: String,
    val timestamp: String
)

/**
 * 일기 정보 수신
 * */
data class LogItem(
    val id: Long,
    val title: String,
    val description: String,
    val timestamp: String
)

data class LogPageResponse(
    val content: List<LogItem>,
    val totalPages: Int,
    val totalElements: Int,
    val number: Int,
    val size: Int,
    val first: Boolean,
    val last: Boolean,
    val empty: Boolean
)

/**
 * LLM 분석 요청
 * */
data class AskRequest(
    val prompt: String
)

data class AskResponse(
    val content: String
)


/**
 * 마이페이지 정보 요청
 * */
data class UserInfoResponse(
    val id: Long,
    val username: String
)

/**
 * 회원탈퇴
 * */
data class DeleteAccountRequest(
    val password: String
)
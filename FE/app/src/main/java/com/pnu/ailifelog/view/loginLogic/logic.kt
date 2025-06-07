package com.pnu.ailifelog.view.loginLogic

import androidx.compose.runtime.Composable

@Composable
fun containsSequentialNumbers(password: String, length: Int = 3): Boolean {
    if (password.length < length || password.isEmpty()) return true
    var checkDigit = ""
    for (char in password) {
        if (char.isDigit()) {
            checkDigit += char
            if (checkDigit.length >= length && isSequential(checkDigit))
                return true
        } else
            checkDigit = ""
    }
    return false
}

fun isSequential(substring: String): Boolean {
    val nums = substring.map { it - '0' }
    if (nums.zipWithNext().all { it.second - it.first == 1 }) return true
    if (nums.zipWithNext().all { it.second - it.first == -1 }) return true

    return false
}

/**
 * 사용자 IME 입력 타입을 설정하는 enum입니다.
 */
enum class InputType {
    TEXT, EMAIL, PASSWORD, NUMBER
}
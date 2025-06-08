package com.pnu.ailifelog.composable.Main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pnu.ailifelog.composable.HeightSpacer
import com.pnu.ailifelog.model.Logs.LogItem
import com.pnu.ailifelog.ui.theme.gray500
import com.pnu.ailifelog.ui.theme.pretendard
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun LogTextField(
    onTitleChanged: (String) -> Unit,
    onTextChanged: (String) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var textState by remember { mutableStateOf("") }
    var textHeight by remember { mutableStateOf(183.dp) }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE1E2E6), RoundedCornerShape(12.dp))
                .background(Color(0xFFFAFAFB), RoundedCornerShape(12.dp))
                .padding(top = 16.dp, bottom = 16.dp)
                .height(18.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = title,
                onValueChange = {
                    if (it.length <= 50) {
                        title = it
                        onTitleChanged(it)
                    }
                },
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = Color(0xFF686D78),
                    fontFamily = pretendard,
                    fontWeight = FontWeight(600)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .focusable(),
                cursorBrush = SolidColor(Color.Blue),
                keyboardOptions = KeyboardOptions.Default,
                singleLine = false,
                onTextLayout = {
                    val newHeight = (it.lineCount * 24).dp
                    textHeight = maxOf(183.dp, newHeight)
                },
                decorationBox = { innerTextField ->
                    if (title.isEmpty()) {
                        Text(
                            text = "제목을 입력해주세요",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color(0xFFAFB8C1),
                                fontFamily = pretendard,
                                fontWeight = FontWeight(500),
                                textAlign = TextAlign.Start,
                                platformStyle = PlatformTextStyle(includeFontPadding = false)
                            )
                        )
                    }
                    innerTextField()
                }
            )
        }

        HeightSpacer(10.dp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "입력해주신 정보를 바탕으로 AI가 분석해요!",
                fontSize = 12.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight(500),
                color = gray500
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${textState.length}/500",
                modifier = Modifier.padding(bottom = 10.dp),
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = pretendard,
                    fontWeight = FontWeight(500),
                    color = gray500,
                )
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE1E2E6), RoundedCornerShape(12.dp))
                .background(Color(0xFFFAFAFB), RoundedCornerShape(12.dp))
                .padding(top = 16.dp, bottom = 16.dp)
                .heightIn(min = 183.dp, max = Dp.Infinity), // 최소 183.dp, 자동 확장
            contentAlignment = Alignment.TopStart
        ) {
            BasicTextField(
                value = textState,
                onValueChange = {
                    if (it.length <= 500) {
                        textState = it
                        onTextChanged(it)
                    }
                },
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = Color(0xFF686D78),
                    fontFamily = pretendard,
                    fontWeight = FontWeight(600)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .focusable(),
                cursorBrush = SolidColor(Color.Blue),
                keyboardOptions = KeyboardOptions.Default,
                singleLine = false,
                onTextLayout = {
                    val newHeight = (it.lineCount * 24).dp
                    textHeight = maxOf(183.dp, newHeight)
                },
                decorationBox = { innerTextField ->
                    if (textState.isEmpty()) {
                        Text(
                            text = "일기를 작성해주세요",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color(0xFFAFB8C1),
                                fontFamily = pretendard,
                                fontWeight = FontWeight(500),
                                textAlign = TextAlign.Start,
                                platformStyle = PlatformTextStyle(includeFontPadding = false)
                            )
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

@Composable
fun AiTextField(
    onTextChanged: (String) -> Unit,
) {
    var text by remember { mutableStateOf("") }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE1E2E6), RoundedCornerShape(12.dp))
                .background(Color(0xFFFAFAFB), RoundedCornerShape(12.dp))
                .padding(top = 16.dp, bottom = 16.dp)
                .height(18.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = text,
                onValueChange = {
                    if (it.length <= 100) {
                        text = it
                        onTextChanged(it)
                    }
                },
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = Color(0xFF686D78),
                    fontFamily = pretendard,
                    fontWeight = FontWeight(600)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .focusable(),
                cursorBrush = SolidColor(Color.Blue),
                keyboardOptions = KeyboardOptions.Default,
                decorationBox = { innerTextField ->
                    if (text.isEmpty()) {
                        Text(
                            text = "내용을 입력해주세요",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color(0xFFAFB8C1),
                                fontFamily = pretendard,
                                fontWeight = FontWeight(500),
                                textAlign = TextAlign.Start,
                                platformStyle = PlatformTextStyle(includeFontPadding = false)
                            )
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

@Composable
fun LogCard(
    item: LogItem,
    onDeleteClick: (Long) -> Unit  // 삭제 클릭 시 호출될 콜백
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF7F8FA), shape = RoundedCornerShape(12.dp))
            .padding(start = 16.dp, bottom = 8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    fontSize = 18.sp,
                    fontFamily = pretendard,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "더보기",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("삭제") },
                            onClick = {
                                menuExpanded = false
                                onDeleteClick(item.id)
                            }
                        )
                    }
                }
            }

            Text(
                text = item.description,
                fontSize = 14.sp,
                fontFamily = pretendard,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formatTimestamp(item.timestamp),
                fontSize = 12.sp,
                fontFamily = pretendard,
                color = Color.Gray
            )
        }
    }
}

fun formatTimestamp(iso: String): String {
    return try {
        val parsed = LocalDateTime.parse(iso)
        parsed.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))
    } catch (e: Exception) {
        iso
    }
}
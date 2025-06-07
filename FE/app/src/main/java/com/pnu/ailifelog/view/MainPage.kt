package com.pnu.ailifelog.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pnu.ailifelog.NextButton
import com.pnu.ailifelog.composable.HeightSpacer
import com.pnu.ailifelog.composable.Main.AiTextField
import com.pnu.ailifelog.composable.Main.BottomBar
import com.pnu.ailifelog.composable.Main.BottomTab
import com.pnu.ailifelog.composable.Main.LogCard
import com.pnu.ailifelog.composable.Main.LogTextField
import com.pnu.ailifelog.composable.WhiteScreenModifier
import com.pnu.ailifelog.composable.buttonModifier
import com.pnu.ailifelog.model.Logs.CreateRecordViewModel
import com.pnu.ailifelog.ui.theme.backgroundColor
import com.pnu.ailifelog.ui.theme.pretendard
import com.pnu.ailifelog.ui.theme.primaryColor

@Composable
fun MainPage(
    navController: NavController,
    viewModel: CreateRecordViewModel
) {
    var selectedTab by remember { mutableStateOf(BottomTab.Home) }
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = true
        )
    }
    Scaffold(
        bottomBar = {
            BottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        containerColor = backgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
        ) {
            when (selectedTab) {
                BottomTab.Home -> HomePage(navController, viewModel) {
                    selectedTab = BottomTab.Log
                }

                BottomTab.Log -> LogPage(navController, viewModel)
                BottomTab.AI -> AiPage(navController, viewModel)
                BottomTab.MyPage -> MyPage(navController, viewModel)
            }
        }
    }
}

@Composable
fun HomePage(
    navController: NavController,
    viewModel: CreateRecordViewModel,
    onRecordComplete: () -> Unit
) {
    val context = LocalContext.current
    val createResult = viewModel.createResult.value
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    Column(
        modifier = WhiteScreenModifier.padding(horizontal = 24.dp)
    ) {
        HeightSpacer(30.dp)
        Text(
            text = "일기 작성하기",
            fontSize = 20.sp,
            fontFamily = pretendard,
            fontWeight = FontWeight(600),
            color = primaryColor,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
        )
        LogTextField(
            onTitleChanged = {
                title = it
            },
            onTextChanged = {
                description = it
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        NextButton(
            mModifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .height(58.dp),
            text = "작성하기",
            onClick = {
                viewModel.createRecord(
                    title = title,
                    description = description,
                    timestamp = null
                )
            }
        )
    }
    LaunchedEffect(createResult) {
        createResult?.onSuccess {
            Toast.makeText(context, "일기 작성 완료!", Toast.LENGTH_SHORT).show()
            viewModel.clearCreateResult()
            onRecordComplete()
        }?.onFailure {
            Toast.makeText(context, "작성 실패: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun LogPage(
    navController: NavController,
    viewModel: CreateRecordViewModel
) {
    val context = LocalContext.current
    val logResult by viewModel.logPage
    val logs = logResult?.getOrNull()?.content ?: emptyList()

    LaunchedEffect(Unit) {
        viewModel.fetchLogs(page = 0, size = 20)
    }

    Column(
        modifier = WhiteScreenModifier.padding(horizontal = 24.dp)
    ) {
        HeightSpacer(30.dp)
        Text(
            text = "작성한 일기 목록",
            fontSize = 20.sp,
            fontFamily = pretendard,
            fontWeight = FontWeight(600),
            color = primaryColor,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
        )

        if (logResult == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            logResult!!.onSuccess { page ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(page.content) { item ->
                        LogCard(item) { id ->
                            viewModel.deleteLog(id)
                        }
                    }
                }
            }.onFailure {
                Text("불러오기 실패: ${it.message}", color = Color.Red)
            }
        }
    }
}

@Composable
fun AiPage(
    navController: NavController,
    viewModel: CreateRecordViewModel
) {
    val context = LocalContext.current
    val askResult by viewModel.askResult
    var promptText by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(askResult) {
        askResult?.onSuccess {
            resultText = it
            isLoading = false
        }?.onFailure {
            Toast.makeText(context, "요청 실패: ${it.message}", Toast.LENGTH_SHORT).show()
            isLoading = false
        }
    }

    Column(
        modifier = WhiteScreenModifier.padding(horizontal = 24.dp),
    ) {
        HeightSpacer(30.dp)
        Text(
            text = "AI 요약 도우미",
            fontSize = 20.sp,
            fontFamily = pretendard,
            fontWeight = FontWeight(600),
            color = primaryColor,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
        )
        HeightSpacer(20.dp)
        AiTextField(
            onTextChanged = {
                promptText = it
            }
        )
        HeightSpacer(20.dp)
        resultText?.let {
            Text(
                text = it,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.DarkGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                    .padding(16.dp)
            )
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                if (promptText.isNotBlank()) {
                    isLoading = true
                    resultText = null
                    viewModel.ask(promptText)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .height(58.dp),
            shape = RoundedCornerShape(size = 16.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFF397CDB)),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "요약 요청",
                    fontFamily = pretendard,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(
                            includeFontPadding = false
                        )
                    )
                )
            }
        }
    }
}

@Composable
fun MyPage(
    navController: NavController,
    viewModel: CreateRecordViewModel
) {
    val context = LocalContext.current
    val userInfo = viewModel.userInfo.value
    val deleteResult = viewModel.deleteResult.value

    var showDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchUserInfo()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Box() {

        }
        HeightSpacer(30.dp)
        Text(
            text = "마이페이지",
            fontSize = 20.sp,
            fontFamily = pretendard,
            fontWeight = FontWeight(600),
            color = primaryColor,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
        )

        userInfo?.onSuccess {
            Text(
                text = "사용자명 : ${it.username}",
                fontSize = 18.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight(500),
                color = Color.Black,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
            )
        } ?: run {
            Text("사용자 정보를 불러오는 중...", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.weight(1f))
        NextButton(
            mModifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .height(58.dp),
            text = "탈퇴하기",
            onClick = {
                showDialog = true
            }
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("회원 탈퇴") },
            text = {
                Column {
                    Text("계정을 삭제하려면 비밀번호를 입력하세요.")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("비밀번호") },
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAccount(password)
                    showDialog = false
                    password = ""
                }) {
                    Text("탈퇴")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    LaunchedEffect(deleteResult) {
        deleteResult?.onSuccess {
            Toast.makeText(context, "회원 탈퇴 완료", Toast.LENGTH_SHORT).show()
            viewModel.clearDeleteResult()
            navController.navigate("LoginPage") {
                popUpTo(0) { inclusive = true }
            }
        }?.onFailure {
            Toast.makeText(context, "탈퇴 실패: ${it.message}", Toast.LENGTH_SHORT).show()
            viewModel.clearDeleteResult()
        }
    }
}

@Preview
@Composable
fun MainPagePreview() {
    MainPage(
        navController = NavController(LocalContext.current),
        viewModel = hiltViewModel()
    )
}
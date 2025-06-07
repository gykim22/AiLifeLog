package com.pnu.ailifelog.view

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.pnu.ailifelog.NextButton
import com.pnu.ailifelog.R
import com.pnu.ailifelog.composable.HeightSpacer
import com.pnu.ailifelog.composable.Login.BackButton
import com.pnu.ailifelog.composable.Login.DoubleLineTitleText
import com.pnu.ailifelog.composable.Login.Guideline
import com.pnu.ailifelog.composable.Login.HighlightingLine
import com.pnu.ailifelog.composable.Login.LoginTextField
import com.pnu.ailifelog.composable.WhiteScreenModifier
import com.pnu.ailifelog.composable.buttonModifier
import com.pnu.ailifelog.model.SignUp.SignUpViewModel
import com.pnu.ailifelog.model.SignUp.TokenManager
import com.pnu.ailifelog.ui.theme.gray100
import com.pnu.ailifelog.ui.theme.gray500
import com.pnu.ailifelog.ui.theme.gray800
import com.pnu.ailifelog.ui.theme.pretendard
import com.pnu.ailifelog.view.loginLogic.containsSequentialNumbers

@Composable
fun LoginPage(
    authViewModel: SignUpViewModel,
    navController: NavController
) {
    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val isLoading by authViewModel.isLoading


    Column(
        modifier = WhiteScreenModifier
    ) {
        HeightSpacer(130.dp)
        DoubleLineTitleText("안녕하세요", "AILifeLog입니다")
        HeightSpacer(12.dp)
        Text(
            text = "로그인 후 이용 부탁드려요!",
            fontSize = 16.sp,
            fontFamily = pretendard,
            fontWeight = FontWeight(500),
            color = gray500
        )
        HeightSpacer(40.dp)
        Text(
            text = "아이디",
            fontSize = 16.sp,
            fontFamily = pretendard,
            fontWeight = FontWeight(600),
            color = gray800
        )
        LoginTextField(placeholder = "아이디", onTextChanged = { id = it })
        HeightSpacer(20.dp)
        Text(
            text = "비밀번호",
            fontSize = 16.sp,
            fontFamily = pretendard,
            fontWeight = FontWeight(600),
            color = gray800
        )
        LoginTextField(placeholder = "비밀번호", onTextChanged = { password = it })
        Spacer(modifier = Modifier.weight(1f))
        NextButton(
            mModifier = buttonModifier,
            text = "로그인",
            buttonColor = Color(0xFF348ADF),
            onClick = {
                authViewModel.login(
                    username = id,
                    password = password,
                    onSuccess = { token ->
                        TokenManager.saveTokens(context, token)
                        Toast.makeText(context, "로그인 성공!", Toast.LENGTH_SHORT).show()
                        navController.navigate("MainPage") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onFailure = { e ->
                        Toast.makeText(context, "로그인 실패: ${e?.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        )
    }
}

/**
 * 아이디 입력 페이지입니다.
 * @param SignUpViewModel로, SignIn Data를 관리합니다.
 * @param navController Navigation Controller입니다.
 * @author 김기윤
 */
@Composable
fun IdPage(
    navController: NavController,
    viewModel: SignUpViewModel
) {
    var ID by remember { mutableStateOf("") }
    var isChecked by remember { mutableStateOf(false) }

    var containsEngNum by remember { mutableStateOf(false) }
    var containsKorean by remember { mutableStateOf(false) }
    var containsSpeical by remember { mutableStateOf(false) }
    val engNumRegex = Regex("[a-zA-Z0-9]")
    val koreanRegex = Regex("[\uAC00-\uD7AF\u1100-\u11FF\u3130-\u318F]+")
    val specialCharRegex = Regex("[!@#$%^&*(),.?\":{}|<>]")
    containsEngNum = engNumRegex.containsMatchIn(ID)
    containsKorean = koreanRegex.containsMatchIn(ID)
    containsSpeical = specialCharRegex.containsMatchIn(ID)

    val isEnglishAndNumberValid =
        ID.matches(Regex(".*[a-zA-Z].*")) && ID.matches(Regex(".*[0-9].*")) && !containsKorean
    val isLengthValid = ID.length >= 8 && ID.length <= 20
    val isSpecialCharValid = !containsSpeical && ID.isNotEmpty()
    val isAllConditionsValid = isEnglishAndNumberValid && isLengthValid && isSpecialCharValid

    val focusRequester = FocusRequester()
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = WhiteScreenModifier
            .pointerInput(Unit) {
                detectTapGestures {
                    focusManager.clearFocus()
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeightSpacer(106.dp)
        DoubleLineTitleText("아이디를", "입력해주세요")
        HeightSpacer(42.dp)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
        ) {
            BasicTextField(
                value = ID,
                cursorBrush = SolidColor(if (isFocused) Color(0xFF397CDB) else Color(0xFFBFBFBF)),
                onValueChange = {
                    ID = it
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                modifier = Modifier
                    .height(22.dp)
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                    },
                textStyle = TextStyle(
                    fontSize = 17.sp,
                    color = Color.Black
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(22.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (ID.isEmpty()) {
                            Text(
                                text = "아이디",
                                style = TextStyle(
                                    fontSize = 17.sp,
                                    fontFamily = pretendard,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFFBFBFBF)
                                )
                            )
                        }
                        innerTextField()
                    }
                },
            )
            if (ID.isNotEmpty()) {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.btn_textfield_eraseall),
                    contentDescription = "x_marker",
                    modifier = Modifier
                        .padding(start = 21.43.dp, end = 9.dp, top = 2.5.dp, bottom = 1.3.dp)
                        .clickable {
                            ID = ""
                        }
                )
            }
        }
        HeightSpacer(14.dp)
        HighlightingLine(
            text = ID,
            isFocused = isFocused,
            isAllConditionsValid = isAllConditionsValid
        )
        HeightSpacer(32.dp)
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 16.dp),
            text = "가이드",
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight.W700,
                color = Color(0xFFAFB8C1)
            )
        )
        Guideline(
            description = "영문, 숫자 조합을 사용해주세요",
            isValid = isEnglishAndNumberValid
        )
        HeightSpacer(12.dp)
        Guideline(
            description = "최소 8자리 이상, 20자 미만으로 구성해주세요",
            isValid = isLengthValid
        )
        HeightSpacer(12.dp)
        Guideline(
            description = "특수문자는 사용할 수 없어요",
            isValid = isSpecialCharValid
        )
        Spacer(modifier = Modifier.weight(1f))
        NextButton(
            mModifier = buttonModifier,
            text = if (isChecked) "다음" else "아이디 중복 확인",
            buttonColor = if (isAllConditionsValid) Color(0xFF348ADF) else Color(0xFFCADCF5),
            onClick = {
                viewModel.updateloginId(ID)
                if (isChecked && isAllConditionsValid) navController.navigate("PasswordPage")
                isChecked = true
            }
        )
    }
}

/**
 * 비밀번호 입력 페이지입니다.
 * @param SignUpViewModel로, SignIn Data를 관리합니다.
 * @param navController Navigation Controller입니다.
 * @author 김기윤
 */
@Composable
fun PasswordPage(
    navController: NavController,
    viewModel: SignUpViewModel,
) {
    var password by remember { mutableStateOf("") }
    var reenteredPassword by remember { mutableStateOf("") }

    val focusRequester = FocusRequester()
    var isFocused by remember { mutableStateOf(false) }
    var isFocusedReentered by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val containsKorean by remember { mutableStateOf(false) }
    val isEnglishAndNumberValid =
        password.matches(Regex(".*[a-zA-Z].*")) && password.matches(Regex(".*[0-9].*")) && !containsKorean
    val isLengthValid = password.length >= 8
    val isSequentialNumbersValid = !containsSequentialNumbers(password)
    val keyboardController = LocalSoftwareKeyboardController.current

    val isAllConditionsValid = isEnglishAndNumberValid && isLengthValid && isSequentialNumbersValid

    var termsOfService by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = WhiteScreenModifier
            .pointerInput(Unit) {
                detectTapGestures {
                    focusManager.clearFocus()
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackButton(navigationTo = ({ navController.navigate("IdPage") }))
        HeightSpacer(56.dp)
        DoubleLineTitleText("비밀번호를", "입력해주세요")
        HeightSpacer(40.dp)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
        ) {
            BasicTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                modifier = Modifier
                    .height(22.dp)
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                    },
                textStyle = TextStyle(
                    fontSize = 17.sp,
                    color = Color.Black
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(21.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (password.isEmpty()) {
                            Text(
                                text = "비밀번호",
                                style = TextStyle(
                                    fontFamily = pretendard,
                                    fontSize = 18.sp,
                                    color = Color(0x99818181)
                                )
                            )
                        }
                        innerTextField()
                    }
                }

            )
            if (password.isNotEmpty()) {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.btn_textfield_eraseall),
                    contentDescription = "x_marker",
                    modifier = Modifier
                        .padding(start = 21.43.dp, end = 9.dp, top = 2.5.dp, bottom = 2.5.dp)
                        .clickable {
                            password = ""
                        }
                )
            }
        }
        HeightSpacer(14.dp)
        HighlightingLine(
            text = password,
            isFocused = isFocused,
            isAllConditionsValid = isAllConditionsValid
        )
        HeightSpacer(32.dp)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
        ) {
            BasicTextField(
                value = reenteredPassword,
                onValueChange = {
                    reenteredPassword = it
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                modifier = Modifier
                    .height(22.dp)
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        isFocusedReentered = focusState.isFocused
                    },
                textStyle = TextStyle(
                    fontSize = 17.sp,
                    color = Color.Black
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(21.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (reenteredPassword.isEmpty()) {
                            Text(
                                text = "비밀번호 재확인",
                                style = TextStyle(
                                    fontFamily = pretendard,
                                    fontSize = 18.sp,
                                    color = Color(0x99818181)
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            )
            if (reenteredPassword.isNotEmpty()) {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.btn_textfield_eraseall),
                    contentDescription = "x_marker",
                    modifier = Modifier
                        .padding(start = 21.43.dp, end = 9.dp, top = 2.5.dp, bottom = 2.5.dp)
                        .clickable {
                            reenteredPassword = ""
                        }
                )
            }
        }
        HeightSpacer(14.dp)
        HighlightingLine(
            text = reenteredPassword,
            isFocused = isFocusedReentered,
            isAllConditionsValid = password == reenteredPassword
        )
        HeightSpacer(32.dp)
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 16.dp),
            text = "가이드",
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = pretendard,
                fontWeight = FontWeight.W700,
                color = Color(0xFFAFB8C1),
            )
        )
        Guideline(
            description = "영문, 숫자 조합을 사용해주세요",
            isValid = isEnglishAndNumberValid
        )
        HeightSpacer(12.dp)
        Guideline(
            description = "최소 8자리 이상으로 구성해주세요",
            isValid = isLengthValid
        )
        HeightSpacer(12.dp)
        Guideline(
            description = "연속된 숫자는 사용할 수 없어요",
            isValid = isSequentialNumbersValid
        )
        Spacer(modifier = Modifier.weight(1f))
        NextButton(
            mModifier = buttonModifier,
            text = "다음",
            buttonColor =
            if (isAllConditionsValid && password == reenteredPassword) Color(0xFF348ADF)
            else Color(0xFFCADCF5),
            onClick = {
                if (isAllConditionsValid && password == reenteredPassword) {
                    viewModel.updatePassword(password)
                    viewModel.completeSignUp(
                        onSuccess = { token ->
                            Log.d("SignUp", "회원가입 성공, 토큰: $token")
                            TokenManager.saveTokens(context, token)
                            Toast.makeText(
                                context,
                                "회원가입 성공",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigate("MainPage") {
                                popUpTo("SignUpPage") { inclusive = true }
                            }
                        },
                        onFailure = { e ->
                            Toast.makeText(
                                context,
                                "회원가입에 실패했습니다. 다시 시도해주세요.",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("SignUp", "회원가입 실패: ${e?.message}")
                        }
                    )
                }
            }
        )
    }
}

@Preview
@Composable
fun LoginPagePreview(){
    LoginPage(
        hiltViewModel(),
        navController = NavController(LocalContext.current)
    )
}
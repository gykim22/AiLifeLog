package com.pnu.ailifelog.composable.Login

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pnu.ailifelog.R
import com.pnu.ailifelog.composable.HeightSpacer
import com.pnu.ailifelog.composable.noRippleClickable
import com.pnu.ailifelog.ui.theme.pretendard
import com.pnu.ailifelog.ui.theme.primaryColor

/**
 * 두 줄에 걸쳐 작성되는 Title Text Composable입니다.
 * @param upperTextLine 윗 줄에 작성되는 Title Text입니다.
 * @param lowerTextLine 아랫 줄에 작성되는 Title Text입니다.
 * @param padding : Dp | Title Text의 horizontal 패딩을 설정합니다.
 * author : 김기윤
 */
@Composable
fun DoubleLineTitleText(
    upperTextLine: String = "Upper TextLine",
    lowerTextLine: String = "Lower TextLine",
    padding: Dp = 24.dp
) {
    Text(
        text = upperTextLine,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = padding)
            .height(40.dp)
            .wrapContentHeight(Alignment.CenterVertically),
        style = TextStyle(
            fontFamily = pretendard,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            letterSpacing = (-0.3).sp,
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            )
        )
    )
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp)
            .height(40.dp)
            .wrapContentHeight(Alignment.CenterVertically),
        text = lowerTextLine,
        fontSize = 28.sp,
        fontFamily = pretendard,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF000000),
        letterSpacing = (-0.3).sp,
    )
}

@Composable
fun LoginTextField(
    placeholder: String,
    onTextChanged: (String) -> Unit,
){
    var text by remember { mutableStateOf("") }
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
                if (it.length <= 15) {
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
            singleLine = false,
            decorationBox = { innerTextField ->
                if (text.isEmpty()) {
                    Text(
                        text = placeholder,
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

/**
 * 하이라이트 라인을 표시하는 Composable입니다.
 * @param text : String | 텍스트 입력 여부를 받습니다.
 * @param isFocused : Boolean | 포커스 여부를 받습니다.
 * @param isAllConditionsValid : Boolean | 모든 조건이 만족되었는지 여부를 받습니다.
 */
@Composable
fun HighlightingLine(text: String, isFocused: Boolean, isAllConditionsValid: Boolean = true) {
    val fillPercentage = if (isFocused) 1f else 0f
    val animatedFillPercentage by animateFloatAsState(targetValue = fillPercentage, label = "")

    if (!isFocused) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(horizontal = 24.dp)
                .background(Color(0xFFBFBFBF))
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .padding(horizontal = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = animatedFillPercentage)
                    .height(2.dp)
                    .background(
                        if (text.isEmpty()) primaryColor
                        else {
                            if (isAllConditionsValid)
                                primaryColor
                            else
                                Color(0xFFE43D45)
                        }
                    )
            )
        }
    }
}

/**
 * 회원가입 페이지에서 조건 만족 여부를 시각화할 때 사용하는 Composable입니다.
 * @param description : String | 조건을 작성합니다.
 * @param isValid : Boolean | 조건을 만족하는지 여부를 판별하는 값입니다.
 */
@Composable
fun Guideline(description: String, isValid: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .height(3.dp)
                .width(3.dp)
                .background(color = Color(0xFFBFBFBF), shape = CircleShape),
        )
        Text(
            text = description,
            fontFamily = pretendard,
            fontSize = 14.sp,
            fontWeight = FontWeight.W400,
            color = Color(0xFFBFBFBF),
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            ),
            letterSpacing = (-0.3).sp,
            modifier = Modifier
                .weight(1f)
        )
        Image(
            imageVector = if (isValid) {
                ImageVector.vectorResource(id = R.drawable.ic_green_checkmark)
            } else {
                ImageVector.vectorResource(id = R.drawable.ic_red_checkmark)
            },
            contentDescription = "status_icon",
            modifier = Modifier.size(18.dp)
        )
    }
}

/**
 * 뒤로가기 버튼입니다.
 * @param title 뒤로가기 버튼 옆에 보여지는 텍스트입니다.
 * @param horizontalPadding 뒤로가기 Row 양 쪽의 가로 패딩입니다.
 * @param verticalPadding 뒤로가기 Row 위아래의 세로 패딩입니다.
 * @param iconDrawable 뒤로가기 버튼 옆에 보여지는 아이콘으로, 한정자를 통해 R.drawable...만 입력 받습니다.
 * @param navigationTo 뒤로가기 버튼을 누르면 실행되는 함수로, navigationTo = ({ navController.navigate(route)}) 형식으로 작성합니다.
 */
@Composable
fun BackButton(
    title: String = "",
    horizontalPadding: Dp = 18.dp,
    verticalPadding: Dp = 18.dp,
    @DrawableRes iconDrawable: Int = 0,
    navigationTo: () -> Unit,
) {
    HeightSpacer(50.dp)
    Row(
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .height(57.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.btn_black_arrow),
            contentDescription = "backButtonIcon",
            modifier = Modifier.noRippleClickable {
                navigationTo()
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontFamily = pretendard,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF121212),
                    textAlign = TextAlign.Center
                )
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (iconDrawable != 0) {
            Image(
                imageVector = ImageVector.vectorResource(id = iconDrawable),
                contentDescription = "logo",
                modifier = Modifier
            )
        } else {
            Box(modifier = Modifier.width(20.dp))
        }
    }
}
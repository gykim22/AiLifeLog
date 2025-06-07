package com.pnu.ailifelog.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pnu.ailifelog.model.Logs.CreateRecordViewModel
import com.pnu.ailifelog.model.SignUp.SignUpViewModel
import com.pnu.ailifelog.view.AiPage
import com.pnu.ailifelog.view.HomePage
import com.pnu.ailifelog.view.IdPage
import com.pnu.ailifelog.view.LogPage
import com.pnu.ailifelog.view.MainPage
import com.pnu.ailifelog.view.MyPage
import com.pnu.ailifelog.view.PasswordPage
import com.pnu.ailifelog.view.LoginPage

@Composable
fun NavGraph(startPage: String) {
    val navcontroller = rememberNavController()
    val authViewModel: SignUpViewModel = hiltViewModel()
    val viewModel: CreateRecordViewModel = hiltViewModel()

    NavHost(
        navController = navcontroller,
        startDestination = startPage
    ) {
        composable("LoginPage") {
            LoginPage(
                authViewModel = authViewModel,
                navController = navcontroller
            )
        }
        composable("IdPage") {
            IdPage(
                navController = navcontroller,
                viewModel = authViewModel
            )
        }
        composable("PasswordPage"){
            PasswordPage(
                navController = navcontroller,
                viewModel = authViewModel
            )
        }

        composable("MainPage"){
            MainPage(
                navController = navcontroller,
                viewModel = viewModel
            )
        }
    }
}
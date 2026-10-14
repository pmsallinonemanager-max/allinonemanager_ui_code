package com.allinonemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.allinonemanager.ui.cc.screens.AddCreditCard
import com.allinonemanager.ui.cc.screens.AddDueDetails
import com.allinonemanager.ui.cc.screens.CreditCardDetails
import com.allinonemanager.ui.cc.screens.CreditCardHome
import com.allinonemanager.ui.home.screens.Home
import com.allinonemanager.ui.theme.AllinOneManagerTheme
import com.allinonemanager.ui.user.screens.LoginScreen
import com.allinonemanager.ui.user.screens.OtpLoginScreen
import com.allinonemanager.ui.user.screens.SignUpScreen
import com.allinonemanager.ui.user.screens.White

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tells the window to not draw automatic/default system bar color backgrounds
        // behind our edge-to-edge content, ensuring full immersion.
        WindowCompat.setDecorFitsSystemWindows(window, false)

        enableEdgeToEdge()
        setContent {
            AllinOneManagerTheme {
                // Ensure status bar icons are dark/visible over our light yellow header
                val window = (this as ComponentActivity).window
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = true
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = White
                ) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("login") {
                            LoginScreen(
                                onLoginClick = { _, _ ->
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onOtpLoginClick = { navController.navigate("otp_login") },
                                onSignUpClick = { navController.navigate("signup") }
                            )
                        }
                        composable("otp_login") {
                            OtpLoginScreen(
                                onLoginClick = { _, _ ->
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onLoginWithPasswordClick = { navController.popBackStack() },
                                onSendOtpClick = { /* trigger OTP send logic later */ },
                                onSignUpClick = { navController.navigate("signup") }
                            )
                        }
                        composable("signup") {
                            SignUpScreen(
                                onSignUpClick = { fullName, email, mobile, password, confirmPassword ->
                                    // TODO: validate & save user, then navigate to dashboard
                                },
                                onLoginClick = { navController.popBackStack() }
                            )
                        }
                        composable("home") {
                            Home(
                                onModuleClick = { route -> navController.navigate(route) },
                                onLogoutClick = {
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                onAccountClick = { /* navigate to profile later */ }
                            )
                        }
                        composable("credit_card") {
                            CreditCardHome(
                                onLogoutClick = {
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                onAccountClick = { /* navigate to profile later */ },
                                onAddCreditCardClick = { navController.navigate("add_credit_card") },
                                onAddDueDetailsClick = { navController.navigate("add_due_details")},
                                onHomeClick = {navController.navigate("home")},
                                onBackClick = {navController.popBackStack()},
                                onRowClick = { index, row ->
                                    val ccNumber = row[0]   // first column is the credit card number
                                    navController.navigate("credit_card_details/$ccNumber")
                                }
                            )
                        }

                        composable("add_credit_card") {
                            AddCreditCard(
                                onLogoutClick = {
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                onAccountClick = { /* navigate to profile later */ },
                                onHomeClick = {navController.navigate("home")},
                                onBackClick = {navController.popBackStack()}
                            )
                        }

                        composable("add_due_details") {
                            AddDueDetails(
                                onLogoutClick = {
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                onAccountClick = { /* navigate to profile later */ },
                                onHomeClick = {navController.navigate("home")},
                                onBackClick = {navController.popBackStack()}
                            )
                        }

                        composable(
                            route = "credit_card_details/{ccNumber}",
                            arguments = listOf(navArgument("ccNumber") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val ccNumber = backStackEntry.arguments?.getString("ccNumber") ?: ""

                            CreditCardDetails(
                                ccNumber = ccNumber,
                                onLogoutClick = {
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onAccountClick = { /* navigate to profile later */ },
                                onHomeClick = { navController.navigate("home") },
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                    }
                }
            }
        }
    }
}
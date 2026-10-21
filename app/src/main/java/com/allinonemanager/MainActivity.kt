package com.allinonemanager

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.allinonemanager.ui.cc.screens.DueDetailsList
import com.allinonemanager.ui.cc.viewmodel.CardsUiState
import com.allinonemanager.ui.cc.viewmodel.CreditCardViewModel
import com.allinonemanager.ui.cc.viewmodel.DuesUiState
import com.allinonemanager.ui.cc.viewmodel.SaveUiState
import com.allinonemanager.data.UserSession
import com.allinonemanager.ui.home.screens.Home
import com.allinonemanager.ui.theme.AllinOneManagerTheme
import com.allinonemanager.ui.user.screens.LoginScreen
import com.allinonemanager.ui.user.screens.OtpLoginScreen
import com.allinonemanager.ui.user.screens.SignUpScreen
import com.allinonemanager.ui.user.screens.White
import com.allinonemanager.ui.user.viewmodel.LoginUiState
import com.allinonemanager.ui.user.viewmodel.LoginViewModel
import com.allinonemanager.ui.user.viewmodel.SignUpUiState
import com.allinonemanager.ui.user.viewmodel.SignUpViewModel

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
                    // Forget the user and clear the whole back stack so Back can't return to their screens
                    val logout: () -> Unit = {
                        UserSession.clear()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("login") {
                            val loginViewModel: LoginViewModel = viewModel()
                            val loginState by loginViewModel.uiState.collectAsState()

                            LaunchedEffect(loginState) {
                                if (loginState is LoginUiState.Success) {
                                    loginViewModel.resetState()
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }

                            LoginScreen(
                                onLoginClick = { request -> loginViewModel.login(request) },
                                onOtpLoginClick = { navController.navigate("otp_login") },
                                onSignUpClick = { navController.navigate("signup") },
                                isLoading = loginState is LoginUiState.Loading,
                                serverError = (loginState as? LoginUiState.Error)?.message
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
                            val signUpViewModel: SignUpViewModel = viewModel()
                            val signUpState by signUpViewModel.uiState.collectAsState()
                            val context = LocalContext.current

                            LaunchedEffect(signUpState) {
                                val state = signUpState
                                if (state is SignUpUiState.Success) {
                                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                                    signUpViewModel.resetState()
                                    navController.popBackStack("login", inclusive = false)
                                }
                            }

                            SignUpScreen(
                                onSignUpClick = { request -> signUpViewModel.signUp(request) },
                                onLoginClick = { navController.popBackStack() },
                                isLoading = signUpState is SignUpUiState.Loading,
                                serverError = (signUpState as? SignUpUiState.Error)?.message
                            )
                        }
                        composable("home") {
                            Home(
                                onModuleClick = { route -> navController.navigate(route) },
                                onLogoutClick = logout,
                                onAccountClick = { /* navigate to profile later */ }
                            )
                        }
                        composable("credit_card") {
                            val ccViewModel: CreditCardViewModel = viewModel()
                            val cardsState by ccViewModel.cardsState.collectAsState()

                            // Runs every time this screen is shown, so cards added on other screens appear
                            LaunchedEffect(Unit) { ccViewModel.loadCards() }

                            CreditCardHome(
                                onLogoutClick = logout,
                                onAccountClick = { /* navigate to profile later */ },
                                onAddCreditCardClick = { navController.navigate("add_credit_card") },
                                onAddDueDetailsClick = { navController.navigate("add_due_details")},
                                onDueDetailsClick = { navController.navigate("due_details") },
                                onHomeClick = {navController.navigate("home")},
                                onBackClick = {navController.popBackStack()},
                                onCardClick = { card ->
                                    card.ccId?.let { navController.navigate("credit_card_details/$it") }
                                },
                                cards = (cardsState as? CardsUiState.Loaded)?.cards.orEmpty(),
                                isLoading = cardsState is CardsUiState.Loading,
                                errorMessage = (cardsState as? CardsUiState.Error)?.message
                            )
                        }

                        composable("add_credit_card") {
                            val ccViewModel: CreditCardViewModel = viewModel()
                            val saveState by ccViewModel.saveState.collectAsState()
                            val context = LocalContext.current

                            LaunchedEffect(saveState) {
                                val state = saveState
                                if (state is SaveUiState.Success) {
                                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                                    ccViewModel.resetSaveState()
                                    navController.popBackStack()
                                }
                            }

                            AddCreditCard(
                                onLogoutClick = logout,
                                onAccountClick = { /* navigate to profile later */ },
                                onHomeClick = {navController.navigate("home")},
                                onBackClick = {navController.popBackStack()},
                                onSaveClick = { request -> ccViewModel.addCard(request) },
                                isSaving = saveState is SaveUiState.Saving,
                                serverError = (saveState as? SaveUiState.Error)?.message
                            )
                        }

                        composable("add_due_details") {
                            val ccViewModel: CreditCardViewModel = viewModel()
                            val cardsState by ccViewModel.cardsState.collectAsState()
                            val saveState by ccViewModel.saveState.collectAsState()
                            val context = LocalContext.current

                            LaunchedEffect(Unit) { ccViewModel.loadCards() }
                            LaunchedEffect(saveState) {
                                val state = saveState
                                if (state is SaveUiState.Success) {
                                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                                    ccViewModel.resetSaveState()
                                    navController.popBackStack()
                                }
                            }

                            AddDueDetails(
                                onLogoutClick = logout,
                                onAccountClick = { /* navigate to profile later */ },
                                onHomeClick = {navController.navigate("home")},
                                onBackClick = {navController.popBackStack()},
                                cards = (cardsState as? CardsUiState.Loaded)?.cards.orEmpty(),
                                onSaveClick = { request -> ccViewModel.addDue(request) },
                                isSaving = saveState is SaveUiState.Saving,
                                // A failed card load is shown here too, otherwise the CC list is just empty
                                serverError = (saveState as? SaveUiState.Error)?.message
                                    ?: (cardsState as? CardsUiState.Error)?.message
                            )
                        }

                        composable(
                            route = "credit_card_details/{ccId}",
                            arguments = listOf(navArgument("ccId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val ccId = backStackEntry.arguments?.getInt("ccId")
                            val ccViewModel: CreditCardViewModel = viewModel()
                            val cardsState by ccViewModel.cardsState.collectAsState()

                            LaunchedEffect(Unit) { ccViewModel.loadCards() }

                            val state = cardsState
                            CreditCardDetails(
                                card = (state as? CardsUiState.Loaded)?.cards?.find { it.ccId == ccId },
                                statusMessage = when (state) {
                                    is CardsUiState.Loaded -> "Credit card not found."
                                    is CardsUiState.Error -> state.message
                                    CardsUiState.Loading -> null
                                },
                                onEditClick = { ccId?.let { navController.navigate("edit_credit_card/$it") } },
                                onViewDuesClick = { ccId?.let { navController.navigate("due_details?ccId=$it") } },
                                onLogoutClick = logout,
                                onAccountClick = { /* navigate to profile later */ },
                                onHomeClick = { navController.navigate("home") },
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        // Same form as Add Credit Card, filled with the card's current values
                        composable(
                            route = "edit_credit_card/{ccId}",
                            arguments = listOf(navArgument("ccId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val ccId = backStackEntry.arguments?.getInt("ccId") ?: return@composable
                            val ccViewModel: CreditCardViewModel = viewModel()
                            val cardsState by ccViewModel.cardsState.collectAsState()
                            val saveState by ccViewModel.saveState.collectAsState()
                            val context = LocalContext.current

                            LaunchedEffect(Unit) { ccViewModel.loadCards() }
                            LaunchedEffect(saveState) {
                                val state = saveState
                                if (state is SaveUiState.Success) {
                                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                                    ccViewModel.resetSaveState()
                                    navController.popBackStack()     // Details reloads the cards when shown again
                                }
                            }

                            val state = cardsState
                            AddCreditCard(
                                title = "EDIT CREDIT CARD",
                                card = (state as? CardsUiState.Loaded)?.cards?.find { it.ccId == ccId },
                                onLogoutClick = logout,
                                onAccountClick = { /* navigate to profile later */ },
                                onHomeClick = { navController.navigate("home") },
                                onBackClick = { navController.popBackStack() },
                                onSaveClick = { request -> ccViewModel.updateCard(ccId, request) },
                                isSaving = saveState is SaveUiState.Saving,
                                serverError = (saveState as? SaveUiState.Error)?.message
                                    ?: (state as? CardsUiState.Error)?.message
                            )
                        }

                        // Without ccId: every card's dues (from Credit Card Home). With ccId: one card (from Credit Card Details)
                        composable(
                            route = "due_details?ccId={ccId}",
                            arguments = listOf(navArgument("ccId") {
                                type = NavType.IntType
                                defaultValue = -1
                            })
                        ) { backStackEntry ->
                            val ccId = backStackEntry.arguments?.getInt("ccId")?.takeIf { it != -1 }
                            val ccViewModel: CreditCardViewModel = viewModel()
                            val duesState by ccViewModel.duesState.collectAsState()

                            LaunchedEffect(Unit) { ccViewModel.loadDues(ccId) }

                            DueDetailsList(
                                dues = (duesState as? DuesUiState.Loaded)?.dues.orEmpty(),
                                isLoading = duesState is DuesUiState.Loading,
                                errorMessage = (duesState as? DuesUiState.Error)?.message,
                                showCardFilter = ccId == null,
                                onLogoutClick = logout,
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
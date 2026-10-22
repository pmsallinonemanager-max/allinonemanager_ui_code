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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
                    // Go back to [route] if it is already in the history (so Back still works), otherwise open it
                    val goBackTo: (String) -> Unit = { route ->
                        if (!navController.popBackStack(route, inclusive = false)) navController.navigate(route)
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
                            val duesState by ccViewModel.duesState.collectAsState()

                            // Runs every time this screen is shown, so cards / dues changed on other screens appear
                            LaunchedEffect(Unit) {
                                ccViewModel.loadCards()
                                ccViewModel.loadDues()      // for the Paid / Unpaid column
                            }

                            CreditCardHome(
                                onLogoutClick = logout,
                                onAccountClick = { /* navigate to profile later */ },
                                onAddCreditCardClick = { navController.navigate("add_credit_card") },
                                onAddDueDetailsClick = { navController.navigate("add_due_details")},
                                onDueDetailsClick = { navController.navigate("due_details") },
                                onHomeClick = { goBackTo("home") },
                                onBackClick = {navController.popBackStack()},
                                onCardClick = { card ->
                                    card.ccId?.let { navController.navigate("credit_card_details/$it") }
                                },
                                cards = (cardsState as? CardsUiState.Loaded)?.cards.orEmpty(),
                                dues = (duesState as? DuesUiState.Loaded)?.dues.orEmpty(),
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
                                onHomeClick = { goBackTo("credit_card") },
                                onBackClick = {navController.popBackStack()},
                                onSaveClick = { request -> ccViewModel.addCard(request) },
                                isSaving = saveState is SaveUiState.Saving,
                                serverError = (saveState as? SaveUiState.Error)?.message
                            )
                        }

                        // Without ccId: user picks the card. With ccId: that card is picked (from its empty Dues page)
                        composable(
                            route = "add_due_details?ccId={ccId}",
                            arguments = listOf(navArgument("ccId") {
                                type = NavType.IntType
                                defaultValue = -1
                            })
                        ) { backStackEntry ->
                            val preselectedCcId = backStackEntry.arguments?.getInt("ccId")?.takeIf { it != -1 }
                            val ccViewModel: CreditCardViewModel = viewModel()
                            val cardsState by ccViewModel.cardsState.collectAsState()
                            val duesState by ccViewModel.duesState.collectAsState()
                            val saveState by ccViewModel.saveState.collectAsState()
                            val context = LocalContext.current

                            LaunchedEffect(Unit) {
                                ccViewModel.loadCards()
                                ccViewModel.loadDues()      // same-month check and unpaid older bills
                            }
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
                                onHomeClick = { goBackTo("credit_card") },
                                onBackClick = {navController.popBackStack()},
                                // Dues are added only to active cards
                                cards = (cardsState as? CardsUiState.Loaded)?.cards.orEmpty().filter { it.active },
                                preselectedCcId = preselectedCcId,
                                dues = (duesState as? DuesUiState.Loaded)?.dues.orEmpty(),
                                onViewDuesClick = { ccId -> navController.navigate("due_details?ccId=$ccId") },
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
                            val duesState by ccViewModel.duesState.collectAsState()
                            val saveState by ccViewModel.saveState.collectAsState()
                            val context = LocalContext.current
                            // Editing happens on the Details page itself
                            var isEditing by rememberSaveable { mutableStateOf(false) }

                            LaunchedEffect(Unit) {
                                ccViewModel.loadCards()
                                ccViewModel.loadDues(ccId)      // Delete is only allowed without dues
                            }
                            LaunchedEffect(saveState) {
                                val state = saveState
                                if (state is SaveUiState.Success) {
                                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                                    ccViewModel.resetSaveState()
                                    if (state.message == CreditCardViewModel.CARD_DELETED) {
                                        goBackTo("credit_card")     // the card is gone
                                    } else {
                                        isEditing = false
                                        ccViewModel.loadCards()     // show the saved values / new status
                                    }
                                }
                            }

                            val state = cardsState
                            CreditCardDetails(
                                card = (state as? CardsUiState.Loaded)?.cards?.find { it.ccId == ccId },
                                statusMessage = when (state) {
                                    is CardsUiState.Loaded -> "Credit card not found."
                                    is CardsUiState.Error -> state.message
                                    CardsUiState.Loading -> null
                                },
                                isEditing = isEditing,
                                onEditClick = { isEditing = true },
                                onCancelEdit = {
                                    isEditing = false
                                    ccViewModel.resetSaveState()    // drop any old save error
                                },
                                onSaveClick = { request -> ccId?.let { ccViewModel.updateCard(it, request) } },
                                isSaving = saveState is SaveUiState.Saving,
                                serverError = (saveState as? SaveUiState.Error)?.message,
                                dueCount = when (val dues = duesState) {
                                    is DuesUiState.Loaded -> dues.dues.size
                                    is DuesUiState.Error -> 0     // could not check; the backend refuses if dues exist
                                    DuesUiState.Loading -> null
                                },
                                onActiveChange = { isActive -> ccId?.let { ccViewModel.setCardActive(it, isActive) } },
                                onDeleteClick = { ccId?.let { ccViewModel.deleteCard(it) } },
                                onViewDuesClick = { ccId?.let { navController.navigate("due_details?ccId=$it") } },
                                onLogoutClick = logout,
                                onAccountClick = { /* navigate to profile later */ },
                                onHomeClick = { goBackTo("credit_card") },
                                onBackClick = { navController.popBackStack() }
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
                            val saveState by ccViewModel.saveState.collectAsState()
                            val context = LocalContext.current
                            // Due being edited in place; null = none
                            var editingDueId by rememberSaveable { mutableStateOf<Int?>(null) }

                            LaunchedEffect(Unit) { ccViewModel.loadDues(ccId) }
                            LaunchedEffect(saveState) {
                                val state = saveState
                                if (state is SaveUiState.Success) {
                                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                                    ccViewModel.resetSaveState()
                                    editingDueId = null
                                    ccViewModel.loadDues(ccId)      // show the edited / paid due
                                }
                            }

                            DueDetailsList(
                                dues = (duesState as? DuesUiState.Loaded)?.dues.orEmpty(),
                                isLoading = duesState is DuesUiState.Loading,
                                errorMessage = (duesState as? DuesUiState.Error)?.message,
                                onSaveDue = { request -> ccViewModel.updateDue(request) },
                                onPaidChange = { ccDueId, isPaid -> ccViewModel.setDuePaid(ccDueId, isPaid) },
                                isSaving = saveState is SaveUiState.Saving,
                                saveError = (saveState as? SaveUiState.Error)?.message,
                                editingDueId = editingDueId,
                                onEditDue = { ccDueId ->
                                    editingDueId = ccDueId          // null = cancel
                                    ccViewModel.resetSaveState()    // drop any old save error
                                },
                                showCardFilter = ccId == null,
                                onDeleteDue = { due ->
                                    if (due.ccDueId != null && due.ccId != null) ccViewModel.deleteDue(due.ccDueId, due.ccId)
                                },
                                onAddDueClick = {
                                    navController.navigate(if (ccId != null) "add_due_details?ccId=$ccId" else "add_due_details")
                                },
                                onLogoutClick = logout,
                                onAccountClick = { /* navigate to profile later */ },
                                onHomeClick = { goBackTo("credit_card") },
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                    }
                }
            }
        }
    }
}
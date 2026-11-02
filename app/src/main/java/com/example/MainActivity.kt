package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.CanteenRepository
import com.example.model.CartItem
import com.example.model.MenuItem
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CustomerHomeScreen
import com.example.ui.screens.CustomerLoginScreen
import com.example.ui.screens.PaymentScreen
import com.example.ui.screens.PortalScreen
import com.example.ui.screens.StaffDashboardScreen
import com.example.ui.screens.StaffLoginScreen
import com.example.ui.screens.TokensScreen
import com.example.ui.theme.MyApplicationTheme

enum class AppScreen {
    PORTAL,
    CUSTOMER_LOGIN,
    STAFF_LOGIN,
    CUSTOMER_HOME,
    CART,
    PAYMENT,
    TOKENS,
    STAFF_DASHBOARD
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FastBiteApp()
                }
            }
        }
    }
}

@Composable
fun FastBiteApp() {
    var currentScreen by remember { mutableStateOf(AppScreen.PORTAL) }
    var isOwnerSession by remember { mutableStateOf(false) }

    // Checkout data
    var paymentItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
    var paymentScheduleTime by remember { mutableStateOf("Immediate (10-15 mins)") }

    val cartItems by CanteenRepository.cart.collectAsState()

    Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
        when (screen) {
            AppScreen.PORTAL -> {
                PortalScreen(
                    onNavigateToCustomerLogin = { currentScreen = AppScreen.CUSTOMER_LOGIN },
                    onNavigateToStaffLogin = { currentScreen = AppScreen.STAFF_LOGIN }
                )
            }

            AppScreen.CUSTOMER_LOGIN -> {
                BackHandler { currentScreen = AppScreen.PORTAL }
                CustomerLoginScreen(
                    onLoginSuccess = { currentScreen = AppScreen.CUSTOMER_HOME },
                    onBackToPortal = { currentScreen = AppScreen.PORTAL }
                )
            }

            AppScreen.STAFF_LOGIN -> {
                BackHandler { currentScreen = AppScreen.PORTAL }
                StaffLoginScreen(
                    onLoginSuccess = { isOwner ->
                        isOwnerSession = isOwner
                        currentScreen = AppScreen.STAFF_DASHBOARD
                    },
                    onBackToPortal = { currentScreen = AppScreen.PORTAL }
                )
            }

            AppScreen.CUSTOMER_HOME -> {
                BackHandler { currentScreen = AppScreen.PORTAL }
                CustomerHomeScreen(
                    onNavigateToCart = { currentScreen = AppScreen.CART },
                    onNavigateToTokens = { currentScreen = AppScreen.TOKENS },
                    onNavigateToDirectPayment = { dish, qty, time ->
                        paymentItems = listOf(CartItem(dish, qty))
                        paymentScheduleTime = time
                        currentScreen = AppScreen.PAYMENT
                    },
                    onLogout = { currentScreen = AppScreen.PORTAL }
                )
            }

            AppScreen.CART -> {
                BackHandler { currentScreen = AppScreen.CUSTOMER_HOME }
                CartScreen(
                    cartItems = cartItems,
                    onProceedToPayment = { scheduleTime ->
                        paymentItems = cartItems
                        paymentScheduleTime = scheduleTime
                        currentScreen = AppScreen.PAYMENT
                    },
                    onBackToHome = { currentScreen = AppScreen.CUSTOMER_HOME }
                )
            }

            AppScreen.PAYMENT -> {
                BackHandler { currentScreen = AppScreen.CUSTOMER_HOME }
                PaymentScreen(
                    itemsToOrder = paymentItems,
                    scheduledTime = paymentScheduleTime,
                    onPaymentComplete = { _ ->
                        currentScreen = AppScreen.TOKENS
                    },
                    onBack = { currentScreen = AppScreen.CUSTOMER_HOME }
                )
            }

            AppScreen.TOKENS -> {
                BackHandler { currentScreen = AppScreen.CUSTOMER_HOME }
                TokensScreen(
                    onBack = { currentScreen = AppScreen.CUSTOMER_HOME }
                )
            }

            AppScreen.STAFF_DASHBOARD -> {
                BackHandler { currentScreen = AppScreen.PORTAL }
                StaffDashboardScreen(
                    initialIsOwner = isOwnerSession,
                    onLogout = { currentScreen = AppScreen.PORTAL }
                )
            }
        }
    }
}

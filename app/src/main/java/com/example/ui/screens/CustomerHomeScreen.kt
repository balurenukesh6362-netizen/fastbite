package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CanteenRepository
import com.example.model.CartItem
import com.example.model.MealCategory
import com.example.model.MenuItem
import com.example.model.OrderStatus
import com.example.ui.components.DishImage
import com.example.ui.components.DishOrderSheet
import com.example.ui.dialogs.CustomerFeedbackDialog
import com.example.ui.dialogs.CustomerProfileDialog
import com.example.ui.dialogs.OrderHistoryReceiptDialog
import com.example.ui.dialogs.RulesAndRegulationsDialog
import com.example.ui.dialogs.WeeklyCalendarDialog
import com.example.ui.theme.FastBitePermanentToken
import com.example.ui.theme.FastBitePrimary
import com.example.ui.theme.FastBiteSecondary
import com.example.ui.theme.FastBiteTertiary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen(
    onNavigateToCart: () -> Unit,
    onNavigateToTokens: () -> Unit,
    onNavigateToDirectPayment: (item: MenuItem, quantity: Int, scheduleTime: String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val logoResId = context.resources.getIdentifier("fastbite_logo", "drawable", context.packageName)
    val scope = rememberCoroutineScope()

    val menuItems by CanteenRepository.menuItems.collectAsState()
    val cartItems by CanteenRepository.cart.collectAsState()
    val orders by CanteenRepository.orders.collectAsState()
    val currentCustomer by CanteenRepository.currentCustomer.collectAsState()
    val activeAlert by CanteenRepository.activeCustomerAlert.collectAsState()

    var selectedCategory by remember { mutableStateOf(MealCategory.LUNCH) }
    var selectedDishForOrder by remember { mutableStateOf<MenuItem?>(null) }

    // Dialog States
    var showWeeklyCalendar by remember { mutableStateOf(false) }
    var showOrderHistory by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var showRulesDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showThreeLinesSheet by remember { mutableStateOf(false) }

    val activeTokensCount = orders.count { it.status != OrderStatus.COMPLETED && it.status != OrderStatus.CANCELLED }
    val totalCartCount = cartItems.sumOf { it.quantity }

    val filteredDishes = menuItems.filter { it.category == selectedCategory }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(top = 8.dp, bottom = 10.dp)
            ) {
                // Top Row: Logo & App Name (Left) + Tokens & Cart & Profile (Right)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // App Logo and App Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showProfileDialog = true }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(FastBitePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            if (logoResId != 0) {
                                Image(
                                    painter = painterResource(id = logoResId),
                                    contentDescription = "FastBite Logo",
                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Fastfood,
                                    contentDescription = "FastBite",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "FastBite",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = FastBitePrimary
                            )
                            Text(
                                text = "Canteen Express",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Action Icons: Tokens, Cart, Profile
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Tokens Button
                        Surface(
                            onClick = onNavigateToTokens,
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier.testTag("tokens_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ConfirmationNumber,
                                    contentDescription = "Tokens",
                                    tint = FastBitePermanentToken,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Tokens",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = FastBitePermanentToken
                                )
                                if (activeTokensCount > 0) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .background(FastBitePermanentToken, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$activeTokensCount",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // Cart Button
                        Surface(
                            onClick = onNavigateToCart,
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFFFE0B2),
                            modifier = Modifier.testTag("cart_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (totalCartCount > 0) {
                                            Badge(
                                                containerColor = FastBitePrimary,
                                                contentColor = Color.White
                                            ) {
                                                Text("$totalCartCount")
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = "Cart",
                                        tint = FastBitePrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Cart",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = FastBitePrimary
                                )
                            }
                        }

                        // Profile Icon
                        IconButton(
                            onClick = { showProfileDialog = true },
                            modifier = Modifier.size(36.dp).testTag("profile_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom of app name: 3-Lines Menu Icon + Category Options (Breakfast, Lunch, Snacks)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Three lines option (Hamburger Menu)
                    Surface(
                        onClick = { showThreeLinesSheet = true },
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF5F5F5),
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("three_lines_menu_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu Options",
                                tint = FastBitePrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Next to it: (Snacks, Lunch, Breakfast options)
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MealCategory.values().forEach { cat ->
                            val isSelected = selectedCategory == cat
                            Surface(
                                onClick = { selectedCategory = cat },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) FastBitePrimary else Color(0xFFF5F5F5),
                                shadowElevation = if (isSelected) 2.dp else 0.dp,
                                modifier = Modifier.testTag("category_${cat.name.lowercase()}_chip")
                            ) {
                                Text(
                                    text = when (cat) {
                                        MealCategory.BREAKFAST -> "🍳 Breakfast"
                                        MealCategory.LUNCH -> "🍛 Lunch"
                                        MealCategory.SNACKS -> "🍿 Snacks"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFBF8F5))
        ) {
            // Live Ping Alert Banner
            if (activeAlert != null) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFE8F5E9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FastBiteTertiary),
                        shadowElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = FastBiteTertiary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = activeAlert ?: "",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                            IconButton(
                                onClick = { CanteenRepository.dismissCustomerAlert() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color.Gray)
                            }
                        }
                    }
                }
            }

            // AI / Popular Recommendation & Quick Order
            item(span = { GridItemSpan(maxLineSpan) }) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3E0)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = FastBitePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "AI Canteen Recommendation",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = FastBitePrimary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(FastBitePrimary)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text("COMBO DEAL", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Royal Lunch Thali + Cutting Chai Combo for ₹130 (Save ₹15)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Hot-selling student & staff favorite combo based on campus orders.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // One-tap Quick Order ("Order Again")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                onClick = {
                                    val thali = menuItems.find { it.id == "ln_1" }
                                    if (thali != null) {
                                        CanteenRepository.addToCart(thali, 1)
                                        onNavigateToCart()
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, FastBitePrimary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, tint = FastBitePrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Quick 'Order Again'", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FastBitePrimary)
                                }
                            }
                        }
                    }
                }
            }

            // Section Header
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${selectedCategory.label} Dishes Prepared Today",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Text(
                        text = "${filteredDishes.size} available",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Dishes Cards (Photo + Name in remaining entire page)
            items(filteredDishes) { dish ->
                Card(
                    onClick = {
                        selectedDishForOrder = dish
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dish_card_${dish.id}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column {
                        // Dish Photo
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {
                            DishImage(
                                imageResName = dish.imageResName,
                                category = dish.category,
                                contentDescription = dish.name,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Veg Indicator & Prep Time
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .border(1.dp, FastBiteTertiary, RoundedCornerShape(2.dp))
                                                .padding(2.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box(modifier = Modifier.size(5.dp).background(FastBiteTertiary, CircleShape))
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("VEG", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = FastBiteTertiary)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${dish.prepTimeMins}m", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Dish Details
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = dish.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )

                                Text(
                                    text = "₹${dish.price.toInt()}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = FastBitePrimary
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = dish.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${dish.rating} (${dish.ratingCount})", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFFE0B2)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = FastBitePrimary, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Select & Order", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FastBitePrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Three Lines Menu Bottom Sheet (Weekly Calendar, Order History, Feedback, Rules)
    if (showThreeLinesSheet) {
        ModalBottomSheet(
            onDismissRequest = { showThreeLinesSheet = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .padding(bottom = 30.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "FastBite Canteen Options",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = FastBitePrimary
                )
                Text(
                    text = "Weekly meals, receipts, guidelines and feedback",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 1. Weekly 7 days calendar
                MenuOptionCard(
                    title = "Weekly 7 Days Calendar",
                    subtitle = "Browse daily dish schedules Monday through Sunday",
                    icon = Icons.Default.CalendarMonth,
                    onClick = {
                        showThreeLinesSheet = false
                        showWeeklyCalendar = true
                    }
                )

                // 2. Order History with Payment History
                MenuOptionCard(
                    title = "Order History & Payment Receipts",
                    subtitle = "View past transactions, payment modes and download invoices",
                    icon = Icons.Default.ReceiptLong,
                    onClick = {
                        showThreeLinesSheet = false
                        showOrderHistory = true
                    }
                )

                // 3. Rules and Regulations
                MenuOptionCard(
                    title = "Rules & Regulations",
                    subtitle = "Canteen token verification and hygiene standards",
                    icon = Icons.Default.Info,
                    onClick = {
                        showThreeLinesSheet = false
                        showRulesDialog = true
                    }
                )

                // 4. Feedback & Issue Report
                MenuOptionCard(
                    title = "Feedback & Issue Report",
                    subtitle = "Directly report food quality or service issues",
                    icon = Icons.Default.Feedback,
                    onClick = {
                        showThreeLinesSheet = false
                        showFeedbackDialog = true
                    }
                )
            }
        }
    }

    // Dish Click Order/Cart Sheet
    if (selectedDishForOrder != null) {
        val dish = selectedDishForOrder!!
        DishOrderSheet(
            dish = dish,
            onAddToCart = { qty, time ->
                CanteenRepository.addToCart(dish, qty)
                selectedDishForOrder = null
            },
            onDirectOrder = { qty, time ->
                selectedDishForOrder = null
                onNavigateToDirectPayment(dish, qty, time)
            },
            onDismiss = { selectedDishForOrder = null }
        )
    }

    // Dialogs
    if (showWeeklyCalendar) {
        WeeklyCalendarDialog(
            menuItems = menuItems,
            onDismiss = { showWeeklyCalendar = false }
        )
    }

    if (showOrderHistory) {
        OrderHistoryReceiptDialog(
            orders = orders,
            onDismiss = { showOrderHistory = false }
        )
    }

    if (showRulesDialog) {
        RulesAndRegulationsDialog(
            onDismiss = { showRulesDialog = false }
        )
    }

    if (showFeedbackDialog) {
        CustomerFeedbackDialog(
            onDismiss = { showFeedbackDialog = false }
        )
    }

    if (showProfileDialog) {
        CustomerProfileDialog(
            user = currentCustomer,
            onLogout = onLogout,
            onDismiss = { showProfileDialog = false }
        )
    }
}

@Composable
private fun MenuOptionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFE0B2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = FastBitePrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

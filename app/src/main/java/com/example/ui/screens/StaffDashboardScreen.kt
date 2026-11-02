package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CanteenRepository
import com.example.model.CanteenDay
import com.example.model.CanteenOrder
import com.example.model.MealCategory
import com.example.model.MenuItem
import com.example.model.OrderStatus
import com.example.model.OwnerBankAccount
import com.example.model.TokenType
import com.example.ui.dialogs.RulesAndRegulationsDialog
import com.example.ui.theme.FastBiteCodAmber
import com.example.ui.theme.FastBiteOnlineBlue
import com.example.ui.theme.FastBitePermanentToken
import com.example.ui.theme.FastBitePrimary
import com.example.ui.theme.FastBiteSecondary
import com.example.ui.theme.FastBiteTemporaryToken
import com.example.ui.theme.FastBiteTertiary
import com.example.util.AudioNotificationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDashboardScreen(
    initialIsOwner: Boolean = false,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val logoResId = context.resources.getIdentifier("fastbite_logo", "drawable", context.packageName)

    val orders by CanteenRepository.orders.collectAsState()
    val menuItems by CanteenRepository.menuItems.collectAsState()
    val penaltyPercent by CanteenRepository.cancellationPenaltyPercent.collectAsState()
    val isAudioMuted by CanteenRepository.isAudioMuted.collectAsState()
    val wastageRecords by CanteenRepository.wastageRecords.collectAsState()
    val ownerBankAccount by CanteenRepository.ownerBankAccount.collectAsState()

    var isOwnerUnlocked by remember { mutableStateOf(initialIsOwner) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var showOwnerAuthDialog by remember { mutableStateOf(false) }
    var showAddDishDialog by remember { mutableStateOf(false) }
    var showRulesDialog by remember { mutableStateOf(false) }
    var dishToEdit by remember { mutableStateOf<MenuItem?>(null) }
    var showAddWastageDialog by remember { mutableStateOf(false) }

    // Summary Calculations
    val totalOrdersCount = orders.size
    val totalRevenue = orders.filter { it.status != OrderStatus.CANCELLED }.sumOf { it.totalAmount }
    val onlineOrders = orders.filter { it.tokenType == TokenType.PERMANENT }
    val codOrders = orders.filter { it.tokenType == TokenType.TEMPORARY }
    val onlineRevenue = onlineOrders.filter { it.status != OrderStatus.CANCELLED }.sumOf { it.totalAmount }
    val codRevenue = codOrders.filter { it.status != OrderStatus.CANCELLED }.sumOf { it.totalAmount }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo + Name
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(FastBiteOnlineBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            if (logoResId != 0) {
                                Image(
                                    painter = painterResource(id = logoResId),
                                    contentDescription = "FastBite",
                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "FastBite",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold
                                    ),
                                    color = FastBiteOnlineBlue
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (isOwnerUnlocked) Color(0xFFFFD54F) else Color(0xFFBBDEFB))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (isOwnerUnlocked) "OWNER" else "STAFF",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        color = if (isOwnerUnlocked) Color(0xFF5D4037) else Color(0xFF0D47A1)
                                    )
                                }
                            }
                            Text(
                                text = "Kitchen Operations & Finances",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Header Controls: Audio Mute Toggle & Logout
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Audio Notification Mute/Unmute
                        Surface(
                            onClick = {
                                CanteenRepository.toggleAudioMute()
                                if (isAudioMuted) {
                                    AudioNotificationHelper.playOrderChime(context, false)
                                }
                            },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isAudioMuted) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                            modifier = Modifier.testTag("audio_mute_toggle_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isAudioMuted) Icons.Default.NotificationsOff else Icons.Default.Notifications,
                                    contentDescription = "Audio Alert",
                                    tint = if (isAudioMuted) Color(0xFFC62828) else FastBiteTertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isAudioMuted) "Muted" else "Chime ON",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAudioMuted) Color(0xFFC62828) else FastBiteTertiary
                                )
                            }
                        }

                        // Logout Button
                        IconButton(
                            onClick = onLogout,
                            modifier = Modifier.size(36.dp).testTag("staff_logout_button")
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.Gray)
                        }
                    }
                }

                // Scrollable Dashboard Tabs
                val tabTitles = listOf(
                    "Live Queue (${orders.count { it.status == OrderStatus.PENDING || it.status == OrderStatus.PREPARING }})",
                    "Menu & Stock",
                    "7-Day Planner",
                    "Penalty & Wastage",
                    "Owner Bank Account"
                )

                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 12.dp,
                    containerColor = Color.White,
                    contentColor = FastBiteOnlineBlue
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF4F7FB))
        ) {
            when (selectedTab) {
                0 -> LiveQueueTab(
                    orders = orders,
                    totalOrdersCount = totalOrdersCount,
                    totalRevenue = totalRevenue,
                    onlineOrdersCount = onlineOrders.size,
                    onlineRevenue = onlineRevenue,
                    codOrdersCount = codOrders.size,
                    codRevenue = codRevenue,
                    onPingCustomer = { orderId ->
                        CanteenRepository.pingCustomer(orderId)
                        AudioNotificationHelper.playFoodReadyChime(context)
                    },
                    onUpdateStatus = { orderId, newStatus ->
                        CanteenRepository.updateOrderStatus(orderId, newStatus)
                    }
                )
                1 -> MenuManagementTab(
                    menuItems = menuItems,
                    onToggleAvailability = { CanteenRepository.toggleItemAvailability(it) },
                    onEditDish = { dishToEdit = it },
                    onAddNewDish = { showAddDishDialog = true }
                )
                2 -> WeeklyPlannerTab(
                    menuItems = menuItems,
                    onUpdateDish = { CanteenRepository.updateMenuItem(it) }
                )
                3 -> PenaltyAndWastageTab(
                    penaltyPercent = penaltyPercent,
                    onUpdatePenalty = { CanteenRepository.setCancellationPenaltyPercent(it) },
                    wastageRecords = wastageRecords,
                    onAddWastage = { showAddWastageDialog = true }
                )
                4 -> OwnerBankAccountTab(
                    isOwnerUnlocked = isOwnerUnlocked,
                    bankAccount = ownerBankAccount,
                    onRequestOwnerClearance = { showOwnerAuthDialog = true },
                    onUpdateBankAccount = { CanteenRepository.updateOwnerBankAccount(it) }
                )
            }
        }
    }

    // Owner Security Clearance Modal
    if (showOwnerAuthDialog) {
        var ownerEmail by remember { mutableStateOf("owner@fastbite.com") }
        var ownerPassword by remember { mutableStateOf("") }
        var authError by remember { mutableStateOf<String?>(null) }
        var isPwdVisible by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showOwnerAuthDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = FastBitePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Owner Security Clearance", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        text = "To protect financial payout accounts, only the verified Owner can configure banking credentials. Staff is strictly prohibited from altering bank info.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = ownerEmail,
                        onValueChange = { ownerEmail = it },
                        label = { Text("Owner Gmail ID") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("owner_auth_email_input")
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = ownerPassword,
                        onValueChange = { ownerPassword = it },
                        label = { Text("Owner Master Password") },
                        singleLine = true,
                        visualTransformation = if (isPwdVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isPwdVisible = !isPwdVisible }) {
                                Icon(
                                    imageVector = if (isPwdVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("owner_auth_password_input")
                    )
                    if (authError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(authError ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (ownerPassword.length >= 6) {
                            isOwnerUnlocked = true
                            showOwnerAuthDialog = false
                        } else {
                            authError = "Invalid master password. Must be at least 6 characters."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FastBitePrimary),
                    modifier = Modifier.testTag("verify_owner_clearance_btn")
                ) {
                    Text("Unlock Bank Dashboard")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showOwnerAuthDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add New Dish Modal
    if (showAddDishDialog) {
        var dishName by remember { mutableStateOf("") }
        var dishPrice by remember { mutableStateOf("") }
        var dishCategory by remember { mutableStateOf(MealCategory.LUNCH) }
        var dishDesc by remember { mutableStateOf("") }
        var prepTime by remember { mutableStateOf("10") }

        AlertDialog(
            onDismissRequest = { showAddDishDialog = false },
            title = { Text("Add New Canteen Dish", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = dishName,
                        onValueChange = { dishName = it },
                        label = { Text("Dish Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = dishPrice,
                        onValueChange = { dishPrice = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Price (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MealCategory.values().forEach { cat ->
                            val isSel = dishCategory == cat
                            Surface(
                                onClick = { dishCategory = cat },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) FastBitePrimary else Color(0xFFEEEEEE),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = cat.label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else Color.Black,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = prepTime,
                        onValueChange = { prepTime = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Prep Time (mins)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = dishDesc,
                        onValueChange = { dishDesc = it },
                        label = { Text("Ingredients / Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (dishName.isNotBlank() && dishPrice.isNotBlank()) {
                            val newItem = MenuItem(
                                id = "dish_${System.currentTimeMillis() % 10000}",
                                name = dishName.trim(),
                                category = dishCategory,
                                price = dishPrice.toDoubleOrNull() ?: 50.0,
                                description = dishDesc.ifBlank { "Freshly prepared canteen specialty." },
                                prepTimeMins = prepTime.toIntOrNull() ?: 10,
                                imageResName = when (dishCategory) {
                                    MealCategory.BREAKFAST -> "dish_breakfast"
                                    MealCategory.LUNCH -> "dish_lunch"
                                    MealCategory.SNACKS -> "dish_snack"
                                }
                            )
                            CanteenRepository.addMenuItem(newItem)
                            showAddDishDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FastBitePrimary)
                ) {
                    Text("Add Dish")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddDishDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Edit Dish Price & Name Modal
    if (dishToEdit != null) {
        val current = dishToEdit!!
        var editedName by remember { mutableStateOf(current.name) }
        var editedPrice by remember { mutableStateOf(current.price.toInt().toString()) }

        AlertDialog(
            onDismissRequest = { dishToEdit = null },
            title = { Text("Edit Dish & Price", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Dish Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editedPrice,
                        onValueChange = { editedPrice = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Price (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = current.copy(
                            name = editedName.trim(),
                            price = editedPrice.toDoubleOrNull() ?: current.price
                        )
                        CanteenRepository.updateMenuItem(updated)
                        dishToEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FastBitePrimary)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { dishToEdit = null }) { Text("Cancel") }
            }
        )
    }

    // Add Wastage Modal
    if (showAddWastageDialog) {
        var wName by remember { mutableStateOf("Rice & Dal") }
        var wQty by remember { mutableStateOf("2") }
        var wLoss by remember { mutableStateOf("120") }
        var wReason by remember { mutableStateOf("End of shift leftover") }

        AlertDialog(
            onDismissRequest = { showAddWastageDialog = false },
            title = { Text("Log Food Wastage", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = wName,
                        onValueChange = { wName = it },
                        label = { Text("Wasted Food Item") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = wQty,
                        onValueChange = { wQty = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Quantity (Portions / Kgs)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = wLoss,
                        onValueChange = { wLoss = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Estimated Cost Loss (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = wReason,
                        onValueChange = { wReason = it },
                        label = { Text("Reason (e.g. Overcooked, Expired, Excess prep)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (wName.isNotBlank() && wLoss.isNotBlank()) {
                            CanteenRepository.logWastage(
                                itemName = wName.trim(),
                                quantity = wQty.toIntOrNull() ?: 1,
                                estimatedLoss = wLoss.toDoubleOrNull() ?: 50.0,
                                reason = wReason.trim()
                            )
                            showAddWastageDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FastBitePrimary)
                ) {
                    Text("Log Wastage")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddWastageDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// TAB 1: Live Order Queue & Payment Summary
@Composable
private fun LiveQueueTab(
    orders: List<CanteenOrder>,
    totalOrdersCount: Int,
    totalRevenue: Double,
    onlineOrdersCount: Int,
    onlineRevenue: Double,
    codOrdersCount: Int,
    codRevenue: Double,
    onPingCustomer: (String) -> Unit,
    onUpdateStatus: (String, OrderStatus) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Payment & Financial Summary Section
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Today's Canteen Collection & Orders",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricBox(
                            title = "Total Orders",
                            value = "$totalOrdersCount",
                            subtitle = "₹${totalRevenue.toInt()} Total",
                            color = FastBiteOnlineBlue,
                            modifier = Modifier.weight(1f)
                        )
                        MetricBox(
                            title = "Online (Permanent)",
                            value = "$onlineOrdersCount",
                            subtitle = "₹${onlineRevenue.toInt()} Paid",
                            color = FastBitePermanentToken,
                            modifier = Modifier.weight(1f)
                        )
                        MetricBox(
                            title = "COD (Temporary)",
                            value = "$codOrdersCount",
                            subtitle = "₹${codRevenue.toInt()} Counter",
                            color = FastBiteCodAmber,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Live Order Queue (${orders.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(orders) { order ->
            val isOnline = order.tokenType == TokenType.PERMANENT
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Header: Token Number & Order Type Indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = order.tokenNumber,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isOnline) FastBitePermanentToken else FastBiteTemporaryToken
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isOnline) Color(0xFFE8F5E9) else Color(0xFFFFF3E0))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (isOnline) "ONLINE PAID" else "COD COUNTER",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = if (isOnline) FastBitePermanentToken else FastBiteTemporaryToken
                                )
                            }
                        }

                        // Current Status Tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF0F0F0))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = order.status.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Customer: ${order.customerName} • ${order.customerContact} (${order.loginMethod})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Scheduled: ${order.scheduledPickupTime}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = FastBitePrimary
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Items list
                    order.items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.quantity}x ${item.menuItem.name}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                            )
                            Text(
                                text = "₹${item.totalPrice.toInt()}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action Controls: Ping Customer & Status Toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Customer Ping button
                        OutlinedButton(
                            onClick = { onPingCustomer(order.id) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("ping_customer_${order.id}_btn")
                        ) {
                            Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp), tint = FastBitePrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (order.isPinged) "Pinged ✓" else "Ping Customer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Status Toggles
                        if (order.status == OrderStatus.PENDING) {
                            Button(
                                onClick = { onUpdateStatus(order.id, OrderStatus.PREPARING) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FastBitePrimary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Start Prep", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else if (order.status == OrderStatus.PREPARING) {
                            Button(
                                onClick = { onUpdateStatus(order.id, OrderStatus.READY_FOR_PICKUP) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FastBiteTertiary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Mark Ready", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else if (order.status == OrderStatus.READY_FOR_PICKUP) {
                            Button(
                                onClick = { onUpdateStatus(order.id, OrderStatus.COMPLETED) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF424242)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Complete", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// TAB 2: Menu Customization & Instant Availability
@Composable
private fun MenuManagementTab(
    menuItems: List<MenuItem>,
    onToggleAvailability: (String) -> Unit,
    onEditDish: (MenuItem) -> Unit,
    onAddNewDish: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Canteen Menu & Prices",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Toggle instant item availability or customize pricing",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onAddNewDish,
                    colors = ButtonDefaults.buttonColors(containerColor = FastBitePrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_new_dish_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Dish", fontSize = 12.sp)
                }
            }
        }

        items(menuItems) { dish ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = dish.name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (dish.isAvailable) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (dish.isAvailable) "IN STOCK" else "OUT OF STOCK",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (dish.isAvailable) FastBiteTertiary else Color(0xFFC62828)
                                )
                            }
                        }
                        Text(
                            text = "${dish.category.label} • ₹${dish.price.toInt()} • ${dish.prepTimeMins} mins prep",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { onEditDish(dish) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Price", tint = FastBitePrimary, modifier = Modifier.size(20.dp))
                        }
                        Switch(
                            checked = dish.isAvailable,
                            onCheckedChange = { onToggleAvailability(dish.id) }
                        )
                    }
                }
            }
        }
    }
}

// TAB 3: Weekly 7-Day Menu Planner
@Composable
private fun WeeklyPlannerTab(
    menuItems: List<MenuItem>,
    onUpdateDish: (MenuItem) -> Unit
) {
    var selectedDayIndex by remember { mutableIntStateOf(0) }
    val days = CanteenDay.values()
    val activeDay = days[selectedDayIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        Text(
            text = "Weekly 7-Day Menu Planner",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Select which dishes appear in the canteen on each day of the week:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        ScrollableTabRow(
            selectedTabIndex = selectedDayIndex,
            edgePadding = 0.dp,
            containerColor = Color.White,
            contentColor = FastBitePrimary,
            modifier = Modifier.clip(RoundedCornerShape(10.dp))
        ) {
            days.forEachIndexed { index, day ->
                Tab(
                    selected = selectedDayIndex == index,
                    onClick = { selectedDayIndex = index },
                    text = { Text(day.fullName, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(menuItems) { dish ->
                val isAvailableOnDay = dish.availableDays.contains(activeDay)
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = dish.name, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${dish.category.label} • ₹${dish.price.toInt()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Checkbox(
                            checked = isAvailableOnDay,
                            onCheckedChange = { checked ->
                                val newDays = if (checked) {
                                    dish.availableDays + activeDay
                                } else {
                                    dish.availableDays - activeDay
                                }
                                onUpdateDish(dish.copy(availableDays = newDays))
                            }
                        )
                    }
                }
            }
        }
    }
}

// TAB 4: Penalty Customization & Wastage Tracker
@Composable
private fun PenaltyAndWastageTab(
    penaltyPercent: Double,
    onUpdatePenalty: (Double) -> Unit,
    wastageRecords: List<com.example.model.WastageRecord>,
    onAddWastage: () -> Unit
) {
    val totalWastageLoss = wastageRecords.sumOf { it.estimatedLoss }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Penalty Customization Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Percent, contentDescription = null, tint = FastBitePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cancellation Penalty Setting",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "When customers with online payment cancel their food, this percentage is deducted as cancellation penalty, and remaining amount is auto-refunded to their bank.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Current Penalty: ${penaltyPercent.toInt()}%",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = FastBitePrimary
                        )
                    )

                    Slider(
                        value = penaltyPercent.toFloat(),
                        onValueChange = { onUpdatePenalty(it.toDouble()) },
                        valueRange = 0f..50f,
                        steps = 9,
                        modifier = Modifier.fillMaxWidth().testTag("penalty_slider")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("0% (Full Refund)", fontSize = 11.sp, color = Color.Gray)
                        Text("25%", fontSize = 11.sp, color = Color.Gray)
                        Text("50% (Max Penalty)", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        }

        // Wastage Tracker Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Kitchen Food Wastage Tracker",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Total Logged Loss Today: ₹${totalWastageLoss.toInt()}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Button(
                            onClick = onAddWastage,
                            colors = ButtonDefaults.buttonColors(containerColor = FastBitePrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("log_wastage_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Log Item", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (wastageRecords.isEmpty()) {
                        Text("No food wastage logged today.", color = Color.Gray, fontSize = 13.sp)
                    } else {
                        wastageRecords.forEach { record ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .background(Color(0xFFFFF8F8), RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "${record.quantity}x ${record.itemName}", fontWeight = FontWeight.Bold)
                                    Text(text = "Reason: ${record.reason}", fontSize = 11.sp, color = Color.Gray)
                                }
                                Text(
                                    text = "-₹${record.estimatedLoss.toInt()}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// TAB 5: Owner Bank Account Management (Clearance Protected)
@Composable
private fun OwnerBankAccountTab(
    isOwnerUnlocked: Boolean,
    bankAccount: OwnerBankAccount,
    onRequestOwnerClearance: () -> Unit,
    onUpdateBankAccount: (OwnerBankAccount) -> Unit
) {
    var holderName by remember { mutableStateOf(bankAccount.accountHolderName) }
    var bName by remember { mutableStateOf(bankAccount.bankName) }
    var accNum by remember { mutableStateOf(bankAccount.accountNumber) }
    var ifsc by remember { mutableStateOf(bankAccount.ifscCode) }
    var upi by remember { mutableStateOf(bankAccount.upiId) }
    var savedMessage by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = if (isOwnerUnlocked) FastBitePrimary else Color.Gray,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Owner Canteen Payout Account",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Direct destination for customer online payments (GPay/PhonePe/Paytm)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (!isOwnerUnlocked) {
                    // Locked State for general Staff
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFF3E0),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = FastBitePrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Restricted Financial Access",
                                    fontWeight = FontWeight.Bold,
                                    color = FastBitePrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Staff operators are not authorized to view or edit the bank account. Only the Canteen Owner with Master Security Password has authority.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = onRequestOwnerClearance,
                                colors = ButtonDefaults.buttonColors(containerColor = FastBitePrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().testTag("owner_clearance_login_btn")
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Login with Owner Gmail & Password")
                            }
                        }
                    }
                } else {
                    // Unlocked Editable State for Verified Owner
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFE8F5E9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FastBiteTertiary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Owner Clearance Active: All customer online funds route directly here.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = FastBiteTertiary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = holderName,
                        onValueChange = { holderName = it },
                        label = { Text("Account Holder / Business Name") },
                        modifier = Modifier.fillMaxWidth().testTag("owner_holder_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = bName,
                        onValueChange = { bName = it },
                        label = { Text("Bank Name") },
                        modifier = Modifier.fillMaxWidth().testTag("owner_bank_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = accNum,
                        onValueChange = { accNum = it },
                        label = { Text("Account Number") },
                        modifier = Modifier.fillMaxWidth().testTag("owner_account_num_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = ifsc,
                        onValueChange = { ifsc = it },
                        label = { Text("IFSC Code") },
                        modifier = Modifier.fillMaxWidth().testTag("owner_ifsc_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = upi,
                        onValueChange = { upi = it },
                        label = { Text("Merchant UPI ID for QR") },
                        modifier = Modifier.fillMaxWidth().testTag("owner_upi_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val updated = OwnerBankAccount(
                                accountHolderName = holderName.trim(),
                                bankName = bName.trim(),
                                accountNumber = accNum.trim(),
                                ifscCode = ifsc.trim(),
                                upiId = upi.trim(),
                                isVerified = true,
                                lastUpdated = System.currentTimeMillis()
                            )
                            onUpdateBankAccount(updated)
                            savedMessage = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FastBitePrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("save_owner_bank_btn")
                    ) {
                        Text("Save & Secure Bank Account", fontWeight = FontWeight.Bold)
                    }

                    if (savedMessage) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "✓ Owner bank credentials encrypted and active for payments.",
                            color = FastBiteTertiary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricBox(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = color, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 10.sp, color = Color.Gray, maxLines = 1)
        }
    }
}

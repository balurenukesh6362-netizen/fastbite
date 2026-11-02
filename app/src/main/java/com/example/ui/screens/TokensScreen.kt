package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CanteenRepository
import com.example.model.CanteenOrder
import com.example.model.OrderStatus
import com.example.model.TokenType
import com.example.ui.theme.FastBiteCodAmber
import com.example.ui.theme.FastBitePermanentToken
import com.example.ui.theme.FastBitePrimary
import com.example.ui.theme.FastBiteSecondary
import com.example.ui.theme.FastBiteTemporaryToken
import com.example.ui.theme.FastBiteTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokensScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val orders by CanteenRepository.orders.collectAsState()
    val penaltyPercent by CanteenRepository.cancellationPenaltyPercent.collectAsState()

    var orderToCancel by remember { mutableStateOf<CanteenOrder?>(null) }
    var orderToRate by remember { mutableStateOf<CanteenOrder?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Canteen Tokens", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("tokens_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        if (orders.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFFBF8F5)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No active tokens yet.\nOrder something delicious to get your token!",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFFBF8F5))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(orders) { order ->
                    TokenCard(
                        order = order,
                        onCancelClick = { orderToCancel = order },
                        onRateClick = { orderToRate = order }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    // Cancellation & Auto-Refund Modal
    if (orderToCancel != null) {
        val order = orderToCancel!!
        val isOnline = order.tokenType == TokenType.PERMANENT
        val penaltyRate = order.penaltyPercentage
        val penaltyAmount = if (isOnline) (order.totalAmount * penaltyRate) / 100.0 else 0.0
        val netRefund = if (isOnline) order.totalAmount - penaltyAmount else 0.0

        AlertDialog(
            onDismissRequest = { orderToCancel = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancel Food Order?")
                }
            },
            text = {
                Column {
                    Text(
                        text = "Are you sure you want to cancel Token ${order.tokenNumber}?",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isOnline) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFF3E0),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Refund Breakdown (Online Payment)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = FastBitePrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Order Total:", fontSize = 12.sp)
                                    Text("₹${order.totalAmount.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Cancellation Penalty (${penaltyRate.toInt()}%):", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                                    Text("-₹${penaltyAmount.toInt()}", fontSize = 12.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                                }
                                Divider(modifier = Modifier.padding(vertical = 4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Net Refund to Your Account:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("₹${netRefund.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = FastBiteTertiary)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Refund automatically credited to your original payment UPI / Bank within minutes.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "This was a Cash on Delivery order. Your temporary token will be voided without charges.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        CanteenRepository.cancelOrder(order.id)
                        orderToCancel = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_cancel_order_btn")
                ) {
                    Text("Confirm Cancellation")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { orderToCancel = null }) {
                    Text("Keep Order")
                }
            }
        )
    }

    // Rating Modal
    if (orderToRate != null) {
        val order = orderToRate!!
        var starRating by remember { mutableFloatStateOf(5f) }
        var comment by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { orderToRate = null },
            title = {
                Text("Rate Your Food", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("How was your meal from ${order.items.joinToString { it.menuItem.name }}?")

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.Center) {
                        for (i in 1..5) {
                            IconButton(onClick = { starRating = i.toFloat() }) {
                                Icon(
                                    imageVector = if (i <= starRating) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "$i Stars",
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Write a quick review") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        CanteenRepository.rateOrder(order.id, starRating, comment)
                        orderToRate = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FastBitePrimary),
                    modifier = Modifier.testTag("submit_rating_btn")
                ) {
                    Text("Submit Rating")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { orderToRate = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun TokenCard(
    order: CanteenOrder,
    onCancelClick: () -> Unit,
    onRateClick: () -> Unit
) {
    val isOnline = order.tokenType == TokenType.PERMANENT
    val isCancelled = order.status == OrderStatus.CANCELLED
    val isReady = order.status == OrderStatus.READY_FOR_PICKUP
    val isCompleted = order.status == OrderStatus.COMPLETED

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Token Badge and Type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Token Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isOnline) Color(0xFFE8F5E9) else Color(0xFFFFF3E0))
                        .border(
                            1.5.dp,
                            if (isOnline) FastBitePermanentToken else FastBiteTemporaryToken,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = order.tokenNumber,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isOnline) FastBitePermanentToken else FastBiteTemporaryToken
                            )
                        )
                        Text(
                            text = if (isOnline) "PERMANENT TOKEN" else "TEMPORARY TOKEN",
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = if (isOnline) FastBitePermanentToken else FastBiteTemporaryToken
                        )
                    }
                }

                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (order.status) {
                                OrderStatus.PENDING -> Color(0xFFE3F2FD)
                                OrderStatus.PREPARING -> Color(0xFFFFF3E0)
                                OrderStatus.READY_FOR_PICKUP -> Color(0xFFE8F5E9)
                                OrderStatus.COMPLETED -> Color(0xFFE0E0E0)
                                OrderStatus.CANCELLED -> Color(0xFFFFEBEE)
                            }
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = order.status.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when (order.status) {
                                OrderStatus.PENDING -> Color(0xFF1565C0)
                                OrderStatus.PREPARING -> FastBitePrimary
                                OrderStatus.READY_FOR_PICKUP -> FastBiteTertiary
                                OrderStatus.COMPLETED -> Color(0xFF424242)
                                OrderStatus.CANCELLED -> Color(0xFFC62828)
                            }
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Ready Notification Banner
            if (isReady) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFE8F5E9),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = FastBiteTertiary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "🔔 FOOD READY FOR PICKUP!",
                                fontWeight = FontWeight.Bold,
                                color = FastBiteTertiary,
                                fontSize = 13.sp
                            )
                            val channel = if (order.loginMethod == "Phone") "WhatsApp (+91 ${order.customerContact.takeLast(10)})" else "Gmail (${order.customerContact})"
                            Text(
                                text = "Notification dispatched to $channel",
                                fontSize = 11.sp,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Order Items & Schedule
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                order.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${item.quantity}x ${item.menuItem.name}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "₹${item.totalPrice.toInt()}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Scheduled: ${order.scheduledPickupTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Total: ₹${order.totalAmount.toInt()} (${order.paymentMethod.displayName})",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = FastBitePrimary
                )
            }

            // If cancelled, show refund summary
            if (isCancelled) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Order Cancelled. Penalty: ${order.penaltyPercentage.toInt()}% • Refund Amount Credited: ₹${order.refundAmount.toInt()}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFC62828)
                    )
                }
            }

            // Rating display or action
            if (order.rating != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "You rated: ${order.rating}★ ${if (!order.reviewComment.isNullOrBlank()) "\"${order.reviewComment}\"" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE65100),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cancel button (allowed if not completed or cancelled)
                if (!isCompleted && !isCancelled) {
                    OutlinedButton(
                        onClick = onCancelClick,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("cancel_token_${order.id}_btn")
                    ) {
                        Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cancel Order", fontSize = 12.sp)
                    }
                }

                // Rate button (allowed if ready or completed and not yet rated)
                if ((isReady || isCompleted) && order.rating == null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onRateClick,
                        colors = ButtonDefaults.buttonColors(containerColor = FastBiteSecondary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("rate_food_${order.id}_btn")
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rate Food", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CanteenDay
import com.example.model.CanteenOrder
import com.example.model.CustomerUser
import com.example.model.MenuItem
import com.example.model.OrderStatus
import com.example.model.TokenType
import com.example.ui.theme.FastBitePermanentToken
import com.example.ui.theme.FastBitePrimary
import com.example.ui.theme.FastBiteSecondary
import com.example.ui.theme.FastBiteTemporaryToken
import com.example.ui.theme.FastBiteTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 1. Weekly 7 Days Calendar Dialog
@Composable
fun WeeklyCalendarDialog(
    menuItems: List<MenuItem>,
    onDismiss: () -> Unit
) {
    var selectedDayIndex by remember { mutableIntStateOf(0) }
    val days = CanteenDay.values()
    val activeDay = days[selectedDayIndex]

    val dayDishes = menuItems.filter { it.availableDays.contains(activeDay) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = FastBitePrimary,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Weekly 7-Day Menu",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Explore specials and regular canteen dishes for the entire week.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                ScrollableTabRow(
                    selectedTabIndex = selectedDayIndex,
                    edgePadding = 0.dp,
                    containerColor = Color(0xFFF5F5F5),
                    contentColor = FastBitePrimary,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                ) {
                    days.forEachIndexed { index, day ->
                        Tab(
                            selected = selectedDayIndex == index,
                            onClick = { selectedDayIndex = index },
                            text = { Text(day.shortName, fontWeight = FontWeight.Bold) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${activeDay.fullName} Menu (${dayDishes.size} dishes available):",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = FastBitePrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(dayDishes) { dish ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = dish.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "${dish.category.label} • ${dish.prepTimeMins} mins",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "₹${dish.price.toInt()}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = FastBitePrimary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = FastBitePrimary)
            ) {
                Text("Close")
            }
        }
    )
}

// 2. Order History & Receipts Dialog
@Composable
fun OrderHistoryReceiptDialog(
    orders: List<CanteenOrder>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = FastBitePrimary,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Order History & Receipts",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            if (orders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No past orders found.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(orders) { order ->
                        val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(order.timestamp))
                        val isOnline = order.tokenType == TokenType.PERMANENT
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = order.tokenNumber,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isOnline) FastBitePermanentToken else FastBiteTemporaryToken
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                when (order.status) {
                                                    OrderStatus.COMPLETED -> Color(0xFFE8F5E9)
                                                    OrderStatus.CANCELLED -> Color(0xFFFFEBEE)
                                                    OrderStatus.READY_FOR_PICKUP -> Color(0xFFFFF8E1)
                                                    else -> Color(0xFFE3F2FD)
                                                }
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = order.status.displayName,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = when (order.status) {
                                                OrderStatus.COMPLETED -> FastBiteTertiary
                                                OrderStatus.CANCELLED -> Color(0xFFC62828)
                                                OrderStatus.READY_FOR_PICKUP -> FastBiteSecondary
                                                else -> Color(0xFF1565C0)
                                            }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Date: $dateStr • ${order.paymentMethod.displayName}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Divider(modifier = Modifier.padding(vertical = 6.dp))

                                order.items.forEach { item ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "${item.quantity}x ${item.menuItem.name}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = "₹${item.totalPrice.toInt()}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                                        )
                                    }
                                }

                                if (order.status == OrderStatus.CANCELLED) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Penalty (${order.penaltyPercentage.toInt()}%): -₹${((order.totalAmount * order.penaltyPercentage) / 100).toInt()} | Refund Credited: ₹${order.refundAmount.toInt()}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFC62828),
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Total Paid", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                        Text("₹${order.totalAmount.toInt()}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = FastBitePrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = FastBitePrimary)
            ) {
                Text("Done")
            }
        }
    )
}

// 3. Rules and Regulations Dialog
@Composable
fun RulesAndRegulationsDialog(
    onDismiss: () -> Unit
) {
    var isStaffRules by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = FastBitePrimary,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Rules & Regulations",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (!isStaffRules) FastBitePrimary else Color.Transparent)
                            .clickable { isStaffRules = false }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Customer Guidelines",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (!isStaffRules) Color.White else Color.Black
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isStaffRules) FastBitePrimary else Color.Transparent)
                            .clickable { isStaffRules = true }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Staff Protocols",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isStaffRules) Color.White else Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (!isStaffRules) {
                        item {
                            RuleItem(
                                number = "1",
                                title = "Token Presentation",
                                body = "Always present your Permanent (Green) or Temporary (Orange) token on your screen at the pickup counter."
                            )
                        }
                        item {
                            RuleItem(
                                number = "2",
                                title = "Cash on Delivery Orders",
                                body = "Temporary tokens require exact payment or counter UPI scan before receiving your meal."
                            )
                        }
                        item {
                            RuleItem(
                                number = "3",
                                title = "Cancellation & Refund Policy",
                                body = "You can cancel before the order is marked 'Preparing'. Online payments receive an immediate automatic refund minus staff-configured penalty percent."
                            )
                        }
                        item {
                            RuleItem(
                                number = "4",
                                title = "Canteen Hygiene",
                                body = "Return stainless steel plates, bowls, and cutlery to the designated wash station after dining."
                            )
                        }
                    } else {
                        item {
                            RuleItem(
                                number = "1",
                                title = "Token Verification",
                                body = "Kitchen operators must strictly check token numbers on the customer screen before handing over any tray."
                            )
                        }
                        item {
                            RuleItem(
                                number = "2",
                                title = "Hygiene & Food Safety",
                                body = "All kitchen handlers must wear hair nets, clean aprons, and use clean tongs/ladles at all times."
                            )
                        }
                        item {
                            RuleItem(
                                number = "3",
                                title = "Owner Bank Security",
                                body = "Only the Master Owner with verified clearance can view or modify the payout bank account. Staff cannot modify financial settings."
                            )
                        }
                        item {
                            RuleItem(
                                number = "4",
                                title = "Prompt Customer Ping",
                                body = "Immediately toggle status to 'Ready for Pickup' and ping customers via WhatsApp/Email to avoid counter congestion."
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = FastBitePrimary)
            ) {
                Text("Got It")
            }
        }
    )
}

@Composable
private fun RuleItem(number: String, title: String, body: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFE0B2)),
            contentAlignment = Alignment.Center
        ) {
            Text(number, fontWeight = FontWeight.Bold, color = FastBitePrimary, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
            Text(body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// 4. Customer Profile Dialog
@Composable
fun CustomerProfileDialog(
    user: CustomerUser,
    onLogout: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = FastBitePrimary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Customer Profile", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF3E0), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = FastBitePrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(user.contact, style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(user.email, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FastBiteTertiary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Logged in via ${if (user.isGoogleLogin) "Google Account" else "Verified Phone Number"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = FastBiteTertiary
                    )
                }

                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("customer_logout_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout / Switch Account")
                }
            }
        },
        confirmButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

// 5. Customer Feedback Dialog
@Composable
fun CustomerFeedbackDialog(
    onDismiss: () -> Unit
) {
    var feedbackText by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Feedback & Issue Report", fontWeight = FontWeight.Bold)
        },
        text = {
            if (submitted) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FastBiteTertiary, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Thank you!", fontWeight = FontWeight.Bold)
                    Text("Your feedback has been sent to FastBite canteen manager.", fontSize = 13.sp)
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Report food quality, service delays, or app issues directly to management:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = feedbackText,
                        onValueChange = { feedbackText = it },
                        label = { Text("Your comments") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 4
                    )
                }
            }
        },
        confirmButton = {
            if (!submitted) {
                Button(
                    onClick = {
                        if (feedbackText.isNotBlank()) submitted = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FastBitePrimary)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Submit")
                }
            } else {
                Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = FastBitePrimary)) {
                    Text("Close")
                }
            }
        },
        dismissButton = {
            if (!submitted) {
                OutlinedButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

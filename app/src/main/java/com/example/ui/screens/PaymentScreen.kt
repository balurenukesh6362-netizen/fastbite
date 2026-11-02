package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CanteenRepository
import com.example.model.CartItem
import com.example.model.PaymentMethod
import com.example.model.TokenType
import com.example.ui.theme.FastBiteCodAmber
import com.example.ui.theme.FastBiteOnlineBlue
import com.example.ui.theme.FastBitePermanentToken
import com.example.ui.theme.FastBitePrimary
import com.example.ui.theme.FastBiteSecondary
import com.example.ui.theme.FastBiteTemporaryToken
import com.example.ui.theme.FastBiteTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    itemsToOrder: List<CartItem>,
    scheduledTime: String,
    onPaymentComplete: (orderId: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMethod by remember { mutableStateOf(PaymentMethod.GOOGLE_PAY) }
    val totalAmount = itemsToOrder.sumOf { it.totalPrice }
    val ownerAccount by CanteenRepository.ownerBankAccount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment & Token Type", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("payment_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 12.dp,
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            val order = CanteenRepository.placeOrder(
                                items = itemsToOrder,
                                scheduledTime = scheduledTime,
                                paymentMethod = selectedMethod
                            )
                            onPaymentComplete(order.id)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("confirm_payment_and_generate_token_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedMethod.isOnline) FastBitePrimary else FastBiteCodAmber
                        )
                    ) {
                        Text(
                            text = if (selectedMethod.isOnline) {
                                "Pay ₹${totalAmount.toInt()} & Get Permanent Token"
                            } else {
                                "Confirm COD & Get Temporary Token"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
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
                .background(Color(0xFFFBF8F5))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Order Summary Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Order Amount",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "₹${totalAmount.toInt()}",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = FastBitePrimary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Scheduled: $scheduledTime • ${itemsToOrder.size} items",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Token Distinction Banner
            val isOnline = selectedMethod.isOnline
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isOnline) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isOnline) FastBitePermanentToken else FastBiteTemporaryToken),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isOnline) Icons.Default.VerifiedUser else Icons.Default.LocalAtm,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = if (isOnline) "You will receive a PERMANENT TOKEN" else "You will receive a TEMPORARY TOKEN",
                            fontWeight = FontWeight.Bold,
                            color = if (isOnline) FastBitePermanentToken else FastBiteTemporaryToken,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isOnline) {
                                "Online paid orders get priority kitchen dispatch and automatic cancellation refund!"
                            } else {
                                "Pay cash or scan UPI at the counter before collecting your food order."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Payment Options List
            Text(
                text = "Select Payment Method",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            // Online Options Group
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "ONLINE UPI (Permanent Token)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = FastBitePermanentToken
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                    )

                    PaymentOptionRow(
                        name = "Google Pay (GPay)",
                        subtitle = "Instant UPI with Google Account",
                        icon = Icons.Default.QrCode2,
                        isSelected = selectedMethod == PaymentMethod.GOOGLE_PAY,
                        onSelect = { selectedMethod = PaymentMethod.GOOGLE_PAY }
                    )

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    PaymentOptionRow(
                        name = "PhonePe UPI",
                        subtitle = "Fast UPI payment",
                        icon = Icons.Default.Payments,
                        isSelected = selectedMethod == PaymentMethod.PHONEPE,
                        onSelect = { selectedMethod = PaymentMethod.PHONEPE }
                    )

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    PaymentOptionRow(
                        name = "Paytm Wallet / UPI",
                        subtitle = "Paytm Instant Gateway",
                        icon = Icons.Default.CreditCard,
                        isSelected = selectedMethod == PaymentMethod.PAYTM,
                        onSelect = { selectedMethod = PaymentMethod.PAYTM }
                    )
                }
            }

            // Cash on Delivery Option Group
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "OFFLINE COUNTER (Temporary Token)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = FastBiteCodAmber
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                    )

                    PaymentOptionRow(
                        name = "Cash on Delivery (COD)",
                        subtitle = "Pay at canteen counter when taking your food",
                        icon = Icons.Default.LocalAtm,
                        isSelected = selectedMethod == PaymentMethod.CASH_ON_DELIVERY,
                        onSelect = { selectedMethod = PaymentMethod.CASH_ON_DELIVERY }
                    )
                }
            }

            // Safe Bank Routing Info
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF1F8E9),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = FastBiteTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Direct Canteen Owner Account Routing",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = FastBiteTertiary
                        )
                        Text(
                            text = "Funds securely transfer to Owner: ${ownerAccount.bankName} (${ownerAccount.upiId}). Protected from unauthorized staff intervention.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF33691E)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun PaymentOptionRow(
    name: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onSelect)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color(0xFFFFE0B2) else Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) FastBitePrimary else Color.Gray,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isSelected) FastBitePrimary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
            contentDescription = if (isSelected) "Selected" else "Unselected",
            tint = if (isSelected) FastBitePrimary else Color.LightGray
        )
    }
}

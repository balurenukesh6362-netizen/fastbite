package com.example.model

enum class MealCategory(val label: String) {
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    SNACKS("Snacks")
}

enum class PaymentMethod(val displayName: String, val isOnline: Boolean) {
    PAYTM("Paytm UPI", true),
    GOOGLE_PAY("Google Pay (GPay)", true),
    PHONEPE("PhonePe UPI", true),
    CASH_ON_DELIVERY("Cash on Delivery (Pay at Counter)", false)
}

enum class TokenType(val label: String, val description: String) {
    PERMANENT("Permanent Token", "Online Paid - Priority Kitchen Dispatch"),
    TEMPORARY("Temporary Token", "Cash on Delivery - Verify & Pay at Counter")
}

enum class OrderStatus(val displayName: String) {
    PENDING("Order Received"),
    PREPARING("Preparing in Kitchen"),
    READY_FOR_PICKUP("Ready for Pickup!"),
    COMPLETED("Picked Up & Enjoyed"),
    CANCELLED("Cancelled")
}

enum class CanteenDay(val shortName: String, val fullName: String) {
    MON("Mon", "Monday"),
    TUE("Tue", "Tuesday"),
    WED("Wed", "Wednesday"),
    THU("Thu", "Thursday"),
    FRI("Fri", "Friday"),
    SAT("Sat", "Saturday"),
    SUN("Sun", "Sunday")
}

data class MenuItem(
    val id: String,
    val name: String,
    val category: MealCategory,
    val price: Double,
    val description: String,
    val isVeg: Boolean = true,
    val isAvailable: Boolean = true,
    val rating: Float = 4.8f,
    val ratingCount: Int = 120,
    val prepTimeMins: Int = 10,
    val imageResName: String = "dish_lunch",
    val availableDays: List<CanteenDay> = CanteenDay.values().toList(),
    val isTrending: Boolean = false,
    val isCombo: Boolean = false
)

data class CartItem(
    val menuItem: MenuItem,
    val quantity: Int = 1
) {
    val totalPrice: Double get() = menuItem.price * quantity
}

data class CanteenOrder(
    val id: String,
    val tokenNumber: String,
    val tokenType: TokenType,
    val items: List<CartItem>,
    val scheduledPickupTime: String,
    val paymentMethod: PaymentMethod,
    val totalAmount: Double,
    val penaltyPercentage: Double = 10.0,
    val refundAmount: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    val customerName: String,
    val customerContact: String,
    val loginMethod: String = "Google", // "Google" or "Phone"
    val timestamp: Long = System.currentTimeMillis(),
    val rating: Float? = null,
    val reviewComment: String? = null,
    val cancellationReason: String? = null,
    val isPinged: Boolean = false
)

data class WastageRecord(
    val id: String,
    val itemName: String,
    val quantity: Int,
    val estimatedLoss: Double,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class OwnerBankAccount(
    val accountHolderName: String = "FastBite Canteen Master Operations",
    val bankName: String = "HDFC Bank Ltd.",
    val accountNumber: String = "50200084920194",
    val ifscCode: String = "HDFC0001234",
    val upiId: String = "fastbitecanteen@hdfcbank",
    val isVerified: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis()
)

data class CustomerUser(
    val name: String,
    val contact: String,
    val email: String,
    val isGoogleLogin: Boolean
)

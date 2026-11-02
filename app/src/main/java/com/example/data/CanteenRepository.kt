package com.example.data

import com.example.model.CanteenDay
import com.example.model.CanteenOrder
import com.example.model.CartItem
import com.example.model.CustomerUser
import com.example.model.MealCategory
import com.example.model.MenuItem
import com.example.model.OrderStatus
import com.example.model.OwnerBankAccount
import com.example.model.PaymentMethod
import com.example.model.TokenType
import com.example.model.WastageRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

object CanteenRepository {

    // Initial Menu items
    private val initialMenuItems = listOf(
        // Breakfast
        MenuItem(
            id = "bf_1",
            name = "Crispy Masala Dosa Combo",
            category = MealCategory.BREAKFAST,
            price = 60.0,
            description = "Crispy golden dosa stuffed with spiced potato masala, served with fresh coconut chutney, tomato chutney & hot sambar.",
            isVeg = true,
            isAvailable = true,
            rating = 4.9f,
            ratingCount = 240,
            prepTimeMins = 8,
            imageResName = "dish_breakfast",
            availableDays = CanteenDay.values().toList(),
            isTrending = true
        ),
        MenuItem(
            id = "bf_2",
            name = "Steaming Idli & Medu Vada Platter",
            category = MealCategory.BREAKFAST,
            price = 50.0,
            description = "2 fluffy steamed rice idlis and 1 crispy medu vada accompanied by fresh chutneys and piping hot drumstick sambar.",
            isVeg = true,
            isAvailable = true,
            rating = 4.7f,
            ratingCount = 180,
            prepTimeMins = 5,
            imageResName = "dish_breakfast",
            availableDays = listOf(CanteenDay.MON, CanteenDay.TUE, CanteenDay.WED, CanteenDay.THU, CanteenDay.FRI, CanteenDay.SAT)
        ),
        MenuItem(
            id = "bf_3",
            name = "Indori Poha with Roasted Peanuts",
            category = MealCategory.BREAKFAST,
            price = 35.0,
            description = "Flavorful flattened rice tempered with mustard, curry leaves, turmeric, topped with crunchy peanuts & fresh sev.",
            isVeg = true,
            isAvailable = true,
            rating = 4.6f,
            ratingCount = 135,
            prepTimeMins = 4,
            imageResName = "dish_breakfast",
            availableDays = CanteenDay.values().toList()
        ),
        MenuItem(
            id = "bf_4",
            name = "South Indian Filter Coffee",
            category = MealCategory.BREAKFAST,
            price = 25.0,
            description = "Authentic chicory-infused decoction frothed with thick steaming milk in traditional steel tumbler.",
            isVeg = true,
            isAvailable = true,
            rating = 4.9f,
            ratingCount = 310,
            prepTimeMins = 3,
            imageResName = "dish_breakfast",
            availableDays = CanteenDay.values().toList()
        ),

        // Lunch
        MenuItem(
            id = "ln_1",
            name = "Royal Deluxe Lunch Thali",
            category = MealCategory.LUNCH,
            price = 120.0,
            description = "Full canteen feast: Paneer Butter Masala, Dal Makhani, Jeera Rice, 3 Butter Rotis, Cucumber Raita, Papad & Gulab Jamun.",
            isVeg = true,
            isAvailable = true,
            rating = 4.9f,
            ratingCount = 420,
            prepTimeMins = 12,
            imageResName = "dish_lunch",
            availableDays = CanteenDay.values().toList(),
            isTrending = true,
            isCombo = true
        ),
        MenuItem(
            id = "ln_2",
            name = "Hyderabadi Veg Dum Biryani",
            category = MealCategory.LUNCH,
            price = 110.0,
            description = "Slow cooked basmati rice layered with garden veggies, fried onions, saffron & aromatic spices. Served with Mirchi Ka Salan.",
            isVeg = true,
            isAvailable = true,
            rating = 4.8f,
            ratingCount = 295,
            prepTimeMins = 10,
            imageResName = "dish_lunch",
            availableDays = listOf(CanteenDay.TUE, CanteenDay.WED, CanteenDay.FRI, CanteenDay.SUN)
        ),
        MenuItem(
            id = "ln_3",
            name = "Paneer Tikka & Butter Naan",
            category = MealCategory.LUNCH,
            price = 99.0,
            description = "Charred tandoori cottage cheese cubes in rich spiced gravy with 2 fluffy tandoor naans and pickled onions.",
            isVeg = true,
            isAvailable = true,
            rating = 4.7f,
            ratingCount = 210,
            prepTimeMins = 11,
            imageResName = "dish_lunch",
            availableDays = CanteenDay.values().toList()
        ),
        MenuItem(
            id = "ln_4",
            name = "South Indian Mini Meal Box",
            category = MealCategory.LUNCH,
            price = 75.0,
            description = "Comfort meal of Sambar Rice, Curd Rice, Potato Kara Curry, Fried Papad and pickle.",
            isVeg = true,
            isAvailable = true,
            rating = 4.6f,
            ratingCount = 160,
            prepTimeMins = 5,
            imageResName = "dish_lunch",
            availableDays = CanteenDay.values().toList()
        ),

        // Snacks
        MenuItem(
            id = "sn_1",
            name = "Crispy Samosa (2 Pcs) & Cutting Chai",
            category = MealCategory.SNACKS,
            price = 45.0,
            description = "Flaky triangular pastries filled with spicy potato & green peas, served with sweet tamarind dip, spicy green chutney & hot cutting tea.",
            isVeg = true,
            isAvailable = true,
            rating = 4.9f,
            ratingCount = 380,
            prepTimeMins = 4,
            imageResName = "dish_snack",
            availableDays = CanteenDay.values().toList(),
            isTrending = true,
            isCombo = true
        ),
        MenuItem(
            id = "sn_2",
            name = "Canteen Grilled Cheese Sandwich",
            category = MealCategory.SNACKS,
            price = 65.0,
            description = "Triple layered toasted bread loaded with cheese, capsicum, onions, sweet corn and spicy cilantro chutney.",
            isVeg = true,
            isAvailable = true,
            rating = 4.7f,
            ratingCount = 190,
            prepTimeMins = 8,
            imageResName = "dish_snack",
            availableDays = CanteenDay.values().toList()
        ),
        MenuItem(
            id = "sn_3",
            name = "Crispy Vegetable Cutlet (2 Pcs)",
            category = MealCategory.SNACKS,
            price = 40.0,
            description = "Golden crumbed patties made with beets, carrots, potatoes & herbs, shallow fried crisp.",
            isVeg = true,
            isAvailable = true,
            rating = 4.6f,
            ratingCount = 140,
            prepTimeMins = 6,
            imageResName = "dish_snack",
            availableDays = CanteenDay.values().toList()
        ),
        MenuItem(
            id = "sn_4",
            name = "Masala Chai & Bun Maska",
            category = MealCategory.SNACKS,
            price = 35.0,
            description = "Freshly baked soft sweet bun loaded with Amul butter, paired with ginger-cardamom spiced milk tea.",
            isVeg = true,
            isAvailable = true,
            rating = 4.8f,
            ratingCount = 265,
            prepTimeMins = 3,
            imageResName = "dish_snack",
            availableDays = CanteenDay.values().toList()
        )
    )

    // Initial Orders
    private val initialOrders = listOf(
        CanteenOrder(
            id = "ORD-101",
            tokenNumber = "PERM-701",
            tokenType = TokenType.PERMANENT,
            items = listOf(
                CartItem(initialMenuItems[4], 1), // Royal Thali
                CartItem(initialMenuItems[3], 1)  // Filter Coffee
            ),
            scheduledPickupTime = "Lunch Break (1:15 PM)",
            paymentMethod = PaymentMethod.GOOGLE_PAY,
            totalAmount = 145.0,
            status = OrderStatus.PREPARING,
            customerName = "Rahul Sharma",
            customerContact = "+91 98765 43210",
            loginMethod = "Google",
            timestamp = System.currentTimeMillis() - 15 * 60 * 1000
        ),
        CanteenOrder(
            id = "ORD-102",
            tokenNumber = "TEMP-402",
            tokenType = TokenType.TEMPORARY,
            items = listOf(
                CartItem(initialMenuItems[8], 2) // Samosa Combo x2
            ),
            scheduledPickupTime = "Ready in 10 mins",
            paymentMethod = PaymentMethod.CASH_ON_DELIVERY,
            totalAmount = 90.0,
            status = OrderStatus.PENDING,
            customerName = "Priya Patel",
            customerContact = "+91 91234 56789",
            loginMethod = "Phone",
            timestamp = System.currentTimeMillis() - 8 * 60 * 1000
        ),
        CanteenOrder(
            id = "ORD-103",
            tokenNumber = "PERM-703",
            tokenType = TokenType.PERMANENT,
            items = listOf(
                CartItem(initialMenuItems[0], 2) // Masala Dosa x2
            ),
            scheduledPickupTime = "Breakfast (9:30 AM)",
            paymentMethod = PaymentMethod.PAYTM,
            totalAmount = 120.0,
            status = OrderStatus.COMPLETED,
            customerName = "Ananya Roy",
            customerContact = "ananya.roy@gmail.com",
            loginMethod = "Google",
            timestamp = System.currentTimeMillis() - 45 * 60 * 1000,
            rating = 5.0f,
            reviewComment = "Crispy dosas and very quick token pickup!"
        )
    )

    // Initial Wastage
    private val initialWastage = listOf(
        WastageRecord(
            id = "WST-1",
            itemName = "Indori Poha",
            quantity = 3,
            estimatedLoss = 105.0,
            reason = "Excess morning preparation"
        ),
        WastageRecord(
            id = "WST-2",
            itemName = "Butter Naan dough",
            quantity = 5,
            estimatedLoss = 150.0,
            reason = "Shift ended"
        )
    )

    // Observable states
    private val _menuItems = MutableStateFlow(initialMenuItems)
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()

    private val _orders = MutableStateFlow(initialOrders)
    val orders: StateFlow<List<CanteenOrder>> = _orders.asStateFlow()

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _cancellationPenaltyPercent = MutableStateFlow(10.0) // 10% penalty
    val cancellationPenaltyPercent: StateFlow<Double> = _cancellationPenaltyPercent.asStateFlow()

    private val _isAudioMuted = MutableStateFlow(false)
    val isAudioMuted: StateFlow<Boolean> = _isAudioMuted.asStateFlow()

    private val _wastageRecords = MutableStateFlow(initialWastage)
    val wastageRecords: StateFlow<List<WastageRecord>> = _wastageRecords.asStateFlow()

    private val _ownerBankAccount = MutableStateFlow(OwnerBankAccount())
    val ownerBankAccount: StateFlow<OwnerBankAccount> = _ownerBankAccount.asStateFlow()

    private val _currentCustomer = MutableStateFlow(
        CustomerUser(
            name = "Balurenukesh",
            contact = "+91 98450 12345",
            email = "balurenukeshbr@gmail.com",
            isGoogleLogin = true
        )
    )
    val currentCustomer: StateFlow<CustomerUser> = _currentCustomer.asStateFlow()

    // Notification message popups for customer
    private val _activeCustomerAlert = MutableStateFlow<String?>(null)
    val activeCustomerAlert: StateFlow<String?> = _activeCustomerAlert.asStateFlow()

    fun setCustomerUser(user: CustomerUser) {
        _currentCustomer.value = user
    }

    fun dismissCustomerAlert() {
        _activeCustomerAlert.value = null
    }

    fun toggleAudioMute() {
        _isAudioMuted.update { !it }
    }

    fun setCancellationPenaltyPercent(percent: Double) {
        _cancellationPenaltyPercent.value = percent
    }

    // Cart Operations
    fun addToCart(item: MenuItem, quantity: Int = 1) {
        _cart.update { currentList ->
            val existingIndex = currentList.indexOfFirst { it.menuItem.id == item.id }
            if (existingIndex >= 0) {
                currentList.mapIndexed { index, cartItem ->
                    if (index == existingIndex) {
                        cartItem.copy(quantity = cartItem.quantity + quantity)
                    } else cartItem
                }
            } else {
                currentList + CartItem(item, quantity)
            }
        }
    }

    fun updateCartItemQuantity(itemId: String, newQuantity: Int) {
        _cart.update { currentList ->
            if (newQuantity <= 0) {
                currentList.filterNot { it.menuItem.id == itemId }
            } else {
                currentList.map {
                    if (it.menuItem.id == itemId) it.copy(quantity = newQuantity) else it
                }
            }
        }
    }

    fun clearCart() {
        _cart.value = emptyList()
    }

    // Place Order
    fun placeOrder(
        items: List<CartItem>,
        scheduledTime: String,
        paymentMethod: PaymentMethod
    ): CanteenOrder {
        val isOnline = paymentMethod.isOnline
        val tokenType = if (isOnline) TokenType.PERMANENT else TokenType.TEMPORARY
        val tokenPrefix = if (isOnline) "PERM-" else "TEMP-"
        val randomNum = (100..999).random()
        val tokenNumber = "$tokenPrefix$randomNum"

        val subtotal = items.sumOf { it.totalPrice }
        val newOrder = CanteenOrder(
            id = "ORD-${System.currentTimeMillis() % 100000}",
            tokenNumber = tokenNumber,
            tokenType = tokenType,
            items = items,
            scheduledPickupTime = scheduledTime,
            paymentMethod = paymentMethod,
            totalAmount = subtotal,
            penaltyPercentage = _cancellationPenaltyPercent.value,
            status = OrderStatus.PENDING,
            customerName = _currentCustomer.value.name,
            customerContact = if (_currentCustomer.value.isGoogleLogin) _currentCustomer.value.email else _currentCustomer.value.contact,
            loginMethod = if (_currentCustomer.value.isGoogleLogin) "Google" else "Phone",
            timestamp = System.currentTimeMillis()
        )

        _orders.update { listOf(newOrder) + it }
        clearCart()
        return newOrder
    }

    // Cancel Order with Penalty & Auto-Refund
    fun cancelOrder(orderId: String, reason: String = "Customer requested cancellation") {
        _orders.update { list ->
            list.map { order ->
                if (order.id == orderId && order.status != OrderStatus.CANCELLED && order.status != OrderStatus.COMPLETED) {
                    val penaltyRate = order.penaltyPercentage
                    val penaltyAmount = if (order.tokenType == TokenType.PERMANENT) {
                        (order.totalAmount * penaltyRate) / 100.0
                    } else 0.0
                    val netRefund = if (order.tokenType == TokenType.PERMANENT) {
                        order.totalAmount - penaltyAmount
                    } else 0.0

                    order.copy(
                        status = OrderStatus.CANCELLED,
                        refundAmount = netRefund,
                        cancellationReason = reason
                    )
                } else order
            }
        }
    }

    // Staff toggle status
    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        _orders.update { list ->
            list.map {
                if (it.id == orderId) it.copy(status = newStatus) else it
            }
        }
        if (newStatus == OrderStatus.READY_FOR_PICKUP) {
            val order = _orders.value.find { it.id == orderId }
            if (order != null) {
                pingCustomer(orderId)
            }
        }
    }

    // Customer Ping System
    fun pingCustomer(orderId: String) {
        _orders.update { list ->
            list.map {
                if (it.id == orderId) it.copy(isPinged = true) else it
            }
        }
        val order = _orders.value.find { it.id == orderId }
        if (order != null) {
            val medium = if (order.loginMethod == "Phone") "WhatsApp (+91 ${order.customerContact.takeLast(10)})" else "Gmail (${order.customerContact})"
            _activeCustomerAlert.value = "🔔 Order ${order.tokenNumber} is READY! Alert dispatched to $medium & In-App Notification."
        }
    }

    // Rate dish/order
    fun rateOrder(orderId: String, rating: Float, comment: String) {
        _orders.update { list ->
            list.map {
                if (it.id == orderId) it.copy(rating = rating, reviewComment = comment) else it
            }
        }
    }

    // Menu Customization
    fun updateMenuItem(updatedItem: MenuItem) {
        _menuItems.update { list ->
            list.map { if (it.id == updatedItem.id) updatedItem else it }
        }
    }

    fun addMenuItem(newItem: MenuItem) {
        _menuItems.update { list -> list + newItem }
    }

    fun toggleItemAvailability(itemId: String) {
        _menuItems.update { list ->
            list.map {
                if (it.id == itemId) it.copy(isAvailable = !it.isAvailable) else it
            }
        }
    }

    // Wastage
    fun logWastage(itemName: String, quantity: Int, estimatedLoss: Double, reason: String) {
        val record = WastageRecord(
            id = "WST-${UUID.randomUUID().toString().take(6)}",
            itemName = itemName,
            quantity = quantity,
            estimatedLoss = estimatedLoss,
            reason = reason,
            timestamp = System.currentTimeMillis()
        )
        _wastageRecords.update { listOf(record) + it }
    }

    // Owner Bank Account
    fun updateOwnerBankAccount(newAccount: OwnerBankAccount) {
        _ownerBankAccount.value = newAccount
    }
}

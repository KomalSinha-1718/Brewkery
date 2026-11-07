package com.brewkery.app.data

import com.brewkery.app.domain.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

/** Holds the most recently placed order so the Home screen can show active-order tracking. */
class OrderRepository {
    private val _activeOrder = MutableStateFlow<Order?>(null)
    val activeOrder: StateFlow<Order?> = _activeOrder.asStateFlow()

    fun place(order: Order) {
        _activeOrder.value = order
    }
}

object TicketIdGenerator {
    /** e.g. "BK-74921" */
    fun next(): String = "BK-" + Random.nextInt(10_000, 100_000)
}

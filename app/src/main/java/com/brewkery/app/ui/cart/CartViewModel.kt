package com.brewkery.app.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.brewkery.app.AppContainer
import com.brewkery.app.data.CartRepository
import com.brewkery.app.data.OrderRepository
import com.brewkery.app.data.TicketIdGenerator
import com.brewkery.app.domain.CartLine
import com.brewkery.app.domain.CartTotals
import com.brewkery.app.domain.Order
import com.brewkery.app.domain.OrderStatus
import com.brewkery.app.domain.PriceCalculator
import com.brewkery.app.domain.StoreMeta
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class CartUiState(
    val lines: List<CartLine>,
    val totals: CartTotals,
    val currencySymbol: String = "\$",
    val taxRatePercent: Double = 8.0,
)

class CartViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val storeMeta: () -> StoreMeta,
    private val ticketIds: () -> String = TicketIdGenerator::next,
) : ViewModel() {

    private fun stateFor(lines: List<CartLine>): CartUiState {
        val meta = storeMeta()
        return CartUiState(lines, PriceCalculator.totals(lines, meta), meta.currencySymbol, meta.taxRatePercent)
    }

    // The initial value is the CURRENT cart, not an empty one, so there is no "empty cart" flash.
    val uiState: StateFlow<CartUiState> = cartRepository.lines
        .map(::stateFor)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), stateFor(cartRepository.lines.value))

    fun increment(key: String) = cartRepository.increment(key)
    fun decrement(key: String) = cartRepository.decrement(key)
    fun clear() = cartRepository.clear()

    /**
     * Snapshots the order, then clears the cart. Returns false (and does nothing) for an empty cart,
     * which also makes a double-tap on "Place Order" harmless.
     */
    fun placeOrder(): Boolean {
        val lines = cartRepository.lines.value
        if (lines.isEmpty()) return false
        val meta = storeMeta()
        val totals = PriceCalculator.totals(lines, meta)
        orderRepository.place(
            Order(
                ticketId = ticketIds(),
                status = OrderStatus.PREPARING,
                itemCount = lines.sumOf { it.quantity },
                estimatedWait = meta.estimatedDeliveryTime.replace("mins", "minutes"),
                totalCents = totals.totalCents,
            ),
        )
        cartRepository.clear()
        return true
    }

    companion object {
        fun factory(container: AppContainer) = viewModelFactory {
            initializer {
                CartViewModel(
                    container.cartRepository,
                    container.orderRepository,
                    storeMeta = { container.menuRepository.storeMeta },
                )
            }
        }
    }
}

package com.brewkery.app.ui

import com.brewkery.app.TestData
import com.brewkery.app.data.CartRepository
import com.brewkery.app.data.OrderRepository
import com.brewkery.app.domain.OrderStatus
import com.brewkery.app.domain.StoreMeta
import com.brewkery.app.ui.cart.CartViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    private val cart = CartRepository()
    private val orders = OrderRepository()

    @Before
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = CartViewModel(cart, orders, { StoreMeta.Default }, { "BK-12345" })

    @Test
    fun uiState_initialValue_reflectsExistingCart_noEmptyFlash() {
        cart.add(TestData.line(unitPriceCents = 550))
        assertEquals(1, viewModel().uiState.value.lines.size)
    }

    @Test
    fun placeOrder_clearsCart_andCreatesPreparingOrder() {
        cart.add(TestData.line(itemId = 1, unitPriceCents = 550, quantity = 2))
        cart.add(TestData.line(itemId = 3, unitPriceCents = 390))
        val vm = viewModel()

        assertTrue(vm.placeOrder())

        assertTrue(cart.lines.value.isEmpty())
        val order = orders.activeOrder.value!!
        assertEquals("BK-12345", order.ticketId)
        assertEquals(OrderStatus.PREPARING, order.status)
        assertEquals(3, order.itemCount)
        assertEquals("20 - 30 minutes", order.estimatedWait)
        assertEquals(1859L, order.totalCents)
    }

    @Test
    fun placeOrder_onEmptyCart_doesNothing_soDoubleTapIsSafe() {
        val vm = viewModel()
        assertFalse(vm.placeOrder())
        assertNull(orders.activeOrder.value)
    }

    @Test
    fun placeOrder_twiceInARow_createsOnlyOneOrder() {
        cart.add(TestData.line())
        val vm = viewModel()
        assertTrue(vm.placeOrder())
        assertFalse(vm.placeOrder())
    }
}

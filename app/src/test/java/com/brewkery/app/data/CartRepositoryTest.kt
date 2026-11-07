package com.brewkery.app.data

import com.brewkery.app.TestData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CartRepositoryTest {

    private val cart = CartRepository()

    @Test
    fun add_sameConfiguration_mergesQuantities() {
        cart.add(TestData.line(quantity = 1))
        cart.add(TestData.line(quantity = 2))

        assertEquals(1, cart.lines.value.size)
        assertEquals(3, cart.lines.value.single().quantity)
    }

    @Test
    fun add_differentMilk_createsSeparateLines() {
        cart.add(TestData.line(milkId = "m_oat"))
        cart.add(TestData.line(milkId = "m_almond"))
        assertEquals(2, cart.lines.value.size)
    }

    @Test
    fun add_sameOptionIdsOnDifferentItems_areNotMerged() {
        // "sz_medium"/"m_oat" ids are reused across items in the real API.
        cart.add(TestData.line(itemId = 1, sizeId = "sz_medium", milkId = "m_oat"))
        cart.add(TestData.line(itemId = 5, sizeId = "sz_medium", milkId = "m_oat"))
        assertEquals(2, cart.lines.value.size)
    }

    @Test
    fun increment_and_decrement_adjustQuantity() {
        cart.add(TestData.line())
        val key = cart.lines.value.single().key

        cart.increment(key)
        assertEquals(2, cart.lines.value.single().quantity)

        cart.decrement(key)
        assertEquals(1, cart.lines.value.single().quantity)
    }

    @Test
    fun decrement_lastUnit_removesLine() {
        cart.add(TestData.line())
        cart.decrement(cart.lines.value.single().key)
        assertTrue(cart.lines.value.isEmpty())
    }

    @Test
    fun quantity_isCappedAtMax() {
        cart.add(TestData.line(quantity = CartRepository.MAX_QUANTITY))
        cart.add(TestData.line(quantity = 5))
        assertEquals(CartRepository.MAX_QUANTITY, cart.lines.value.single().quantity)
    }

    @Test
    fun clear_emptiesCart() {
        cart.add(TestData.line())
        cart.clear()
        assertTrue(cart.lines.value.isEmpty())
    }
}

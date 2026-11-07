package com.brewkery.app.domain

import com.brewkery.app.TestData
import org.junit.Assert.assertEquals
import org.junit.Test

class PriceCalculatorTest {

    private val item = TestData.macchiato()

    @Test
    fun unitPrice_addsSizeMilkAndSugarExtras() {
        // Grande (+0.65) + Oat (+0.00) on a $4.85 base = $5.50 — the value shown in the design mock.
        val selection = Selection(item.sizes[1], item.milkOptions[0], item.sugarLevels[0])
        assertEquals(550L, PriceCalculator.unitPriceCents(item, selection))
    }

    @Test
    fun unitPrice_ventiAlmond() {
        val selection = Selection(item.sizes[2], item.milkOptions[1], item.sugarLevels[0])
        assertEquals(485L + 125L + 50L, PriceCalculator.unitPriceCents(item, selection))
    }

    @Test
    fun unitPrice_withNoSelection_isBasePrice() {
        assertEquals(485L, PriceCalculator.unitPriceCents(item, Selection()))
    }

    @Test
    fun unitPrice_includesSugarSurcharge() {
        val honey = SugarLevel("Light Wildflower Honey (+0.40)", extraCents = 40)
        assertEquals(525L, PriceCalculator.unitPriceCents(item, Selection(sugar = honey)))
    }

    @Test
    fun totals_matchDesignMock() {
        val lines = listOf(
            TestData.line(itemId = 1, unitPriceCents = 550),
            TestData.line(itemId = 3, unitPriceCents = 390),
        )
        val totals = PriceCalculator.totals(lines, StoreMeta.Default)

        assertEquals(940L, totals.subtotalCents) // $9.40
        assertEquals(250L, totals.deliveryFeeCents) // $2.50
        assertEquals(75L, totals.taxCents) // 8% of $9.40 = $0.752 -> $0.75 (delivery is NOT taxed)
        assertEquals(1265L, totals.totalCents) // $12.65
    }

    @Test
    fun totals_respectQuantity() {
        val lines = listOf(
            TestData.line(itemId = 1, unitPriceCents = 550, quantity = 2),
            TestData.line(itemId = 3, unitPriceCents = 390),
        )
        val totals = PriceCalculator.totals(lines, StoreMeta.Default)
        assertEquals(1490L, totals.subtotalCents)
        assertEquals(119L, totals.taxCents)
        assertEquals(1859L, totals.totalCents)
    }

    @Test
    fun totals_emptyCart_hasNoDeliveryFee() {
        assertEquals(CartTotals.Empty, PriceCalculator.totals(emptyList(), StoreMeta.Default))
        assertEquals(0L, CartTotals.Empty.totalCents)
    }

    @Test
    fun tax_roundsHalfUp() {
        val meta = StoreMeta.Default.copy(taxRatePercent = 7.5)
        // 7.5% of $1.00 = 7.5 cents -> rounds half-up to 8
        val totals = PriceCalculator.totals(listOf(TestData.line(unitPriceCents = 100)), meta)
        assertEquals(8L, totals.taxCents)
    }
}

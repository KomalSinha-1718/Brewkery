package com.brewkery.app.domain

import java.math.BigDecimal
import java.math.RoundingMode

data class CartTotals(
    val subtotalCents: Long,
    val deliveryFeeCents: Long,
    val taxCents: Long,
) {
    val totalCents: Long get() = subtotalCents + deliveryFeeCents + taxCents

    companion object {
        val Empty = CartTotals(0, 0, 0)
    }
}

/** Pure functions, no Android deps: the single source of truth for every price on screen. */
object PriceCalculator {

    fun unitPriceCents(item: MenuItem, selection: Selection): Long =
        item.basePriceCents +
            (selection.size?.extraCents ?: 0L) +
            (selection.milk?.extraCents ?: 0L) +
            (selection.sugar?.extraCents ?: 0L)

    /**
     * Tax is charged on the subtotal only (not on delivery). This is what the design mock shows:
     * $9.40 subtotal -> $0.75 tax (8%), $12.65 total.
     */
    fun totals(lines: List<CartLine>, meta: StoreMeta): CartTotals {
        if (lines.isEmpty()) return CartTotals.Empty
        val subtotal = lines.sumOf { it.totalCents }
        val tax = BigDecimal(subtotal)
            .multiply(BigDecimal.valueOf(meta.taxRatePercent))
            .divide(BigDecimal(100), 0, RoundingMode.HALF_UP)
            .toLong()
        return CartTotals(subtotal, meta.deliveryFeeCents, tax)
    }
}

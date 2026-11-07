package com.brewkery.app.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class MoneyTest {
    @Test
    fun fromDollars_avoidsFloatingPointDrift() {
        assertEquals(485L, Money.fromDollars(4.85)) // 4.85 * 100 = 485.00000000000006 in IEEE-754
        assertEquals(65L, Money.fromDollars(0.65))
        assertEquals(395L, Money.fromDollars(3.95))
    }

    @Test
    fun format_padsCentsAndUsesSymbol() {
        assertEquals("\$4.85", Money.format(485))
        assertEquals("\$12.05", Money.format(1205))
        assertEquals("\$0.05", Money.format(5))
        assertEquals("+\$0.50", Money.formatExtra(50))
    }
}

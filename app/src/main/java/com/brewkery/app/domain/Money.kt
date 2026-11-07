package com.brewkery.app.domain

import java.util.Locale
import kotlin.math.roundToLong

object Money {
    fun fromDollars(dollars: Double): Long = (dollars * 100).roundToLong()

    fun format(cents: Long, symbol: String = "\$"): String =
        String.format(Locale.US, "%s%d.%02d", symbol, cents / 100, cents % 100)

    fun formatExtra(cents: Long, symbol: String = "\$"): String = "+" + format(cents, symbol)
}

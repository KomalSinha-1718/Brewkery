package com.brewkery.app

import com.brewkery.app.domain.CartLine
import com.brewkery.app.domain.MenuItem
import com.brewkery.app.domain.MilkOption
import com.brewkery.app.domain.SizeOption
import com.brewkery.app.domain.SugarLevel

object TestData {
    fun macchiato() = MenuItem(
        id = 1,
        categoryId = "cat_hot_coffee",
        name = "Toasted Caramel Macchiato",
        tagline = "tagline",
        description = "desc",
        basePriceCents = 485,
        rating = 4.9,
        reviewCount = 312,
        prepTime = "5-7 mins",
        calories = 240,
        imageUrl = "https://example.com/1.jpg",
        badge = "BESTSELLER",
        ingredients = listOf("Signature Espresso"),
        sizes = listOf(
            SizeOption("sz_small", "Tall (8 oz)", 0),
            SizeOption("sz_medium", "Grande (12 oz)", 65),
            SizeOption("sz_large", "Venti (16 oz)", 125),
        ),
        sugarLevels = listOf(SugarLevel("0% Unsweetened", 0), SugarLevel("50% Mild", 0)),
        milkOptions = listOf(
            MilkOption("m_oat", "Oat Milk (Barista Blend)", 0),
            MilkOption("m_almond", "Roasted Almond Milk", 50),
        ),
    )

    fun line(
        itemId: Int = 1,
        sizeId: String = "sz_small",
        milkId: String = "m_oat",
        unitPriceCents: Long = 485,
        quantity: Int = 1,
    ) = CartLine(
        itemId = itemId,
        itemName = "Item $itemId",
        imageUrl = "url",
        size = SizeOption(sizeId, sizeId, 0),
        milk = MilkOption(milkId, milkId, 0),
        sugar = SugarLevel("sugar", 0),
        unitPriceCents = unitPriceCents,
        quantity = quantity,
    )
}

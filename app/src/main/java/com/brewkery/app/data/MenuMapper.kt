package com.brewkery.app.data

import com.brewkery.app.data.remote.ItemDto
import com.brewkery.app.data.remote.MenuResponseDto
import com.brewkery.app.data.remote.MetaDto
import com.brewkery.app.domain.Category
import com.brewkery.app.domain.Menu
import com.brewkery.app.domain.MenuItem
import com.brewkery.app.domain.MilkOption
import com.brewkery.app.domain.Money
import com.brewkery.app.domain.SizeOption
import com.brewkery.app.domain.StoreMeta
import com.brewkery.app.domain.SugarLevel

internal fun MenuResponseDto.toDomain() = Menu(
    meta = meta.toDomain(),
    categories = categories.map { Category(it.id, it.name, it.icon) },
    items = items.map { it.toDomain() },
)

internal fun MetaDto.toDomain() = StoreMeta(
    deliveryFeeCents = Money.fromDollars(deliveryFee),
    taxRatePercent = taxRatePercent,
    estimatedDeliveryTime = estimatedDeliveryTime,
    currencySymbol = currencySymbol,
)

internal fun ItemDto.toDomain() = MenuItem(
    id = id,
    categoryId = categoryId,
    name = name,
    tagline = tagline,
    description = description,
    basePriceCents = Money.fromDollars(basePrice),
    rating = rating,
    reviewCount = reviewCount,
    prepTime = prepTime,
    calories = calories,
    imageUrl = imageUrl,
    badge = badge?.takeIf { it.isNotBlank() },
    ingredients = ingredients,
    sizes = customizations.sizes.map { SizeOption(it.id, it.label, Money.fromDollars(it.extraPrice)) },
    sugarLevels = customizations.sugarLevels.map { SugarLevel(it, parseSurchargeCents(it)) },
    milkOptions = customizations.milkOptions.map { MilkOption(it.id, it.name, Money.fromDollars(it.extraPrice)) },
)

// Matches a trailing "(+0.40)" / "(+$0.40)" surcharge embedded in a label.
private val SURCHARGE = Regex("""\(\+\s*\$?(\d+(?:\.\d+)?)\)""")

/**
 * The API ships one surcharge ("Light Wildflower Honey (+0.40)") only as free text in a sugar-level
 * label instead of a structured field. Parsing it keeps the displayed price honest.
 */
internal fun parseSurchargeCents(label: String): Long {
    val match = SURCHARGE.find(label) ?: return 0L
    return Money.fromDollars(match.groupValues[1].toDouble())
}

package com.brewkery.app.domain

/** All money in the domain layer is stored as Long cents to avoid floating point drift. */
data class StoreMeta(
    val deliveryFeeCents: Long,
    val taxRatePercent: Double,
    val estimatedDeliveryTime: String,
    val currencySymbol: String,
) {
    companion object {
        /** Values from the assignment brief; used only if the cart is opened before the menu ever loaded. */
        val Default = StoreMeta(
            deliveryFeeCents = 250L,
            taxRatePercent = 8.0,
            estimatedDeliveryTime = "20 - 30 mins",
            currencySymbol = "\$",
        )
    }
}

data class Category(val id: String, val name: String, val icon: String)

data class SizeOption(val id: String, val label: String, val extraCents: Long)

data class MilkOption(val id: String, val name: String, val extraCents: Long)

/** [extraCents] is non-zero only when the API embeds a surcharge in the label, e.g. "Honey (+0.40)". */
data class SugarLevel(val label: String, val extraCents: Long)

data class MenuItem(
    val id: Int,
    val categoryId: String,
    val name: String,
    val tagline: String,
    val description: String,
    val basePriceCents: Long,
    val rating: Double,
    val reviewCount: Int,
    val prepTime: String,
    val calories: Int,
    val imageUrl: String,
    val badge: String?,
    val ingredients: List<String>,
    val sizes: List<SizeOption>,
    val sugarLevels: List<SugarLevel>,
    val milkOptions: List<MilkOption>,
)

data class Menu(
    val meta: StoreMeta,
    val categories: List<Category>,
    val items: List<MenuItem>,
)

/** The user's choices on the detail screen. Nullable because an item may expose no options of a kind. */
data class Selection(
    val size: SizeOption? = null,
    val milk: MilkOption? = null,
    val sugar: SugarLevel? = null,
) {
    companion object {
        fun defaultsFor(item: MenuItem) = Selection(
            size = item.sizes.firstOrNull(),
            milk = item.milkOptions.firstOrNull(),
            sugar = item.sugarLevels.firstOrNull(),
        )
    }
}

data class CartLine(
    val itemId: Int,
    val itemName: String,
    val imageUrl: String,
    val size: SizeOption?,
    val milk: MilkOption?,
    val sugar: SugarLevel?,
    val unitPriceCents: Long,
    val quantity: Int,
) {
    /**
     * Identity of a configuration. The item id MUST be part of it: option ids such as "sz_medium"
     * and "m_oat" are reused across different items in the API.
     */
    val key: String get() = "$itemId|${size?.id}|${milk?.id}|${sugar?.label}"

    val totalCents: Long get() = unitPriceCents * quantity

    val optionsSummary: String get() = listOfNotNull(size?.label, milk?.name).joinToString(" • ")
}

enum class OrderStatus { PREPARING }

data class Order(
    val ticketId: String,
    val status: OrderStatus,
    val itemCount: Int,
    val estimatedWait: String,
    val totalCents: Long,
)

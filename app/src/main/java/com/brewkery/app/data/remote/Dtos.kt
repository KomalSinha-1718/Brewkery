package com.brewkery.app.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuResponseDto(
    val meta: MetaDto = MetaDto(),
    val categories: List<CategoryDto> = emptyList(),
    val items: List<ItemDto> = emptyList(),
)

@Serializable
data class MetaDto(
    @SerialName("currency_symbol") val currencySymbol: String = "\$",
    @SerialName("delivery_fee") val deliveryFee: Double = 2.5,
    @SerialName("tax_rate_percent") val taxRatePercent: Double = 8.0,
    @SerialName("estimated_delivery_time") val estimatedDeliveryTime: String = "20 - 30 mins",
)

@Serializable
data class CategoryDto(
    val id: String,
    val name: String,
    val icon: String = "",
)

@Serializable
data class ItemDto(
    val id: Int,
    @SerialName("category_id") val categoryId: String = "",
    val name: String,
    val tagline: String = "",
    val description: String = "",
    @SerialName("base_price") val basePrice: Double,
    val rating: Double = 0.0,
    @SerialName("review_count") val reviewCount: Int = 0,
    @SerialName("prep_time") val prepTime: String = "",
    val calories: Int = 0,
    @SerialName("image_url") val imageUrl: String = "",
    val badge: String? = null,
    val ingredients: List<String> = emptyList(),
    val customizations: CustomizationsDto = CustomizationsDto(),
)

@Serializable
data class CustomizationsDto(
    val sizes: List<SizeDto> = emptyList(),
    @SerialName("sugar_levels") val sugarLevels: List<String> = emptyList(),
    @SerialName("milk_options") val milkOptions: List<MilkDto> = emptyList(),
)

@Serializable
data class SizeDto(
    val id: String,
    val label: String,
    @SerialName("extra_price") val extraPrice: Double = 0.0,
)

@Serializable
data class MilkDto(
    val id: String,
    val name: String,
    @SerialName("extra_price") val extraPrice: Double = 0.0,
)

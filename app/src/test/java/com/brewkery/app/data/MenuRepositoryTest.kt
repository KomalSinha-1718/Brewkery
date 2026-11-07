package com.brewkery.app.data

import com.brewkery.app.data.remote.BrewkeryApi
import com.brewkery.app.data.remote.ItemDto
import com.brewkery.app.data.remote.MenuResponseDto
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

private val SAMPLE_JSON = """
{
  "meta": { "app": "Brewkery", "currency_symbol": "$", "delivery_fee": 2.50,
            "tax_rate_percent": 8.0, "estimated_delivery_time": "20 - 30 mins", "extra": "ignored" },
  "categories": [ { "id": "cat_hot_coffee", "name": "Hot Coffee", "icon": "☕", "item_count": 1 } ],
  "items": [
    { "id": 1, "category_id": "cat_hot_coffee", "name": "Toasted Caramel Macchiato",
      "tagline": "t", "description": "d", "base_price": 4.85, "rating": 4.9, "review_count": 312,
      "prep_time": "5-7 mins", "calories": 240, "image_url": "https://x/1.jpg", "badge": "BESTSELLER",
      "ingredients": ["Espresso"],
      "customizations": {
        "sizes": [ { "id": "sz_small", "label": "Tall (8 oz)", "extra_price": 0.00 },
                   { "id": "sz_medium", "label": "Grande (12 oz)", "extra_price": 0.65 } ],
        "sugar_levels": [ "0% Unsweetened" ],
        "milk_options": [ { "id": "m_oat", "name": "Oat Milk", "extra_price": 0.0 } ] } },
    { "id": 6, "category_id": "cat_cold_brews", "name": "Nitro Cherry Cascara",
      "base_price": 5.60, "rating": 4.7, "review_count": 89,
      "customizations": {
        "sizes": [ { "id": "sz_pint", "label": "Nitro Glass (16 oz)", "extra_price": 0.0 } ],
        "sugar_levels": [ "Zero Added Sugar", "Light Wildflower Honey (+0.40)" ],
        "milk_options": [ { "id": "m_black", "name": "Pure Nitro (No Milk)", "extra_price": 0.0 } ] } }
  ]
}
"""

private class FakeApi(private val json: String = SAMPLE_JSON, var fail: Boolean = false) : BrewkeryApi {
    private val parser = Json { ignoreUnknownKeys = true; coerceInputValues = true }
    var menuCalls = 0
    var itemCalls = 0

    override suspend fun getMenu(): MenuResponseDto {
        menuCalls++
        if (fail) throw IOException("offline")
        return parser.decodeFromString(json)
    }

    override suspend fun getItem(id: Int): ItemDto {
        itemCalls++
        if (fail) throw IOException("offline")
        return parser.decodeFromString<MenuResponseDto>(json).items.first { it.id == id }
    }
}

class MenuRepositoryTest {

    @Test
    fun getMenu_mapsDtoToDomain_inCents() = runTest {
        val menu = MenuRepository(FakeApi()).getMenu().getOrThrow()

        assertEquals(250L, menu.meta.deliveryFeeCents)
        assertEquals(8.0, menu.meta.taxRatePercent, 0.0)
        val macchiato = menu.items.first { it.id == 1 }
        assertEquals(485L, macchiato.basePriceCents)
        assertEquals(listOf(0L, 65L), macchiato.sizes.map { it.extraCents })
        assertEquals("BESTSELLER", macchiato.badge)
    }

    @Test
    fun getMenu_toleratesMissingOptionalFields() = runTest {
        val nitro = MenuRepository(FakeApi()).getMenu().getOrThrow().items.first { it.id == 6 }
        assertNull(nitro.badge)
        assertTrue(nitro.ingredients.isEmpty())
    }

    @Test
    fun sugarLabelSurcharge_isParsed() = runTest {
        val nitro = MenuRepository(FakeApi()).getMenu().getOrThrow().items.first { it.id == 6 }
        assertEquals(listOf(0L, 40L), nitro.sugarLevels.map { it.extraCents })
        assertEquals(0L, parseSurchargeCents("50% Mild"))
        assertEquals(40L, parseSurchargeCents("Honey (+\$0.40)"))
    }

    @Test
    fun getItem_afterMenuLoaded_usesCache() = runTest {
        val api = FakeApi()
        val repo = MenuRepository(api)
        repo.getMenu()
        val item = repo.getItem(1).getOrThrow()

        assertEquals("Toasted Caramel Macchiato", item.name)
        assertEquals(0, api.itemCalls)
    }

    @Test
    fun getItem_withoutCache_fallsBackToItemEndpoint() = runTest {
        val api = FakeApi()
        val item = MenuRepository(api).getItem(6).getOrThrow()

        assertEquals("Nitro Cherry Cascara", item.name)
        assertEquals(1, api.itemCalls)
    }

    @Test
    fun getMenu_networkFailure_isReturnedAsFailure_notThrown() = runTest {
        val result = MenuRepository(FakeApi(fail = true)).getMenu()
        assertTrue(result.exceptionOrNull() is IOException)
    }
}

package com.brewkery.app.data

import com.brewkery.app.domain.CartLine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** In-memory cart (the brief explicitly allows the cart to reset when the app closes). */
class CartRepository {

    private val _lines = MutableStateFlow<List<CartLine>>(emptyList())
    val lines: StateFlow<List<CartLine>> = _lines.asStateFlow()

    /** Adding an identical configuration merges into the existing line instead of duplicating it. */
    fun add(line: CartLine) = _lines.update { current ->
        val index = current.indexOfFirst { it.key == line.key }
        if (index < 0) {
            current + line.copy(quantity = line.quantity.coerceIn(1, MAX_QUANTITY))
        } else {
            current.toMutableList().also { list ->
                val existing = list[index]
                list[index] = existing.copy(
                    quantity = (existing.quantity + line.quantity).coerceAtMost(MAX_QUANTITY),
                )
            }
        }
    }

    fun increment(key: String) = _lines.update { current ->
        current.map { if (it.key == key) it.copy(quantity = (it.quantity + 1).coerceAtMost(MAX_QUANTITY)) else it }
    }

    /** Decrementing the last unit removes the line. */
    fun decrement(key: String) = _lines.update { current ->
        current.mapNotNull {
            when {
                it.key != key -> it
                it.quantity <= 1 -> null
                else -> it.copy(quantity = it.quantity - 1)
            }
        }
    }

    fun clear() = _lines.update { emptyList() }

    companion object {
        const val MAX_QUANTITY = 20
    }
}

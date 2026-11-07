package com.brewkery.app.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.brewkery.app.AppContainer
import com.brewkery.app.data.CartRepository
import com.brewkery.app.data.MenuRepository
import com.brewkery.app.domain.CartLine
import com.brewkery.app.domain.MenuItem
import com.brewkery.app.domain.MilkOption
import com.brewkery.app.domain.PriceCalculator
import com.brewkery.app.domain.Selection
import com.brewkery.app.domain.SizeOption
import com.brewkery.app.domain.SugarLevel
import com.brewkery.app.ui.common.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val item: MenuItem? = null,
    val selection: Selection = Selection(),
    val quantity: Int = 1,
) {
    /** Price of ONE unit with the current customizations. */
    val unitPriceCents: Long
        get() = item?.let { PriceCalculator.unitPriceCents(it, selection) } ?: 0L

    /** What the "Add to Cart" button shows: unit price x quantity. */
    val totalCents: Long get() = unitPriceCents * quantity
}

class ItemDetailViewModel(
    private val itemId: Int,
    private val menuRepository: MenuRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            menuRepository.getItem(itemId).fold(
                onSuccess = { item ->
                    _uiState.update {
                        it.copy(isLoading = false, item = item, selection = Selection.defaultsFor(item))
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.toUserMessage()) }
                },
            )
        }
    }

    fun selectSize(size: SizeOption) = _uiState.update { it.copy(selection = it.selection.copy(size = size)) }
    fun selectMilk(milk: MilkOption) = _uiState.update { it.copy(selection = it.selection.copy(milk = milk)) }
    fun selectSugar(sugar: SugarLevel) = _uiState.update { it.copy(selection = it.selection.copy(sugar = sugar)) }

    fun increment() = _uiState.update {
        it.copy(quantity = (it.quantity + 1).coerceAtMost(CartRepository.MAX_QUANTITY))
    }

    fun decrement() = _uiState.update { it.copy(quantity = (it.quantity - 1).coerceAtLeast(1)) }

    fun addToCart() {
        val state = _uiState.value
        val item = state.item ?: return
        cartRepository.add(
            CartLine(
                itemId = item.id,
                itemName = item.name,
                imageUrl = item.imageUrl,
                size = state.selection.size,
                milk = state.selection.milk,
                sugar = state.selection.sugar,
                unitPriceCents = state.unitPriceCents,
                quantity = state.quantity,
            ),
        )
    }

    companion object {
        fun factory(container: AppContainer, itemId: Int) = viewModelFactory {
            initializer { ItemDetailViewModel(itemId, container.menuRepository, container.cartRepository) }
        }
    }
}

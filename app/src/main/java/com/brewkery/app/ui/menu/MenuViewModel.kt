package com.brewkery.app.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.brewkery.app.AppContainer
import com.brewkery.app.data.CartRepository
import com.brewkery.app.data.MenuRepository
import com.brewkery.app.data.OrderRepository
import com.brewkery.app.domain.Menu
import com.brewkery.app.domain.MenuItem
import com.brewkery.app.domain.Order
import com.brewkery.app.ui.common.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface LoadState {
    data object Loading : LoadState
    data class Error(val message: String) : LoadState
    data class Loaded(val menu: Menu) : LoadState
}

data class MenuUiState(
    val loadState: LoadState = LoadState.Loading,
    val query: String = "",
    val selectedCategoryId: String? = null,
    val cartCount: Int = 0,
    val cartSubtotalCents: Long = 0L,
    val activeOrder: Order? = null,
) {
    val visibleItems: List<MenuItem>
        get() {
            val menu = (loadState as? LoadState.Loaded)?.menu ?: return emptyList()
            val q = query.trim()
            return menu.items.filter { item ->
                val inCategory = selectedCategoryId == null || item.categoryId == selectedCategoryId
                val matchesQuery = q.isEmpty() ||
                    item.name.contains(q, ignoreCase = true) ||
                    item.tagline.contains(q, ignoreCase = true) ||
                    item.ingredients.any { it.contains(q, ignoreCase = true) }
                inCategory && matchesQuery
            }
        }
}

class MenuViewModel(
    private val menuRepository: MenuRepository,
    cartRepository: CartRepository,
    orderRepository: OrderRepository,
) : ViewModel() {

    private val loadState = MutableStateFlow<LoadState>(LoadState.Loading)
    private val query = MutableStateFlow("")
    private val selectedCategoryId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<MenuUiState> = combine(
        loadState,
        query,
        selectedCategoryId,
        cartRepository.lines,
        orderRepository.activeOrder,
    ) { load, q, categoryId, lines, order ->
        MenuUiState(
            loadState = load,
            query = q,
            selectedCategoryId = categoryId,
            cartCount = lines.sumOf { it.quantity },
            cartSubtotalCents = lines.sumOf { it.totalCents },
            activeOrder = order,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MenuUiState())

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            loadState.value = LoadState.Loading
            loadState.value = menuRepository.getMenu().fold(
                onSuccess = { LoadState.Loaded(it) },
                onFailure = { LoadState.Error(it.toUserMessage()) },
            )
        }
    }

    fun onQueryChange(value: String) {
        query.value = value
    }

    /** Tapping the selected chip again, or "All Items", clears the filter. */
    fun onCategorySelected(categoryId: String?) {
        selectedCategoryId.value = categoryId
    }

    companion object {
        fun factory(container: AppContainer) = viewModelFactory {
            initializer {
                MenuViewModel(
                    container.menuRepository,
                    container.cartRepository,
                    container.orderRepository,
                )
            }
        }
    }
}

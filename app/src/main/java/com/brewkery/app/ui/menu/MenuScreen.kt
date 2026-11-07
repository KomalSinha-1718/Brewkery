package com.brewkery.app.ui.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.brewkery.app.domain.Category
import com.brewkery.app.domain.MenuItem
import com.brewkery.app.domain.Money
import com.brewkery.app.domain.Order
import com.brewkery.app.domain.StoreMeta
import com.brewkery.app.ui.common.BadgeChip
import com.brewkery.app.ui.common.ErrorView
import com.brewkery.app.ui.common.LoadingView
import com.brewkery.app.ui.common.PriceText
import com.brewkery.app.ui.common.RatingRow
import com.brewkery.app.ui.theme.BrewColors

@Composable
fun MenuRoute(
    viewModel: MenuViewModel,
    onItemClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onOrderClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    MenuScreen(
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onCategorySelected = viewModel::onCategorySelected,
        onRetry = viewModel::load,
        onItemClick = onItemClick,
        onCartClick = onCartClick,
        onOrderClick = onOrderClick,
    )
}

@Composable
fun MenuScreen(
    state: MenuUiState,
    onQueryChange: (String) -> Unit,
    onCategorySelected: (String?) -> Unit,
    onRetry: () -> Unit,
    onItemClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onOrderClick: () -> Unit,
) {
    Scaffold(
        containerColor = BrewColors.Cream,
        bottomBar = {
            if (state.cartCount > 0) {
                CartBar(state.cartCount, state.cartSubtotalCents, onCartClick)
            }
        },
    ) { padding ->
        when (val load = state.loadState) {
            LoadState.Loading -> LoadingView(Modifier.padding(padding))
            is LoadState.Error -> ErrorView(load.message, onRetry, Modifier.padding(padding))
            is LoadState.Loaded -> MenuContent(
                state = state,
                meta = load.menu.meta,
                categories = load.menu.categories,
                onQueryChange = onQueryChange,
                onCategorySelected = onCategorySelected,
                onItemClick = onItemClick,
                onOrderClick = onOrderClick,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    meta: StoreMeta,
    categories: List<Category>,
    onQueryChange: (String) -> Unit,
    onCategorySelected: (String?) -> Unit,
    onItemClick: (Int) -> Unit,
    onOrderClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val visible = state.visibleItems
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { StoreBanner(meta) }
        state.activeOrder?.let { order -> item { ActiveOrderCard(order, onOrderClick) } }
        item { SearchField(state.query, onQueryChange) }
        item { CategoryRow(categories, state.selectedCategoryId, onCategorySelected) }
        if (visible.isEmpty()) {
            item {
                Text(
                    text = "No items match your search.",
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    textAlign = TextAlign.Center,
                    color = BrewColors.Muted,
                )
            }
        } else {
            items(visible, key = { it.id }) { item ->
                MenuItemCard(item, meta.currencySymbol, onClick = { onItemClick(item.id) })
            }
        }
    }
}

@Composable
private fun StoreBanner(meta: StoreMeta) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = BrewColors.AccentSoft,
        border = BorderStroke(1.dp, BrewColors.Border),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(Color.White),
                contentAlignment = Alignment.Center,
            ) { Text("🛵", fontSize = 22.sp) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "STORE INFO ●",
                    color = BrewColors.Accent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                )
                Text(
                    text = "Delivery in ${meta.estimatedDeliveryTime}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                )
                Text(
                    text = "${Money.format(meta.deliveryFeeCents, meta.currencySymbol)} flat fee",
                    color = BrewColors.Muted,
                    fontSize = 13.sp,
                )
            }
            Text(
                text = "Open",
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun ActiveOrderCard(order: Order, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BrewColors.Success),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("☕", fontSize = 24.sp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Active order #${order.ticketId}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    "Barista is preparing your order · ${order.estimatedWait}",
                    color = BrewColors.Muted,
                    fontSize = 12.sp,
                )
            }
            BadgeChip(order.status.name)
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        placeholder = { Text("Search roast, cold brew, pastry…", color = BrewColors.Muted) },
        leadingIcon = {
            Icon(Icons.Filled.Search, contentDescription = "Search", tint = BrewColors.Muted)
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrewColors.Accent,
            unfocusedBorderColor = BrewColors.Border,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            cursorColor = BrewColors.Accent,
        ),
    )
}

@Composable
private fun CategoryRow(
    categories: List<Category>,
    selectedId: String?,
    onSelected: (String?) -> Unit,
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            CategoryChip("All Items", selected = selectedId == null, onClick = { onSelected(null) })
        }
        items(categories, key = { it.id }) { category ->
            CategoryChip(
                label = "${category.icon} ${category.name}".trim(),
                selected = selectedId == category.id,
                onClick = { onSelected(if (selectedId == category.id) null else category.id) },
            )
        }
    }
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = if (selected) BrewColors.Espresso else Color.White,
        border = if (selected) null else BorderStroke(1.dp, BrewColors.Border),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
            color = if (selected) Color.White else BrewColors.Muted,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 15.sp,
        )
    }
}

@Composable
private fun MenuItemCard(item: MenuItem, currencySymbol: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BrewColors.Border),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BrewColors.AccentSoft),
            )
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                item.badge?.let {
                    BadgeChip(it)
                    Spacer(Modifier.height(4.dp))
                }
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 2)
                Spacer(Modifier.height(4.dp))
                RatingRow(item.rating, item.reviewCount)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PriceText(Money.format(item.basePriceCents, currencySymbol), Modifier.weight(1f))
                    Button(
                        onClick = onClick,
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrewColors.Accent,
                            contentColor = Color.White,
                        ),
                    ) {
                        Text("+ Customize", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CartBar(count: Int, subtotalCents: Long, onClick: () -> Unit) {
    Box(Modifier.navigationBarsPadding().padding(horizontal = 16.dp, vertical = 10.dp)) {
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(24.dp),
            color = BrewColors.Espresso,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(BrewColors.Accent),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(count.toString(), color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("View Your Cart", color = Color.White, fontSize = 13.sp)
                    Text(
                        Money.format(subtotalCents),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Default,
                    )
                }
                Text(
                    "Proceed to Checkout →",
                    color = BrewColors.Amber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                )
            }
        }
    }
}

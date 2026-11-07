package com.brewkery.app.ui.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.brewkery.app.domain.MenuItem
import com.brewkery.app.domain.Money
import com.brewkery.app.ui.common.BadgeChip
import com.brewkery.app.ui.common.ErrorView
import com.brewkery.app.ui.common.LoadingView
import com.brewkery.app.ui.common.PriceText
import com.brewkery.app.ui.common.PrimaryButton
import com.brewkery.app.ui.common.QuantityStepper
import com.brewkery.app.ui.theme.BrewColors

@Composable
fun ItemDetailRoute(
    viewModel: ItemDetailViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ItemDetailScreen(
        state = state,
        onBack = onBack,
        onRetry = viewModel::load,
        viewModel = viewModel,
    )
}

@Composable
private fun ItemDetailScreen(
    state: DetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    viewModel: ItemDetailViewModel,
) {
    val item = state.item
    Scaffold(
        containerColor = BrewColors.Cream,
        bottomBar = {
            if (item != null) {
                AddToCartBar(
                    quantity = state.quantity,
                    totalCents = state.totalCents,
                    onDecrement = viewModel::decrement,
                    onIncrement = viewModel::increment,
                    onAdd = {
                        viewModel.addToCart()
                        onBack()
                    },
                )
            }
        },
    ) { padding ->
        when {
            state.isLoading -> LoadingView(Modifier.padding(padding))
            state.error != null -> ErrorView(state.error, onRetry, Modifier.padding(padding))
            item != null -> DetailContent(
                item = item,
                state = state,
                viewModel = viewModel,
                onBack = onBack,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailContent(
    item: MenuItem,
    state: DetailUiState,
    viewModel: ItemDetailViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(20.dp))) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(200.dp).background(BrewColors.AccentSoft),
            )
            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(onClickLabel = "Back", role = Role.Button, onClick = onBack)
                    .align(Alignment.TopStart),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            item.badge?.let {
                BadgeChip(it, Modifier.align(Alignment.BottomStart).padding(12.dp))
            }
        }

        Row(verticalAlignment = Alignment.Top) {
            Text(
                text = item.name,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
            )
            Spacer(Modifier.width(8.dp))
            PriceText(Money.format(item.basePriceCents), fontSize = 17)
        }
        Text(item.description, color = BrewColors.Muted, fontSize = 13.sp, lineHeight = 18.sp)
        Text(
            text = listOf(item.prepTime, "${item.calories} kcal").filter { it.isNotBlank() }.joinToString("  •  "),
            color = BrewColors.Muted,
            fontSize = 12.sp,
        )

        if (item.ingredients.isNotEmpty()) {
            Text(
                "KEY INGREDIENTS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = BrewColors.Muted,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                item.ingredients.forEach { ingredient ->
                    Text(
                        text = ingredient,
                        modifier = Modifier
                            .border(1.dp, BrewColors.Border, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        color = BrewColors.Muted,
                    )
                }
            }
        }

        if (item.sizes.isNotEmpty()) {
            OptionSection("Size Selection") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item.sizes.forEach { size ->
                        SelectableCard(
                            selected = state.selection.size?.id == size.id,
                            onClick = { viewModel.selectSize(size) },
                            modifier = Modifier.weight(1f),
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    size.label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                )
                                Spacer(Modifier.height(2.dp))
                                ExtraPrice(size.extraCents)
                            }
                        }
                    }
                }
            }
        }

        if (item.milkOptions.isNotEmpty()) {
            OptionSection("Milk Options / Spreads") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item.milkOptions.forEach { milk ->
                        SelectableCard(
                            selected = state.selection.milk?.id == milk.id,
                            onClick = { viewModel.selectMilk(milk) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    milk.name,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                                ExtraPrice(milk.extraCents)
                            }
                        }
                    }
                }
            }
        }

        if (item.sugarLevels.isNotEmpty()) {
            OptionSection("Sugar Levels / Serving") {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(item.sugarLevels, key = { it.label }) { sugar ->
                        val selected = state.selection.sugar?.label == sugar.label
                        Text(
                            text = sugar.label,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) BrewColors.Espresso else Color.White)
                                .border(
                                    1.dp,
                                    if (selected) BrewColors.Espresso else BrewColors.Border,
                                    RoundedCornerShape(10.dp),
                                )
                                .selectable(selected = selected, role = Role.RadioButton) {
                                    viewModel.selectSugar(sugar)
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            color = if (selected) Color.White else BrewColors.Muted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun ExtraPrice(extraCents: Long) {
    Text(
        text = Money.formatExtra(extraCents),
        color = BrewColors.Muted,
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
    )
}

@Composable
private fun OptionSection(title: String, content: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BrewColors.Border),
    ) {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            content()
        }
    }
}

@Composable
private fun SelectableCard(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(10.dp),
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(if (selected) BrewColors.AccentSoft else Color.White)
            .border(1.dp, if (selected) BrewColors.Accent else BrewColors.Border, shape)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Composable
private fun AddToCartBar(
    quantity: Int,
    totalCents: Long,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onAdd: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrewColors.Cream)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        QuantityStepper(quantity, onDecrement, onIncrement)
        PrimaryButton(
            text = "Add to Cart  •  ${Money.format(totalCents)}",
            onClick = onAdd,
            modifier = Modifier.weight(1f),
        )
    }
}

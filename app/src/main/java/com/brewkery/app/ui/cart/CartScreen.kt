package com.brewkery.app.ui.cart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brewkery.app.domain.CartLine
import com.brewkery.app.domain.CartTotals
import com.brewkery.app.domain.Money
import com.brewkery.app.ui.common.PriceText
import com.brewkery.app.ui.common.PrimaryButton
import com.brewkery.app.ui.common.QuantityStepper
import com.brewkery.app.ui.theme.BrewColors

@Composable
fun CartRoute(
    viewModel: CartViewModel,
    onBack: () -> Unit,
    onOrderPlaced: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CartScreen(
        state = state,
        onBack = onBack,
        onClear = viewModel::clear,
        onIncrement = viewModel::increment,
        onDecrement = viewModel::decrement,
        onPlaceOrder = { if (viewModel.placeOrder()) onOrderPlaced() },
    )
}

@Composable
fun CartScreen(
    state: CartUiState,
    onBack: () -> Unit,
    onClear: () -> Unit,
    onIncrement: (String) -> Unit,
    onDecrement: (String) -> Unit,
    onPlaceOrder: () -> Unit,
) {
    Scaffold(
        containerColor = BrewColors.Cream,
        topBar = { CartTopBar(onBack, onClear, showClear = state.lines.isNotEmpty()) },
        bottomBar = {
            if (state.lines.isNotEmpty()) {
                Column(
                    Modifier
                        .background(BrewColors.Cream)
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    TotalsCard(state.totals, state.currencySymbol, state.taxRatePercent)
                    Button(
                        onClick = onPlaceOrder,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrewColors.Accent,
                            contentColor = Color.White,
                        ),
                    ) {
                        Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text(
                            text = "  Place Order Now  •  ${Money.format(state.totals.totalCents, state.currencySymbol)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                        )
                    }
                }
            }
        },
    ) { padding ->
        if (state.lines.isEmpty()) {
            EmptyCart(onBack, Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(state.lines, key = { it.key }) { line ->
                    CartLineCard(line, state.currencySymbol, onIncrement, onDecrement)
                }
            }
        }
    }
}

@Composable
private fun CartTopBar(onBack: () -> Unit, onClear: () -> Unit, showClear: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrewColors.Cream)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(onClickLabel = "Back", role = Role.Button, onClick = onBack)
                .align(Alignment.CenterStart),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(20.dp))
        }
        Text(
            text = "YOUR CART",
            modifier = Modifier.align(Alignment.Center),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            letterSpacing = 0.5.sp,
        )
        if (showClear) {
            Text(
                text = "Clear Cart",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable(role = Role.Button, onClick = onClear)
                    .padding(8.dp),
                color = BrewColors.Danger,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
private fun CartLineCard(
    line: CartLine,
    currencySymbol: String,
    onIncrement: (String) -> Unit,
    onDecrement: (String) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BrewColors.Border),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(line.itemName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                if (line.optionsSummary.isNotEmpty()) {
                    Text(line.optionsSummary, color = BrewColors.Muted, fontSize = 12.sp)
                }
                PriceText(Money.format(line.totalCents, currencySymbol), fontSize = 13)
            }
            QuantityStepper(
                quantity = line.quantity,
                onDecrement = { onDecrement(line.key) },
                onIncrement = { onIncrement(line.key) },
            )
        }
    }
}

@Composable
private fun TotalsCard(totals: CartTotals, symbol: String, taxRatePercent: Double) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BrewColors.Border),
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            TotalsRow("Subtotal", Money.format(totals.subtotalCents, symbol))
            TotalsRow("Delivery Fee", Money.format(totals.deliveryFeeCents, symbol))
            TotalsRow("Est. Tax ($taxRatePercent%)", Money.format(totals.taxCents, symbol))
            HorizontalDivider(color = BrewColors.Border)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Total Payable", Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 17.sp)
                PriceText(Money.format(totals.totalCents, symbol), fontSize = 20)
            }
        }
    }
}

@Composable
private fun TotalsRow(label: String, value: String) {
    Row {
        Text(label, Modifier.weight(1f), color = BrewColors.Muted, fontSize = 13.sp)
        Text(value, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, fontSize = 13.sp)
    }
}

@Composable
private fun EmptyCart(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🛒", fontSize = 40.sp)
            Spacer(Modifier.height(8.dp))
            Text("Your cart is empty", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(
                "Add a coffee or a pastry to get started.",
                color = BrewColors.Muted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(20.dp))
            PrimaryButton("Back to Menu", onBack)
        }
    }
}

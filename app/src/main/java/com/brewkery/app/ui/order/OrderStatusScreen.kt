package com.brewkery.app.ui.order

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brewkery.app.data.OrderRepository
import com.brewkery.app.domain.Order
import com.brewkery.app.ui.common.BadgeChip
import com.brewkery.app.ui.theme.BrewColors

@Composable
fun OrderStatusRoute(orderRepository: OrderRepository, onBackToMenu: () -> Unit) {
    val order by orderRepository.activeOrder.collectAsStateWithLifecycle()
    // System back from the confirmation screen should return to the menu, not to a cleared cart.
    BackHandler(onBack = onBackToMenu)
    OrderStatusScreen(order, onBackToMenu)
}

@Composable
fun OrderStatusScreen(order: Order?, onBackToMenu: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrewColors.Cream)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(40.dp))
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(BrewColors.AccentSoft, CircleShape)
                .border(2.dp, BrewColors.Accent, CircleShape),
            contentAlignment = Alignment.Center,
        ) { Text("☕", fontSize = 34.sp) }
        Spacer(Modifier.height(12.dp))
        Text(
            "ORDER DISPATCHED",
            color = BrewColors.Accent,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 0.6.sp,
        )
        Text("Brewing in Progress!", fontWeight = FontWeight.ExtraBold, fontSize = 26.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            "Your ticket was dispatched to our barista.",
            color = BrewColors.Muted,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.weight(1f))

        if (order != null) {
            TicketCard(order)
        } else {
            Text("No active order.", color = BrewColors.Muted)
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onBackToMenu,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrewColors.Espresso,
                contentColor = Color.White,
            ),
        ) { Text("Back to Menu", fontWeight = FontWeight.Bold, fontSize = 15.sp) }
    }
}

@Composable
private fun TicketCard(order: Order) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BrewColors.Border),
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("ORDER TICKET", color = BrewColors.Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("#${order.ticketId}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
                BadgeChip(order.status.name)
            }
            HorizontalDivider(color = BrewColors.Border)
            InfoRow("Estimated Wait:", order.estimatedWait, valueColor = BrewColors.Accent, bold = true)
            InfoRow("Items Ordered:", if (order.itemCount == 1) "1 Item" else "${order.itemCount} Items")
            HorizontalDivider(color = BrewColors.Border)
            Text("Status:", color = BrewColors.Muted, fontSize = 11.sp)
            Text("Barista accepted your order!", color = BrewColors.Success, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, valueColor: Color = Color.Unspecified, bold: Boolean = false) {
    Row {
        Text(label, Modifier.weight(1f), color = BrewColors.Muted, fontSize = 13.sp)
        Text(
            value,
            color = valueColor,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
            fontSize = 13.sp,
        )
    }
}


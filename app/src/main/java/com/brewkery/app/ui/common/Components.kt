package com.brewkery.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brewkery.app.ui.theme.BrewColors

@Composable
fun BadgeChip(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .background(BrewColors.BadgeBg, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        color = BrewColors.BadgeText,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.3.sp,
    )
}

@Composable
fun RatingRow(rating: Double, reviewCount: Int, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = BrewColors.Amber,
            modifier = Modifier.size(15.dp),
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = "$rating ($reviewCount)",
            color = BrewColors.Muted,
            fontSize = 13.sp,
        )
    }
}

@Composable
fun PriceText(text: String, modifier: Modifier = Modifier, fontSize: Int = 15) {
    Text(
        text = text,
        modifier = modifier,
        color = BrewColors.Accent,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Bold,
        fontSize = fontSize.sp,
    )
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BrewColors.Accent,
            contentColor = androidx.compose.ui.graphics.Color.White,
        ),
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = BrewColors.Accent)
            Spacer(Modifier.height(12.dp))
            Text("Brewing the menu…", color = BrewColors.Muted, fontSize = 14.sp)
        }
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("☕", fontSize = 40.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Oops, spilled the beans",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = BrewColors.Espresso,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = message,
                color = BrewColors.Muted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(20.dp))
            PrimaryButton("Try again", onRetry, Modifier.widthIn(min = 160.dp))
        }
    }
}

@Composable
fun QuantityStepper(
    quantity: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(BrewColors.AccentSoft, RoundedCornerShape(12.dp))
            .padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        StepperButton("−", "Decrease quantity", onDecrement)
        Text(
            text = quantity.toString(),
            modifier = Modifier.widthIn(min = 22.dp),
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
        )
        StepperButton("+", "Increase quantity", onIncrement)
    }
}

@Composable
private fun StepperButton(symbol: String, description: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .clickable(onClickLabel = description, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(symbol, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

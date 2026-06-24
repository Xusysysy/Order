package com.order.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun QuantityStepper(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val btnSize = if (compact) 26.dp else 36.dp
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(btnSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                .clickable { onDecrement() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "−",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = "$quantity",
            style = if (compact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Box(
            modifier = Modifier
                .size(btnSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable { onIncrement() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}

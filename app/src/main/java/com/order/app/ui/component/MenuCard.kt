package com.order.app.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.order.app.data.db.entity.MenuItemEntity
import kotlin.math.roundToInt

@Composable
fun MenuCard(
    item: MenuItemEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showAddButton: Boolean = false,
    orderQuantity: Int = 0,
    onAddClick: (Int, Int) -> Unit = { _, _ -> },
    onIncrement: () -> Unit = {},
    onDecrement: () -> Unit = {}
) {
    var addBtnX by remember { mutableIntStateOf(0) }
    var addBtnY by remember { mutableIntStateOf(0) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box {
            Column(modifier = Modifier
                .then(
                    if (!showAddButton) Modifier.clickable { onClick() } else Modifier
                )
                .padding(12.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "¥%.0f".format(item.price),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.fillMaxWidth().heightIn(min = 32.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showAddButton) {
                        Box(
                            modifier = Modifier
                                .onGloballyPositioned { coords ->
                                    val pos = coords.positionInWindow()
                                    addBtnX = pos.x.roundToInt()
                                    addBtnY = pos.y.roundToInt()
                                }
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable { onAddClick(addBtnX, addBtnY) },
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
            }
        }
    }
}

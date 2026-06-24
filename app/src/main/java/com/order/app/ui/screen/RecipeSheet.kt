package com.order.app.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.order.app.data.db.entity.MenuItemEntity
import com.order.app.data.db.entity.RecipeIngredientEntity
import com.order.app.data.db.entity.RecipeStepEntity
import com.order.app.ui.component.QuantityStepper
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeSheet(
    item: MenuItemEntity?,
    steps: List<RecipeStepEntity>,
    ingredients: List<RecipeIngredientEntity>,
    onDismiss: () -> Unit,
    isStaff: Boolean = true,
    orderQuantity: Int = 0,
    onAddClick: (Int, Int) -> Unit = { _, _ -> },
    onIncrement: () -> Unit = {},
    onDecrement: () -> Unit = {},
    onSaveRecipe: (List<RecipeStepEntity>, List<RecipeIngredientEntity>) -> Unit = { _, _ -> }
) {
    var addBtnX by remember { mutableIntStateOf(0) }
    var addBtnY by remember { mutableIntStateOf(0) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val editSteps = remember(steps) { mutableStateListOf<RecipeStepEntity>().also { it.addAll(steps) } }
    val editIngredients = remember(ingredients) { mutableStateListOf<RecipeIngredientEntity>().also { it.addAll(ingredients) } }
    var newStepText by remember { mutableStateOf("") }
    var newIngName by remember { mutableStateOf("") }
    var newIngAmount by remember { mutableStateOf("") }
    var newIngUnit by remember { mutableStateOf("") }
    var showSaved by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = item?.name ?: "", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "原料", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            editIngredients.forEachIndexed { index, ing ->
                if (isStaff) {
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(value = ing.name, onValueChange = { editIngredients[index] = ing.copy(name = it) }, label = { Text("原料名称") }, singleLine = true, modifier = Modifier.weight(1f))
                            TextButton(onClick = { editIngredients.removeAt(index) }) { Text("✕", color = MaterialTheme.colorScheme.error) }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(value = ing.amount, onValueChange = { editIngredients[index] = ing.copy(amount = it) }, label = { Text("用量") }, singleLine = true, modifier = Modifier.weight(1f))
                            Spacer(Modifier.width(4.dp))
                            OutlinedTextField(value = ing.unit, onValueChange = { editIngredients[index] = ing.copy(unit = it) }, label = { Text("单位") }, singleLine = true, modifier = Modifier.width(80.dp))
                        }
                    }
                } else {
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text(text = "• ${ing.name}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                        Text(text = ing.amount, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    }
                }
            }
            if (isStaff) {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text("添加原料", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(value = newIngName, onValueChange = { newIngName = it }, label = { Text("名称") }, singleLine = true, modifier = Modifier.weight(1f))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(value = newIngAmount, onValueChange = { newIngAmount = it }, label = { Text("用量") }, singleLine = true, modifier = Modifier.weight(1f))
                        Spacer(Modifier.width(4.dp))
                        OutlinedTextField(value = newIngUnit, onValueChange = { newIngUnit = it }, label = { Text("单位") }, singleLine = true, modifier = Modifier.width(80.dp))
                    }
                    TextButton(onClick = {
                        if (newIngName.isNotBlank()) {
                            editIngredients.add(RecipeIngredientEntity(menuItemId = item?.id ?: 0, name = newIngName, amount = newIngAmount, unit = newIngUnit))
                            newIngName = ""; newIngAmount = ""; newIngUnit = ""
                        }
                    }) { Text("+ 添加", color = MaterialTheme.colorScheme.secondary) }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "步骤", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            editSteps.forEachIndexed { index, step ->
                if (isStaff) {
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("${index + 1}.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.padding(end = 8.dp))
                        OutlinedTextField(value = step.description, onValueChange = { editSteps[index] = step.copy(description = it) }, label = { Text("步骤说明") }, modifier = Modifier.weight(1f))
                        TextButton(onClick = { editSteps.removeAt(index) }) { Text("✕", color = MaterialTheme.colorScheme.error) }
                    }
                } else {
                    Text(text = "${step.stepNumber}. ${step.description}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(vertical = 2.dp))
                }
            }
            if (isStaff) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = newStepText, onValueChange = { newStepText = it }, label = { Text("新步骤说明") }, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(4.dp))
                    TextButton(onClick = {
                        if (newStepText.isNotBlank()) {
                            editSteps.add(RecipeStepEntity(menuItemId = item?.id ?: 0, stepNumber = editSteps.size + 1, description = newStepText))
                            newStepText = ""
                        }
                    }) { Text("+ 添加", color = MaterialTheme.colorScheme.secondary) }
                }
            }

            if (isStaff) {
                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    AnimatedVisibility(visible = showSaved, enter = fadeIn(), exit = fadeOut()) {
                        Text("已保存", color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = {
                        val updatedSteps = editSteps.mapIndexed { i, s -> s.copy(stepNumber = i + 1) }
                        onSaveRecipe(updatedSteps, editIngredients.toList())
                        showSaved = true
                        scope.launch { delay(800); showSaved = false; onDismiss() }
                    }) { Text("保存配方", color = MaterialTheme.colorScheme.primary) }
                }
            }

            if (!isStaff) {
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    if (orderQuantity > 0) {
                        QuantityStepper(quantity = orderQuantity, onIncrement = onIncrement, onDecrement = onDecrement)
                    } else {
                        Box(
                            modifier = Modifier.onGloballyPositioned { coords ->
                                val pos = coords.positionInWindow()
                                addBtnX = pos.x.roundToInt()
                                addBtnY = pos.y.roundToInt()
                            }.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary)
                                .clickable { onAddClick(addBtnX, addBtnY) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "+", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimary, textAlign = TextAlign.Center)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

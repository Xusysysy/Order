package com.order.app.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.order.app.data.db.dao.OrderBill
import com.order.app.data.db.dao.OrderWithItems
import com.order.app.data.db.entity.MenuItemEntity
import com.order.app.data.db.entity.OrderItemEntity
import com.order.app.data.db.entity.RecipeData
import com.order.app.data.db.entity.RecipeIngredientEntity
import com.order.app.data.db.entity.RecipeStepEntity
import com.order.app.data.db.entity.TableEntity
import com.order.app.ui.component.MenuCard
import com.order.app.ui.component.QuantityStepper
import com.order.app.ui.component.TableChip
import com.order.app.viewmodel.MainViewModel
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun categoryLabel(category: String): String = when (category) {
    MenuItemEntity.CATEGORY_COCKTAIL -> "调酒"
    MenuItemEntity.CATEGORY_DRINK -> "饮料"
    MenuItemEntity.CATEGORY_SNACK -> "小食"
    else -> category
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel, isDark: Boolean, onToggleTheme: () -> Unit) {
    val tables by viewModel.tables.collectAsStateWithLifecycle()
    val selectedTableId by viewModel.selectedTableId.collectAsStateWithLifecycle()
    val zones by viewModel.zones.collectAsStateWithLifecycle()
    val menuItems by viewModel.menuItems.collectAsStateWithLifecycle()
    val currentOrder by viewModel.currentOrder.collectAsStateWithLifecycle()
    val totalPrice by viewModel.totalPrice.collectAsStateWithLifecycle()
    val selectedMenuItem by viewModel.selectedMenuItem.collectAsStateWithLifecycle()
    val recipeSteps by viewModel.recipeSteps.collectAsStateWithLifecycle()
    val recipeIngredients by viewModel.recipeIngredients.collectAsStateWithLifecycle()
    val sheetVisible by viewModel.sheetVisible.collectAsStateWithLifecycle()
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val selectedBillId by viewModel.selectedBillId.collectAsStateWithLifecycle()
    val isEditing by viewModel.isEditing.collectAsStateWithLifecycle()
    val recipeMap by viewModel.recipeMap.collectAsStateWithLifecycle()
    val isTablet = LocalConfiguration.current.screenWidthDp >= 600

    val orderItems = currentOrder?.items ?: emptyList()
    val orderQuantities = orderItems.associate { it.menuItemId to it.quantity }
    val orderItemMap = orderItems.associateBy { it.menuItemId }
    val totalItemCount = orderItems.sumOf { it.quantity }
    val selectedTable = tables.find { it.id == selectedTableId }

    val snackbarHostState = remember { SnackbarHostState() }

    var showTableDrawer by remember { mutableStateOf(false) }
    var showAddTableDialog by remember { mutableStateOf(false) }
    var newTableName by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<MenuItemEntity?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var deleteOrderId by remember { mutableStateOf<Long?>(null) }
    var showTableEditDialog by remember { mutableStateOf(false) }
    var editingTable by remember { mutableStateOf<TableEntity?>(null) }

    // Delete confirmation dialog (shared by tablet and phone)
    if (showDeleteConfirm && deleteOrderId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false; deleteOrderId = null },
            title = { Text("确认删除") },
            text = { Text("确定要删除此订单吗？此操作不可撤销。") },
            confirmButton = {
                Button(onClick = { viewModel.deleteOrder(deleteOrderId!!); showDeleteConfirm = false; deleteOrderId = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("删除")
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = false; deleteOrderId = null }) { Text("取消") } }
        )
    }

    // Table edit dialog (shared by tablet and phone)
    if (showTableEditDialog && editingTable != null) {
        TableEditDialog(
            table = editingTable!!,
            allZones = zones,
            onDismiss = { showTableEditDialog = false; editingTable = null },
            onSave = { viewModel.updateTable(it); showTableEditDialog = false; editingTable = null },
            onDelete = { viewModel.deleteTable(editingTable!!.id); showTableEditDialog = false; editingTable = null }
        )
    }

    // Tablet layout
    if (isTablet) {
        TabletLayout(
            tables = tables, selectedTableId = selectedTableId, selectedTable = selectedTable,
            zones = zones, menuItems = menuItems, currentOrder = currentOrder, totalPrice = totalPrice,
            allOrders = allOrders, selectedBillId = selectedBillId, orderQuantities = orderQuantities,
            orderItemMap = orderItemMap, totalItemCount = totalItemCount,             isEditing = isEditing,
            snackbarHostState = snackbarHostState, viewModel = viewModel,
            recipeMap = recipeMap,
            isDark = isDark, onToggleTheme = onToggleTheme,
            sheetVisible = sheetVisible, selectedMenuItem = selectedMenuItem,
            recipeSteps = recipeSteps, recipeIngredients = recipeIngredients,
            showTableDrawer = showTableDrawer, onShowTableDrawer = { showTableDrawer = true },
            onDismissTableDrawer = { showTableDrawer = false },
            showAddTableDialog = showAddTableDialog, newTableName = newTableName,
            onNewTableNameChange = { newTableName = it },
            onShowAddTableDialog = { showAddTableDialog = true },
            onDismissAddTableDialog = { showAddTableDialog = false },
            onAddTable = { if (newTableName.isNotBlank()) { viewModel.addTable(newTableName); newTableName = ""; showAddTableDialog = false } },
            showEditDialog = showEditDialog, editingItem = editingItem,
            onShowEditDialog = { item -> editingItem = item; showEditDialog = true },
            onDismissEditDialog = { showEditDialog = false },
            onRequestDeleteOrder = { id -> deleteOrderId = id; showDeleteConfirm = true },
            editingTable = editingTable,
            onShowTableEditDialog = { table -> editingTable = table; showTableEditDialog = true }
        )
        return
    }

    // Phone layout
    val pagerState = rememberPagerState(pageCount = { 2 })
    val pagerScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        PageIndicator(currentPage = pagerState.currentPage)
                    },
                    actions = {
                        TextButton(onClick = { viewModel.toggleEdit() }) {
                            Text(
                                if (isEditing) "保存" else "编辑",
                                color = if (isEditing) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            },
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 4.dp) {
                    NavigationBarItem(
                        selected = pagerState.currentPage == 0,
                        onClick = { pagerScope.launch { pagerState.animateScrollToPage(0) } },
                        icon = { Text("🍽", style = MaterialTheme.typography.titleLarge) },
                        label = {
                            Text("菜单", fontSize = 11.sp,
                                color = if (pagerState.currentPage == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
                    )
                    NavigationBarItem(
                        selected = pagerState.currentPage == 1,
                        onClick = { pagerScope.launch { pagerState.animateScrollToPage(1) } },
                        icon = { Text("📋", style = MaterialTheme.typography.titleLarge) },
                        label = {
                            Text("账单", fontSize = 11.sp,
                                color = if (pagerState.currentPage == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
                    )
                }
            }
        ) { innerPadding ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                userScrollEnabled = false
            ) { page ->
                when (page) {
                    0 ->             PhoneMenuPanel(
                        tables = tables, selectedTableId = selectedTableId, selectedTable = selectedTable,
                        zones = zones, menuItems = menuItems, currentOrder = currentOrder, totalPrice = totalPrice,
                        orderQuantities = orderQuantities, orderItemMap = orderItemMap, totalItemCount = totalItemCount,
                        isEditing = isEditing,
                        showTableDrawer = showTableDrawer,
                        onShowTableDrawer = { showTableDrawer = true },
                        onDismissTableDrawer = { showTableDrawer = false },
                        showAddTableDialog = showAddTableDialog, newTableName = newTableName,
                        onNewTableNameChange = { newTableName = it },
                        onShowAddTableDialog = { showAddTableDialog = true },
                        onDismissAddTableDialog = { showAddTableDialog = false },
                        onAddTable = { if (newTableName.isNotBlank()) { viewModel.addTable(newTableName); newTableName = ""; showAddTableDialog = false } },
                        showEditDialog = showEditDialog, editingItem = editingItem,
                        onShowEditDialog = { item -> editingItem = item; showEditDialog = true },
            onDismissEditDialog = { showEditDialog = false; editingItem = null },
                        viewModel = viewModel
                    )
                    1 -> PhoneBillPanel(
                        currentOrder = currentOrder, totalPrice = totalPrice,
                        allOrders = allOrders, selectedBillId = selectedBillId,
                        isEditing = isEditing, viewModel = viewModel,
                        recipeMap = recipeMap,
                        onRequestDeleteOrder = { id -> deleteOrderId = id; showDeleteConfirm = true }
                    )
                }
            }
        }

        // Table drawer overlay
        AnimatedVisibility(
            visible = showTableDrawer,
            enter = slideInHorizontally(initialOffsetX = { -it }),
            exit = slideOutHorizontally(targetOffsetX = { -it }),
            modifier = Modifier.zIndex(2f).fillMaxHeight()
        ) {
            PhoneTableDrawer(
                tables = tables, selectedTableId = selectedTableId, zones = zones,
                isEditing = isEditing,
                onSelectTable = { viewModel.selectTable(it.id); showTableDrawer = false },
                onDeleteTable = { viewModel.deleteTable(it.id) },
                onAddTable = { showTableDrawer = false; showAddTableDialog = true },
                onDismiss = { showTableDrawer = false },
                onShowTableEditDialog = { table -> editingTable = table; showTableEditDialog = true }
            )
        }
        if (showTableDrawer) {
            Box(modifier = Modifier.fillMaxSize().zIndex(1f)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { showTableDrawer = false })
        }
    }

    // Add table dialog
    if (showAddTableDialog) {
        AlertDialog(
            onDismissRequest = { showAddTableDialog = false },
            title = { Text("添加桌位") },
            text = { OutlinedTextField(value = newTableName, onValueChange = { newTableName = it }, label = { Text("桌号名称") }, singleLine = true) },
            confirmButton = { Button(onClick = {
                if (newTableName.isNotBlank()) { viewModel.addTable(newTableName); newTableName = ""; showAddTableDialog = false }
            }) { Text("添加") } },
            dismissButton = { TextButton(onClick = { showAddTableDialog = false }) { Text("取消") } }
        )
    }

    // Recipe sheet
    if (sheetVisible) {
        RecipeSheet(
            item = selectedMenuItem, steps = recipeSteps, ingredients = recipeIngredients,
            onDismiss = { viewModel.dismissSheet() }, isStaff = isEditing,
            orderQuantity = selectedMenuItem?.let { orderQuantities[it.id] } ?: 0,
            onAddClick = { _, _ ->
                val item = selectedMenuItem ?: return@RecipeSheet
                selectedTableId?.let { viewModel.addToOrder(it, item.id, item.name, item.price) }
                viewModel.dismissSheet()
            },
            onIncrement = {
                val item = selectedMenuItem ?: return@RecipeSheet
                orderItemMap[item.id]?.let { viewModel.updateQuantity(it, 1) }
            },
            onDecrement = {
                val item = selectedMenuItem ?: return@RecipeSheet
                orderItemMap[item.id]?.let { viewModel.updateQuantity(it, -1) }
            },
            onSaveRecipe = { steps, ingredients ->
                val item = selectedMenuItem ?: return@RecipeSheet
                viewModel.saveRecipe(item.id, steps, ingredients)
            }
        )
    }

    // Edit dialog
    if (showEditDialog) {
        val categories = menuItems.map { it.category }.distinct()
        MenuItemEditDialog(
            editingItem = editingItem,
            allCategories = categories,
            onDismiss = { showEditDialog = false },
            onSave = { item ->
                if (editingItem != null) viewModel.updateItem(item) else viewModel.addItem(item)
                showEditDialog = false
            },
            onDelete = if (editingItem != null) {
                { viewModel.deleteItem(editingItem!!.id); showEditDialog = false }
            } else null
        )
    }
}

@Composable
private fun PageIndicator(currentPage: Int) {
    val label = if (currentPage == 0) "点餐" else "账单"
    Text(label, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
}

// ======================= TABLET LAYOUT =======================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TabletLayout(
    tables: List<TableEntity>, selectedTableId: Long?, selectedTable: TableEntity?,
    zones: List<String>, menuItems: List<MenuItemEntity>,
    currentOrder: OrderWithItems?, totalPrice: Double,
    allOrders: List<OrderBill>, selectedBillId: Long?,
    orderQuantities: Map<Long, Int>, orderItemMap: Map<Long, OrderItemEntity>, totalItemCount: Int,
    isEditing: Boolean, snackbarHostState: SnackbarHostState, viewModel: MainViewModel,
    recipeMap: Map<Long, RecipeData>,
    isDark: Boolean, onToggleTheme: () -> Unit,
    sheetVisible: Boolean, selectedMenuItem: MenuItemEntity?,
    recipeSteps: List<RecipeStepEntity>, recipeIngredients: List<RecipeIngredientEntity>,
    showTableDrawer: Boolean, onShowTableDrawer: () -> Unit, onDismissTableDrawer: () -> Unit,
    showAddTableDialog: Boolean, newTableName: String, onNewTableNameChange: (String) -> Unit,
    onShowAddTableDialog: () -> Unit, onDismissAddTableDialog: () -> Unit, onAddTable: () -> Unit,
    showEditDialog: Boolean, editingItem: MenuItemEntity?,
    onShowEditDialog: (MenuItemEntity?) -> Unit, onDismissEditDialog: () -> Unit,
    onRequestDeleteOrder: (Long) -> Unit,
    editingTable: TableEntity?,
    onShowTableEditDialog: (TableEntity) -> Unit
) {
    var showTableMenu by remember { mutableStateOf(false) }

    if (showEditDialog) {
        val categories = menuItems.map { it.category }.distinct()
        MenuItemEditDialog(
            editingItem = editingItem,
            allCategories = categories,
            onDismiss = { onDismissEditDialog() },
            onSave = { item ->
                if (editingItem != null) viewModel.updateItem(item) else viewModel.addItem(item)
                onDismissEditDialog()
            },
            onDelete = if (editingItem != null) {
                { viewModel.deleteItem(editingItem!!.id); onDismissEditDialog() }
            } else null
        )
    }

    if (sheetVisible) {
        RecipeSheet(
            item = selectedMenuItem, steps = recipeSteps, ingredients = recipeIngredients,
            onDismiss = { viewModel.dismissSheet() }, isStaff = isEditing,
            orderQuantity = selectedMenuItem?.let { orderQuantities[it.id] } ?: 0,
            onAddClick = { _, _ ->
                val item = selectedMenuItem ?: return@RecipeSheet
                selectedTableId?.let { viewModel.addToOrder(it, item.id, item.name, item.price) }
                viewModel.dismissSheet()
            },
            onIncrement = { val item = selectedMenuItem ?: return@RecipeSheet; orderItemMap[item.id]?.let { viewModel.updateQuantity(it, 1) } },
            onDecrement = { val item = selectedMenuItem ?: return@RecipeSheet; orderItemMap[item.id]?.let { viewModel.updateQuantity(it, -1) } },
            onSaveRecipe = { steps2, ingredients2 ->
                val item = selectedMenuItem ?: return@RecipeSheet
                viewModel.saveRecipe(item.id, steps2, ingredients2)
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("点餐", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground) },
                actions = {
                    TextButton(onClick = { viewModel.toggleEdit() }) {
                        Text(if (isEditing) "保存" else "编辑",
                            color = if (isEditing) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Row(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Box(modifier = Modifier.weight(2f).fillMaxHeight()) {
                TabletMenuPanel(
                    tables = tables, selectedTableId = selectedTableId, selectedTable = selectedTable,
                    zones = zones, menuItems = menuItems, isEditing = isEditing,
                    orderQuantities = orderQuantities, orderItemMap = orderItemMap,
                    currentOrder = currentOrder, totalPrice = totalPrice, totalItemCount = totalItemCount,
                    viewModel = viewModel, showTableMenu = showTableMenu,
                    onToggleTableMenu = { showTableMenu = it },
                    onShowEditDialog = { item -> onShowEditDialog(item) },
                    onShowTableEditDialog = onShowTableEditDialog
                )
            }
            Surface(modifier = Modifier.weight(1f).fillMaxHeight(), color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), tonalElevation = 4.dp) {
                TabletBillPanel(
                    currentOrder = currentOrder, totalPrice = totalPrice,
                    allOrders = allOrders, selectedBillId = selectedBillId,
                    isEditing = isEditing, viewModel = viewModel,
                    recipeMap = recipeMap,
                    onRequestDeleteOrder = onRequestDeleteOrder
                )
            }
        }
    }
}

@Composable
private fun TabletMenuPanel(
    tables: List<TableEntity>, selectedTableId: Long?, selectedTable: TableEntity?,
    zones: List<String>, menuItems: List<MenuItemEntity>, isEditing: Boolean,
    orderQuantities: Map<Long, Int>, orderItemMap: Map<Long, OrderItemEntity>,
    currentOrder: OrderWithItems?, totalPrice: Double, totalItemCount: Int,
    viewModel: MainViewModel, showTableMenu: Boolean,
    onToggleTableMenu: (Boolean) -> Unit, onShowEditDialog: (MenuItemEntity?) -> Unit,
    onShowTableEditDialog: (TableEntity) -> Unit
) {
    val categories = menuItems.map { it.category }.distinct()
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull() ?: "") }

    LaunchedEffect(categories) { if (selectedCategory.isEmpty() && categories.isNotEmpty()) selectedCategory = categories.first() }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar with table selector and add button
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                val label = if (isEditing) "桌位" else (selectedTable?.name ?: "选择桌位 ▾")
                Text(label, modifier = Modifier.clickable { onToggleTableMenu(true) }
                    .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.small)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary)
                DropdownMenu(expanded = showTableMenu, onDismissRequest = { onToggleTableMenu(false) }) {
                    zones.forEach { zone ->
                        val zoneTables = tables.filter { it.zone == zone }
                        if (zone.isEmpty() || zoneTables.isEmpty()) return@forEach
                        Text(zone, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                        zoneTables.forEach { table ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(8.dp).background(
                                            if (table.status == "ORDERED") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), shape = CircleShape))
                                        Spacer(Modifier.width(8.dp))
                                        Text(table.name)
                                    }
                                },
                                onClick = {
                                    if (isEditing) {
                                        onShowTableEditDialog(table)
                                    } else {
                                        viewModel.selectTable(table.id)
                                    }
                                    onToggleTableMenu(false)
                                },
                                leadingIcon = if (table.id == selectedTableId) {{ Text("✓", color = MaterialTheme.colorScheme.primary) }} else null
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            if (isEditing) {
                TextButton(onClick = { onShowEditDialog(null) }) {
                    Text("+ 添加菜品", color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))

        // Category filters
        LazyRow(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { cat ->
                FilterChip(selected = cat == selectedCategory, onClick = { selectedCategory = cat }, label = { Text(categoryLabel(cat)) })
            }
        }

        val filteredItems = menuItems.filter { it.category == selectedCategory }
        if (filteredItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Text("暂无菜单项", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f))
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 200.dp),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                gridItems(filteredItems) { item ->
                    val qty = orderQuantities[item.id] ?: 0
                    Box {
                        MenuCard(
                            item = item,
                            onClick = {
                                if (isEditing) {
                                    viewModel.selectItem(item)
                                } else {
                                    selectedTableId?.let { viewModel.addToOrder(it, item.id, item.name, item.price) }
                                }
                            },
                            showAddButton = !isEditing,
                            orderQuantity = qty,
                            onAddClick = { _, _ -> selectedTableId?.let { viewModel.addToOrder(it, item.id, item.name, item.price) } },
                            onIncrement = { orderItemMap[item.id]?.let { viewModel.updateQuantity(it, 1) } },
                            onDecrement = { orderItemMap[item.id]?.let { viewModel.updateQuantity(it, -1) } }
                        )
                        if (qty > 0) {
                            Badge(modifier = Modifier.align(Alignment.TopEnd).offset(x = 4.dp, y = (-4).dp), containerColor = MaterialTheme.colorScheme.error) { Text("$qty") }
                        }
                        if (isEditing) {
                            TextButton(onClick = { onShowEditDialog(item) }, modifier = Modifier.align(Alignment.TopEnd)) {
                                Text("✏", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabletBillPanel(
    currentOrder: OrderWithItems?, totalPrice: Double,
    allOrders: List<OrderBill>, selectedBillId: Long?,
    isEditing: Boolean, viewModel: MainViewModel,
    recipeMap: Map<Long, RecipeData>,
    onRequestDeleteOrder: (Long) -> Unit
) {
    val listState = rememberLazyListState()
    val itemCount = currentOrder?.items?.size ?: 0
    var prevCount by remember { mutableIntStateOf(itemCount) }
    LaunchedEffect(itemCount) {
        if (itemCount > prevCount && itemCount > 0) {
            listState.animateScrollToItem(0)
        }
        prevCount = itemCount
    }

    LazyColumn(state = listState, modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        // Current order
        if (currentOrder != null && currentOrder.items.isNotEmpty()) {
            item(key = "active") {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("当前", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            Text("¥%.0f".format(totalPrice), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                        currentOrder.items.forEach { item ->
                            key(item.id) {
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300))
                                ) {
                                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Column(Modifier.weight(1f)) {
                                            Text(item.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                                            Text("¥%.0f".format(item.price), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                                            RecipeSubCard(recipe = recipeMap[item.menuItemId])
                                        }
                                        if (!isEditing) {
                                            QuantityStepper(quantity = item.quantity,
                                                onIncrement = { viewModel.updateQuantity(item, 1) },
                                                onDecrement = { viewModel.updateQuantity(item, -1) },
                                                compact = true)
                                        }
                                    }
                                }
                            }
                        }
                        if (!isEditing) {
                            Button(onClick = { viewModel.settleOrder() }, modifier = Modifier.fillMaxWidth().height(36.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                                Text("结账", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
        // All orders
        if (allOrders.isNotEmpty()) {
            item(key = "order_header") { Text("所有订单", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)) }
            items(allOrders, key = { it.orderId }) { bill ->
                val isSelected = bill.orderId == selectedBillId
                val isSettled = bill.status == "SETTLED"
                if (isSettled) {
                    SwipeableCard(
                        onDelete = { onRequestDeleteOrder(bill.orderId) },
                        content = {
                            Card(
                                modifier = Modifier.fillMaxWidth().animateItem(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = null
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Column {
                                            Text(bill.tableName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                                            Text("已结账", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                        }
                                    }
                                }
                            }
                        }
                    )
                } else {
                Card(
                    modifier = Modifier.fillMaxWidth().animateItem().then(
                        if (!isSettled) Modifier.clickable { viewModel.selectBill(bill.orderId) } else Modifier
                    ),
                    colors = CardDefaults.cardColors(containerColor = when {
                        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.surface
                    }),
                    border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(bill.tableName, style = MaterialTheme.typography.bodyMedium, color = if (isSettled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface)
                                Text(if (isSettled) "已结账" else "${bill.itemCount}件 ¥%.0f".format(bill.totalPrice), style = MaterialTheme.typography.bodySmall, color = if (isSettled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) else MaterialTheme.colorScheme.primary)
                            }
                            if (!isSettled) {
                                TextButton(onClick = { onRequestDeleteOrder(bill.orderId) }) {
                                    Text("删除", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                        if (isSelected && !isSettled) {
                            Spacer(Modifier.height(4.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                            bill.items.forEach { item ->
                                Column {
                                    Row(Modifier.fillMaxWidth().padding(vertical = 1.dp)) {
                                        Text(item.name, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                                        Text("x${item.quantity}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                    }
                                    RecipeSubCard(recipe = recipeMap[item.menuItemId])
                                }
                            }
                            Button(onClick = { viewModel.settleBill(bill.orderId) }, modifier = Modifier.fillMaxWidth().height(40.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                                Text("结账", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
                }
            }
        }
        if ((currentOrder == null || currentOrder.items.isEmpty()) && allOrders.isEmpty()) {
            item { Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) { Text("暂无订单", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)) } }
        }
    }
}

// ======================= PHONE LAYOUT =======================

@Composable
private fun PhoneMenuPanel(
    tables: List<TableEntity>, selectedTableId: Long?, selectedTable: TableEntity?,
    zones: List<String>, menuItems: List<MenuItemEntity>,
    currentOrder: OrderWithItems?, totalPrice: Double,
    orderQuantities: Map<Long, Int>, orderItemMap: Map<Long, OrderItemEntity>, totalItemCount: Int,
    isEditing: Boolean,
    showTableDrawer: Boolean, onShowTableDrawer: () -> Unit, onDismissTableDrawer: () -> Unit,
    showAddTableDialog: Boolean, newTableName: String, onNewTableNameChange: (String) -> Unit,
    onShowAddTableDialog: () -> Unit, onDismissAddTableDialog: () -> Unit, onAddTable: () -> Unit,
    showEditDialog: Boolean, editingItem: MenuItemEntity?,
    onShowEditDialog: (MenuItemEntity?) -> Unit, onDismissEditDialog: () -> Unit,
    viewModel: MainViewModel
) {
    val categories = menuItems.map { it.category }.distinct()
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull() ?: "") }
    LaunchedEffect(categories) { if (selectedCategory.isEmpty() && categories.isNotEmpty()) selectedCategory = categories.first() }

    Column(modifier = Modifier.fillMaxSize()) {
        // Table selector
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val label = if (selectedTable != null) selectedTable.name else "选择桌位 ▸"
            Text(label, modifier = Modifier.clickable { onShowTableDrawer() }
                .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.small)
                .padding(horizontal = 16.dp, vertical = 10.dp),
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.weight(1f))
            if (isEditing) {
                TextButton(onClick = { onShowEditDialog(null) }) {
                    Text("+ 添加菜品", color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))

        // Quick order summary
        if (!isEditing && totalItemCount > 0) {
            Surface(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("已点 ${totalItemCount} 件 · ¥%.0f".format(totalPrice), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // Category filters
        LazyRow(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { cat -> FilterChip(selected = cat == selectedCategory, onClick = { selectedCategory = cat }, label = { Text(categoryLabel(cat)) }) }
        }

        val filteredItems = menuItems.filter { it.category == selectedCategory }
        if (filteredItems.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Text("暂无菜单项", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f))
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                gridItems(filteredItems) { item ->
                    val qty = orderQuantities[item.id] ?: 0
                    Box {
                        MenuCard(
                            item = item,
                            onClick = {
                                if (isEditing) viewModel.selectItem(item)
                                else selectedTableId?.let { viewModel.addToOrder(it, item.id, item.name, item.price) }
                            },
                            showAddButton = !isEditing,
                            orderQuantity = qty,
                            onAddClick = { _, _ -> selectedTableId?.let { viewModel.addToOrder(it, item.id, item.name, item.price) } },
                            onIncrement = { orderItemMap[item.id]?.let { viewModel.updateQuantity(it, 1) } },
                            onDecrement = { orderItemMap[item.id]?.let { viewModel.updateQuantity(it, -1) } }
                        )
                        if (qty > 0) {
                            Badge(modifier = Modifier.align(Alignment.TopEnd).offset(x = 4.dp, y = (-4).dp), containerColor = MaterialTheme.colorScheme.error) { Text("$qty") }
                        }
                        if (isEditing) {
                            TextButton(onClick = { onShowEditDialog(item) }, modifier = Modifier.align(Alignment.TopEnd)) { Text("✏", fontSize = 12.sp) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhoneBillPanel(
    currentOrder: OrderWithItems?, totalPrice: Double,
    allOrders: List<OrderBill>, selectedBillId: Long?,
    isEditing: Boolean, viewModel: MainViewModel,
    recipeMap: Map<Long, RecipeData>,
    onRequestDeleteOrder: (Long) -> Unit
) {
    val listState = rememberLazyListState()
    val itemCount = currentOrder?.items?.size ?: 0
    var prevCount by remember { mutableIntStateOf(itemCount) }
    LaunchedEffect(itemCount) {
        if (itemCount > prevCount && itemCount > 0) {
            listState.animateScrollToItem(0)
        }
        prevCount = itemCount
    }

    LazyColumn(state = listState, modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Current order
        if (currentOrder != null && currentOrder.items.isNotEmpty()) {
            item(key = "active") {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("当前订单", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                            Text("¥%.0f".format(totalPrice), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(Modifier.height(8.dp))
                        currentOrder.items.forEach { item ->
                            key(item.id) {
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn(animationSpec = tween(300)) + expandVertically(animationSpec = tween(300))
                                ) {
                                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                            Column(Modifier.weight(1f)) {
                                                Text(item.name, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                                                Text("¥%.0f x${item.quantity}".format(item.price), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                            }
                                            if (!isEditing) {
                                                QuantityStepper(quantity = item.quantity,
                                                    onIncrement = { viewModel.updateQuantity(item, 1) },
                                                    onDecrement = { viewModel.updateQuantity(item, -1) })
                                            }
                                        }
                                        RecipeSubCard(recipe = recipeMap[item.menuItemId])
                                    }
                                }
                            }
                        }
                        if (!isEditing) {
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { viewModel.settleOrder() }, modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) { Text("结账") }
                        }
                    }
                }
            }
        }
        // All orders
        if (allOrders.isNotEmpty()) {
            item(key = "order_header") { Text("所有订单", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground) }
            items(allOrders, key = { it.orderId }) { bill ->
                val isSelected = bill.orderId == selectedBillId
                val isSettled = bill.status == "SETTLED"
                if (isSettled) {
                    SwipeableCard(
                        onDelete = { onRequestDeleteOrder(bill.orderId) },
                        content = {
                            Card(
                                modifier = Modifier.fillMaxWidth().animateItem(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = null
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Column {
                                            Text(bill.tableName, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                                            Text("已结账", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                        }
                                    }
                                }
                            }
                        }
                    )
                } else {
                Card(
                    modifier = Modifier.fillMaxWidth().animateItem().then(
                        if (!isSettled) Modifier.clickable { viewModel.selectBill(bill.orderId) } else Modifier
                    ),
                    colors = CardDefaults.cardColors(containerColor = when {
                        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.surface
                    }),
                    border = if (isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(bill.tableName, style = MaterialTheme.typography.titleMedium, color = if (isSettled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface)
                                Text(if (isSettled) "已结账" else "${bill.itemCount}件 ¥%.0f".format(bill.totalPrice), style = MaterialTheme.typography.bodyMedium, color = if (isSettled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) else MaterialTheme.colorScheme.primary)
                            }
                            if (!isSettled) {
                                TextButton(onClick = { onRequestDeleteOrder(bill.orderId) }) {
                                    Text("删除", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                        if (isSelected && !isSettled) {
                            Spacer(Modifier.height(8.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                            bill.items.forEach { item ->
                                Column {
                                    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                                        Text(item.name, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                                        Text("x${item.quantity}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                    }
                                    RecipeSubCard(recipe = recipeMap[item.menuItemId])
                                }
                            }
                            Button(onClick = { viewModel.settleBill(bill.orderId) }, modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) { Text("结账") }
                        }
                    }
                }
                }
            }
        }
        if ((currentOrder == null || currentOrder.items.isEmpty()) && allOrders.isEmpty()) {
            item { Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { Text("暂无订单", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)) } }
        }
    }
}

// ======================= PHONE TABLE DRAWER =======================

@Composable
private fun PhoneTableDrawer(
    tables: List<TableEntity>, selectedTableId: Long?, zones: List<String>,
    isEditing: Boolean,
    onSelectTable: (TableEntity) -> Unit,
    onDeleteTable: (TableEntity) -> Unit,
    onAddTable: () -> Unit,
    onDismiss: () -> Unit,
    onShowTableEditDialog: (TableEntity) -> Unit
) {
    Surface(modifier = Modifier.width(280.dp).fillMaxHeight(), color = MaterialTheme.colorScheme.surface, tonalElevation = 8.dp) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Text("桌位", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(12.dp))
            LazyColumn(modifier = Modifier.weight(1f)) {
                zones.forEach { zone ->
                    if (zone.isNotEmpty()) {
                        item {
                            Text(zone, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp))
                        }
                    }
                    items(tables.filter { it.zone == zone }) { table ->
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.weight(1f).clickable {
                                if (isEditing) {
                                    onShowTableEditDialog(table)
                                } else {
                                    onSelectTable(table)
                                }
                            }) {
                                TableChip(table = table, isSelected = table.id == selectedTableId, onClick = {})
                            }
                            if (isEditing) {
                                TextButton(onClick = { onDeleteTable(table) }) { Text("删除", color = MaterialTheme.colorScheme.error) }
                            }
                        }
                        Spacer(Modifier.height(2.dp))
                    }
                }
            }
            if (isEditing) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onAddTable, modifier = Modifier.fillMaxWidth()) { Text("+ 添加桌位") }
            }
        }
    }
}

// ======================= MENU ITEM EDIT DIALOG =======================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuItemEditDialog(
    editingItem: MenuItemEntity?,
    allCategories: List<String>,
    onDismiss: () -> Unit,
    onSave: (MenuItemEntity) -> Unit,
    onDelete: (() -> Unit)?
) {
    var name by remember(editingItem) { mutableStateOf(editingItem?.name ?: "") }
    var price by remember(editingItem) { mutableStateOf(editingItem?.price?.toString() ?: "") }
    var hasRecipe by remember(editingItem) { mutableStateOf(editingItem?.hasRecipe ?: false) }
    var selectedCategory by remember(editingItem) { mutableStateOf(editingItem?.category ?: allCategories.firstOrNull() ?: "other") }
    var newCategory by remember { mutableStateOf("") }
    var showAddCategory by remember { mutableStateOf(false) }
    val customCategories = remember { mutableStateListOf<String>() }
    val displayedCategories = (allCategories + customCategories).distinct()
    val isNew = editingItem == null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isNew) "添加菜品" else "编辑菜品") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("名称") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("价格") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    displayedCategories.forEach { cat ->
                        FilterChip(selected = cat == selectedCategory, onClick = { selectedCategory = cat }, label = { Text(categoryLabel(cat)) })
                    }
                    if (!showAddCategory) {
                        TextButton(onClick = { showAddCategory = true }) { Text("+新分类", fontSize = 12.sp) }
                    }
                }
                if (showAddCategory) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(value = newCategory, onValueChange = { newCategory = it }, label = { Text("新分类") }, singleLine = true, modifier = Modifier.weight(1f))
                        TextButton(onClick = {
                            if (newCategory.isNotBlank()) {
                                selectedCategory = newCategory
                                if (newCategory !in customCategories) customCategories.add(newCategory)
                                newCategory = ""
                                showAddCategory = false
                            }
                        }) { Text("确认") }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("含配方步骤", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.weight(1f))
                    androidx.compose.material3.Switch(checked = hasRecipe, onCheckedChange = { hasRecipe = it })
                }
                if (onDelete != null) {
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = onDelete) { Text("删除此菜品", color = MaterialTheme.colorScheme.error) }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val priceVal = price.toDoubleOrNull() ?: 0.0
                onSave(MenuItemEntity(
                    id = editingItem?.id ?: 0, name = name, price = priceVal,
                    category = selectedCategory, hasRecipe = hasRecipe
                ))
            }) { Text("保存") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}

// ======================= TABLE EDIT DIALOG =======================

@Composable
private fun TableEditDialog(
    table: TableEntity,
    allZones: List<String>,
    onDismiss: () -> Unit,
    onSave: (TableEntity) -> Unit,
    onDelete: () -> Unit
) {
    var name by remember { mutableStateOf(table.name) }
    var zone by remember { mutableStateOf(table.zone) }
    var newZone by remember { mutableStateOf("") }
    var showNewZone by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑桌位") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("桌号名称") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Text("分区", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    allZones.forEach { z ->
                        FilterChip(selected = z == zone, onClick = { zone = z }, label = { Text(z.ifEmpty { "未分类" }) })
                    }
                    if (!showNewZone) {
                        TextButton(onClick = { showNewZone = true }) { Text("+新区", fontSize = 12.sp) }
                    }
                }
                if (showNewZone) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(value = newZone, onValueChange = { newZone = it }, label = { Text("新区名") }, singleLine = true, modifier = Modifier.weight(1f))
                        TextButton(onClick = { if (newZone.isNotBlank()) { zone = newZone; newZone = ""; showNewZone = false } }) { Text("确认") }
                    }
                }
                Spacer(Modifier.height(4.dp))
                HorizontalDivider()
                TextButton(onClick = onDelete) { Text("删除此桌位", color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = { Button(onClick = { onSave(table.copy(name = name, zone = zone)) }) { Text("保存") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}

// ======================= SWIPEABLE CARD =======================

@Composable
private fun SwipeableCard(
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val deleteWidthPx = with(density) { 80.dp.toPx() }
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.width(70.dp).fillMaxHeight()
                    .clickable {
                        scope.launch { offsetX.animateTo(0f) }
                        onDelete()
                    },
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("删除", color = MaterialTheme.colorScheme.onError, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                if (offsetX.value < -deleteWidthPx / 2) {
                                    offsetX.animateTo(-deleteWidthPx)
                                } else {
                                    offsetX.animateTo(0f)
                                }
                            }
                        },
                        onDragCancel = {
                            scope.launch { offsetX.animateTo(0f) }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            scope.launch {
                                offsetX.snapTo((offsetX.value + dragAmount).coerceIn(-deleteWidthPx, 0f))
                            }
                        }
                    )
                }
        ) {
            content()
        }
    }
}

@Composable
private fun RecipeSubCard(recipe: RecipeData?) {
    if (recipe == null) return
    if (recipe.steps.isEmpty() && recipe.ingredients.isEmpty()) return
    Column(modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)) {
        if (recipe.ingredients.isNotEmpty()) {
            recipe.ingredients.forEach { ing ->
                Text("• ${ing.name} ${ing.amount}${ing.unit}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
        if (recipe.steps.isNotEmpty()) {
            recipe.steps.forEach { step ->
                Text("${step.stepNumber}. ${step.description}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}

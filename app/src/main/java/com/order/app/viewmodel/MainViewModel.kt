package com.order.app.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.order.app.data.repository.MenuRepository
import com.order.app.data.repository.OrderRepository
import com.order.app.data.repository.TableRepository
import com.order.app.data.db.dao.OrderBill
import com.order.app.data.db.dao.OrderWithItems
import com.order.app.data.db.entity.MenuItemEntity
import com.order.app.data.db.entity.OrderEntity
import com.order.app.data.db.entity.OrderItemEntity
import com.order.app.data.db.entity.RecipeData
import com.order.app.data.db.entity.RecipeIngredientEntity
import com.order.app.data.db.entity.RecipeStepEntity
import com.order.app.data.db.entity.TableEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainViewModel(
    private val tableRepository: TableRepository,
    private val menuRepository: MenuRepository,
    private val orderRepository: OrderRepository,
    private val prefs: SharedPreferences
) : ViewModel() {

    // Edit mode
    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing

    // Tables
    private val _tables = MutableStateFlow<List<TableEntity>>(emptyList())
    val tables: StateFlow<List<TableEntity>> = _tables

    private val _selectedTableId = MutableStateFlow<Long?>(null)
    val selectedTableId: StateFlow<Long?> = _selectedTableId

    private val _zones = MutableStateFlow<List<String>>(emptyList())
    val zones: StateFlow<List<String>> = _zones

    // Menu
    private val _menuItems = MutableStateFlow<List<MenuItemEntity>>(emptyList())
    val menuItems: StateFlow<List<MenuItemEntity>> = _menuItems

    private val _selectedMenuItem = MutableStateFlow<MenuItemEntity?>(null)
    val selectedMenuItem: StateFlow<MenuItemEntity?> = _selectedMenuItem

    private val _recipeSteps = MutableStateFlow<List<RecipeStepEntity>>(emptyList())
    val recipeSteps: StateFlow<List<RecipeStepEntity>> = _recipeSteps

    private val _recipeIngredients = MutableStateFlow<List<RecipeIngredientEntity>>(emptyList())
    val recipeIngredients: StateFlow<List<RecipeIngredientEntity>> = _recipeIngredients

    private val _sheetVisible = MutableStateFlow(false)
    val sheetVisible: StateFlow<Boolean> = _sheetVisible

    // Order
    private val _currentOrder = MutableStateFlow<OrderWithItems?>(null)
    val currentOrder: StateFlow<OrderWithItems?> = _currentOrder

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice

    private val _allOrders = MutableStateFlow<List<OrderBill>>(emptyList())
    val allOrders: StateFlow<List<OrderBill>> = _allOrders

    private val _selectedBillId = MutableStateFlow<Long?>(null)
    val selectedBillId: StateFlow<Long?> = _selectedBillId

    private val _recipeMap = MutableStateFlow<Map<Long, RecipeData>>(emptyMap())
    val recipeMap: StateFlow<Map<Long, RecipeData>> = _recipeMap

    private val _activeOrderCount = MutableStateFlow(0)
    val activeOrderCount: StateFlow<Int> = _activeOrderCount

    private var autoRefreshJob: Job? = null

    init {
        viewModelScope.launch {
            tableRepository.createDefaultTables()
            loadTables()
            loadMenuItems()

            val savedTableId = prefs.getLong("selected_table_id", -1L)
            val tablesList = _tables.value
            if (savedTableId > 0 && tablesList.any { it.id == savedTableId }) {
                selectTable(savedTableId)
            } else if (tablesList.isNotEmpty()) {
                selectTable(tablesList.first().id)
            }
        }
    }

    fun toggleEdit() {
        val newEditing = !_isEditing.value
        _isEditing.value = newEditing
        if (!newEditing) {
            loadMenuItems()
            loadTables()
            loadAllOrders()
        }
    }

    // Table functions
    fun loadTables() {
        viewModelScope.launch {
            val list = tableRepository.getAllDirect()
            _tables.value = list
            _zones.value = list.map { it.zone }.distinct()
        }
    }

    fun selectTable(id: Long) {
        _selectedTableId.value = id
        prefs.edit().putLong("selected_table_id", id).apply()
        loadOrder(id)
    }

    fun addTable(name: String, zone: String = "大厅") {
        viewModelScope.launch {
            tableRepository.insert(TableEntity(name = name, zone = zone))
            loadTables()
        }
    }

    fun updateTable(table: TableEntity) {
        viewModelScope.launch {
            tableRepository.update(table)
            loadTables()
        }
    }

    fun deleteTable(id: Long) {
        viewModelScope.launch {
            tableRepository.delete(id)
            if (_selectedTableId.value == id) {
                _selectedTableId.value = null
            }
            loadTables()
        }
    }

    fun reorderTables(reordered: List<TableEntity>) {
        _tables.value = reordered
        viewModelScope.launch {
            tableRepository.updateSortOrders(reordered.map { it.id })
        }
    }

    // Menu functions
    fun loadMenuItems() {
        viewModelScope.launch {
            menuRepository.getAll().collect { _menuItems.value = it }
        }
    }

    fun reorderItems(reordered: List<MenuItemEntity>) {
        _menuItems.value = reordered
        viewModelScope.launch {
            menuRepository.updateSortOrders(reordered.map { it.id })
        }
    }

    fun selectItem(item: MenuItemEntity) {
        _selectedMenuItem.value = item
        if (item.hasRecipe) {
            _sheetVisible.value = true
            viewModelScope.launch {
                _recipeSteps.value = menuRepository.getRecipeSteps(item.id)
                _recipeIngredients.value = menuRepository.getRecipeIngredients(item.id)
            }
        }
    }

    fun loadRecipesForMenuItems(menuItemIds: List<Long>) {
        viewModelScope.launch {
            val recipes = menuRepository.getRecipesForMenuItems(menuItemIds)
            _recipeMap.value = _recipeMap.value + recipes
        }
    }

    fun dismissSheet() {
        _sheetVisible.value = false
    }

    fun addItem(item: MenuItemEntity) {
        viewModelScope.launch {
            menuRepository.insert(item)
            menuRepository.getAll().collect { _menuItems.value = it }
        }
    }

    fun updateItem(item: MenuItemEntity) {
        viewModelScope.launch {
            menuRepository.update(item)
            menuRepository.getAll().collect { _menuItems.value = it }
        }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            menuRepository.delete(id)
            if (_selectedMenuItem.value?.id == id) {
                _selectedMenuItem.value = null
                _sheetVisible.value = false
            }
            menuRepository.getAll().collect { _menuItems.value = it }
        }
    }

    fun saveRecipe(
        menuItemId: Long,
        steps: List<RecipeStepEntity>,
        ingredients: List<RecipeIngredientEntity>
    ) {
        viewModelScope.launch {
            menuRepository.saveRecipe(menuItemId, steps, ingredients)
            menuRepository.markHasRecipe(menuItemId, steps.isNotEmpty())
            _recipeSteps.value = steps
            _recipeIngredients.value = ingredients
            _sheetVisible.value = false
            menuRepository.getAll().collect { _menuItems.value = it }
        }
    }

    // Order functions
    fun loadOrder(tableId: Long) {
        viewModelScope.launch {
            val order = orderRepository.getActiveOrder(tableId)
            if (order != null) {
                val withItems = orderRepository.getOrderWithItems(order.id)
                _currentOrder.value = withItems
                _totalPrice.value = withItems?.items?.sumOf { it.price * it.quantity } ?: 0.0
            } else {
                _currentOrder.value = null
                _totalPrice.value = 0.0
            }
        }
    }

    fun addToOrder(tableId: Long, menuItemId: Long, name: String, price: Double, quantity: Int = 1) {
        viewModelScope.launch {
            val order = _currentOrder.value?.order ?: orderRepository.createOrder(tableId)
            if (_currentOrder.value == null) {
                _currentOrder.value = OrderWithItems(order, emptyList())
            }
            orderRepository.addItem(order.id, OrderItemEntity(
                orderId = order.id, menuItemId = menuItemId, name = name, quantity = quantity, price = price
            ))
            reloadOrder(order.id)
        }
    }

    fun updateQuantity(item: OrderItemEntity, delta: Int) {
        viewModelScope.launch {
            val newQty = (item.quantity + delta).coerceAtLeast(0)
            if (newQty <= 0) {
                orderRepository.removeItem(item.id)
            } else {
                orderRepository.updateItemQuantity(item.copy(quantity = newQty))
            }
            _currentOrder.value?.order?.let { reloadOrder(it.id) }
        }
    }

    fun settleOrder() {
        viewModelScope.launch {
            _currentOrder.value?.order?.let {
                orderRepository.updateOrderStatus(it.id, OrderEntity.STATUS_SETTLED)
                _currentOrder.value = null
                _totalPrice.value = 0.0
            }
        }
    }

    fun loadAllOrders() {
        viewModelScope.launch {
            val orders = orderRepository.getAllOrderBills()
            _allOrders.value = orders
            _activeOrderCount.value = orders.count { it.status == OrderEntity.STATUS_ACTIVE }
            _selectedBillId.value = null
            val ids = orders.flatMap { it.items.map { i -> i.menuItemId } }.distinct()
            if (ids.isNotEmpty()) {
                val recipes = menuRepository.getRecipesForMenuItems(ids)
                _recipeMap.value = _recipeMap.value + recipes
            }
        }
    }

    fun selectBill(orderId: Long) {
        _selectedBillId.value = if (_selectedBillId.value == orderId) null else orderId
    }

    fun settleBill(orderId: Long) {
        viewModelScope.launch {
            orderRepository.updateOrderStatus(orderId, OrderEntity.STATUS_SETTLED)
            loadAllOrders()
        }
    }

    fun deleteOrder(id: Long) {
        viewModelScope.launch {
            orderRepository.deleteOrder(id)
            loadAllOrders()
        }
    }

    fun startAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (isActive) {
                delay(3000)
                val orders = orderRepository.getAllOrderBills()
                _allOrders.value = orders
                _activeOrderCount.value = orders.count { it.status == OrderEntity.STATUS_ACTIVE }
            }
        }
    }

    fun stopAutoRefresh() {
        autoRefreshJob?.cancel()
        autoRefreshJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopAutoRefresh()
    }

    private suspend fun reloadOrder(orderId: Long) {
        val updated = orderRepository.getOrderWithItems(orderId)
        _currentOrder.value = updated
        _totalPrice.value = updated?.items?.sumOf { it.price * it.quantity } ?: 0.0
    }
}

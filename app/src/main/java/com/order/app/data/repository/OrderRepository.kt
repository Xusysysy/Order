package com.order.app.data.repository

import com.order.app.data.db.dao.OrderDao
import com.order.app.data.db.dao.OrderBill
import com.order.app.data.db.dao.OrderWithItems
import com.order.app.data.db.entity.OrderEntity
import com.order.app.data.db.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OrderRepository(private val dao: OrderDao) {
    suspend fun getActiveOrder(tableId: Long): OrderEntity? = dao.getActiveOrder(tableId)

    suspend fun getOrderWithItems(orderId: Long): OrderWithItems? = dao.getOrderWithItems(orderId)

    fun getAllOrders(): Flow<List<OrderEntity>> = flow { emit(dao.getAllOrderBills().map { it.copy() }.let { emptyList<OrderEntity>() }) }

    suspend fun createOrder(tableId: Long): OrderEntity {
        val order = OrderEntity(tableId = tableId)
        val id = dao.insertOrder(order)
        return order.copy(id = id)
    }

    suspend fun updateOrderStatus(id: Long, status: String) = dao.updateStatus(id, status)

    suspend fun addItem(orderId: Long, item: OrderItemEntity): OrderItemEntity {
        val existing = dao.getItem(orderId, item.menuItemId)
        return if (existing != null) {
            val updated = existing.copy(quantity = existing.quantity + item.quantity)
            dao.updateItem(updated)
            updated
        } else {
            val id = dao.insertItem(item)
            item.copy(id = id)
        }
    }

    suspend fun updateItemQuantity(item: OrderItemEntity) = dao.updateItem(item)

    suspend fun removeItem(id: Long) = dao.deleteItem(id)

    suspend fun getItems(orderId: Long): List<OrderItemEntity> = dao.getItems(orderId)

    suspend fun getAllOrderBills(): List<OrderBill> = dao.getAllOrderBills()

    suspend fun deleteOrder(id: Long) = dao.deleteOrder(id)
}

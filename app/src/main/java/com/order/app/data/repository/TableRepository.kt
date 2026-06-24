package com.order.app.data.repository

import com.order.app.data.db.dao.TableDao
import com.order.app.data.db.entity.TableEntity
import kotlinx.coroutines.flow.Flow

class TableRepository(private val dao: TableDao) {
    fun getAll(): Flow<List<TableEntity>> = dao.getAllFlow()

    suspend fun getAllDirect(): List<TableEntity> = dao.getAll()

    suspend fun getById(id: Long): TableEntity? = dao.getById(id)

    suspend fun insert(table: TableEntity): Long = dao.insert(table)

    suspend fun update(table: TableEntity) = dao.update(table)

    suspend fun delete(id: Long) = dao.deleteById(id)

    suspend fun updateStatus(id: Long, status: String) = dao.updateStatus(id, status)

    suspend fun createDefaultTables() {
        if (dao.getById(1) == null) {
            for (i in 1..10) {
                dao.insert(TableEntity(name = "${i}号桌", zone = "大厅"))
            }
        }
    }

    suspend fun updateSortOrders(ids: List<Long>) = dao.updateSortOrders(ids)
}

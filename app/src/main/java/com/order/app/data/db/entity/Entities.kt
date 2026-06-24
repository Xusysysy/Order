package com.order.app.data.db.entity

data class TableEntity(
    val id: Long = 0,
    val name: String,
    val zone: String = "",
    val status: String = STATUS_IDLE,
    val sortOrder: Int = 0
) {
    companion object {
        const val STATUS_IDLE = "IDLE"
        const val STATUS_ORDERED = "ORDERED"
    }
}

data class MenuItemEntity(
    val id: Long = 0,
    val name: String,
    val price: Double = 0.0,
    val category: String = CATEGORY_COCKTAIL,
    val hasRecipe: Boolean = false,
    val sortOrder: Int = 0
) {
    companion object {
        const val CATEGORY_COCKTAIL = "cocktail"
        const val CATEGORY_DRINK = "drink"
        const val CATEGORY_SNACK = "snack"
    }
}

data class RecipeStepEntity(
    val id: Long = 0,
    val menuItemId: Long,
    val stepNumber: Int,
    val description: String
)

data class RecipeIngredientEntity(
    val id: Long = 0,
    val menuItemId: Long,
    val name: String,
    val amount: String,
    val unit: String
)

data class OrderEntity(
    val id: Long = 0,
    val tableId: Long,
    val status: String = STATUS_ACTIVE,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val STATUS_ACTIVE = "ACTIVE"
        const val STATUS_SETTLED = "SETTLED"
    }
}

data class OrderItemEntity(
    val id: Long = 0,
    val orderId: Long,
    val menuItemId: Long,
    val name: String,
    val quantity: Int = 1,
    val price: Double = 0.0
)

data class RecipeData(
    val steps: List<RecipeStepEntity>,
    val ingredients: List<RecipeIngredientEntity>
)

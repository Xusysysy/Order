package com.order.app.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context.applicationContext, "order_database", null, 2
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE tables (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                zone TEXT DEFAULT '',
                status TEXT DEFAULT 'IDLE',
                sort_order INTEGER DEFAULT 0
            )
        """)
        db.execSQL("INSERT INTO tables (name, zone) VALUES ('大厅1', '大厅')")
        db.execSQL("INSERT INTO tables (name, zone) VALUES ('包间1', '包间')")
        db.execSQL("""
            CREATE TABLE menu_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                price REAL DEFAULT 0,
                category TEXT DEFAULT 'cocktail',
                hasRecipe INTEGER DEFAULT 0,
                sort_order INTEGER DEFAULT 0
            )
        """)
        db.execSQL("""
            CREATE TABLE recipe_steps (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                menuItemId INTEGER NOT NULL,
                stepNumber INTEGER NOT NULL,
                description TEXT NOT NULL
            )
        """)
        db.execSQL("""
            CREATE TABLE recipe_ingredients (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                menuItemId INTEGER NOT NULL,
                name TEXT NOT NULL,
                amount TEXT,
                unit TEXT
            )
        """)
        db.execSQL("""
            CREATE TABLE orders (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tableId INTEGER NOT NULL,
                status TEXT DEFAULT 'ACTIVE',
                createdAt INTEGER NOT NULL
            )
        """)
        db.execSQL("""
            CREATE TABLE order_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                orderId INTEGER NOT NULL,
                menuItemId INTEGER NOT NULL,
                name TEXT NOT NULL,
                quantity INTEGER DEFAULT 1,
                price REAL DEFAULT 0
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE menu_items ADD COLUMN sort_order INTEGER DEFAULT 0")
            db.execSQL("ALTER TABLE tables ADD COLUMN sort_order INTEGER DEFAULT 0")
        }
    }
}

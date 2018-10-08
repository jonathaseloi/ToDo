package com.jonathas.eloi.todo.bd

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import org.jetbrains.anko.db.*

class BDHandler(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "Tododb", null, 1) {
    companion object {
        private var instance: BDHandler? = null

        @Synchronized
        fun getInstance(ctx: Context): BDHandler {
            if (instance == null) {
                instance = BDHandler(ctx.getApplicationContext())
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Here you create tables
        db.createTable("Todo", true,
                "id" to INTEGER + PRIMARY_KEY + UNIQUE,
                "texto" to TEXT,
                "prioridade" to TEXT,
                "id" to TEXT,
                "data" to INTEGER,
                "check" to INTEGER)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
        db.dropTable("Todo", true)
    }
}

// Access property for Context
val Context.database: BDHandler
    get() = BDHandler.getInstance(getApplicationContext())
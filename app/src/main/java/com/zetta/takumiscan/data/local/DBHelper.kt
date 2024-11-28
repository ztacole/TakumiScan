package com.zetta.takumiscan.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object{
        private const val DB_NAME = "TakumiUser"
        private const val DB_VERSION = 1
        private const val TABLE_DATA_USER  = "DataUser"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query = "create table $TABLE_DATA_USER (" +
                "NISN integer primary key," +
                "Nama text not null," +
                "Kelas text not null," +
                "Email text not null," +
                "NoHP text not null)"
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val query = "drop table if exists $TABLE_DATA_USER"
        db?.execSQL(query)
        onCreate(db)
    }
}
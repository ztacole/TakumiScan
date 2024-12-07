package com.zetta.takumiscan.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.zetta.takumiscan.model.DataUser

class DBHelper(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object{
        private const val DB_NAME = "TakumiUser"
        private const val DB_VERSION = 1

        private const val TABLE_DATA_USER = "DataUser"
        private const val COLUMN_NISN = "NISN"
        private const val COLUMN_NAMA = "nama"
        private const val COLUMN_KELAS = "kelas"
        private const val COLUMN_JURUSAN = "jurusan"
        private const val COLUMN_PASWORD = "password"

        private const val TABLE_HISTORY = "History"
        private const val COLUMN_ID = "ID"
        private const val COLUMN_STATUS = "status"
        private const val COLUMN_PHOTO = "photo"
        private const val COLUMN_EMOSI = "emosi"
        private const val COLUMN_DATETIME = "dateTime"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val queryDataUser = "create table $TABLE_DATA_USER (" +
                "$COLUMN_NISN text primary key," +
                "$COLUMN_NAMA text not null," +
                "$COLUMN_KELAS text not null," +
                "$COLUMN_JURUSAN text not null," +
                "$COLUMN_PHOTO blob not null," +
                "$COLUMN_PASWORD text not null)"
        val queryHistory = "create table $TABLE_HISTORY (" +
                "$COLUMN_ID integer primary key," +
                "$COLUMN_STATUS text not null," +
                "$COLUMN_PHOTO blob not null," +
                "$COLUMN_EMOSI text not null," +
                "$COLUMN_DATETIME text not null)"
        db?.execSQL(queryDataUser)
        db?.execSQL(queryHistory)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val query = "drop table if exists $TABLE_HISTORY"
        val query2 = "drop table if exists $TABLE_DATA_USER"
        db?.execSQL(query)
        db?.execSQL(query2)
        onCreate(db)
    }

    fun insertUser(data: DataUser){
        val db = readableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NISN, data.nisn)
            put(COLUMN_NAMA, data.nama)
            put(COLUMN_KELAS, data.kelas)
            put(COLUMN_JURUSAN, data.jurusan)
            put(COLUMN_PHOTO, data.photo)
            put(COLUMN_PASWORD, data.password)
        }
        db.insert(TABLE_DATA_USER, null, values)
        db.close()
    }
}
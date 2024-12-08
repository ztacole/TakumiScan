package com.zetta.takumiscan.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.zetta.takumiscan.model.DataUser
import com.zetta.takumiscan.model.History

class DBHelper(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object{
        private const val DB_NAME = "TakumiUser"
        private const val DB_VERSION = 1

        private const val TABLE_DATA_USER = "DataUser"
        private const val COLUMN_NISN = "NISN"
        private const val COLUMN_NAMA = "nama"
        private const val COLUMN_KELAS = "kelas"
        private const val COLUMN_JURUSAN = "jurusan"
        private const val COLUMN_PASsWORD = "password"

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
                "$COLUMN_PASsWORD text not null)"
        val queryHistory = "create table $TABLE_HISTORY (" +
                "$COLUMN_ID integer primary key autoincrement," +
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

    fun registerUser(data: DataUser){
        val db = readableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NISN, data.nisn)
            put(COLUMN_NAMA, data.nama)
            put(COLUMN_KELAS, data.kelas)
            put(COLUMN_JURUSAN, data.jurusan)
            put(COLUMN_PHOTO, data.photo)
            put(COLUMN_PASsWORD, data.password)
        }
        db.insert(TABLE_DATA_USER, null, values)
        db.close()
    }

    fun getDataUser(): DataUser {
        val db = readableDatabase
        var data : DataUser? = null
        val query = "SELECT * FROM $TABLE_DATA_USER"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()){
            val nisn = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NISN))
            val nama = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA))
            val kelas = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KELAS))
            val jurusan = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JURUSAN))
            val photo = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PHOTO))
            val password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASsWORD))

            data = DataUser(nisn, nama, kelas, jurusan, photo, password)
        }
        cursor.close()
        db.close()

        return data!!
    }

    fun addHistory(data: History){
        val db = readableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STATUS, data.status)
            put(COLUMN_PHOTO, data.photo)
            put(COLUMN_STATUS, data.mood)
            put(COLUMN_STATUS, data.dateTime)
        }
        db.insert(TABLE_HISTORY, null, values)
        db.close()
    }
}
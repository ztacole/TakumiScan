package com.zetta.takumiscan.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.zetta.takumiscan.model.DataUser
import com.zetta.takumiscan.model.History
import com.zetta.takumiscan.model.Note

class DBHelper(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object{
        private const val DB_NAME = "TakumiUser"
        private const val DB_VERSION = 1

        private const val TABLE_DATA_USER = "DataUser"
        private const val COLUMN_NISN = "NISN"
        private const val COLUMN_NAMA = "nama"
        private const val COLUMN_KELAS = "kelas"
        private const val COLUMN_JURUSAN = "jurusan"
        private const val COLUMN_PASSWORD = "password"

        private const val TABLE_HISTORY = "History"
        private const val COLUMN_ID = "ID"
        private const val COLUMN_STATUS = "status"
        private const val COLUMN_PHOTO = "photo"
        private const val COLUMN_MOOD = "mood"
        private const val COLUMN_DATETIME = "dateTime"

        private const val TABLE_NOTE = "Note"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_NOTES = "notes"
        private const val COLUMN_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val queryDataUser = "create table $TABLE_DATA_USER (" +
                "$COLUMN_NISN text primary key," +
                "$COLUMN_NAMA text not null," +
                "$COLUMN_KELAS text not null," +
                "$COLUMN_JURUSAN text not null," +
                "$COLUMN_PHOTO blob not null," +
                "$COLUMN_PASSWORD text not null)"

        val queryHistory = "create table $TABLE_HISTORY (" +
                "$COLUMN_ID integer primary key autoincrement," +
                "$COLUMN_STATUS text not null," +
                "$COLUMN_PHOTO blob not null," +
                "$COLUMN_MOOD text not null," +
                "$COLUMN_DATETIME text not null)"

        val queryNote = "create table $TABLE_NOTE (" +
                "$COLUMN_ID integer primary key autoincrement," +
                "$COLUMN_TITLE text," +
                "$COLUMN_NOTES text not null," +
                "$COLUMN_DATE text)"
        db?.execSQL(queryDataUser)
        db?.execSQL(queryHistory)
        db?.execSQL(queryNote)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val query = "drop table if exists $TABLE_HISTORY"
        val query2 = "drop table if exists $TABLE_DATA_USER"
        val query3 = "drop table if exists $TABLE_NOTE"
        db?.execSQL(query)
        db?.execSQL(query2)
        db?.execSQL(query3)
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
            put(COLUMN_PASSWORD, data.password)
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
            val password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))

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
            put(COLUMN_MOOD, data.mood)
            put(COLUMN_DATETIME, data.dateTime)
        }
        db.insert(TABLE_HISTORY, null, values)
        db.close()
    }

    fun getListHistory(): List<History>{
        val db = readableDatabase
        val histories = mutableListOf<History>()
        val query = "SELECT * FROM $TABLE_HISTORY ORDER BY $COLUMN_ID DESC"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS))
            val photo = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PHOTO))
            val mood = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MOOD))
            val dateTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATETIME))

            val history = History(
                id = id,
                status = status,
                photo = photo,
                mood = mood,
                dateTime = dateTime
            )
            histories.add(history)
        }

        cursor.close()
        db.close()
        return histories
    }

    fun getStatus(): List<Int>{
        val db = readableDatabase
        val status = mutableListOf<Int>()

        val queryTepatWaktu = "SELECT COUNT($COLUMN_STATUS) AS Jumlah FROM $TABLE_HISTORY WHERE $COLUMN_STATUS = 'Tepat Waktu'"
        val queryTerlambat = "SELECT COUNT($COLUMN_STATUS) AS Jumlah FROM $TABLE_HISTORY WHERE $COLUMN_STATUS = 'Terlambat'"
        val cursorTepatWaktu = db.rawQuery(queryTepatWaktu, null).apply { moveToNext() }
        val cursorTerlambat = db.rawQuery(queryTerlambat, null).apply { moveToNext() }

        val tepatWaktu = cursorTepatWaktu.getInt(cursorTepatWaktu.getColumnIndexOrThrow("Jumlah"))
        val terlambat = cursorTerlambat.getInt(cursorTerlambat.getColumnIndexOrThrow("Jumlah"))

        status.add(tepatWaktu)
        status.add(terlambat)

        db.close()
        cursorTerlambat.close()
        cursorTepatWaktu.close()

        return status
    }

    fun deleteAllHistories(){
        val db = readableDatabase
        db.delete(TABLE_HISTORY, null, null)
        db.close()
    }

    fun addNote(data: Note){
        val db = readableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, data.title)
            put(COLUMN_NOTES, data.notes)
            put(COLUMN_DATE, data.date)
        }
        db.insert(TABLE_NOTE, null, values)
        db.close()
    }

    fun getListNotes(): List<Note>{
        val db = readableDatabase
        val notes = mutableListOf<Note>()
        val query = "SELECT * FROM $TABLE_NOTE ORDER BY $COLUMN_ID DESC"
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val textNotes = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))

            val note = Note(
                id = id,
                title = title,
                notes = textNotes,
                date = date,
            )
            notes.add(note)
        }

        cursor.close()
        db.close()
        return notes
    }
}
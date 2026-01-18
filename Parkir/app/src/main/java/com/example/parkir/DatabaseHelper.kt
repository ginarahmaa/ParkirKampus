package com.example.parkir

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "parkir.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_NAME = "kendaraan"
        private const val COLUMN_PLAT = "plat_nomor"
        private const val COLUMN_JENIS = "jenis"
        private const val COLUMN_MASUK = "jam_masuk"
        private const val COLUMN_KELUAR = "jam_keluar"
        private const val COLUMN_BIAYA = "biaya"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_NAME ($COLUMN_PLAT TEXT PRIMARY KEY, $COLUMN_JENIS TEXT, $COLUMN_MASUK INTEGER, $COLUMN_KELUAR INTEGER, $COLUMN_BIAYA INTEGER)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    data class DataParkir(
        val plat: String,
        val jenis: String,
        val masuk: Int,
        val keluar: Int,
        val biaya: Int
    )

    fun insertKendaraan(data: DataParkir) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PLAT, data.plat)
            put(COLUMN_JENIS, data.jenis)
            put(COLUMN_MASUK, data.masuk)
            put(COLUMN_KELUAR, data.keluar)
            put(COLUMN_BIAYA, data.biaya)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllKendaraan(): List<DataParkir> {
        val list = mutableListOf<DataParkir>()
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        while (cursor.moveToNext()) {
            val plat = cursor.getString(0)
            val jenis = cursor.getString(1)
            val masuk = cursor.getInt(2)
            val keluar = cursor.getInt(3)
            val biaya = cursor.getInt(4)
            list.add(DataParkir(plat, jenis, masuk, keluar, biaya))
        }
        cursor.close()
        db.close()
        return list
    }

    fun deleteKendaraan(plat: String): Int {
        val db = writableDatabase
        val hasil = db.delete(TABLE_NAME, "$COLUMN_PLAT = ?", arrayOf(plat))

        db.close()
        return hasil
    }

    fun updateKendaraan(oldPlat: String, data: DataParkir) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_PLAT, data.plat)
            put(COLUMN_JENIS, data.jenis)
            put(COLUMN_MASUK, data.masuk)
            put(COLUMN_KELUAR, data.keluar)
            put(COLUMN_BIAYA, data.biaya)
        }
        db.update(TABLE_NAME, values, "$COLUMN_PLAT = ?", arrayOf(oldPlat))
        db.close()
    }

    fun searchKendaraan(keyword: String): List<DataParkir> {
        val list = mutableListOf<DataParkir>()
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_PLAT LIKE ?", arrayOf("%$keyword%"))

        while (cursor.moveToNext()) {
            val plat = cursor.getString(0)
            val jenis = cursor.getString(1)
            val masuk = cursor.getInt(2)
            val keluar = cursor.getInt(3)
            val biaya = cursor.getInt(4)
            list.add(DataParkir(plat, jenis, masuk, keluar, biaya))
        }
        cursor.close()
        db.close()
        return list
    }

    fun addKendaraan(plat: String, jenis: String, masuk: Int, keluar: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_PLAT, plat)
        values.put(COLUMN_JENIS, jenis)
        values.put(COLUMN_MASUK, masuk)
        values.put(COLUMN_KELUAR, keluar)

        val biaya = (keluar - masuk) * if (jenis.equals("Mobil", ignoreCase = true)) 3000 else 1000
        values.put(COLUMN_BIAYA, if(biaya < 0) 0 else biaya)

        val success = db.insert(TABLE_NAME, null, values)
        db.close()
        return success
    }

    fun updateKendaraan(platLama: String, platBaru: String, jenis: String, masuk: Int, keluar: Int): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_PLAT, platBaru)
        values.put(COLUMN_JENIS, jenis)
        values.put(COLUMN_MASUK, masuk)
        values.put(COLUMN_KELUAR, keluar)

        val biaya = (keluar - masuk) * if (jenis.equals("Mobil", ignoreCase = true)) 3000 else 1000
        values.put(COLUMN_BIAYA, if(biaya < 0) 0 else biaya)

        val result = db.update(TABLE_NAME, values, "$COLUMN_PLAT = ?", arrayOf(platLama))
        db.close()
        return result
    }

}
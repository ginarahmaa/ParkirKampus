package com.example.parkirkampus

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DatabaseHelper()

        val etPlat = findViewById<EditText>(R.id.etPlat)
        val etJenis = findViewById<EditText>(R.id.etJenis)
        val etJam = findViewById<EditText>(R.id.etJam)
        val etTarif = findViewById<EditText>(R.id.etTarif)
        val btnSimpan = findViewById<Button>(R.id.btnSimpan)
        val btnDelete = findViewById<Button>(R.id.btnDelete)
        val tvData = findViewById<TextView>(R.id.tvData)

        loadData(tvData)

        btnSimpan.setOnClickListener {

            val plat = etPlat.text.toString().trim()
            val jenis = etJenis.text.toString()
            val jam = etJam.text.toString()
            val tarif = etTarif.text.toString()

            if (plat.isEmpty() || jenis.isEmpty() || jam.isEmpty() || tarif.isEmpty()) {
                Toast.makeText(this, "Semua data harus diisi", Toast.LENGTH_SHORT).show()
            } else {

                val parkir = Parkir(plat, jenis, jam, tarif)
                db.insertData(parkir)

                Toast.makeText(this, "Data dikirim ke server", Toast.LENGTH_SHORT).show()

                etPlat.text.clear()
                etJenis.text.clear()
                etJam.text.clear()
                etTarif.text.clear()

                loadData(tvData)
            }
        }

        btnDelete.setOnClickListener {

            val plat = etPlat.text.toString().trim()

            if (plat.isEmpty()) {
                Toast.makeText(this, "Plat harus diisi", Toast.LENGTH_SHORT).show()
            } else {
                db.deleteData(plat)
                Toast.makeText(this, "Data dihapus di server", Toast.LENGTH_SHORT).show()
                loadData(tvData)
            }
        }
    }

    private fun loadData(tvData: TextView) {
        db.getAllData { hasil ->
            runOnUiThread {
                tvData.text = hasil
            }
        }
    }
}

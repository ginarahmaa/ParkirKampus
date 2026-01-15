package com.example.parkirkampus

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DatabaseHelper()

        val etPlat = findViewById<EditText>(R.id.etPlat)
        val etJenis = findViewById<EditText>(R.id.etJenis)
        val etJamMasuk = findViewById<EditText>(R.id.etJamMasuk)
        val etJamKeluar = findViewById<EditText>(R.id.etJamKeluar)
        val btnSimpan = findViewById<Button>(R.id.btnSimpan)
        val btnDelete = findViewById<Button>(R.id.btnDelete)
        val tvData = findViewById<TextView>(R.id.tvData)

        loadData(tvData)

        btnSimpan.setOnClickListener {
            val parkir = Parkir(
                etPlat.text.toString(),
                etJenis.text.toString(),
                etJamMasuk.text.toString(),
                etJamKeluar.text.toString(),
                0
            )

            db.insertData(parkir)
            Toast.makeText(this, "Data terkirim", Toast.LENGTH_SHORT).show()
            loadData(tvData)
        }

        btnDelete.setOnClickListener {
            db.deleteData(etPlat.text.toString())
            Toast.makeText(this, "Data dihapus", Toast.LENGTH_SHORT).show()
            loadData(tvData)
        }
    }

    private fun loadData(tvData: TextView) {
        db.getAllData {
            runOnUiThread {
                tvData.text = it
            }
        }
    }
}

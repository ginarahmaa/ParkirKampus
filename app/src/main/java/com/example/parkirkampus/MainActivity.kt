package com.example.parkirkampus

import android.content.Intent
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
        val tvData = findViewById<TextView>(R.id.tvData)

        val btnSimpan = findViewById<Button>(R.id.btnSimpan)
        val btnDelete = findViewById<Button>(R.id.btnDelete)
        val btnEdit = findViewById<Button>(R.id.btnEdit)

        loadData(tvData)

        btnSimpan.setOnClickListener {
            val parkir = Parkir(
                etPlat.text.toString(),
                etJenis.text.toString(),
                etJamMasuk.text.toString(),
                etJamKeluar.text.toString()
            )

            db.insertData(parkir)
            Toast.makeText(this, "Data tersimpan", Toast.LENGTH_SHORT).show()
            loadData(tvData)
        }

        btnDelete.setOnClickListener {
            db.deleteData(etPlat.text.toString())
            Toast.makeText(this, "Data dihapus", Toast.LENGTH_SHORT).show()
            loadData(tvData)
        }

        btnEdit.setOnClickListener {
            val intent = Intent(this, EditParkirActivity::class.java)
            intent.putExtra("plat", etPlat.text.toString())
            startActivity(intent)
        }
    }

    private fun loadData(tv: TextView) {
        db.getAllData {
            runOnUiThread {
                tv.text = it
            }
        }
    }
}

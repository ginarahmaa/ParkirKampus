package com.example.parkirkampus

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class EditParkirActivity : AppCompatActivity() {

    lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_parkir)

        db = DatabaseHelper()

        val etPlat = findViewById<EditText>(R.id.etPlat)
        val etJenis = findViewById<EditText>(R.id.etJenis)
        val etJamMasuk = findViewById<EditText>(R.id.etJamMasuk)
        val etJamKeluar = findViewById<EditText>(R.id.etJamKeluar)
        val btnSave = findViewById<Button>(R.id.btnSave)

        val plat = intent.getStringExtra("plat") ?: return

        etPlat.setText(plat)

        btnSave.setOnClickListener {
            val parkir = Parkir(
                etPlat.text.toString(),
                etJenis.text.toString(),
                etJamMasuk.text.toString(),
                etJamKeluar.text.toString()
            )

            db.updateData(parkir) { success ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Update gagal", Toast.LENGTH_SHORT).show()
                    }
                    finish()
                }
            }
        }
    }
}
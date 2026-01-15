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
            val jenis = etJenis.text.toString()
            val jamMasuk = etJamMasuk.text.toString()
            val jamKeluar = etJamKeluar.text.toString()

            val total = hitungTotalValue(jenis, jamMasuk, jamKeluar)

            val parkir = Parkir(
                plat = etPlat.text.toString(),
                jenis = jenis,
                jam_masuk = jamMasuk,
                jam_keluar = jamKeluar,
                total_bayar = total
            )


            db.updateData(parkir) { success ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Update gagal", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun hitungTotalValue(jenis: String, jm: String, jk: String): Int {
        try {
            if (jenis.isEmpty() || jm.length < 2 || jk.length < 2) return 0

            val jamMasuk = jm.substring(0, 2).toInt()
            val jamKeluar = jk.substring(0, 2).toInt()

            var durasi = jamKeluar - jamMasuk
            if (durasi < 1) durasi = 1

            val tarif = if (jenis.equals("Motor", true)) 2000 else 5000
            return durasi * tarif
        } catch (e: Exception) {
            return 0
        }
    }
}
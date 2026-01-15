package com.example.parkirkampus

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
        val tvTotal = findViewById<TextView>(R.id.tvTotal)
        val btnSimpan = findViewById<Button>(R.id.btnSimpan)

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val total = hitungTotalValue(
                    etJenis.text.toString(),
                    etJamMasuk.text.toString(),
                    etJamKeluar.text.toString()
                )
                tvTotal.text = "Total Bayar: Rp $total"
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        etJenis.addTextChangedListener(watcher)
        etJamMasuk.addTextChangedListener(watcher)
        etJamKeluar.addTextChangedListener(watcher)

        btnSimpan.setOnClickListener {

            val plat = etPlat.text.toString()
            val jenis = etJenis.text.toString()
            val jamMasuk = etJamMasuk.text.toString()
            val jamKeluar = etJamKeluar.text.toString()

            if (plat.isEmpty() || jenis.isEmpty() || jamMasuk.isEmpty() || jamKeluar.isEmpty()) {
                Toast.makeText(this, "Semua data harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val total = hitungTotalValue(jenis, jamMasuk, jamKeluar)

            val parkir = Parkir(
                plat = plat,
                jenis = jenis,
                jam_masuk = jamMasuk,
                jam_keluar = jamKeluar,
                total_bayar = total
            )

            db.insertData(parkir) { success ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, "Data tersimpan", Toast.LENGTH_SHORT).show()
                        etPlat.text.clear()
                        etJenis.text.clear()
                        etJamMasuk.text.clear()
                        etJamKeluar.text.clear()
                        tvTotal.text = "Total Bayar: Rp 0"
                    } else {
                        Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun hitungTotal(
        etJenis: EditText,
        etJamMasuk: EditText,
        etJamKeluar: EditText,
        tvTotal: TextView
    ) {
        val total = hitungTotalValue(etJenis.text.toString(), etJamMasuk.text.toString(), etJamKeluar.text.toString())
        tvTotal.text = "Total Bayar: Rp $total"
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

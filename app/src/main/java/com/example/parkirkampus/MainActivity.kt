package com.example.parkirkampus

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    lateinit var db: DatabaseHelper
    lateinit var etPlat: EditText
    lateinit var etJenis: EditText
    lateinit var etJamMasuk: EditText
    lateinit var etJamKeluar: EditText
    lateinit var tvTotal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DatabaseHelper()

        etPlat = findViewById(R.id.etPlat)
        etJenis = findViewById(R.id.etJenis)
        etJamMasuk = findViewById(R.id.etJamMasuk)
        etJamKeluar = findViewById(R.id.etJamKeluar)
        tvTotal = findViewById(R.id.tvTotal)
        val btnSimpan = findViewById<Button>(R.id.btnSimpan)
        val btnEdit = findViewById<Button>(R.id.btnEdit)
        val btnDelete = findViewById<Button>(R.id.btnDelete)


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
                        resetForm()
                    } else {
                        Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        btnEdit.setOnClickListener {
            val plat = etPlat.text.toString()
            if (plat.isEmpty()) {
                Toast.makeText(this, "Masukkan plat dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.checkPlat(plat) { exists ->
                runOnUiThread {
                    if (!exists) {
                        Toast.makeText(this, "Plat tidak ditemukan", Toast.LENGTH_SHORT).show()
                    } else {
                        val jenis = etJenis.text.toString()
                        val jamMasuk = etJamMasuk.text.toString()
                        val jamKeluar = etJamKeluar.text.toString()
                        val total = hitungTotalValue(jenis, jamMasuk, jamKeluar)

                        val parkir = Parkir(plat, jenis, jamMasuk, jamKeluar, total)

                        db.updateData(parkir) { success ->
                            runOnUiThread {
                                if (success) {
                                    Toast.makeText(this, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
                                    resetForm()
                                } else {
                                    Toast.makeText(this, "Update gagal", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }


        btnDelete.setOnClickListener {
            val plat = etPlat.text.toString()
            if (plat.isEmpty()) {
                Toast.makeText(this, "Masukkan plat dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.checkPlat(plat) { exists ->
                runOnUiThread {
                    if (!exists) {
                        Toast.makeText(this, "Plat tidak ditemukan", Toast.LENGTH_SHORT).show()
                    } else {
                        db.deleteData(plat) { success ->
                            runOnUiThread {
                                if (success) {
                                    Toast.makeText(this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                                    resetForm()
                                } else {
                                    Toast.makeText(this, "Hapus gagal", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private fun resetForm() {
        etPlat.text.clear()
        etJenis.text.clear()
        etJamMasuk.text.clear()
        etJamKeluar.text.clear()
        tvTotal.text = "Total Bayar: Rp 0"
    }

    private fun hitungTotalValue(jenis: String, jm: String, jk: String): Int {
        return try {
            if (jenis.isEmpty() || jm.length < 2 || jk.length < 2) return 0
            val jamMasuk = jm.substring(0, 2).toInt()
            val jamKeluar = jk.substring(0, 2).toInt()
            var durasi = jamKeluar - jamMasuk
            if (durasi < 1) durasi = 1
            val tarif = if (jenis.equals("Motor", true)) 2000 else 5000
            durasi * tarif
        } catch (e: Exception) {
            0
        }
    }
}

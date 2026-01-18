package com.example.parkir

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.parkir.databinding.ActivityFormParkirBinding
import okhttp3.*
import java.io.IOException

class FormParkirActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormParkirBinding
    private lateinit var db: DatabaseHelper
    private var isEdit = false
    private var platLama = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormParkirBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        if (intent.hasExtra("plat_extra")) {
            isEdit = true
            platLama = intent.getStringExtra("plat_extra").toString()

            binding.etPlat.setText(platLama)
            binding.etJenis.setText(intent.getStringExtra("jenis_extra"))
            binding.etMasuk.setText(intent.getIntExtra("masuk_extra", 0).toString())
            binding.etKeluar.setText(intent.getIntExtra("keluar_extra", 0).toString())
            binding.btnSimpan.text = "Update Data"
        }

        binding.btnHitung.setOnClickListener {
            val masuk = binding.etMasuk.text.toString().toIntOrNull() ?: 0
            val keluar = binding.etKeluar.text.toString().toIntOrNull() ?: 0
            val jenis = binding.etJenis.text.toString()

            var durasi = keluar - masuk
            if (durasi < 0) durasi = 0

            val hargaPerJam = if (jenis.equals("mobil", ignoreCase = true)) 3000 else 1000
            val total = durasi * hargaPerJam

            binding.tvTotalBayar.text = "Rp $total"
        }

        binding.btnSimpan.setOnClickListener {
            val plat = binding.etPlat.text.toString()
            val jenis = binding.etJenis.text.toString()
            val masukStr = binding.etMasuk.text.toString()
            val keluarStr = binding.etKeluar.text.toString()

            if (plat.isEmpty() || jenis.isEmpty() || masukStr.isEmpty() || keluarStr.isEmpty()) {
                Toast.makeText(this, "Semua data harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val masuk = masukStr.toInt()
            val keluar = keluarStr.toInt()

            if (isEdit) {
                db.updateKendaraan(platLama, plat, jenis, masuk, keluar)

                val url = "https://appocalypse.my.id/parkir_kampus.php?proc=ed&plat=$platLama&jenis=$jenis&masuk=$masuk&keluar=$keluar"
                kirimKeServer(url)

                Toast.makeText(this, "Data Berhasil Diupdate", Toast.LENGTH_SHORT).show()
            } else {
                db.addKendaraan(plat, jenis, masuk, keluar)

                val url = "https://appocalypse.my.id/parkir_kampus.php?proc=in&plat=$plat&jenis=$jenis&masuk=$masuk&keluar=$keluar"
                kirimKeServer(url)

                Toast.makeText(this, "Data Berhasil Disimpan", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    private fun kirimKeServer(url: String) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                // Response handled by server
            }
        })
    }
}
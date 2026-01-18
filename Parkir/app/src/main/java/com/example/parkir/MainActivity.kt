package com.example.parkir

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.parkir.databinding.ActivityMainBinding
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ParkirAdapter
    private lateinit var db: DatabaseHelper
    private var daftarParkir: ArrayList<DatabaseHelper.DataParkir> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        adapter = ParkirAdapter(daftarParkir,
            onDelete = { plat ->
                hapusData(plat)
            },
            onEdit = { data ->
                val intent = Intent(this, FormParkirActivity::class.java)
                intent.putExtra("plat_extra", data.plat)
                intent.putExtra("jenis_extra", data.jenis)
                intent.putExtra("masuk_extra", data.masuk)
                intent.putExtra("keluar_extra", data.keluar)
                startActivity(intent)
            }
        )

        binding.rvParkir.layoutManager = LinearLayoutManager(this)
        binding.rvParkir.adapter = adapter
        binding.fabTambah.setOnClickListener {
            val intent = Intent(this, FormParkirActivity::class.java)
            startActivity(intent)
        }

        loadDataDariDB()
    }

    override fun onResume() {
        super.onResume()
        loadDataDariDB()
    }

    private fun loadDataDariDB() {
        val dataTerbaru = db.getAllKendaraan()

        daftarParkir.clear()
        daftarParkir.addAll(dataTerbaru)
        adapter.refreshData(daftarParkir)
    }

    private fun hapusData(plat: String) {
        // 1. Hapus dari SQLite (HP)
        val result = db.deleteKendaraan(plat)

        if (result > 0) {
            hapusDariServer(plat)

            Toast.makeText(this, "Data Berhasil Dihapus", Toast.LENGTH_SHORT).show()

            loadDataDariDB()
        } else {
            Toast.makeText(this, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hapusDariServer(plat: String) {
        val client = OkHttpClient()

        val url = "https://appocalypse.my.id/parkir_kampus.php?proc=del&plat=$plat"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
            }
        })
    }
}
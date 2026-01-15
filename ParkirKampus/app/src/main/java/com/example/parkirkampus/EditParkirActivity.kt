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
        val etJam = findViewById<EditText>(R.id.etJam)
        val etTarif = findViewById<EditText>(R.id.etTarif)
        val btnSave = findViewById<Button>(R.id.btnSave)


        val platBawaan = intent.getStringExtra("plat")

        if (platBawaan == null) {
            Toast.makeText(this, "Plat tidak dikirim", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        db.getDataFromPlat(platBawaan) { parkir: Parkir? ->
            runOnUiThread {
                if (parkir != null) {
                    etPlat.setText(parkir.plat)
                    etJenis.setText(parkir.jenis)
                    etJam.setText(parkir.jam)
                    etTarif.setText(parkir.tarif)
                } else {
                    Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
        }


        btnSave.setOnClickListener {

            val parkirBaru = Parkir(
                etPlat.text.toString(),
                etJenis.text.toString(),
                etJam.text.toString(),
                etTarif.text.toString()
            )

            db.updateDataServer(platBawaan, parkirBaru)

            Toast.makeText(this, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}

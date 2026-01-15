package com.example.parkirkampus

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    lateinit var db: DatabaseHelper
    lateinit var etPlat: EditText
    lateinit var etJenis: EditText
    lateinit var etJamMasuk: EditText
    lateinit var etJamKeluar: EditText
    lateinit var tvTotal: TextView
    lateinit var llData: LinearLayout
    lateinit var btnSimpan: Button

    private var isEditMode = false
    private var currentEditPlat: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DatabaseHelper()

        etPlat = findViewById(R.id.etPlat)
        etJenis = findViewById(R.id.etJenis)
        etJamMasuk = findViewById(R.id.etJamMasuk)
        etJamKeluar = findViewById(R.id.etJamKeluar)
        tvTotal = findViewById(R.id.tvTotal)
        llData = findViewById(R.id.llData)
        btnSimpan = findViewById(R.id.btnSimpan)

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
            val total = hitungTotalValue(jenis, jamMasuk, jamKeluar)
            val parkir = Parkir(plat, jenis, jamMasuk, jamKeluar, total)

            if (!isEditMode) {
                db.insertData(parkir) { success ->
                    runOnUiThread {
                        if (success) {
                            Toast.makeText(this, "Data tersimpan", Toast.LENGTH_SHORT).show()
                            resetForm()
                            loadDataFromServer()
                        }
                    }
                }
            } else {
                db.updateData(parkir) { success ->
                    runOnUiThread {
                        if (success) {
                            Toast.makeText(this, "Data diperbarui", Toast.LENGTH_SHORT).show()
                            resetForm()
                            isEditMode = false
                            btnSimpan.text = "SIMPAN"
                            loadDataFromServer()
                        }
                    }
                }
            }
        }

        loadDataFromServer()
    }

    private fun loadDataFromServer() {
        db.getAllData { dataJson ->
            runOnUiThread {
                llData.removeAllViews()
                val jsonArray = JSONArray(dataJson)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val plat = obj.getString("plat")
                    val jenis = obj.getString("jenis")
                    val jamMasuk = obj.getString("jam_masuk")
                    val jamKeluar = obj.getString("jam_keluar")
                    val totalBayar = obj.getInt("total_bayar")

                    val itemLayout = LinearLayout(this)
                    itemLayout.orientation = LinearLayout.HORIZONTAL
                    itemLayout.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                    val tvItem = TextView(this)
                    tvItem.text = "$plat | $jenis | $jamMasuk-$jamKeluar | Rp $totalBayar"
                    tvItem.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

                    val btnEdit = Button(this)
                    btnEdit.text = "🖊️"
                    btnEdit.setOnClickListener {
                        etPlat.setText(plat)
                        etJenis.setText(jenis)
                        etJamMasuk.setText(jamMasuk)
                        etJamKeluar.setText(jamKeluar)
                        isEditMode = true
                        currentEditPlat = plat
                        btnSimpan.text = "UPDATE"
                    }

                    val btnDelete = Button(this)
                    btnDelete.text = "❌"
                    btnDelete.setOnClickListener {
                        db.deleteData(plat) { success ->
                            runOnUiThread {
                                if (success) {
                                    Toast.makeText(this, "Data dihapus", Toast.LENGTH_SHORT).show()
                                    if (currentEditPlat == plat) resetForm()
                                    isEditMode = false
                                    btnSimpan.text = "SIMPAN"
                                    loadDataFromServer()
                                }
                            }
                        }
                    }

                    itemLayout.addView(tvItem)
                    itemLayout.addView(btnEdit)
                    itemLayout.addView(btnDelete)
                    llData.addView(itemLayout)
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

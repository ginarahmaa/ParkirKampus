package com.example.parkirkampus

import okhttp3.*
import java.io.IOException

class DatabaseHelper {

    private val client = OkHttpClient()
    private val baseUrl = "https://appocalypse.my.id/parkir_kampus.php"


    fun insertData(p: Parkir, callback: (Boolean) -> Unit = {}) {
        val url = "$baseUrl?proc=in" +
                "&plat=${p.plat}" +
                "&jenis=${p.jenis}" +
                "&jam_masuk=${p.jam_masuk}" +
                "&jam_keluar=${p.jam_keluar}" +
                "&total_bayar=${p.total_bayar}"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                callback(false)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                callback(response.isSuccessful)
            }
        })
    }

    fun updateData(p: Parkir, callback: (Boolean) -> Unit = {}) {
        val url = "$baseUrl?proc=update" +
                "&plat=${p.plat}" +
                "&jenis=${p.jenis}" +
                "&jam_masuk=${p.jam_masuk}" +
                "&jam_keluar=${p.jam_keluar}" +
                "&total_bayar=${p.total_bayar}"

        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = callback(false)
            override fun onResponse(call: Call, response: Response) = callback(response.isSuccessful)
        })
    }

    fun getAllData(callback: (String) -> Unit) {
        val request = Request.Builder()
            .url("$baseUrl?proc=getdata")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("Gagal konek server")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                callback(response.body!!.string())
            }
        })
    }

    fun deleteData(plat: String, callback: (Boolean) -> Unit = {}) {
        val url = "$baseUrl?proc=del&plat=$plat"
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = callback(false)
            override fun onResponse(call: Call, response: Response) = callback(response.isSuccessful)
        })
    }
}

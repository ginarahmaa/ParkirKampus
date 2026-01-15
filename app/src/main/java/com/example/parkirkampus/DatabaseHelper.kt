package com.example.parkirkampus

import okhttp3.*
import java.io.IOException

class DatabaseHelper {

    private val client = OkHttpClient()
    private val baseUrl = "https://appocalypse.my.id/parkir_kampus.php"

 
    fun insertData(p: Parkir) {
        val url = "$baseUrl?proc=in" +
                "&plat=${p.plat}" +
                "&jenis=${p.jenis}" +
                "&jam_masuk=${p.jam_masuk}" +
                "&jam_keluar=${p.jam_keluar}"

        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {}
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
                callback(response.body!!.string())
            }
        })
    }

    fun deleteData(plat: String) {
        val request = Request.Builder()
            .url("$baseUrl?proc=del&plat=$plat")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {}
        })
    }
}

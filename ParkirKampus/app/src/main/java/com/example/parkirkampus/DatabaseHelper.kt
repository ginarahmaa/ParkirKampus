package com.example.parkirkampus

import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class DatabaseHelper {

    private val client = OkHttpClient()
    private val baseUrl = "https://appocalypse.my.id/parkir.php"

    fun getAllData(callback: (String) -> Unit) {
        val url = "$baseUrl?proc=getdata"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("Gagal ambil data")
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.body!!.string())
            }
        })
    }


    fun insertData(parkir: Parkir) {
        val url =
            "$baseUrl?proc=in&plat=${parkir.plat}&jenis=${parkir.jenis}&jam=${parkir.jam}&tarif=${parkir.tarif}"

        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {}
        })
    }


    fun deleteData(plat: String) {
        val url = "$baseUrl?proc=del&plat=$plat"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
            }
        })
    }


    fun getDataFromPlat(
        platBawaan: String,
        callback: (Parkir?) -> Unit
    ) {
        val url = "$baseUrl?proc=getbyplat&plat=$platBawaan"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val result = response.body!!.string()
                val jsonArray = JSONArray(result)

                if (jsonArray.length() > 0) {
                    val obj = jsonArray.getJSONObject(0)

                    val parkir = Parkir(
                        obj.getString("plat"),
                        obj.getString("jenis"),
                        obj.getString("jam"),
                        obj.getString("tarif")
                    )
                    callback(parkir)
                } else {
                    callback(null)
                }
            }
        })
    }


    fun updateDataServer(
        platLama: String,
        parkir: Parkir
    ) {
        val url =
            "$baseUrl?proc=ed&plat=${parkir.plat}&jenis=${parkir.jenis}&jam=${parkir.jam}&tarif=${parkir.tarif}&platlama=$platLama"

        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {}
        })
    }


}

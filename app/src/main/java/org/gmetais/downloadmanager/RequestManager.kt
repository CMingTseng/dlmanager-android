package org.gmetais.downloadmanager

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RequestManager {
    private val BASE_URL = "http://192.168.1.18:8088/"
    private val browserService: IBrowser

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
        browserService = retrofit.create(IBrowser::class.java)
    }

    fun browse(path : String?, onSuccess: (Directory) -> Unit, onFailure: (String) -> Unit) {
        val files = if (path == null) browserService.browseRoot() else browserService.browseDir(RequestBody(path, ""))
        files.enqueue(object : Callback<Directory> {
            override fun onResponse(call: Call<Directory>,
                                    response: Response<Directory>) {
                val body : Directory? = response.body()
                if (body != null)
                    onSuccess(body)
                else
                    onFailure("Error reading response")
            }

            override fun onFailure(call: Call<Directory>, t: Throwable) {
                onFailure(t.message ?: "Internal error")
            }
        })
    }

    fun listShares(onSuccess: (List<SharedFile>) -> Unit, onFailure: (String) -> Unit) {
        val shares = browserService.getShares()
        shares.enqueue(object : Callback<List<SharedFile>> {
            override fun onResponse(call: Call<List<SharedFile>>,
                                    response: Response<List<SharedFile>>) {
                val body : List<SharedFile>? = response.body()
                if (body != null)
                    onSuccess(body)
                else
                    onFailure("Error reading response")
            }

            override fun onFailure(call: Call<List<SharedFile>>, t: Throwable) {
                onFailure(t.message ?: "Internal error")
            }
        })
    }
}
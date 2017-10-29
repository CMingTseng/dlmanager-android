@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package org.gmetais.downloadmanager.repo

import org.gmetais.downloadmanager.data.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.experimental.suspendCoroutine

object ApiRepo {

    suspend fun browse(path: String?) = retrofitResponseCall { RequestManager.browse(path) }

    suspend fun fetchShares() {
        val result = retrofitResponseCall { RequestManager.listShares() }
        if (result is Success<*>)
            SharesDatabase.db.sharesDao().insertShares(result.content as List<SharedFile>)
    }

    suspend fun add(file: SharedFile) = retrofitResponseCall { RequestManager.add(file) }

    suspend fun delete(key: String) = retrofitBooleanCall { RequestManager.delete(key) }

    private suspend inline fun <reified T> retrofitResponseCall(crossinline call: () -> Call<T>) = try {
        with(retrofitSuspendCall(call)) {
            if (isSuccessful)
                Success(body()!!)
            else
                Error(code(), message())
        }
    } catch(e: Exception) {
        Error(408, e.localizedMessage)
    }


    private suspend inline fun retrofitBooleanCall(crossinline call: () -> Call<Void>) = try {
        retrofitSuspendCall(call).isSuccessful
    } catch(e: Exception) {
        false
    }

    private suspend inline fun <reified T> retrofitSuspendCall(crossinline call: () -> Call<T>) : Response<T> = suspendCoroutine { continuation ->
        call.invoke().enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>?, response: Response<T>) = continuation.resume(response)
            override fun onFailure(call: Call<T>, t: Throwable) = continuation.resumeWithException(t)
        })
    }
}
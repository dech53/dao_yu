package com.dech53.dao_yu.utils

import android.util.Log
import com.dech53.dao_yu.models.Thread
import com.dech53.dao_yu.static.Url
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import okhttp3.Request
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.concurrent.TimeUnit

object Http_request {
    val client = OkHttpClient().newBuilder()
        .cookieJar(CookieJar.NO_COOKIES)
        .callTimeout(3, TimeUnit.SECONDS)
        .build()
    val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    //get request , add url and form params in the future
    inline fun <reified T> get(url: String): List<T>? {
        //reflect type and build List T
        val type = Types.newParameterizedType(List::class.java, T::class.java)
        //define adapter
        val adapter = moshi.adapter<List<T>>(type)
        //create request
        val request = Request.Builder()
            .get()
            .url(Url.API_BASE_URL + url)
            .build()
        //create call
        val call = client.newCall(request)
//        call.enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                //failure callback
//                Log.d("${url} request", "fail")
//                return
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                //response callback
//                if (!response.isSuccessful) {
//                    Log.d("${url} request", "failed with code: ${response.code}")
//                    return
//                }
//                Log.d("request data",response.body.toString())
//                //body process
//                data = adapter.fromJson(response.body.toString())
//
//            }
//        })
        val response = call.execute()
        val responseBody = response.body?.string() ?: ""
        Log.d("request data", responseBody)
        //body process
        return adapter.fromJson(responseBody)
    }


    //Thread info get method
    fun getThreadInfo(url: String): Thread? {
        val adapter = moshi.adapter<Thread>(Thread::class.java)
        val request = Request.Builder()
            .get()
            .url(Url.API_BASE_URL + url)
            .build()
        val call = client.newCall(request)
        val response = call.execute()
        val responseBody = response.body?.string() ?: ""
        Log.d("request data", responseBody)
        //body process
        return adapter.fromJson(responseBody)
    }
}
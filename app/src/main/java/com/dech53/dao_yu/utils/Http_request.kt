package com.dech53.dao_yu.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.dech53.dao_yu.models.QuoteRef
import com.dech53.dao_yu.models.Thread
import com.dech53.dao_yu.models.emptyQuoteRefWithContent
import com.dech53.dao_yu.static.Url
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import okhttp3.Request
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object Http_request {
    val client = OkHttpClient().newBuilder()
        .cookieJar(CookieJar.NO_COOKIES)
        .callTimeout(30, TimeUnit.SECONDS)
        .build()
    val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    //get request , add url and form params in the future
    inline fun <reified T> get(url: String, cookie: String = ""): List<T>? {
        //reflect type and build List T
        val type = Types.newParameterizedType(List::class.java, T::class.java)
        //define adapter
        val adapter = moshi.adapter<List<T>>(type)
        //create request
        val request = Request.Builder()
            .get()
            .addHeader("Cookie", "userhash=${cookie}")
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
    fun getThreadInfo(url: String, cookie: String = ""): Thread? {
        val adapter = moshi.adapter<Thread>(Thread::class.java)
        val request = Request.Builder()
            .get()
            .addHeader("Cookie", "userhash=${cookie}")
            .url(Url.API_BASE_URL + url)
            .build()
        val call = client.newCall(request)
        val response = call.execute()
        val responseBody = response.body?.string() ?: ""
        Log.d("request data", responseBody)
        //body process
        return adapter.fromJson(responseBody)
    }

    fun getRef(id: String, cookie: String): QuoteRef? {
        val adapter = moshi.adapter<QuoteRef>(QuoteRef::class.java)
        val request = Request.Builder()
            .get()
            .addHeader("Cookie", "userhash=${cookie}")
            .url(Url.API_BASE_URL + "ref?id=${id}")
            .build()
        val call = client.newCall(request)
        val response = call.execute()
        val responseBody = response.body?.string() ?: ""
        Log.d("request data", responseBody)
        try {
            val quoteRef = adapter.fromJson(responseBody)
            return quoteRef
        } catch (e: Exception) {
            val json = JSONObject(responseBody)
            Log.d("json", json.toString())
            Log.d("json", json.getString("error"))
            return emptyQuoteRefWithContent(
                "<font color=\"#FF0000\">${json.getString("error")}</font>",
                id.toLong()
            )
        }
        return emptyQuoteRefWithContent("未知错误", id.toLong())
    }

    fun postThread(content: String, fid: Int, cookie: String) {
        val requestData = FormBody.Builder()
            .add("content", content)
            .add("fid", fid.toString())
            .build()
        val mediaType = "text/html;charset=utf-8".toMediaType()
        val request = Request.Builder()
            .addHeader("Cookie", "userhash=${cookie}")
            .url(Url.Post_Thread_URL)
            .post(requestData)
            .build()
        val call = client.newCall(request)
        call.execute()
    }

    fun replyThread(content: String, resto: String, cookie: String, img: Uri? = null,context: Context) {
        val multipartBuilder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("content", content)
            .addFormDataPart("resto", resto)

        img?.let { uri ->
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = "image_${System.currentTimeMillis()}.jpg"
            val bytes = inputStream?.readBytes() ?: byteArrayOf()
            val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull(), 0, bytes.size)
            multipartBuilder.addFormDataPart("image", fileName, requestBody)
        }

        val requestBody = multipartBuilder.build()

        val request = Request.Builder()
            .addHeader("Cookie", "userhash=$cookie")
            .url(Url.Reply_Thread_URL)
            .post(requestBody)
            .build()

        val call = client.newCall(request)
        call.execute()
    }
}
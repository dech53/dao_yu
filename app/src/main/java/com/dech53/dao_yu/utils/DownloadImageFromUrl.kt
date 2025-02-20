package com.dech53.dao_yu.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import com.dech53.dao_yu.static.Url
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object DownloadImageFromUrl {
    suspend fun downloadImageFromUrl(context: Context, url: String):String? {
        //coil loader and request for bitmap
        val loader = ImageLoader(context)
        val request = ImageRequest
            .Builder(context)
            .allowHardware(false)
            .data(Url.IMG_FULL_QA + Regex(pattern = "&").replace(url, "/"))
            .build()
        val result = loader.execute(request) as SuccessResult
        val bitmap = result.image.toBitmap()
        val fileDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "dao_yu"
        )
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }
        // file path composed
        val file = File(fileDir, Regex(pattern = "/").replace(url, "-"))
        if (file.exists()){
            return file.absolutePath
        }else{//output stream write the img into the pointed dir
            val outputStream: OutputStream = FileOutputStream(file,true)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            return file.absolutePath
        }
    }
}
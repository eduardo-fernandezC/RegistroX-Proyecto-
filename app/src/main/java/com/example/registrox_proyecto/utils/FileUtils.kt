package com.example.registrox_proyecto.utils

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

fun uriToMultipart(context: Context, uri: Uri, partName: String): MultipartBody.Part {
    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw Exception("No se pudo abrir la imagen")

    val tempFile = File(context.cacheDir, "temp_image.jpg")
    val outputStream = FileOutputStream(tempFile)

    inputStream.copyTo(outputStream)
    inputStream.close()
    outputStream.close()

    val requestFile = tempFile.asRequestBody("image/*".toMediaType())
    return MultipartBody.Part.createFormData(partName, tempFile.name, requestFile)
}

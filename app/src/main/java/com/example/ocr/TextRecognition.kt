package com.example.ocr

import android.content.Context
import com.google.gson.Gson
import id.zelory.compressor.Compressor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection

object TextRecognition {

    private const val API_URL = "https://api.ocr.space/parse/image"

    private var apiKey: String? = null
    private var okHttpClient: OkHttpClient = OkHttpClient()
    private val coroutineJob: Job = Job()
    private val ioScope: CoroutineScope = CoroutineScope(coroutineJob + Dispatchers.IO)
    private val mainScope: CoroutineScope = CoroutineScope(coroutineJob + Dispatchers.Main)

    fun initialize(apiKey: String) {
        TextRecognition.apiKey = apiKey
    }

    fun fromFile(file: File, callback: Callback) {

        fun failure(e: Throwable) {
            mainScope.launch {
                callback.failure(e)
            }
        }

        fun success(content: String) {
            mainScope.launch {
                callback.success(content)
            }
        }

        if (apiKey == null) {
            throw ApiKeyIsMissingException()
        }

        val imagePart = MultipartBody.Part.createFormData(
            "image",
            file.name,
            file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        )

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addPart(imagePart)
            .addFormDataPart("apikey", apiKey!!)
            .build()

        val request = Request.Builder()
            .url(API_URL)
            .post(requestBody)
            .build()

        okHttpClient.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                failure(e)
            }

            override fun onResponse(call: Call, rsp: Response) {
                if (isHttpOK(rsp)) {
                    try {
                        val response: TextResponse = parseHttpResponse(rsp)
                        if (isErrorOccurredWhenOCRProcessing(response)) {
                            throw Throwable(getErrorMessage(response))
                        }
                        if (isEmptyResult(response)) {
                            throw EmptyContentException()
                        }
                        success(getContent(response))
                    } catch (e: Throwable) {
                        failure(e)
                    }
                } else {
                    failure(HttpException(rsp.code))
                }
            }
        })

    }

    private fun getErrorMessage(response: TextResponse): String {
        return response.errorMessage.first()
    }

    private fun isErrorOccurredWhenOCRProcessing(response: TextResponse): Boolean {
        return response.isErroredOnProcessing
    }

    private fun isHttpOK(response: Response): Boolean {
        return response.code == HttpURLConnection.HTTP_OK
    }

    @Throws(ParseTextResponseException::class)
    private fun parseHttpResponse(response: Response): TextResponse {
        return Gson().fromJson(response.body?.string(), TextResponse::class.java)
    }

    private fun isEmptyResult(response: TextResponse): Boolean {
        if (response.parsedResults.isEmpty()) return true
        if (response.parsedResults.first().parsedText.isBlank()) return true
        return false
    }

    private fun getContent(response: TextResponse): String {
        return response.parsedResults.first().parsedText
    }

    fun compressFile(context: Context, file: File, callback: (file: File) -> Unit) {
        ioScope.launch {
            Compressor.compress(context, file, Dispatchers.IO).let {
                mainScope.launch {
                    callback.invoke(it)
                }
            }
        }
    }

    interface Callback {
        fun success(response: String)
        fun failure(e: Throwable)
    }

}
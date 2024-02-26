package com.example.integration_niubiz.Services

import com.example.integration_niubiz.Constants
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiServiceToken {

    //Generar token de seguridad - Niubiz
    @Headers(
        "Accept: text/plain",
        "Authorization: Basic aW50ZWdyYWNpb25lc0BuaXViaXouY29tLnBlOl83ejNAOGZG"
    )
    @GET(Constants.SECURITY_ENDPOINT)
    suspend fun getToken(): Response<ResponseBody>
}
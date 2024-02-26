package com.example.integration_niubiz.Providers

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.example.integration_niubiz.*
import com.example.integration_niubiz.Model.CertificateApp
import com.example.integration_niubiz.Services.ApiCertificateApps
import com.example.integration_niubiz.Services.ApiServiceToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit

class Visanet {

    fun getTokenSecurityProvider(activity : MainActivity) {

        enableComponents(activity)

        //Corrutina en el hilo de E/S
        //Hilo en segundo plano
        CoroutineScope(Dispatchers.IO).launch {

            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.URL)
                .build()

            // Crea el servicio
            val service = retrofit.create(ApiServiceToken::class.java)

            // Solicitud para obtener token
            val response = service.getToken()

            //Hilo principal
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    // Crea un nuevo objeto Gson con la opción de "pretty printing" habilitada     ----x
                    val gson = GsonBuilder().setPrettyPrinting().create()

                    val tokenResponse = gson.toJson(JsonParser.parseString(response.body()?.string()))
                    Log.d("json-Token :", tokenResponse)
                    //activity.receiveToken(tokenResponse)

                    val retrofit2 = Retrofit.Builder()
                        .baseUrl(Constants.URL)
                        .build()

                    val service2 = retrofit2.create(ApiCertificateApps::class.java)
                    val token = tokenResponse.replace("\"", "")

                    // Solicitud para obtener el certificado(data y pinhash)
                    val response2 = service2.getCertificate(token)

                    withContext(Dispatchers.Main){
                        if (response2.isSuccessful) {
                            val gson2 = GsonBuilder().setPrettyPrinting().create()
                            val certificate = gson2.toJson(JsonParser.parseString(response2.body()?.string()))
                            val gsonConverter = Gson()
                            val data = gsonConverter.fromJson(certificate, CertificateApp::class.java)
                            activity.receiveToken(token,data.pinHash)
                            Log.d("cert :", certificate)
                        }else{
                            Toast.makeText(activity,"ERROR Intentelo mas tarde!",Toast.LENGTH_LONG).show()
                            Log.e("CERT_ERROR", response2.code().toString())
                            disableComponents(activity)
                        }
                    }


                } else {
                    Toast.makeText(activity,"ERROR Inténtelo mas tarde!",Toast.LENGTH_LONG).show()
                    Log.e("RETROFIT_ERROR", response.code().toString())
                    disableComponents(activity)
                }
            }
        }
    }

private fun enableComponents(activity: MainActivity){
    val button : Button = activity.findViewById(R.id.pay)
    val progressBar : ProgressBar = activity.findViewById(R.id.progress)

    button.isEnabled = false
    progressBar.visibility = View.VISIBLE
}

private fun disableComponents(activity: MainActivity){
        val button : Button = activity.findViewById(R.id.pay)
        val progressBar : ProgressBar = activity.findViewById(R.id.progress)

        button.isEnabled = true
        progressBar.visibility = View.GONE
    }

}
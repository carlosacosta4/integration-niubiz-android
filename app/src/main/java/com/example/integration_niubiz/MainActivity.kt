package com.example.integration_niubiz

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import lib.visanet.com.pe.visanetlib.VisaNet
import lib.visanet.com.pe.visanetlib.data.custom.Channel
import lib.visanet.com.pe.visanetlib.presentation.custom.VisaNetViewAuthorizationCustom
import java.lang.Exception
import java.util.HashMap
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import com.example.integration_niubiz.Providers.Visanet
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val payButton : Button = findViewById(R.id.pay)
        payButton.setOnClickListener {
            Visanet().getTokenSecurityProvider(this)
        }

    }

    fun receiveToken(token : String , pinHash : String){

        val TAG = "NIUBIZ"

        val data: MutableMap<String, Any> = HashMap()
        data[VisaNet.VISANET_SECURITY_TOKEN] = token
        data[VisaNet.VISANET_CHANNEL] = Channel.MOBILE
        data[VisaNet.VISANET_COUNTABLE] = true
        data[VisaNet.VISANET_MERCHANT] = "341198210"
        data[VisaNet.VISANET_PURCHASE_NUMBER] = "2020111701"
        data[VisaNet.VISANET_AMOUNT] = 10.50

        val MDDdata = HashMap<String, String>()
        MDDdata["19"] = "LIM"
        MDDdata["20"] = "AQP"
        MDDdata["21"] = "AFKI345"
        MDDdata["94"] = "ABC123DEF"

        data[VisaNet.VISANET_MDD] = MDDdata
        data[VisaNet.VISANET_ENDPOINT_URL] = "https://apisandbox.vnforappstest.com/"
        data[VisaNet.VISANET_CERTIFICATE_HOST] = "apisandbox.vnforappstest.com"
        data[VisaNet.VISANET_CERTIFICATE_PIN] =
        "sha256/p4Sev8TEZTsSi51XmL0mfAP0RzRoMIR9n/3Nj8M/C2Q="        //Linea modificada
        //"sha256/$pinHash"

        val custom = VisaNetViewAuthorizationCustom()
        custom.logoImage = R.drawable.logo_completo
        custom.buttonColorMerchant = R.color.black

        //Invocación al método autorización
        try {
            VisaNet.authorization(this, data, custom)

        } catch (e: Exception) {
            Log.i(TAG, "onClick: " + e.message)
            disableComponents()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VisaNet.VISANET_AUTHORIZATION) {
            if (data != null) {
                val gson = GsonBuilder().setPrettyPrinting().create()
                if (resultCode == RESULT_OK) {
                    disableComponents()
                    val JSONString = data.extras!!.getString("keySuccess")
                    val jsonElement = JsonParser.parseString(JSONString)
                    Log.d("Success JSON", gson.toJson(jsonElement)) // Imprime el JSON de éxito
                    val toast1 = Toast.makeText(applicationContext, "¡Pago exitoso!", Toast.LENGTH_LONG)
                    toast1.show()

                } else {
                    disableComponents()
                    var JSONString = data.extras!!.getString("keyError")
                    JSONString = JSONString ?: ""
                    val jsonElement = JsonParser.parseString(JSONString)
                    Log.d("Error JSON", gson.toJson(jsonElement)) // Imprime el JSON de error
                    val toast1 = Toast.makeText(applicationContext, "Error al procesar el pago", Toast.LENGTH_LONG)
                    toast1.show()
                }
            } else {
                val toast1 = Toast.makeText(applicationContext, "Cancelado...", Toast.LENGTH_SHORT)
                toast1.show()
                disableComponents()
            }
        }
    }

    private fun disableComponents(){
        val button : Button =  findViewById(R.id.pay)
        val progressBar : ProgressBar = findViewById(R.id.progress)

        button.isEnabled = true
        progressBar.visibility = View.GONE
    }
}
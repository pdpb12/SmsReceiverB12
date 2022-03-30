package me.ruyeo.smsreceiverb12

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.phone.SmsRetriever
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    private lateinit var smsBroadcastReceiver: SmsBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startSmsUserConsent()
        registerSmsReceiver()
    }

    private fun registerSmsReceiver(){
        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver.smsBroadcastReceiverListener = object : SmsBroadcastReceiver.SmsBroadcastReceiverListener{
            override fun onSuccess(intent: Intent) {
               startActivityForResult(intent,200)
            }

            override fun onFailure() {
                Log.d("errorR","Something went wrong")
            }

        }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsBroadcastReceiver,intentFilter)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200) {
            if (resultCode == Activity.RESULT_OK && data != null) { //That gives all message to us.
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                Log.d("messsage",message.toString())
                message?.let {
                   getOtpFromMessage(message)
                    Log.d("sss",getOtpFromMessage(message).toString())
                }
            }
        }
    }

    private fun getOtpFromMessage(message: String){
        val matcher = Pattern.compile("(|^)\\d{5}").matcher(message)
        if (matcher.find()){
            Toast.makeText(this, matcher.group(0), Toast.LENGTH_SHORT).show()
        }
    }

    private fun startSmsUserConsent(){
        val client = SmsRetriever.getClient(this)
        client.startSmsUserConsent(null)
    }
}
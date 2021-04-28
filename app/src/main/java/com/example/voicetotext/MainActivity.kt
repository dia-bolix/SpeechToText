package com.example.voicetotext

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import android.app.AlertDialog
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.TextView
import android.widget.Toast
import java.util.*


class MainActivity : AppCompatActivity() {
    private val RECORD_REQ_CODE = 101
    private val REQ_CODE = 102
    private val TAG = "PermissionDemo"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }
    //function that checks for permission to record audio
    //if not true, will make a request to record audio
    //if denied, will show an alert, then make request again
    private fun checkPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.RECORD_AUDIO)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            //Log.i(TAG,"Permission to record denied")
            //show user a message why this is needed if they deny it
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Permission to access microphone is required to record audio")
                    .setTitle("Permission required")
                builder.setPositiveButton("OK") {dialog, id ->
                    Log.i(TAG, "Clicked")
                    requestPermission()
                }
                val dialog = builder.create()
                dialog.show()
            }
            else {
                requestPermission()
            }
        }
    }
    //function that makes the request to record
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO),
            RECORD_REQ_CODE)
    }
    //function that logs if permission was granted or not
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_REQ_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                }
            }
        }
    }

    fun call_voice_api(view: View) {
        checkPermissions()
        collect_speech()

    }
    fun collect_speech() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Speech recognition is not available", Toast.LENGTH_SHORT).show()
        } else {
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "speak human")
            startActivityForResult(i, REQ_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val textBox: TextView = findViewById(R.id.display_box)
            textBox.text = result?.get(0).toString()
        }
    }


}

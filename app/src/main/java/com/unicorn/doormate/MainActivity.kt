package com.unicorn.doormate

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.concurrent.schedule

@SuppressLint("StaticFieldLeak")
lateinit var register: Button
@SuppressLint("StaticFieldLeak")
lateinit var contacts: Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register = findViewById(R.id.register)
        contacts = findViewById(R.id.contacts)

        register.setOnClickListener {
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        contacts.setOnClickListener {

            val intent = Intent(this@MainActivity, ContactsActivity::class.java)
            startActivity(intent)

        }

        register.isEnabled = false
        register.isClickable = false

        contacts.isEnabled = false
        contacts.isClickable = false

        firstCheck()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            register.isEnabled = true
            register.isClickable = true

            secondCheck()

        } else {
            Toast.makeText(applicationContext, "Please grant permission to use the app.",
                Toast.LENGTH_LONG).show()
            Timer().schedule(3500) {
                requestPermission()
            }
        }
    }

    private val requestPermissionLauncher2 = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            contacts.isEnabled = true
            contacts.isClickable = true
        } else {
            Toast.makeText(applicationContext, "Please grant permission to use the app.",
                Toast.LENGTH_LONG).show()
            Timer().schedule(3500) {
                requestPermission2()
            }
        }
    }

    private fun firstCheck() {
        when {
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                register.isEnabled = true
                register.isClickable = true

                secondCheck()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION)
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun requestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                register.isEnabled = true
                register.isClickable = true

                secondCheck()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION)
            }
            else -> {
                goToSettings()
            }
        }
    }

    private fun secondCheck() {
        when {
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                contacts.isEnabled = true
                contacts.isClickable = true
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                requestPermissionLauncher2.launch(
                    Manifest.permission.READ_CONTACTS)
            }
            else -> {
                requestPermissionLauncher2.launch(
                    Manifest.permission.READ_CONTACTS)
            }
        }
    }

    private fun requestPermission2() {
        when {
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                contacts.isEnabled = true
                contacts.isClickable = true
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_CONTACTS)
            }
            else -> {
                goToSettings()
            }
        }
    }

    private fun goToSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}


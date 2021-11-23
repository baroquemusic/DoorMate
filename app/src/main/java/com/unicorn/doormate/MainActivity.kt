package com.unicorn.doormate

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.location.Geocoder
import android.net.Uri
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import java.util.*
import kotlin.concurrent.schedule
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), LocationListener {
    private lateinit var locationManager: LocationManager
    private lateinit var houseNumber: EditText
    private lateinit var streetName: EditText
    private lateinit var cityName: EditText
    private lateinit var postalCode: EditText
    private lateinit var countryName: EditText
    private lateinit var buttonContinue: Button
    private lateinit var buttonRefresh: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        houseNumber = findViewById(R.id.houseNumber)
        streetName = findViewById(R.id.streetName)
        cityName = findViewById(R.id.cityName)
        postalCode = findViewById(R.id.postalCode)
        countryName = findViewById(R.id.countryName)

        buttonContinue = findViewById(R.id.buttonContinue)
        buttonRefresh = findViewById(R.id.buttonRefresh)

        buttonRefresh.setOnClickListener {
            getLocation()
        }

        buttonContinue.setOnClickListener {
            moveTaskToBack(true)
            exitProcess(-1)
        }

        firstCheck()
    }

    private val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getLocation()
            } else {
                Toast.makeText(applicationContext, "Please grant permission to use the app.", Toast.LENGTH_LONG).show()
                Timer().schedule(3500) {
                    requestPermission()
                }
            }
        }

    private fun firstCheck() {
        when {
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getLocation()
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
                getLocation()
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

    private fun goToSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun getLocation() {
        when {
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {

                locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

                if ((ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED)
                ) {
                    requestPermissionLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION)
                }

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10f, this)
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

    override fun onLocationChanged(location: Location) {

        val geoCoder = Geocoder(applicationContext, Locale.getDefault())

        val addresses: List<Address> =
            geoCoder.getFromLocation(location.latitude, location.longitude, 1)

        val ownerLatitude = location.latitude
        val ownerLongitude = location.longitude

        houseNumber.setText(addresses[0].subThoroughfare)
        streetName.setText(addresses[0].thoroughfare)
        cityName.setText(addresses[0].locality)
        postalCode.setText(addresses[0].postalCode)
        countryName.setText(addresses[0].countryName)

        Toast.makeText(
            applicationContext,
            "Latitude: ${location.latitude}\nLongitude: ${location.longitude}\n" +
                    "Address: ${addresses[0].getAddressLine(0)}",
            Toast.LENGTH_LONG
        ).show()

        locationManager.removeUpdates(this)
    }
}
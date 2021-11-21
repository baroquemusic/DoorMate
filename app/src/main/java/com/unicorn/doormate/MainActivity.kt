package com.unicorn.doormate

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.location.Geocoder
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import java.util.*

class MainActivity : AppCompatActivity(), LocationListener {
    private lateinit var locationManager: LocationManager
    private lateinit var myLocation: TextView
    private val locationPermissionCode = 2
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myLocation = findViewById(R.id.textView)

        requestPermission()

    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getLocation()
            } else {
                Toast.makeText(applicationContext, "Please grant this permission to properly use the app.", Toast.LENGTH_LONG).show()
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
            shouldShowRequestPermissionRationale() -> {
                Toast.makeText(applicationContext, "Permission is truly necessary, it won't hurt.", Toast.LENGTH_LONG).show()
        }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun shouldShowRequestPermissionRationale(): Boolean {
        Toast.makeText(applicationContext, "shouldShowRequestPermissionRationale called.", Toast.LENGTH_LONG).show()
        return false
    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1f, this)
    }

    override fun onLocationChanged(location: Location) {

        val geoCoder = Geocoder(applicationContext, Locale.getDefault())

        val addresses: List<Address> = geoCoder.getFromLocation(location.latitude, location.longitude, 1)

        val last = addresses.lastIndex

        myLocation.text = addresses[last].toString()

        Toast.makeText(applicationContext, "Text updated.\nArray element: $last\n${location.latitude}\n${location.longitude}\n${addresses[last]}", Toast.LENGTH_SHORT).show()
    }

}
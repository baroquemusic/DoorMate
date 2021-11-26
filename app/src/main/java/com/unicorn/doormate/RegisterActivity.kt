package com.unicorn.doormate

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.*
import kotlin.system.exitProcess

class RegisterActivity : AppCompatActivity(), LocationListener {

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
        setContentView(R.layout.activity_register)

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

        getLocation()
    }


    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10f, this)
    }

    override fun onLocationChanged(location: Location) {

        val geoCoder = Geocoder(applicationContext, Locale.getDefault())

        val addresses: List<Address> =
            geoCoder.getFromLocation(location.latitude, location.longitude, 1)

        /*       val ownerLatitude = location.latitude
               val ownerLongitude = location.longitude*/

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
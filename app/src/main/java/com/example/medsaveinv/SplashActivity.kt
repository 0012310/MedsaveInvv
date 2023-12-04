package com.example.medsaveinv

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler


import android.location.LocationManager
import android.net.Uri
import android.provider.Settings

import android.widget.Toast
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import com.example.medsaveinv.DataBase.SharedPreferenceUtils
import com.google.android.gms.location.FusedLocationProviderClient


class SplashActivity : AppCompatActivity(), LocationListener {

    private val locationPermissionCode = 2
    private lateinit var locationManager: LocationManager
    private lateinit var sharedPreferences: SharedPreferenceUtils


    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        sharedPreferences = SharedPreferenceUtils.getInstance(this)

        if (ContextCompat.checkSelfPermission(
                this@SplashActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            waitAndGo()
        } else {
            getLocationPermission()
        }


    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this@SplashActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !==
            PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@SplashActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@SplashActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )

            } else {
                ActivityCompat.requestPermissions(
                    this@SplashActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            }
        } else {
            //  getLatLong()
            waitAndGo()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(
                            this@SplashActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) === PackageManager.PERMISSION_GRANTED)
                    ) {
                        //  getLatLong()
                        waitAndGo()
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()

                    }

                } else {
                    if (grantResults[0] == -1) {
                        showDialogForPermissionDetail()

                    } else {
                        getLocationPermission()
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                    }

                }

                return
            }
        }
    }

    private fun showDialogForPermissionDetail() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("You have denied Location permission. Please allow to use Location feature.")
        builder.setCancelable(true)
        builder.setPositiveButton(
            "Ok"
        ) { _, _ ->
            showSettingAlert()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, _ -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    private fun showSettingAlert() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }


    private fun getLatLong() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }
        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 600, 5f, this)
    }


    override fun onLocationChanged(location: Location) {
        Toast.makeText(this, "" + location.latitude + location.longitude, Toast.LENGTH_SHORT).show()
        sharedPreferences.setStringValue("latitudeinv", location.latitude.toString())
        sharedPreferences.setStringValue("longitudeinv", location.longitude.toString())
    }


    private fun waitAndGo() {
        Handler().postDelayed(
            {
                if (sharedPreferences.getBoolanValue("isLogin", false)) {
                    val intent = Intent(this, DashBoradActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }, 3000
        )
    }


}

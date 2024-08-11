package med.Save.medsaveinv


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import med.Save.medsaveinv.DataBase.SharedPreferenceUtils
import org.json.JSONObject


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(), LocationListener {

    private val locationPermissionCode = 2
    private lateinit var locationManager: LocationManager
    private lateinit var sharedPreferences: SharedPreferenceUtils

    lateinit var tvVersion: TextView
    private var appVersion = "0"
    private var forceUpdate = "0"
    private var latestVersion = ""
    private var messageFromServer = ""
    private lateinit var updateAlertDialog: androidx.appcompat.app.AlertDialog
    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        sharedPreferences = SharedPreferenceUtils.getInstance(this)
        requestQueue = Volley.newRequestQueue(this)
        tvVersion = findViewById(R.id.tvVersion)
        val manager = this.packageManager
        val info = manager.getPackageInfo(this.packageName, PackageManager.GET_ACTIVITIES)

        checkVersion()
        tvVersion.text = "V :"+info.versionName.toString()

        if (ContextCompat.checkSelfPermission(
                this@SplashActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
          //  waitAndGo()
        } else {
            getLocationPermission()
        }


    }

    private fun checkVersion() {
        val manager = this.packageManager
        val info = manager.getPackageInfo(this.packageName, PackageManager.GET_ACTIVITIES)
        appVersion = info.versionName.toString()
        val request: StringRequest = object : StringRequest(
            Method.GET,
            "https://medsave.in/AgentAPI/api/search/getAppVersion?appName=Medsaveinv",
            Response.Listener { response ->
                val jsonobj = JSONObject(response)
                val jsondata = jsonobj.getJSONArray("data")
                for (i in 0 until jsondata.length()) {
                    var data = jsondata.getJSONObject(i)
                    latestVersion = data.optString("CurrentVersion")
                    forceUpdate = "1"
                    if (latestVersion.isNotBlank()) {
                        try {
                            Log.d("DDDL", latestVersion)
                            Log.d("DDDLAppVersion", appVersion)
                            if ((latestVersion) > appVersion) {
                                showUpdateAlertDialog()
                            } else {
                                waitAndGo()
                            }
                        } catch (ne: NumberFormatException) {
                            ne.printStackTrace()
                            Toast.makeText(this, "$ne", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        waitAndGo()
                    }

                }


            },
            Response.ErrorListener {
                Toast.makeText(this, "Error$it", Toast.LENGTH_SHORT).show()
            }) {

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = java.util.HashMap()
                headers["X-Access-Token"] =
                    "PZvHQLGHdMTyJBr20d3SAbU5Y2NdnW3dmRhRO6v/MfHHIsK!HrupC41?dEdgAIUR"
                return headers
            }
        }

        request.setShouldCache(false)
        request.retryPolicy = DefaultRetryPolicy(
            4000,
            2,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(request)
    }

    private fun showUpdateAlertDialog() {
        val msg: String = if (messageFromServer.isEmpty()) {
            getString(R.string.versionStr) + getString(R.string.versionEnd)
        } else {
            messageFromServer
        }
        updateAlertDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(R.string.newVersionTitle)
            .setMessage(msg)
            .setPositiveButton(R.string.updateBtn, null)
            .setNegativeButton(R.string.cancelBtn, null)
            .setCancelable(false)
            .show()

        val updateBtn = updateAlertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE) as Button
        updateBtn.setOnClickListener {
            updateAlertDialog.dismiss()
            val appPackageName =
                packageName // packageName is an extension property available in Kotlin
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        // Uri.parse("market://details?id=$appPackageName")
                        Uri.parse("https://play.google.com/store/apps/details?id=med.Save.medsaveinv&hl=en_US")
                    )
                )
                finish()
            } catch (anfe: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=med.Save.medsaveinv&hl=en_US")
                    )
                )
            }
        }

        val cancelBtn = updateAlertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE) as Button
        if (forceUpdate == "1") {
            cancelBtn.visibility = View.GONE
        }
        cancelBtn.setOnClickListener {
            updateAlertDialog.dismiss()
            waitAndGo()

        }
        updateAlertDialog.setOnKeyListener { dialog, keyCode, event -> keyCode == KeyEvent.KEYCODE_BACK }
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
                      //  Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()

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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 600, 5f, this)
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

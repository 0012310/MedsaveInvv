package com.example.medsaveinv

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.medsaveinv.DataBase.SharedPreferenceUtils
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso


class DashBoradActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    LocationListener {
    private lateinit var sharedPreferences: SharedPreferenceUtils
    private lateinit var imgDrawer: ImageView
    private lateinit var imgLogout: ImageView
    private lateinit var lllogout: LinearLayout
    private lateinit var btnFileNo: Button
    private lateinit var profileImage: ImageView
    private lateinit var tvUserName: TextView
    private lateinit var llInvestigationQSurvey: LinearLayout

    private lateinit var locationManager: LocationManager
    val MYLOCATION_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_borad)
        sharedPreferences = SharedPreferenceUtils.getInstance(this)


        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)

        profileImage = headerView.findViewById(R.id.profileImage)
        if (sharedPreferences.getContainsKey("ImageUrl")) {

            Picasso.get()
                .load(sharedPreferences.getStringValue("ImageUrl", ""))
                .into(profileImage);
        }
        tvUserName = headerView.findViewById(R.id.tvUserName)
        tvUserName.text = sharedPreferences.getStringValue("username", "")

        llInvestigationQSurvey = headerView.findViewById(R.id.llInvestigationQSurvey)
        llInvestigationQSurvey.setOnClickListener {

            var intent = Intent(this, InvestigationSearchActivity::class.java)
            intent.putExtra("From", "Survey")
            startActivity(intent)
        }

        lllogout = headerView.findViewById(R.id.lllogout)
        lllogout.setOnClickListener {
            logOut()
        }
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
            this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        navigationView.setNavigationItemSelectedListener(this)
        imgDrawer = findViewById(R.id.imgDrawer)
        imgDrawer.setOnClickListener {
            val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
            if (!drawer.isDrawerOpen(GravityCompat.START)) drawer.openDrawer(GravityCompat.START) else drawer.closeDrawer(
                GravityCompat.END
            )
        }


        imgLogout = findViewById(R.id.imgLogout)
        imgLogout.setOnClickListener {
            logOut()
        }

        btnFileNo = findViewById(R.id.btnFileNo)

        btnFileNo.setOnClickListener {
            var intent = Intent(this, InvestigationSearchActivity::class.java)
            intent.putExtra("From", "Direct")
            startActivity(intent)
        }
        getLocation()
    }


    private fun logOut() {
        val alert = AlertDialog.Builder(this)
        alert.setMessage("Are you sure want to logout?")
        alert.setCancelable(false).setPositiveButton("Logout") { _, _ ->
            sharedPreferences.clear()
            finish()
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
        }.setNegativeButton("Cancel", null)
        val alert1 = alert.create()
        alert1.show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        callBackAlertDialog()
    }

    private fun callBackAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("MedSave investigator")
        builder.setIcon(R.drawable.appicon)
        builder.setMessage("Do you want to Exit from MedSave investigator App?")
        builder.setPositiveButton("Yes") { dialog, which ->
            finish()
        }

        builder.setNegativeButton("No") { dialog, which ->
        }
        builder.show()
    }

    private fun getLocation() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_ACCESS_FINE_LOCATION
            )
            return
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, this)

    }

    override
    fun onLocationChanged(location: Location) {
       // Toast.makeText(this, "" + location.latitude + location.longitude, Toast.LENGTH_SHORT).show()
        sharedPreferences.setStringValue("latitudeinv", location.latitude.toString())
        sharedPreferences.setStringValue("longitudeinv", location.longitude.toString())

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> getLocation()
                PackageManager.PERMISSION_DENIED -> {}
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
    }

}
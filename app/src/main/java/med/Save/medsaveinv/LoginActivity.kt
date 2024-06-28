package med.Save.medsaveinv

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import med.Save.medsaveinv.DataBase.SharedPreferenceUtils
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var requestQueue: RequestQueue
    private lateinit var sharedPreferences: SharedPreferenceUtils
    lateinit var etUserId: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    private lateinit var userTypeSpinner: AppCompatSpinner
    private lateinit var progressLayout: ConstraintLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPreferences = SharedPreferenceUtils.getInstance(this)
        progressLayout = findViewById(R.id.progressLayout)
        requestQueue = Volley.newRequestQueue(this)

        userTypeSpinner = findViewById(R.id.userTypeSpinner)
        etUserId = findViewById(R.id.etUserId)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        val inputTypeAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            this.resources.getStringArray(R.array.user_type)
        )
        inputTypeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        userTypeSpinner.adapter = inputTypeAdapter

        userTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                when (userTypeSpinner.selectedItem) {

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnLogin.setOnClickListener {
         //   throw RuntimeException("Test Crash")
         //   FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
            validateInput()

        }



    }

    private fun validateInput() {
        if (userTypeSpinner.selectedItem == "Select User Type") {
            Toast.makeText(this, "Please Select User Type", Toast.LENGTH_SHORT).show()
        } else {
            callLoginApi()

        }
    }


    private fun callLoginApi() {
        var data = "?userid=${etUserId.text.toString() + "&password=${etPassword.text.toString()}"}"
        Log.d("dd", data)
        showProgress()
        val jsonObject = JSONObject()
        val jsonObjectRequest = object : JsonObjectRequest(Method.GET,
            //"https://medsave.in/InvAPI/api/investigation/getlogin$data",
            "https://medsave.in/MedApp/api/investigation/getlogin$data",
            jsonObject,
            {
                hideProgress()
                var jsonObject = JSONObject(it.toString())
                var jsonArray = jsonObject.getJSONArray("response")
                var msg = jsonObject.getString("message")
                if (msg.equals("Success")) {
                    for (i in 0 until jsonArray.length()) {
                        var data = jsonArray.getJSONObject(i)
                        var username = data.optString("NAME")
                        var loginID = data.optString("LOGINID")
                        sharedPreferences.setStringValue("username", username)
                        sharedPreferences.setStringValue("loginID", loginID)
                        sharedPreferences.setBoolanValue("isLogin", true)
                        val intent = Intent(this, DashBoradActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                }
                else {
                    Toast.makeText(this, "Invalid User name or password", Toast.LENGTH_SHORT).show()
                }

            },
            { error ->
                hideProgress()
                Toast.makeText(this, "" + error.toString(), Toast.LENGTH_SHORT).show()

            }) {

        }

        jsonObjectRequest.setShouldCache(false)
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(jsonObjectRequest)
    }

    private fun showProgress() {
        progressLayout.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        progressLayout.visibility = View.GONE
    }
}
package med.Save.medsaveinv

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import med.Save.medsaveinv.DataBase.SharedPreferenceUtils
import med.Save.medsaveinv.R
import org.json.JSONObject

class InvestigationSearchActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    private lateinit var sharedPreferences: SharedPreferenceUtils
    lateinit var imgBack: ImageView
    lateinit var etFileNo: EditText
    lateinit var btnSearch: Button


    lateinit var llparent: LinearLayout

    lateinit var imageview: ImageView
    lateinit var tvName: TextView
    lateinit var tvCardNo: TextView
    lateinit var tvProposer: TextView
    lateinit var tvPolicyNo: TextView
    lateinit var tvHospital: TextView
    lateinit var tvHospitalAddress: TextView
    lateinit var tvHospitalPhone: TextView
    lateinit var tvAddmissionDate: TextView
    lateinit var tvClaimAmount: TextView
    lateinit var tvDisease: TextView
    lateinit var tvPatientMobile: EditText
    lateinit var tvPatientEmialID: EditText
    private var isFrom = ""
    lateinit var imagearrow: ImageView
    private var responeData = ""
    var mProgressDialog: ProgressDialog? = null

    var ststusText = "No"

    private lateinit var progressLayout: ConstraintLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_investigation_search)
        imgBack = findViewById(R.id.imgBack)
        imgBack.setOnClickListener {
            finish()
        }
        sharedPreferences = SharedPreferenceUtils.getInstance(this)
        requestQueue = Volley.newRequestQueue(this)
        progressLayout = findViewById(R.id.progressLayout)

        mProgressDialog = ProgressDialog(this)
        mProgressDialog?.setCancelable(false)
        mProgressDialog?.setTitle("Please wait...")

        val intent = intent
        val dataFile = intent.getStringExtra("dataKey")


        etFileNo = findViewById(R.id.etFileNo)
        btnSearch = findViewById(R.id.btnSearch)
        if (dataFile != null) {
            etFileNo.setText(dataFile)
        }
        btnSearch.setOnClickListener {
            validateData()

        }

        llparent = findViewById(R.id.llparent)
        imageview = findViewById(R.id.imageview)
        tvName = findViewById(R.id.tvName)
        tvCardNo = findViewById(R.id.tvCardNo)
        tvProposer = findViewById(R.id.tvProposer)
        tvPolicyNo = findViewById(R.id.tvPolicyNo)
        tvHospital = findViewById(R.id.tvHospital)
        tvHospitalAddress = findViewById(R.id.tvHospitalAddress)
        tvHospitalPhone = findViewById(R.id.tvHospitalPhone)
        tvAddmissionDate = findViewById(R.id.tvAddmissionDate)
        tvClaimAmount = findViewById(R.id.tvClaimAmount)
        tvDisease = findViewById(R.id.tvDisease)
        tvPatientMobile = findViewById(R.id.tvPatientMobile)
        tvPatientEmialID = findViewById(R.id.tvPatientEmialID)

        imagearrow = findViewById(R.id.imagearrow)

        if (intent.hasExtra("From")) {
            isFrom = intent.getStringExtra("From")!!
        }


      //  Toast.makeText(this, "" + isFrom, Toast.LENGTH_SHORT).show()

        imagearrow.setOnClickListener {
            if (validateMobEmail()) {
                if (ststusText == "Investigation Already Done.") {
                    showStatusDialog(ststusText)
                } else {
                    callProcess()
                }
            }

        }
    }

    private fun callProcess() {
        if (isFrom == "Survey") {
            var intent = Intent(this, SurveyFormActivity::class.java)
            intent.putExtra("Data", responeData)
            startActivity(intent)
        } else if (isFrom == "Direct") {
            Log.d("dddddd", responeData)
            var intent = Intent(this, DirectActivity::class.java)
            intent.putExtra("Data", responeData)
            startActivity(intent)

        }
    }

    private fun showStatusDialog(msg: String) {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setIcon(R.drawable.appicon).setTitle(msg)
        builder.setMessage("You have already submit that request. Want to do it again?")
        builder.setPositiveButton("Yes") { dialog, which ->
            if (msg == "Investigation Already Done.") {
                callProcess()
            } else {
                dialog.dismiss()
            }

        }

        builder.setNegativeButton("No") { dialog, which ->
            if (msg.equals("Confirm! Do you want to continue Survey?")) {
                finish()
            } else {
                dialog.dismiss()

            }

        }

        builder.show()
    }

    private fun validateMobEmail(): Boolean {
        if ((!tvPatientMobile.text.isNullOrBlank() && tvPatientMobile.length() == 10) && (!tvPatientEmialID.text.isNullOrBlank())) {
            sharedPreferences.setStringValue("userMobileNo", tvPatientMobile.text.toString())
            sharedPreferences.setStringValue("userEmailID", tvPatientEmialID.text.toString())
            return true
        } else {
            //  Toast.makeText(this, "Please Enter Mobile No and Email ID", Toast.LENGTH_SHORT).show()
            return true
        }
    }

    private fun validateData() {
        if (etFileNo.text.isBlank()) {
            Toast.makeText(this, "Please Enter File No", Toast.LENGTH_SHORT).show()
            etFileNo.setError("Enter File No")
        } else {
            CallAPi()
        }
    }


    private fun CallAPi() {
        showProgress()
        mProgressDialog?.show()
        var data = "?fileno=${etFileNo.text.toString()}"
        Log.d("dd", data)
        val jsonObject = JSONObject()
        val jsonObjectRequest = object : JsonObjectRequest(Method.GET,
            //"https://medsave.in/InvAPI/api/investigation/getInvestigationSearch$data",
            "https://medsave.in/MedApp/api/investigation/getInvestigationSearch$data",
            jsonObject,
            {
                mProgressDialog?.dismiss()
                var jsonObject = JSONObject(it.toString())
                responeData = it.toString()

                var jsonArray = jsonObject.getJSONArray("response")
                Log.d("DataRes", responeData)
                if (jsonArray.length() > 0) {
                    sharedPreferences.setStringValue("fileNo", etFileNo.text.toString())
                    imagearrow.visibility = View.VISIBLE
                    llparent.visibility = View.VISIBLE
                    for (i in 0 until jsonArray.length()) {
                        var data = jsonArray.getJSONObject(i)

                        var ImageUrl = data.getString("photopath")
                        sharedPreferences.setStringValue("ImageUrl", ImageUrl)
                        var memname = data.getString("memname")
                        var cardno = data.getString("cardno")

                        Glide
                            .with(this)
                            .load(ImageUrl)
                            .placeholder(R.drawable.profile)
                            .into(imageview);

                        //      Picasso.get().load(ImageUrl).into(imageview)

                        ststusText = data.getString("messageText")
                        tvName.text = memname
                        tvCardNo.text = cardno
                        tvProposer.text = data.getString("propname")
                        tvPolicyNo.text = data.getString("polno")
                        tvHospital.text = data.getString("npname")
                        tvHospitalAddress.text = data.getString("CareAddr")
                        tvHospitalPhone.text = data.getString("hphone")
                        tvAddmissionDate.text = data.getString("dischargedate")
                        tvClaimAmount.text = data.getString("estcost")
                        tvDisease.text = data.getString("intdisease")
                        val mobile = data.getString("patientmobileno")

                        if (!tvPatientMobile.text.isNullOrBlank() && tvPatientMobile.length() == 10) {
                            tvPatientMobile.setText(mobile)
                            tvPatientMobile.setTextIsSelectable(false)
                            sharedPreferences.setStringValue("userMobileNo", mobile)
                        } else {

                        }
                    }

                } else {
                    llparent.visibility = View.GONE
                    imagearrow.visibility = View.GONE
                    Toast.makeText(this, "No Data found of that File No", Toast.LENGTH_SHORT)
                        .show()

                }
            },
            { error ->
                mProgressDialog?.dismiss()
                Toast.makeText(this, "Volley_\n" + error.toString(), Toast.LENGTH_SHORT).show()

            }) {}

        jsonObjectRequest.setShouldCache(false)
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
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
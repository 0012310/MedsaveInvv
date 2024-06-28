package med.Save.medsaveinv

import android.app.DatePickerDialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import med.Save.medsaveinv.DataBase.SharedPreferenceUtils
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar

class SurveyFormActivity : AppCompatActivity() {
    var mProgressDialog: ProgressDialog? = null
    lateinit var requestQueue: RequestQueue
    private lateinit var sharedPreferences: SharedPreferenceUtils

    private var osw: FileOutputStream? = null
    private var logFile: File? = null
    lateinit var tvTitle: TextView
    private var responeData = ""
    private var forall = arrayOf("Select", "YES", "NO")
    private var policy_yr = arrayOf("Select Policy Yr", "YES", "NO")
    private var lineofTreatment =
        arrayOf("Select Treatment Type", "Allopathy", "Ayurvedic", "Homopathic")   //spinnerVeri_PatientLineTreatment_Adapter
    private var treatment_type = arrayOf("Select Treatment Type", "Conservative", "Surgical")
    private var claim_type = arrayOf("Select Claim Type", "Conservative", "Surgical")
    private var room_type = arrayOf(
        "Select Room Type",
        "General/Economy",
        "Cabin",
        "4 Beded Room",
        "3 Baded Room",
        "Twin Sharing/Double Beded",
        "Single/Private Room",
        "Semi Private Room",
        "AC Room",
        "Deluxe",
        "Super Deluxe",
        "Suite",
        "Intensive Care Unit (ICU)",
        "Critical Care Unit (CCU)",
        "Intensive Treatment Unit (ITU)",
        "Neonatal Intensive Care Unit (NICU)",
        "Pediatric Intensive Care Unit (PICU)",
        "Psychiatric Intensive Care Unit (PICU)",
        "Coronary Care Unit (CCU)",
        "Post-anesthesia Care Unit (PACU)",
        "High Dependency Unit (HDU)",
        "Surgical Intensive Care Unit (SICU)",
        "Day Care",
        "Nursury",
        "Not Admitted"
    )

    lateinit var spinnerFirstYrPolicy: Spinner
    lateinit var spinnerTypeofClaim: Spinner
    lateinit var spinnerModeofTratment: Spinner
    lateinit var spinnerRoomType: Spinner


    lateinit var etHopNameAdd: EditText
    lateinit var etAddDrName: EditText
    lateinit var etNameAndAdd: EditText
    lateinit var etDrMob: EditText
    lateinit var etAge: EditText
    lateinit var etSex: EditText
    lateinit var etRelationInsured: EditText
    lateinit var etSumInsured: EditText
    lateinit var etPolicyNo: EditText
    lateinit var etDateofAddmission: TextView
    lateinit var etDateofDis: TextView
    lateinit var etDateNAtureofAilment: EditText
    lateinit var etRoomRent: EditText
    lateinit var etReqAmount: EditText
    lateinit var etAmoutByTpa: EditText


    lateinit var spinnerDoc_IDCard: Spinner
    lateinit var spinnerDoc_PreAuth: Spinner
    lateinit var spinnerDoc_IndoreCase: Spinner
    lateinit var spinnerVeri_HospReg: Spinner
    lateinit var spinnerVeri_susBack: Spinner
    lateinit var spinnerVeri_PatientonBed: Spinner
    lateinit var spinnerVeri_PatientPhotoMatched: Spinner
    lateinit var spinnerVeri_PatientAddDetails: Spinner
    lateinit var spinnerVeri_PatientAgeMAtched: Spinner
    lateinit var spinnerVeri_PatientSignature: Spinner
    lateinit var spinnerVeri_PatientApperedDisco: Spinner
    lateinit var spinnerVeri_PatientRecords: Spinner
    lateinit var spinnerVeri_PatientSymptoms: Spinner
    lateinit var spinnerVeri_PatientAbleDisSymptoms: Spinner
    lateinit var spinnerVeri_PatientRelaPres: Spinner
    lateinit var spinnerVeri_PatientNoDrVisited: Spinner
    lateinit var spinnerVeri_PatientLineTreatment: Spinner

    lateinit var etNoOfBed: EditText
    lateinit var etVeri_PatientonBed: EditText
    lateinit var etVeri_PatientPhotoMatched: EditText
    lateinit var etVeri_PatientAddDetails: EditText
    lateinit var etVeri_PatientAgeMAtched: EditText
    lateinit var etVeri_PatientSignature: EditText
    lateinit var etDurationPreIll: EditText
    lateinit var etTemp: EditText
    lateinit var etBP: EditText
    lateinit var etBloodSugar: EditText
    lateinit var etTLCWBC: EditText
    lateinit var etInvestigatorMob: EditText


    lateinit var llOne: LinearLayout
    lateinit var llTwo: LinearLayout
    lateinit var btnNext: Button
    lateinit var btnSubmit: Button
    var roomtype = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_form)
        tvTitle = findViewById(R.id.tvTitle)
        findAll()
        logFile = File(getExternalFilesDir("Logs"), "Survey Activity.txt")
        try {
            osw = FileOutputStream(logFile)
            osw?.write("Log file created\n".toByteArray())
        } catch (io: IOException) {
            io.printStackTrace()
        }


        mProgressDialog = ProgressDialog(this)
        mProgressDialog?.setTitle("Please wait...")

        requestQueue = Volley.newRequestQueue(this)
        sharedPreferences = SharedPreferenceUtils.getInstance(this)

        if (intent.hasExtra("Data")) {
            responeData = intent.getStringExtra("Data")!!
            Log.d("responeData", responeData)
            setData(responeData)

        }

        tvTitle.text = "Investigation Form"
        spinnerFirstYrPolicy = findViewById(R.id.spinnerFirstYrPolicy)
        spinnerTypeofClaim = findViewById(R.id.spinnerTypeofClaim)
        spinnerModeofTratment = findViewById(R.id.spinnerModeofTratment)
        spinnerRoomType = findViewById(R.id.spinnerRoomType)


        spinnerDoc_IDCard = findViewById(R.id.spinnerDoc_IDCard)
        spinnerDoc_PreAuth = findViewById(R.id.spinnerDoc_PreAuth)
        spinnerDoc_IndoreCase = findViewById(R.id.spinnerDoc_IndoreCase)
        spinnerVeri_HospReg = findViewById(R.id.spinnerVeri_HospReg)
        spinnerVeri_susBack = findViewById(R.id.spinnerVeri_susBack)
        spinnerVeri_PatientonBed = findViewById(R.id.spinnerVeri_PatientonBed)
        spinnerVeri_PatientPhotoMatched = findViewById(R.id.spinnerVeri_PatientPhotoMatched)
        spinnerVeri_PatientAddDetails = findViewById(R.id.spinnerVeri_PatientAddDetails)
        spinnerVeri_PatientAgeMAtched = findViewById(R.id.spinnerVeri_PatientAgeMAtched)
        spinnerVeri_PatientSignature = findViewById(R.id.spinnerVeri_PatientSignature)
        spinnerVeri_PatientApperedDisco = findViewById(R.id.spinnerVeri_PatientApperedDisco)
        spinnerVeri_PatientRecords = findViewById(R.id.spinnerVeri_PatientRecords)
        spinnerVeri_PatientSymptoms = findViewById(R.id.spinnerVeri_PatientSymptoms)
        spinnerVeri_PatientAbleDisSymptoms = findViewById(R.id.spinnerVeri_PatientAbleDisSymptoms)
        spinnerVeri_PatientRelaPres = findViewById(R.id.spinnerVeri_PatientRelaPres)
        spinnerVeri_PatientNoDrVisited = findViewById(R.id.spinnerVeri_PatientNoDrVisited)
        spinnerVeri_PatientLineTreatment = findViewById(R.id.spinnerVeri_PatientLineTreatment)



        etNoOfBed = findViewById(R.id.etNoOfBed)
        etVeri_PatientonBed = findViewById(R.id.etVeri_PatientonBed)
        etVeri_PatientPhotoMatched = findViewById(R.id.etVeri_PatientPhotoMatched)
        etVeri_PatientAddDetails = findViewById(R.id.etVeri_PatientAddDetails)
        etVeri_PatientAgeMAtched = findViewById(R.id.etVeri_PatientAgeMAtched)
        etVeri_PatientSignature = findViewById(R.id.etVeri_PatientSignature)
        etDurationPreIll = findViewById(R.id.etDurationPreIll)
        etTemp = findViewById(R.id.etTemp)
        etBP = findViewById(R.id.etBP)
        etBloodSugar = findViewById(R.id.etBloodSugar)
        etTLCWBC = findViewById(R.id.etTLCWBC)
        etInvestigatorMob = findViewById(R.id.etInvestigatorMob)

        llOne = findViewById(R.id.llOne)
        llTwo = findViewById(R.id.llTwo)

        setListenerAndData()
        btnNext = findViewById(R.id.btnNext)
        btnSubmit = findViewById(R.id.btnSubmit)

        btnNext.setOnClickListener {
            if (etAddDrName.text.isNullOrBlank()) {
                etAddDrName.requestFocus()
                etAddDrName.setError("Please Enter Doctor Name")
                showToast("Please Enter Doctor Name")
            } else if (TextUtils.isEmpty(etDrMob.text.toString().trim())) {
                etDrMob.error = "Please enter mobile number"
                etDrMob.requestFocus()
            } else if (etDrMob.text?.length!! < 10) {
                etDrMob.error = "Please enter valid mobile number"
                etDrMob.requestFocus()

            } else if (spinnerModeofTratment.selectedItem == "Select Treatment Type") {
                spinnerModeofTratment.requestFocus()
                showToast("Please Select Treatment Type")
            } else if (spinnerRoomType.selectedItem == "Select Room Type") {
                spinnerRoomType.requestFocus()
                showToast("Please Select Room Type")
            } else if (etRoomRent.text.toString().isBlank()) {
                etRoomRent.requestFocus()
                etRoomRent.setError("Please Enter Room Rent")
                showToast("Please Enter Room Rent")
            } else {
                callApiformain()

            }


        }



        btnSubmit.setOnClickListener {
            if (spinnerDoc_IDCard.selectedItem == "Select") {
                showToast("Please select ID Cards/Any Other PHOTO ID")
            } else if (spinnerVeri_HospReg.selectedItem == "Select") {
                showToast("Please select Is Hospital Registered")
            } else if (etNoOfBed.text.isNullOrBlank()) {
                etNoOfBed.requestFocus()
                etNoOfBed.setError("Please Enter No of Beds Name")
                showToast("Please Enter No of Beds Name")
            } else if (spinnerVeri_susBack.selectedItem == "Select") {
                showToast("Please select Is the Hospital of Suspicious Background")
            } else if (spinnerVeri_PatientonBed.selectedItem == "Select") {
                showToast("Please select Patinet was Present on Bed")
            } else if (spinnerVeri_PatientPhotoMatched.selectedItem == "Select") {
                showToast("Please select Patient Photo ID Matched with Him ,Her")
            } else if (spinnerVeri_PatientRecords.selectedItem == "Select") {
                showToast("Please select Patient Case Records were Shown")
            } else if (spinnerVeri_PatientLineTreatment.selectedItem == "Select") {
                showToast("Please select Line of Treatment")
            } else if (etTemp.text.isNullOrBlank()) {
                etTemp.requestFocus()
                etTemp.setError("Please Enter Temp.")
                showToast("Please Enter Temp")
            } else if (etBP.text.isNullOrBlank()) {
                etBP.requestFocus()
                etBP.setError("Please Enter BP(LowerSide/UpperSide)")
                showToast("Please Enter BP(LowerSide/UpperSide)")
            } else if (etBloodSugar.text.isNullOrBlank()) {
                etBloodSugar.requestFocus()
                etBloodSugar.setError("Please Enter Blood Sugar(Fasting/PP)")
                showToast("Please Enter Blood Sugar(Fasting/PP)")
            } else if (etTLCWBC.text.isNullOrBlank()) {
                etTLCWBC.requestFocus()
                etTLCWBC.setError("Please Enter TLC/WBC")
                showToast("Please Enter TLC/WBC")
            } else {
                callSubmitFormApi()
            }

        }


    }

    private fun showToast(s: String) {
        Toast.makeText(this, "$s", Toast.LENGTH_SHORT).show()

    }


    private fun callApiformain() {
        mProgressDialog?.show()
        val url = "https://medsave.in/MedApp/api/investigation/SaveSurveyData"
        val postData = JSONObject()
        try {
            postData.put("ADDRESSOFHOSPITAL", etHopNameAdd.text.toString())
            postData.put("ATTENDINGDOCTOR", etAddDrName.text.toString())
            postData.put("ADDRESSOFPATIENT", etNameAndAdd.text.toString().trim())
            postData.put("ContactNoofDoctor", etDrMob.text.toString())
            postData.put("AGE", etAge.text.toString().trim())
            postData.put("SEX", etSex.text.toString().trim())
            postData.put("RELATIONINSURED", etRelationInsured.text.toString().trim())
            postData.put("SUMINSURED", etSumInsured.text.toString())
            postData.put("POLICYNO", etPolicyNo.text.toString())
            postData.put(
                "FIRSTYEARPOLICY", if (spinnerFirstYrPolicy.selectedItem == "YES") {
                    "1"
                } else if (spinnerFirstYrPolicy.selectedItem == "Select Policy Yr") {
                    ""
                } else {
                    "0"
                }
            )
            postData.put("ROOMTYPE", roomtype.toString())
            postData.put(
                "TYPEOFCLAIM", if (spinnerTypeofClaim.selectedItem == "Conservative") {
                    "0"
                } else if ((spinnerTypeofClaim.selectedItem == "Select Claim Type")) {
                    ""
                } else {
                    "1"
                }
            )
            postData.put("ROOMRENT", etRoomRent.text.toString())
            postData.put("PREAUTHREQUESTAMOUNT", etReqAmount.text.toString())
            postData.put("DATEOFADMISSION", etDateofAddmission.text.toString())
            postData.put("DATEOFDISCHARGE", etDateofDis.text.toString())
            postData.put("NATUREOFAILMENT", etDateNAtureofAilment.text.toString())
            postData.put(
                "MODEOFTREATMENT",
                if (spinnerModeofTratment.selectedItem == "Conservative") {
                    "0"
                } else if (spinnerModeofTratment.selectedItem == "Select Treatment Type") {
                    ""
                } else {
                    "1"
                }
            )

            postData.put("FileNo", sharedPreferences.getStringValue("fileNo", ""))
            postData.put("flag", "main")
            Log.d("survey_Req_main", postData.toString())
            osw?.write("survey_Req_main  $postData\n".toByteArray())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val request =
            JsonObjectRequest(Request.Method.POST, url, postData, { response ->
                mProgressDialog?.dismiss()
                osw?.write("survey_Res_main  $response.toString()\n".toByteArray())
                Log.d("survey_Res_main", response.toString())
                val obj: JSONObject = response
                val status = obj.getString("response")
                if (status.equals("update")) {
                    llOne.visibility = View.GONE
                    llTwo.visibility = View.VISIBLE
                    tvTitle.text = "Questions Verification"
                    Toast.makeText(this, "Survey main Form upload Success ", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, "Survey Form upload Failed ", Toast.LENGTH_SHORT).show()
                }

            }, { error ->
                mProgressDialog?.dismiss()
                Log.d("survey_Res", error.toString())
                Toast.makeText(this, "" + error.toString(), Toast.LENGTH_SHORT).show()
            }
            )
        requestQueue.add(request)
    }


    private fun callSubmitFormApi() {
        mProgressDialog?.show()
        val url = "https://medsave.in/MedApp/api/investigation/SaveSurveyData"
        val postData = JSONObject()
        try {
            postData.put(
                "IDCARDS", if (spinnerDoc_IDCard.selectedItem == "YES") {
                    "1"
                } else if (spinnerDoc_IDCard.selectedItem == "Select") {
                    ""
                } else {
                    "0"
                }
            )
            postData.put(
                "PREAUTHORISATION", if (spinnerDoc_PreAuth.selectedItem == "YES") {
                    "1"
                } else if (spinnerDoc_PreAuth.selectedItem == "Select") {
                    ""
                } else {
                    "0"
                }
            )
            postData.put(
                "INDOORCASE", if (spinnerDoc_IndoreCase.selectedItem == "YES") {
                    "1"
                } else if (spinnerDoc_IndoreCase.selectedItem == "Select") {
                    ""
                } else {
                    "0"
                }
            )
            postData.put(
                "ISHOSPITALREGISTERED", if (spinnerVeri_HospReg.selectedItem == "YES") {
                    "1"
                } else if (spinnerVeri_HospReg.selectedItem == "Select") {
                    ""
                } else {
                    "0"
                }
            )
            postData.put("NOOFBED", etNoOfBed.text.toString())
            postData.put(
                "ISTHEHOSPITAL", if (spinnerVeri_susBack.selectedItem == "YES") {
                    "1"
                } else if (spinnerVeri_susBack.selectedItem == "Select") {
                    ""
                } else {
                    "0"
                }
            )
            postData.put(
                "PATIENTWASPRESENT", if (spinnerVeri_PatientonBed.selectedItem == "YES") {
                    "1"
                } else if (spinnerVeri_PatientonBed.selectedItem == "Select") {
                    ""
                } else {
                    "0"
                }
            )
            postData.put("THENREASON", etVeri_PatientonBed.text.toString())
            postData.put("PATIENTDISHCARGED", "")
            postData.put(
                "PATIENTPHOTO",
                if (spinnerVeri_PatientPhotoMatched.selectedItem == "YES") {
                    "1"
                } else if (spinnerVeri_PatientPhotoMatched.selectedItem == "Select") {
                    ""
                } else {
                    "0"
                }
            )
            postData.put("NOREASON", etVeri_PatientPhotoMatched.text.toString())
            postData.put(
                "PROVIDEADDRESSDETAILS",
                if (spinnerVeri_PatientAddDetails.selectedItem == "YES") {
                    "1"
                } else if (spinnerVeri_PatientAddDetails.selectedItem == "Select") {
                    ""
                } else {
                    "0"
                }
            )
            postData.put("IFREASON", etVeri_PatientAddDetails.text.toString())
            postData.put(
                "PATIENTAGEMATCHED",
                if ("spinnerVeri_PatientAgeMAtched.selectedItem" == "YES") {
                    "1"
                } else if (spinnerVeri_PatientAgeMAtched.selectedItem == "Select") {
                    ""
                } else {
                    "0"
                }
            )
            postData.put("REASON", etVeri_PatientAgeMAtched.text.toString())
            postData.put(
                "PATIENTSSIGNATURE",
                if (spinnerVeri_PatientSignature.selectedItem == "YES") {
                    "1"
                } else if (spinnerVeri_PatientSignature.selectedItem == "Select") {
                    ""
                } else {
                    "0"
                }
            )
            postData.put("IFNOTHENREASON", etVeri_PatientSignature.text.toString())
            postData.put("DURATIONOFPRESENT", etDurationPreIll.text.toString())
            postData.put(
                "BEINDISCOMFORT",
                if (spinnerVeri_PatientApperedDisco.selectedItem == "YES") {
                    "1"
                } else if (spinnerVeri_PatientApperedDisco.selectedItem == "Select") {
                    ""
                } else {
                    0
                }
            )
            postData.put(
                "PATIENTSCASERECORDS",
                if (spinnerVeri_PatientRecords.selectedItem == "YES") {
                    "1"
                } else if (spinnerVeri_PatientRecords.selectedItem == "Select") {
                    ""
                } else {
                    "0"
                }
            )
            postData.put(
                "ABLETODESCRIBEPATIENT",
                ""
            )  // spinnerVeri_PatientSymptoms  // spinnerVeri_PatientNoDrVisited
            postData.put(
                "DESCRIBESYMPTOMS",
                if (spinnerVeri_PatientAbleDisSymptoms.selectedItem == "YES") {
                    1
                } else if (spinnerVeri_PatientAbleDisSymptoms.selectedItem == "Select") {
                    ""
                } else {
                    0
                }
            )
            postData.put(
                "PATIENTRELATIVES",
                if (spinnerVeri_PatientRelaPres.selectedItem == "YES") {
                    "1"
                } else if (spinnerVeri_PatientRelaPres.selectedItem == "Select") {
                    ""
                } else {
                    "0"
                }
            )
            postData.put("TREATINGDOCOTR", "")
            postData.put(
                "Lineoftreatment",
                if (spinnerVeri_PatientLineTreatment.selectedItem == "Select Treatment Type") {
                    ""
                } else {
                    spinnerVeri_PatientLineTreatment.selectedItem
                }
            )

            postData.put("Temp", etTemp.text.toString())
            postData.put("BP", etBP.text.toString())
            postData.put("BloodSugar", etBloodSugar.text.toString())
            postData.put("TLCWBC", etTLCWBC.text.toString())
            postData.put("InvestigatorPhoneNo", etInvestigatorMob.text.toString())
            postData.put("FileNo", sharedPreferences.getStringValue("fileNo", ""))
            postData.put("flag", "question")
            Log.d("survey_Req", postData.toString())
            osw?.write("survey_Req  $postData\n".toByteArray())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val request =
            JsonObjectRequest(Request.Method.POST, url, postData, { response ->
                mProgressDialog?.dismiss()
                osw?.write("survey_Res  $response.toString()\n".toByteArray())
                Log.d("survey_Res", response.toString())
                val obj: JSONObject = response
                val status = obj.getString("response")
                if (status.equals("update")) {
                    finish()
                    Toast.makeText(this, "Survey Form upload successfully ", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, "Survey Form upload Failed ", Toast.LENGTH_SHORT).show()
                }

            }, { error ->
                mProgressDialog?.dismiss()
                Log.d("survey_Res", error.toString())
                Toast.makeText(this, "" + error.toString(), Toast.LENGTH_SHORT).show()
            }
            )
        requestQueue.add(request)
    }

    private fun findAll() {
        etHopNameAdd = findViewById(R.id.etHopNameAdd)
        etAddDrName = findViewById(R.id.etAddDrName)
        etNameAndAdd = findViewById(R.id.etNameAndAdd)
        etDrMob = findViewById(R.id.etDrMob)
        etAge = findViewById(R.id.etAge)
        etSex = findViewById(R.id.etSex)
        etRelationInsured = findViewById(R.id.etRelationInsured)
        etSumInsured = findViewById(R.id.etSumInsured)
        etPolicyNo = findViewById(R.id.etPolicyNo)
        etDateofAddmission = findViewById(R.id.etDateofAddmission)
        etDateofDis = findViewById(R.id.etDateofDis)
        etDateNAtureofAilment = findViewById(R.id.etDateNAtureofAilment)
        etRoomRent = findViewById(R.id.etRoomRent)
        etReqAmount = findViewById(R.id.etReqAmount)
        etAmoutByTpa = findViewById(R.id.etAmoutByTpa)

        etDateofAddmission.setOnClickListener {
            if (etDateofAddmission.text.isNullOrBlank()) {
                callDatePicker("DOA")
            }

        }
        etDateofDis.setOnClickListener {
            if (etDateofDis.text.isNullOrBlank()) {
                callDatePicker("DOE")
            }

        }
    }

    private fun callDatePicker(msg: String) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                if (msg.equals("DOA")) {
                    etDateofAddmission.setText("" + dayOfMonth + "/" + month + "/" + year)
                } else {

                    etDateofDis.setText("" + dayOfMonth + "/" + month + "/" + year)
                }

            },
            year,
            month,
            day
        )
        dpd.show()
    }

    private fun setData(responeData: String) {
        var jsonObject = JSONObject(responeData)
        var jsonArray = jsonObject.getJSONArray("response")
        if (jsonArray.length() > 0) {
            for (i in 0 until jsonArray.length()) {
                var data = jsonArray.getJSONObject(i)
                etHopNameAdd.setText(data.getString("npname") + "," + data.getString("CareAddr"))
                etAge.setText(data.getString("memage"))
                etSex.setText(data.getString("memsex"))
                etRelationInsured.setText(data.getString("relation"))
                etSumInsured.setText(data.getString("si"))
                etPolicyNo.setText(data.getString("polno"))
                etReqAmount.setText(data.getString("clmamt"))
                etAmoutByTpa.setText(data.getString("aamt"))
                etNameAndAdd.setText(
                    data.getString("memname").trim() + "," + data.getString("MEMADDRESS")
                )
                etDateofAddmission.setText(data.getString("admindate"))
                etDateofDis.setText(data.getString("dischargedate"))

            }
        } else {

        }

    }

    private fun setListenerAndData() {
        setAdapterIdAdapter(
            policy_yr,
            treatment_type,
            room_type,
            claim_type,
            forall,
            lineofTreatment
        )
    }

    private fun setAdapterIdAdapter(
        policy_yr: Array<String>,
        treatment_type: Array<String>,
        room_type: Array<String>,
        claim_type: Array<String>,
        forall: Array<String>,
        lineofTreatment: Array<String>
    ) {
        val policy_TypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, policy_yr)
        policy_TypeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1)

        val forall_TypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, forall)
        forall_TypeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1)

        val spinnerVeri_PatientLineTreatment_Adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, lineofTreatment)
        spinnerVeri_PatientLineTreatment_Adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)


        val claim_typeAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item,
            claim_type
        )
        claim_typeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1)

        val treatment_typeAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, treatment_type)
        treatment_typeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1)

        val room_typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, room_type)
        room_typeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1)

        spinnerFirstYrPolicy.adapter = policy_TypeAdapter
        spinnerFirstYrPolicy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                when (spinnerFirstYrPolicy.selectedItem) {
                    "YES" -> {
                    }

                    "No" -> {

                    }

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        spinnerTypeofClaim.adapter = claim_typeAdapter
        spinnerTypeofClaim.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                when (spinnerFirstYrPolicy.selectedItem) {


                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }



        spinnerModeofTratment.adapter = treatment_typeAdapter
        spinnerModeofTratment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                when (spinnerModeofTratment.selectedItem) {
                    "Conservative" -> {

                    }


                    "Surgical" -> {

                    }

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerRoomType.adapter = room_typeAdapter
        spinnerRoomType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                when (spinnerRoomType.selectedItem) {
                    "Select Room Type" -> roomtype = 0
                    "General/Economy" -> roomtype = 1
                    "Cabin" -> roomtype = 2
                    "4 Bedded Room" -> roomtype = 3
                    "3 Bedded Room" -> roomtype = 4
                    "Twin Sharing/Double Bedded" -> roomtype = 5
                    "Single/Private Room" -> roomtype = 6
                    "Semi Private Room" -> roomtype = 7
                    "AC Room" -> roomtype = 8
                    "Deluxe" -> roomtype = 9
                    "Super Deluxe" -> roomtype = 10
                    "Suite" -> roomtype = 11
                    "Intensive Care Unit (ICU)" -> roomtype = 12
                    "Critical Care Unit (CCU)" -> roomtype = 13
                    "Intensive Treatment Unit (ITU)" -> roomtype = 14
                    "Neonatal Intensive Care Unit (NICU)" -> roomtype = 15
                    "Pediatric Intensive Care Unit (PICU)" -> roomtype = 16
                    "Psychiatric Intensive Care Unit (PICU)" -> roomtype = 17
                    "Coronary Care Unit (CCU)" -> roomtype = 18
                    "Post-anesthesia Care Unit (PACU)" -> roomtype = 19
                    "High Dependency Unit (HDU)" -> roomtype = 20
                    "Surgical Intensive Care Unit (SICU)" -> roomtype = 21
                    "Day Care" -> roomtype = 22
                    "Nursury" -> roomtype = 23
                    "Not Admitted" -> roomtype = 24

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


/// ??????????????????????????????>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

        spinnerDoc_IDCard.adapter = forall_TypeAdapter
        spinnerDoc_IDCard.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                when (spinnerFirstYrPolicy.selectedItem) {
                    "YES" -> {

                    }

                    "No" -> {

                    }

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerDoc_PreAuth.adapter = forall_TypeAdapter
        spinnerDoc_PreAuth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                when (spinnerFirstYrPolicy.selectedItem) {
                    "YES" -> {
                    }


                    "No" -> {

                    }

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerDoc_IndoreCase.adapter = forall_TypeAdapter
        spinnerDoc_IndoreCase.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                when (spinnerFirstYrPolicy.selectedItem) {
                    "YES" -> {
                    }


                    "No" -> {

                    }

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerVeri_HospReg.adapter = forall_TypeAdapter
        spinnerVeri_HospReg.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                when (spinnerFirstYrPolicy.selectedItem) {
                    "YES" -> {
                    }


                    "No" -> {

                    }

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerVeri_susBack.adapter = forall_TypeAdapter
        spinnerVeri_susBack.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                when (spinnerFirstYrPolicy.selectedItem) {
                    "YES" -> {
                    }


                    "No" -> {

                    }

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        spinnerVeri_PatientonBed.adapter = forall_TypeAdapter
        spinnerVeri_PatientonBed.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    when (spinnerFirstYrPolicy.selectedItem) {
                        "YES" -> {
                        }


                        "No" -> {

                        }

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }


        spinnerVeri_PatientPhotoMatched.adapter = forall_TypeAdapter
        spinnerVeri_PatientPhotoMatched.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    when (spinnerFirstYrPolicy.selectedItem) {
                        "YES" -> {
                        }


                        "No" -> {

                        }

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }


        spinnerVeri_PatientAddDetails.adapter = forall_TypeAdapter
        spinnerVeri_PatientAddDetails.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    when (spinnerFirstYrPolicy.selectedItem) {
                        "YES" -> {
                        }


                        "No" -> {

                        }

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }


        spinnerVeri_PatientAgeMAtched.adapter = forall_TypeAdapter
        spinnerVeri_PatientAgeMAtched.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    when (spinnerFirstYrPolicy.selectedItem) {
                        "YES" -> {
                        }


                        "No" -> {

                        }

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        spinnerVeri_PatientSignature.adapter = forall_TypeAdapter
        spinnerVeri_PatientSignature.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    when (spinnerFirstYrPolicy.selectedItem) {
                        "YES" -> {
                        }


                        "No" -> {

                        }

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        spinnerVeri_PatientSignature.adapter = forall_TypeAdapter
        spinnerVeri_PatientSignature.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    when (spinnerFirstYrPolicy.selectedItem) {
                        "YES" -> {
                        }


                        "No" -> {

                        }

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        spinnerVeri_PatientApperedDisco.adapter = forall_TypeAdapter
        spinnerVeri_PatientApperedDisco.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    when (spinnerFirstYrPolicy.selectedItem) {
                        "YES" -> {
                        }


                        "No" -> {

                        }

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }


        spinnerVeri_PatientRecords.adapter = forall_TypeAdapter
        spinnerVeri_PatientRecords.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    when (spinnerFirstYrPolicy.selectedItem) {
                        "YES" -> {
                        }


                        "No" -> {

                        }

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }


        spinnerVeri_PatientSymptoms.adapter = forall_TypeAdapter
        spinnerVeri_PatientSymptoms.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    when (spinnerFirstYrPolicy.selectedItem) {
                        "YES" -> {
                        }


                        "No" -> {

                        }

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        spinnerVeri_PatientAbleDisSymptoms.adapter = forall_TypeAdapter
        spinnerVeri_PatientAbleDisSymptoms.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    when (spinnerFirstYrPolicy.selectedItem) {
                        "YES" -> {
                        }


                        "No" -> {

                        }

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        spinnerVeri_PatientRelaPres.adapter = forall_TypeAdapter
        spinnerVeri_PatientRelaPres.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    when (spinnerFirstYrPolicy.selectedItem) {
                        "YES" -> {
                        }


                        "No" -> {

                        }

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }


        spinnerVeri_PatientNoDrVisited.adapter = forall_TypeAdapter
        spinnerVeri_PatientNoDrVisited.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    when (spinnerFirstYrPolicy.selectedItem) {
                        "YES" -> {
                        }


                        "No" -> {

                        }

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }


// TODO Confirm it t
        spinnerVeri_PatientLineTreatment.adapter = spinnerVeri_PatientLineTreatment_Adapter
        spinnerVeri_PatientLineTreatment.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }


    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (llOne.getVisibility() == View.GONE) {
            tvTitle.text = "Investigation Form"
            llOne.visibility = View.VISIBLE
            llTwo.visibility = View.GONE
        } else {
            super.onBackPressed()

        }
    }
}
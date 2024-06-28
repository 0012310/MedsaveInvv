package med.Save.medsaveinv

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import med.Save.medsaveinv.DataBase.SharedPreferenceUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


@Suppress("DEPRECATION")
class DirectActivity_ : AppCompatActivity() {
    lateinit var requestQueue: RequestQueue
    private lateinit var sharedPreferences: SharedPreferenceUtils
    private var responeData = ""
    lateinit var imgBack: ImageView
    lateinit var imagearrow: ImageView
    lateinit var tvObservationData: EditText
    lateinit var llObvious: LinearLayout
    lateinit var layout: LinearLayout
    lateinit var layoutmulti: LinearLayout
    private var ubvStatus = arrayOf("Select Status", "Approved", "Reject")
    var imgData = ""

    lateinit var tvRetry: TextView
    private var latestPhoto: String = ""
    private var docType: String = ""
    lateinit var imageClicked: ImageView

    lateinit var btnMore: Button
    lateinit var btnMulti: Button
    lateinit var btnUploadMulti: Button
    lateinit var btnUploadAll: Button
    lateinit var spinnerObviousStatus: Spinner

    var mProgressDialog: ProgressDialog? = null
    var status = 0

    var photoFile: File? = null
    var mCurrentPhotoPath: String? = null
    val CAPTURE_IMAGE_REQUEST = 1

    private var osw: FileOutputStream? = null
    private var logFile: File? = null

    var position = 0
    var PICK_CODE = 100

    val images = ArrayList<Uri>()

    ///////
    private var currentImageIndex = 0

    private val pickImages =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImages: List<Uri>? = result.data?.getClipDataImages()

                // Process the selected images and set them on the ImageView
                selectedImages?.let {
                    setImagesOnImageView(it)
                }
            }
        }

    private fun Intent.getClipDataImages(): List<Uri>? {
        return clipData?.let { clipData ->
            (0 until clipData.itemCount).map { index ->
                clipData.getItemAt(index).uri
            }
        } ?: run {
            data?.let { listOf(it) }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_direct)
        logFile = File(getExternalFilesDir("Logs"), "Direct Activity.txt")

        try {
            osw = FileOutputStream(logFile)
            osw?.write("Log file created\n".toByteArray())
        } catch (io: IOException) {
            io.printStackTrace()
        }

        mProgressDialog = ProgressDialog(this)
        mProgressDialog?.setCancelable(false)
        mProgressDialog?.setTitle("Please wait...")


        imgBack = findViewById(R.id.imgBack)
        tvObservationData = findViewById(R.id.tvObservationData)
        btnUploadAll = findViewById(R.id.btnUploadAll)
        btnMore = findViewById(R.id.btnMore)
        btnMulti = findViewById(R.id.btnMulti)
        layout = findViewById(R.id.layout)
        layoutmulti = findViewById(R.id.layoutmulti)
        btnUploadMulti = findViewById(R.id.btnUploadMulti)
        requestQueue = Volley.newRequestQueue(this)
        sharedPreferences = SharedPreferenceUtils.getInstance(this)

        btnUploadAll.setOnClickListener {
            CallUpdateDialoge("Confirm! Do you want to Upload all files.")

        }


        btnUploadMulti.setOnClickListener {
            mProgressDialog?.show()

            /*     for (i in 0 until images.size) {
                     Handler().postDelayed(
                         {
                             val bitmap =
                                 MediaStore.Images.Media.getBitmap(contentResolver, images.get(i))
                             val stream = ByteArrayOutputStream()
                             bitmap.compress(Bitmap.CompressFormat.JPEG, 15, stream)
                             val bytes = stream.toByteArray()
                             latestPhoto = Base64.encodeToString(bytes, Base64.DEFAULT)
                             callApitoUlpadMultipleImages(i, latestPhoto)
                         }, 5000
                     )

                 }*/

            callFun(0)

        }

        btnMore.setOnClickListener {
            CallCustomDialog("MoreDocumnets")
            docType = "MoreDocumnets"
        }

        btnMulti.setOnClickListener {
//            images.clear()
//            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//            intent.addCategory(Intent.CATEGORY_OPENABLE)
//            intent.type = "image/*"
//            startActivityForResult(intent, PICK_CODE)


            openImagePicker()


        }


        imagearrow = findViewById(R.id.imagearrow)
        imagearrow.setOnClickListener {
            imageClicked.visibility = View.VISIBLE
            llObvious.visibility = View.VISIBLE
            if (llObvious.getVisibility() == View.VISIBLE) {
                if (tvObservationData.text.toString() == "") {
                    Toast.makeText(this, "Please Enter Observation Reason", Toast.LENGTH_SHORT)
                        .show()
                } else if (spinnerObviousStatus.selectedItem == "Select Status") {
                    Toast.makeText(this, "Please Select Observation Reason", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    callUploadImageApi("SelfImage")
                    // callAPI_SaveInc()
                }
            }

        }

        llObvious = findViewById(R.id.llObvious)
        imgBack.setOnClickListener {
            finish()
        }
        tvRetry = findViewById(R.id.tvRetry)
        tvRetry.setOnClickListener {
            CallCustomDialog("Plain")
            docType = "Plain"
        }

        CallCustomDialog("Plain")
        docType = "Plain"
        imageClicked = findViewById(R.id.imageClicked)
        spinnerObviousStatus = findViewById(R.id.spinnerObviousStatus)
        setAdapterIdAdapter(ubvStatus)

        /*   showToast(
               sharedPreferences.getStringValue("latitudeinv", "") +
                       sharedPreferences.getStringValue("longitudeinv", "")
           )*/

    }


    private fun setImagesOnImageView(images: List<Uri>) {

        if (currentImageIndex < images.size) {
            showToast(images.size.toString())
            val imageUrl = images[currentImageIndex]
            val imageViewmulti = ImageView(this)
            imageViewmulti.layoutParams = LinearLayout.LayoutParams(100, 100)
            imageViewmulti.setImageURI(imageUrl)
          //  images.add(imageUri)
            layoutmulti.visibility = View.VISIBLE
            layoutmulti = findViewById(R.id.layoutmulti)
            layoutmulti.addView(imageViewmulti)
            btnUploadMulti.visibility = View.VISIBLE

            currentImageIndex++

        }


    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }

        pickImages.launch(intent)
    }

    private fun callFun(i: Int) {
        btnUploadMulti.visibility = View.GONE
        val bitmap =
            MediaStore.Images.Media.getBitmap(contentResolver, images.get(i))
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 15, stream)
        val bytes = stream.toByteArray()
        latestPhoto = Base64.encodeToString(bytes, Base64.DEFAULT)
        callApitoUlpadMultipleImages(i, latestPhoto)
    }


    private fun callUploadImageApi(msg: String) {
        mProgressDialog?.show()
        //    val url = "https://medsave.in/InvAPI/api/investigation/uploadImages"
        val url = "https://medsave.in/MedApp/api/investigation/uploadImages"
        val postData = JSONObject()
        try {
            postData.put("ImageName", sharedPreferences.getStringValue("username", "") + msg)
            postData.put("ImageData", latestPhoto)
            postData.put("fileno", sharedPreferences.getStringValue("fileNo", ""))
            postData.put("userName", sharedPreferences.getStringValue("username", ""))
            Log.d("Image_Req_DP", postData.toString())
            osw?.write("Image_Req_DP  $postData\n".toByteArray())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonObjectRequest =
            JsonObjectRequest(Request.Method.POST, url, postData, { response ->
                mProgressDialog?.dismiss()
                osw?.write("Image_Res_DP  $response.toString()\n".toByteArray())
                Log.d("Image_Res_DP", response.toString())
                val obj: JSONObject = response
                val status = obj.getString("message")
                if (status.equals("Save")) {
                    btnUploadAll.visibility = View.VISIBLE
                    showDetails()
                    showToast("You have successfully upload the Image File.")
                } else {
                    showToast("Image File Uploading failed")
                }

            }, { error ->
                mProgressDialog?.dismiss()
                Log.d("Data", error.toString())
                Toast.makeText(this, "" + error.toString(), Toast.LENGTH_SHORT).show()

            }
            )

        jsonObjectRequest.setShouldCache(false)
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            20000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(jsonObjectRequest)

    }

    private fun callUploadAllData(msg: String) {
        mProgressDialog?.show()
        //  val url = "https://medsave.in/MedApp/api/investigation/SaveInvestigation"
        val url = "https://medsave.in/MedApp/api/investigation/SaveInvestigation"

        val postData = JSONObject()

        if (spinnerObviousStatus.selectedItem == "Approved") {
            status = 1
        } else {
            status = 2
        }
        try {
            postData.put("FileNo", sharedPreferences.getStringValue("fileNo", ""))
            postData.put("Observation", tvObservationData.text.toString())
            postData.put("userName", sharedPreferences.getStringValue("loginID", ""))
            postData.put("patientMbile", sharedPreferences.getStringValue("userMobileNo", ""))
            postData.put("patientemail", sharedPreferences.getStringValue("userEmailID", ""))
            postData.put("latitude", sharedPreferences.getStringValue("latitudeinv", ""))
            postData.put("longitude", sharedPreferences.getStringValue("longitudeinv", ""))
            postData.put("Status", status)
            Log.d("AllData_Req", postData.toString())
            osw?.write("AllData_Req  ${postData}\n".toByteArray())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonObjectRequest =
            JsonObjectRequest(Request.Method.POST, url, postData, { response ->
                mProgressDialog?.dismiss()
                Log.d("AllData_Res", response.toString())
                osw?.write("AllData_Res  ${response}\n".toByteArray())
                val obj: JSONObject = response
                val msg = obj.getString("message")
                val status = obj.getString("success")
                if (status.equals("1")) {
                    if (msg.equals("Record Already Exists.")) {
                        showToast("Record Already Exists.")
                        CallUpdateDialoge("Confirm! Do you want to continue Survey")
                    } else if (msg.equals("Save")) {
                        showToast(
                            sharedPreferences.getStringValue("latitudeinv", "") +
                                    sharedPreferences.getStringValue("longitudeinv", "")
                        )
                        showToast("Record Successfully Saved")
                        CallUpdateDialoge("Confirm! Do you want to continue Survey")
                    } else {
                        showToast("Save Api not Working")
                    }
                }

            }, { error ->
                mProgressDialog?.dismiss()
                Log.d("Data", error.toString())
                Toast.makeText(this, "" + error.toString(), Toast.LENGTH_SHORT).show()

            }
            )
        jsonObjectRequest.setShouldCache(false)
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            20000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(jsonObjectRequest)
    }

    private fun showDetails() {
        tvRetry.visibility = View.GONE
        imagearrow.visibility = View.GONE
        btnMore.visibility = View.VISIBLE
        btnMulti.visibility = View.VISIBLE
        btnUploadAll.visibility = View.VISIBLE
        layout.visibility = View.VISIBLE
        layoutmulti.visibility = View.VISIBLE
        tvObservationData.visibility = View.GONE
        spinnerObviousStatus.visibility = View.GONE
    }

    private fun CallUpdateDialoge(msg: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(msg)
        builder.setCancelable(false)
        builder.setPositiveButton("Yes") { dialog, which ->
            if (msg.equals("Confirm! Do you want to Upload all files.")) {
                callUploadAllData("")
            } else if (msg.equals("Confirm! Do you want to continue Survey")) {
                CallAPi()
            } else {
                dialog.dismiss()
            }

        }

        builder.setNegativeButton("No") { dialog, which ->
            if (msg.equals("Confirm! Do you want to continue Survey")) {
                finish()
            } else {
                dialog.dismiss()

            }

        }

        builder.show()
    }

    private fun CallAPi() {
        var data = "?fileno=${sharedPreferences.getStringValue("fileNo", "")}"
        Log.d("dd", data)

        val jsonObject = JSONObject()
        val jsonObjectRequest = object : JsonObjectRequest(Method.GET,
            "https://medsave.in/MedApp/api/investigation/getInvestigationSearch$data",
            jsonObject,
            {

                var jsonObject = JSONObject(it.toString())
                responeData = it.toString()

                var jsonArray = jsonObject.getJSONArray("response")
                if (jsonArray.length() > 0) {

                    for (i in 0 until jsonArray.length()) {
                        var data = jsonArray.getJSONObject(i)

                        var ImageUrl = data.getString("photopath")
                        sharedPreferences.setStringValue("ImageUrl", ImageUrl)
                        var memname = data.getString("memname")
                        var cardno = data.getString("cardno")

                        var intent = Intent(this, SurveyFormActivity::class.java)
                        intent.putExtra("Data", responeData)
                        startActivity(intent)
                        finish()

                        //   Picasso.get().load(ImageUrl).into(imageview)


                    }

                } else {

                    Toast.makeText(this, "No Data found of that File No", Toast.LENGTH_SHORT).show()

                }

            },
            { error ->
                Toast.makeText(this, "" + error.toString(), Toast.LENGTH_SHORT).show()

            }) {}

        jsonObjectRequest.setShouldCache(false)
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(jsonObjectRequest)
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, "$msg", Toast.LENGTH_SHORT).show()

    }

    private fun setAdapterIdAdapter(ubvStatus: Array<String>) {

        val inputTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ubvStatus)
        inputTypeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1)

        spinnerObviousStatus.adapter = inputTypeAdapter
        spinnerObviousStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                when (spinnerObviousStatus.selectedItem) {
                    "Approved" -> {
                    }

                    "Rejected" -> {

                    }

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


    }

    private fun CallCustomDialog(msg: String) {
        val dialog = BottomSheetDialog(this)
        dialog.setTitle("Select Image Source")
        dialog.setContentView(R.layout.custom_dialog)
        val tvGalley = dialog.findViewById<TextView>(R.id.tvGalley)
        val tvCamera = dialog.findViewById<TextView>(R.id.tvCamera)
        val tvCancle = dialog.findViewById<TextView>(R.id.tvCancle)
        tvGalley?.setOnClickListener {
            galeryIntent()
        }

        tvCamera?.setOnClickListener {
            //   cameraIntent()
            captureImage()
        }

        tvCancle?.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun captureImage() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                0
            )
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                try {
                    photoFile = createImageFile()
                    if (photoFile != null) {
                        val photoURI = FileProvider.getUriForFile(
                            this,
                            "com.example.medsaveinv.fileprovider",
                            photoFile!!
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)
                    }
                } catch (ex: Exception) {
                    displayMessage(baseContext, ex.message.toString())
                }

            } else {
                displayMessage(baseContext, "Null")
            }
        }

    }

    private fun galeryIntent() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_CHOOSE)

    }

    private fun cameraIntent() {
        /*  if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
              requestPermissions(
                  arrayOf(
                      Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
                  ), MY_CAMERA_PERMISSION_CODE
              )
          }
          else {
              val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
              if (cameraIntent.resolveActivity(packageManager) != null) {
                  startActivityForResult(cameraIntent, CAMERA_REQUEST)
              } else {
                  Toast.makeText(this, "No Camera Found", Toast.LENGTH_SHORT).show()
              }
          }*/


        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                0
            )
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                try {
                    photoFile = createImageFile()
                    if (photoFile != null) {
                        val photoURI = FileProvider.getUriForFile(
                            this,
                            "com.example.medsaveinv.fileprovider",
                            photoFile!!
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)
                    }
                } catch (ex: Exception) {
                    displayMessage(baseContext, ex.message.toString())
                }

            } else {
                displayMessage(baseContext, "Null")
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    private fun displayMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val IMAGE_CHOOSE = 101
        private const val MY_CAMERA_PERMISSION_CODE = 100
        private const val CAMERA_REQUEST = 1888
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //  cameraIntent()
                captureImage()
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    requestPermissions(
                        arrayOf(Manifest.permission.CAMERA), MY_CAMERA_PERMISSION_CODE
                    )
                } else {
                    showDialogForPermissionDetail(MY_CAMERA_PERMISSION_CODE)
                }
            }
        }
    }

    private fun showDialogForPermissionDetail(permissionCode: Int) {
        val builder = AlertDialog.Builder(this)
        if (permissionCode == MY_CAMERA_PERMISSION_CODE) {
            builder.setMessage("You have denied camera permission. Please allow to use camera feature.")
        } else {

        }
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


    private fun callApitoUploadMoreDocImageGallery(data: Uri?, latestPhoto: String) {
        mProgressDialog?.show()
        val url = "https://medsave.in/MedApp/api/investigation/uploadImages"
        val postData = JSONObject()
        try {
            postData.put(
                "ImageName", sharedPreferences.getStringValue("username", "") + "MoreDocGallery"
            )
            postData.put("ImageData", latestPhoto)
            postData.put("fileno", sharedPreferences.getStringValue("fileNo", ""))
            postData.put("userName", sharedPreferences.getStringValue("username", ""))
            Log.d("ImageData", postData.toString())
            osw?.write("Image_Req_Gallery  $postData\n".toByteArray())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonObjectRequest =
            JsonObjectRequest(Request.Method.POST, url, postData, { response ->
                mProgressDialog?.dismiss()
                osw?.write("Image_Res_Gallery  $response\n".toByteArray())
                Log.d("Data", response.toString())
                val obj: JSONObject = response
                val status = obj.getString("message")
                if (status.equals("Save")) {
                    val imageView = ImageView(this)
                    imageView.layoutParams = LinearLayout.LayoutParams(120, 120)
                    imageView.setImageURI(data)
                    layout = findViewById(R.id.layout)
                    layout.addView(imageView)
                } else {
                    showToast("Image File Uploading failed")
                }

            }, { error ->
                mProgressDialog?.dismiss()
                Log.d("Data", error.toString())
                Toast.makeText(this, "" + error.toString(), Toast.LENGTH_SHORT).show()

            }
            )
        jsonObjectRequest.setShouldCache(false)
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            20000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(jsonObjectRequest)


    }

    private fun callApitoUlpadMultipleImages(pos: Int, latestPhoto: String) {
        mProgressDialog?.show()
        val url = "https://medsave.in/MedApp/api/investigation/uploadImages"
        val postData = JSONObject()
        try {
            postData.put("Sequence", " " + pos)
            postData.put(
                "ImageName",
                sharedPreferences.getStringValue("username", "") + "MultipleImages"
            )
            postData.put("ImageData", latestPhoto)
            postData.put("fileno", sharedPreferences.getStringValue("fileNo", ""))
            postData.put("userName", sharedPreferences.getStringValue("username", ""))
            Log.d("ImageData", postData.toString())
            osw?.write("Image_Req_Multiple  $postData\n".toByteArray())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonObjectRequest =
            JsonObjectRequest(Request.Method.POST, url, postData, { response ->
                osw?.write("Image_Res_Gallery  $response\n".toByteArray())
                Log.d("Data", response.toString())
                val obj: JSONObject = response
                val status = obj.getString("message")
                if (status.equals("Save")) {
                    showToast("Image File Uploading Successfully")
                    val data = images.size
                    val poss = pos + 1
                    if (data == poss) {
                        //     mProgressDialog?.dismiss()
                        val imageViewmulti = ImageView(this)
                        btnUploadMulti.visibility = View.GONE
                        layoutmulti.removeAllViews()
                        for (i in 0 until images.size) {
                            layoutmulti.removeAllViews()
                            mProgressDialog?.dismiss()
                        }
                    } else {
                        callFun(poss)
                    }


                } else {
                    mProgressDialog?.dismiss()
                    showToast("Image File Uploading failed try again")
                }

            }, { error ->
                mProgressDialog?.dismiss()
                Log.d("Data", error.toString())
                Toast.makeText(this, "" + error.toString(), Toast.LENGTH_SHORT).show()

            }
            )
        jsonObjectRequest.setShouldCache(false)
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            20000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(jsonObjectRequest)


    }

    private fun callApitoUploadMoreDocImageClick(data: Bitmap?, latestPhoto: String) {
        mProgressDialog?.show()
        val url = "https://medsave.in/MedApp/api/investigation/uploadImages"
        val postData = JSONObject()
        try {
            postData.put(
                "ImageName",
                sharedPreferences.getStringValue("username", "") + "MoreDocClicked"
            )
            postData.put("ImageData", latestPhoto)
            postData.put("fileno", sharedPreferences.getStringValue("fileNo", ""))
            postData.put("userName", sharedPreferences.getStringValue("username", ""))
            Log.d("ImageData", postData.toString())
            osw?.write("Image_Req_Camera  $postData\n".toByteArray())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val request =
            JsonObjectRequest(Request.Method.POST, url, postData, { response ->
                mProgressDialog?.dismiss()
                Log.d("Data", response.toString())
                osw?.write("Image_Res_Camera  ${response}\n".toByteArray())
                val obj: JSONObject = response
                val status = obj.getString("message")
                if (status.equals("Save")) {
                    val imageView = ImageView(this)
                    imageView.layoutParams = LinearLayout.LayoutParams(120, 120)
                    imageView.setImageBitmap(data)
                    layout = findViewById(R.id.layout)
                    layout.addView(imageView)

                } else {
                    showToast("Image File Uploading failed")
                }

            }, { error ->
                mProgressDialog?.dismiss()
                Log.d("Data", error.toString())
                Toast.makeText(this, "" + error.toString(), Toast.LENGTH_SHORT).show()
            }
            )
        requestQueue.add(request)


    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val myBitmap = BitmapFactory.decodeFile(photoFile!!.absolutePath)
            imageClicked.setImageBitmap(myBitmap)
            if (docType == "MoreDocumnets") {
                val bytes = ByteArrayOutputStream()
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 15, bytes)
                val byteArr = bytes.toByteArray()
                latestPhoto = Base64.encodeToString(byteArr, Base64.DEFAULT)
                callApitoUploadMoreDocImageClick(myBitmap, latestPhoto)
            } else {
                val bytes = ByteArrayOutputStream()
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 15, bytes)
                val byteArr = bytes.toByteArray()
                latestPhoto = Base64.encodeToString(byteArr, Base64.DEFAULT)
                imagearrow.visibility = View.VISIBLE
            }


        } else if (requestCode == IMAGE_CHOOSE && resultCode == RESULT_OK) {
            if (data != null) {
                if (docType == "MoreDocumnets") {
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, data.data)
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 15, stream)
                        val bytes = stream.toByteArray()
                        latestPhoto = Base64.encodeToString(bytes, Base64.DEFAULT)
                        //      callDialog(latestPhoto)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    callApitoUploadMoreDocImageGallery(data.data, latestPhoto)
                } else {
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, data.data)
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 15, stream)

                        val bytes = stream.toByteArray()
                        latestPhoto = Base64.encodeToString(bytes, Base64.DEFAULT)
                        //                callDialog(latestPhoto)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    imageClicked.setImageURI(data.data)
                    imagearrow.visibility = View.VISIBLE
                }

            }
        } else if (requestCode == PICK_CODE && resultCode == RESULT_OK) {
            if (resultCode == Activity.RESULT_OK) {
                if (data?.clipData != null) {
                    btnUploadMulti.visibility = View.VISIBLE
                    val count = data.clipData?.itemCount!!
                    for (i in 0 until count) {
                        var imageUri: Uri = data.clipData?.getItemAt(i)!!.uri
                        val imageViewmulti = ImageView(this)
                        imageViewmulti.layoutParams = LinearLayout.LayoutParams(100, 100)
                        imageViewmulti.setImageURI(imageUri)
                        images.add(imageUri)
                        layoutmulti.visibility = View.VISIBLE
                        layoutmulti = findViewById(R.id.layoutmulti)
                        layoutmulti.addView(imageViewmulti)
                        btnUploadMulti.visibility = View.VISIBLE

                    }


                } else {
                    if (data?.data != null) {
                        var imageUri: Uri = data.data!!
                        val imageViewmulti = ImageView(this)
                        imageViewmulti.layoutParams = LinearLayout.LayoutParams(100, 100)
                        imageViewmulti.setImageURI(imageUri)
                        images.add(imageUri)
                        layoutmulti.visibility = View.VISIBLE
                        layoutmulti = findViewById(R.id.layoutmulti)
                        layoutmulti.addView(imageViewmulti)
                        btnUploadMulti.visibility = View.VISIBLE

                    } else {
                        showToast("Unable to fetch Images")
                    }
                }
            } else {
                showToast("Result Not OK")
            }
        }

    }
}


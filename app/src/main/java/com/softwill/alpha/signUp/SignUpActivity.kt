package com.softwill.alpha.signUp

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivitySignUpBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.signIn.SignInActivity
import com.softwill.alpha.signUp.model.FacultyModel
import com.softwill.alpha.signUp.model.InstituteModel
import com.softwill.alpha.signUp.model.StreamClassModel
import com.softwill.alpha.signUp.model.StreamModel
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Timer
import java.util.TimerTask


class SignUpActivity : AppCompatActivity() {

    var progressDialog: Dialog? = null


    private lateinit var binding: ActivitySignUpBinding
    private var mIsStudent: Boolean = true
    private var mInstituteId: Int = -1
    private var mFacultyId: Int = -1
    private var mStreamId: Int = -1
    private var mClassId: Int = -1
    private var instituteModel: InstituteModel? = null

    var mFacilitiesList: ArrayList<String> = ArrayList()
    var mStreamsList: ArrayList<String> = ArrayList()
    var mClassList: ArrayList<String> = ArrayList()

    private var mNumber: String? = null

    var adapter: ArrayAdapter<String> ?=null

      var countries = arrayOf(
          "India", "Australia", "West indies", "indonesia", "Indiana",
          "South Africa", "England", "Bangladesh", "Srilanka", "singapore"
      )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        val bundle: Bundle? = intent.extras
        mNumber = bundle?.getString("mNumber")

//        binding.llBottom.visibility = View.GONE

        setupBack()
        onClickListener()


    }

    private fun onClickListener() {

        binding.btnSubmit.setOnClickListener {

            binding.btnSubmit.isEnabled = false
            binding.btnSubmit.isClickable = false

            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()
            val username = binding.etUsername.text.toString().trim()


            if (mIsStudent) {

                if (firstName.isEmpty()) {
                    UtilsFunctions().showToast(this@SignUpActivity, "First name can't be empty")
                } else if (firstName.length < 3) {
                    UtilsFunctions().showToast(
                        this@SignUpActivity,
                        "First name must contains 3 letters"
                    )
                } else if (lastName.isEmpty()) {
                    UtilsFunctions().showToast(this@SignUpActivity, "Last name can't be empty")
                } else if (lastName.length < 3) {
                    UtilsFunctions().showToast(
                        this@SignUpActivity,
                        "Last name must contains 3 letters"
                    )
                }else if(username.isEmpty()){
                    UtilsFunctions().showToast(this@SignUpActivity, "Create username can't be empty")
                }
                else if (mInstituteId == -1) {
                    UtilsFunctions().showToast(
                        this@SignUpActivity,
                        "Please enter valid institute username"
                    )
                } else if (mFacultyId == -1) {
                    UtilsFunctions().showToast(this@SignUpActivity, "Please select faculty")
                } else if (mStreamId == -1) {
                    UtilsFunctions().showToast(this@SignUpActivity, "Please select stream")
                } else if (mClassId == -1) {
                    UtilsFunctions().showToast(this@SignUpActivity, "Please select class")
                } else {
                    apiStudentRegister(firstName, lastName, username)
                }

                binding.btnSubmit.isEnabled = false
                binding.btnSubmit.isClickable = false

            } else {
                if (firstName.isEmpty()) {
                    UtilsFunctions().showToast(this@SignUpActivity, "First name can't be empty")
                } else if (firstName.length < 3) {
                    UtilsFunctions().showToast(
                        this@SignUpActivity,
                        "First name must contains 3 letters"
                    )
                } else if (lastName.isEmpty()) {
                    UtilsFunctions().showToast(this@SignUpActivity, "Last name can't be empty")
                } else if (lastName.length < 3) {
                    UtilsFunctions().showToast(
                        this@SignUpActivity,
                        "Last name must contains 3 letters"
                    )
                } else if (mInstituteId == -1) {
                    UtilsFunctions().showToast(
                        this@SignUpActivity,
                        "Please enter valid institute username"
                    )
                } else {
                    apiTeacherRegister(firstName, lastName, username)
                }
                binding.btnSubmit.isEnabled = false
                binding.btnSubmit.isClickable = false
            }

        }

        binding.ivInfo.setOnClickListener {
            userNameBottomSheet()
        }


        binding.llStudent.setOnClickListener {
            binding.tvStudent.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.blue
                )
            )
//            binding.llStudent.setBackgroundResource(R.drawable.bg_rounded_3_selected)
            binding.tvTeacher.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.gray_color
                )
            )
//            binding.llTeacher.setBackgroundResource(R.drawable.bg_rounded_3)
            binding.viewInfo.setBackgroundColor(resources.getColor(R.color.blue))
            binding.viewAboutMe.setBackgroundColor(resources.getColor(R.color.faint_blue))


//            binding.llBottom.visibility = View.GONE

            mIsStudent = true

            binding.etFirstName.requestFocus()
            binding.etFirstName.setText("")
            binding.etLastName.setText("")
            binding.etUsername.setText("")
            binding.etInstituteUsername.setText("")
            binding.etFaculty.setText("")
            binding.etStream.setText("")
            binding.etClass.setText("")
//            binding.spinnerFaculty.selectedItem=0
//            binding.spinnerFaculty.selectedItem=0

        }

        binding.llTeacher.setOnClickListener {
            binding.tvTeacher.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.blue
                )
            )
//            binding.llTeacher.setBackgroundResource(R.drawable.bg_rounded_3_selected)
            binding.tvStudent.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.gray_color
                )
            )
//            binding.llStudent.setBackgroundResource(R.drawable.bg_rounded_3)

            binding.viewInfo.setBackgroundColor(resources.getColor(R.color.faint_blue))
            binding.viewAboutMe.setBackgroundColor(resources.getColor(R.color.blue))

            binding.llBottom.visibility = View.GONE

            mIsStudent = false

            binding.etFirstName.requestFocus()
            binding.etFirstName.setText("")
            binding.etLastName.setText("")
            binding.etUsername.setText("")
            binding.etInstituteUsername.setText("")
            binding.etFaculty.setText("")
            binding.etStream.setText("")
            binding.etClass.setText("")
        }

        binding.etInstituteUsername.addTextChangedListener(textWatcher)

        adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, countries)
        binding.etInstituteUsernae.threshold = 3
        binding.etInstituteUsernae.setAdapter(adapter)

//        binding.etInstituteUsername.addTextChangedListener(textWatcher)

//        binding.etUsername.addTextChangedListener(textWatcher)

        binding.etInstituteUsernae.setOnItemClickListener { parent, view, position, id ->
            val selectedText = parent.getItemAtPosition(position) as String
            // Do something with the selected text
            Toast.makeText(this,selectedText, Toast.LENGTH_LONG).show()
        }

        binding.etUsername.addTextChangedListener {
            textWatcher
        }

    }



    private val textWatcher = object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        private var timer: Timer = Timer()
        private val DELAY: Long = 5000 // Milliseconds

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
             progressDialog = UtilsFunctions().showCustomProgressDialog(this@SignUpActivity)
            timer.cancel()
            timer = Timer()
            timer.schedule(
                object : TimerTask() {
                    override fun run() {
                        // TODO: Do what you need here (refresh list).
                        // You will probably need to use
                        // runOnUiThread(Runnable action) for some
                        // specific actions (e.g., manipulating views).
                        apiCheckUserName(s.toString())
                    }
                },
                DELAY
            )


//            apiCheckInstitute(s.toString())

        }

    }

    private fun apiCheckUserName(userName: String) {
        val jsonObject = JsonObject().apply {
            addProperty("username", userName)
        }

        val call: Call<ResponseBody> = RetrofitClient.getInstance(this@SignUpActivity).myApi.api_CheckUserName(jsonObject)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    progressDialog!!.dismiss()
                    val responseJson = response.body()?.string()
                    if (!responseJson.isNullOrEmpty()) {
                        binding.tvValidUserName1.text = "Valid"
                        binding.tvValidUserName1.visibility = View.VISIBLE
                        binding.tvValidUserName1.setTextColor(ContextCompat.getColor(this@SignUpActivity, com.softwill.alpha.R.color.green));

//                        instituteModel = parseInstituteFromJson(responseJson)
//
//                        mFacilitiesList.clear()
//
//                        for (faculty in instituteModel?.faculties!!) {
//                            mFacilitiesList.add(faculty.name)
//                        }

//                        val textView = findViewById<View>(com.softwill.alpha.R.id.etInstituteUsername) as AutoCompleteTextView


//                        mInstituteId = instituteModel!!.instituteId
//
//                        if (mFacilitiesList.isNotEmpty()) {
//                            setSpinnerFaculties()
//                        }

                    }
                    /*else{
                        resetSpinners()
                    }*/
                } else {
                    binding.tvValidUserName1.visibility = View.VISIBLE
                    binding.tvValidUserName1.text = "Invalid"
                    binding.tvValidUserName1.setTextColor(ContextCompat.getColor(this@SignUpActivity, R.color.red))
                    UtilsFunctions().handleErrorResponse(response, this@SignUpActivity);
                    progressDialog!!.dismiss()

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                progressDialog!!.dismiss()

            }
        })
    }

    private fun userNameBottomSheet() {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_username, null)


        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_registration)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        supportActionBar!!.title = (Html.fromHtml("<font color=\"#060B13\">" + "Registration" + "</font>"));

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun apiCheckInstitute(s: String?) {
        val jsonObject = JsonObject().apply {
            addProperty("instituteUsername", s)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@SignUpActivity).myApi.api_CheckInstitute(jsonObject)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    if (!responseJson.isNullOrEmpty()) {
                        binding.tvValidUserName.text = "Valid"
                        binding.tvValidUserName.visibility = View.VISIBLE
                        binding.tvValidUserName.setTextColor(ContextCompat.getColor(this@SignUpActivity, com.softwill.alpha.R.color.green));

                        instituteModel = parseInstituteFromJson(responseJson)

                        mFacilitiesList.clear()

                        for (faculty in instituteModel?.faculties!!) {
                            mFacilitiesList.add(faculty.name)
                        }

//                        val textView = findViewById<View>(com.softwill.alpha.R.id.etInstituteUsername) as AutoCompleteTextView


                        mInstituteId = instituteModel!!.instituteId

                        if (mFacilitiesList.isNotEmpty()) {
                            setSpinnerFaculties()
                        }

                    }
                    /*else{
                        resetSpinners()
                    }*/
                } else {
                    binding.tvValidUserName.visibility = View.VISIBLE
                    binding.tvValidUserName.text = "Invalid"
                    binding.tvValidUserName.setTextColor(ContextCompat.getColor(this@SignUpActivity, R.color.red))
                    UtilsFunctions().handleErrorResponse(response, this@SignUpActivity);
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun resetSpinners() {
        mInstituteId = -1

        //Clear Faculty Spinner
        mFacilitiesList.clear()
        mFacultyId = -1;
        binding.etFaculty.text.clear()
        setSpinnerFaculties()


        //Clear Stream Spinner
        mStreamsList.clear()
        mStreamId = -1;
        binding.etStream.text.clear()
        setSpinnerStream(-1)

        //Clear Stream Spinner
        mClassList.clear()
        mClassId = -1;
        binding.etClass.text.clear()
        setSpinnerClass(-1, -1)
    }


    private fun setSpinnerFaculties() {
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item2, mFacilitiesList)
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        binding.spinnerFaculty.adapter = adapter
        binding.spinnerFaculty.setSelection(0)
        binding.spinnerFaculty.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, pos: Int, l: Long) {
                val value = adapterView.getItemAtPosition(pos).toString()
                binding.etFaculty.setText(binding.spinnerFaculty.selectedItem.toString())

                mFacultyId = instituteModel?.faculties?.get(pos)?.facultyId!!

                mStreamsList.clear()
                for (streams in instituteModel?.faculties?.get(pos)?.streams!!) {
                    mStreamsList.add(streams.streamName)
                }

                if (mStreamsList.isNotEmpty()) {
                    setSpinnerStream(pos)
                }

                System.out.println("mFacultyId : $mFacultyId")
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun setSpinnerStream(facultyPosition: Int) {
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item2, mStreamsList)
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        binding.spinnerStream.adapter = adapter
        binding.spinnerStream.setSelection(0)
        binding.spinnerStream.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                pos: Int,
                l: Long
            ) {
                val value = adapterView.getItemAtPosition(pos).toString()
                binding.etStream.setText(binding.spinnerStream.selectedItem.toString())

                if(facultyPosition != -1){
                    mStreamId = instituteModel?.faculties?.get(facultyPosition)?.streams?.get(pos)?.streamId!!;

                    mClassList.clear()
                    for (classes in instituteModel?.faculties?.get(facultyPosition)?.streams?.get(pos)?.streamClasses!!) {
                        mClassList.add(classes.className)
                    }
                    if (mClassList.isNotEmpty()) {
                        setSpinnerClass(facultyPosition, pos)
                    }

                    System.out.println("mStreamId : $mStreamId")
                }


            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun setSpinnerClass(facultyPos: Int, streamPos: Int) {
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item2, mClassList)
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        binding.spinnerClass.adapter = adapter
        binding.spinnerClass.setSelection(0)
        binding.spinnerClass.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                pos: Int,
                l: Long
            ) {
                val value = adapterView.getItemAtPosition(pos).toString()
                binding.etClass.setText(binding.spinnerClass.selectedItem.toString())


                if(facultyPos != -1 && streamPos != -1){
                    mClassId = instituteModel?.faculties?.get(facultyPos)?.streams?.get(streamPos)?.streamClasses?.get(pos)?.classId!!;

                    System.out.println("mClassId : $mClassId")
                }

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun parseInstituteFromJson(jsonString: String?): InstituteModel? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val instituteId = jsonObject.getInt("instituteId")

            val facultiesArray = jsonObject.getJSONArray("faculties")
            val faculties = mutableListOf<FacultyModel>()

            for (i in 0 until facultiesArray.length()) {
                val facultyObject = facultiesArray.getJSONObject(i)
                val facultyId = facultyObject.getInt("facultyId")
                val facultyName = facultyObject.getString("name")

                val streamsArray = facultyObject.getJSONArray("streams")
                val streams = mutableListOf<StreamModel>()

                for (j in 0 until streamsArray.length()) {
                    val streamObject = streamsArray.getJSONObject(j)
                    val streamId = streamObject.getInt("streamId")
                    val streamName = streamObject.getString("streamName")

                    val streamClassesArray = streamObject.getJSONArray("stream_classes")
                    val streamClasses = mutableListOf<StreamClassModel>()

                    for (k in 0 until streamClassesArray.length()) {
                        val streamClassObject = streamClassesArray.getJSONObject(k)
                        val classId = streamClassObject.getInt("classId")
                        val className = streamClassObject.getString("className")

                        val streamClass = StreamClassModel(classId, className)
                        streamClasses.add(streamClass)
                    }

                    val stream = StreamModel(streamId, streamClasses, streamName)
                    streams.add(stream)
                }

                val faculty = FacultyModel(facultyId, facultyName, streams)
                faculties.add(faculty)
            }

            InstituteModel(instituteId, faculties)
        } catch (e: JSONException) {
            e.printStackTrace()
            null
        }
    }


    private fun apiStudentRegister(firstName: String, lastName: String, userName: String) {
        val jsonObject = JsonObject().apply {
            addProperty("firstName", firstName)
            addProperty("lastName", lastName)
            addProperty("instituteId", mInstituteId)
            addProperty("facultyId", mFacultyId)
            addProperty("streamId", mStreamId)
            addProperty("classId", mClassId)
            addProperty("mobile", mNumber)
            addProperty("userName", userName)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@SignUpActivity).myApi.api_StudentRegister(jsonObject)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)
                    val message = responseObject.getString("message")

                    if (message == "Register successfully"){

                        val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                        startActivity(intent)
                        finish()

                    }

                    UtilsFunctions().showToast(this@SignUpActivity, message)

                } else {
                    UtilsFunctions().handleErrorResponse(response, this@SignUpActivity);
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun apiTeacherRegister(firstName: String, lastName: String, username: String) {
        val jsonObject = JsonObject().apply {
            addProperty("firstName", firstName)
            addProperty("lastName", lastName)
            addProperty("instituteId", mInstituteId)
            addProperty("mobile", mNumber)
            addProperty("facultyId", mFacultyId)
            addProperty("userName", username)
        }

        val call: Call<ResponseBody> = RetrofitClient.getInstance(this@SignUpActivity).myApi.api_TeacherRegister(jsonObject)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)
                    val message = responseObject.getString("message")

                    if (message == "Register successfully"){

                        val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
                    UtilsFunctions().showToast(this@SignUpActivity, message)

                } else {
                    UtilsFunctions().handleErrorResponse(response, this@SignUpActivity);
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }

        })

        binding.btnSubmit.isEnabled = false
        binding.btnSubmit.isClickable = false

    }


}
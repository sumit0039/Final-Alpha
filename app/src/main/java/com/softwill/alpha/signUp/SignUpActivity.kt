package com.softwill.alpha.signUp

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivitySignUpBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.signIn.SignInActivity
import com.softwill.alpha.signUp.model.FacultyModel
import com.softwill.alpha.signUp.model.GetInstitueListResponseItem
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

    private var timer: Timer = Timer()
    private val DELAY: Long = 3000 // Milliseconds

    private lateinit var binding: ActivitySignUpBinding
    private var mIsStudent: Boolean = true
    private var mInstituteId: Int = -1
    private var mFacultyId: Int = -1
    private var mStreamId: Int = -1
    private var mClassId: Int = -1
    private var instituteModel: InstituteModel? = null
    val retailerListName = ArrayList<String>()
    var mFacilitiesList: ArrayList<String> = ArrayList()
    var mStreamsList: ArrayList<String> = ArrayList()
    var mClassList: ArrayList<String> = ArrayList()

    private var mNumber: String? = null

//    var adapter: ArrayAdapter<GetInstitueListResponseItem> ?=null

    private val mGetInstituteList = java.util.ArrayList<GetInstitueListResponseItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        val bundle: Bundle? = intent.extras
        mNumber = bundle?.getString("mNumber")

//        binding.llBottom.visibility = View.GONE

        setupBack()
        onClickListener()
        apiCheckInstitute()


        binding.etInstituteListUsername.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                pos: Int,
                l: Long
            ) {

                mInstituteId = mGetInstituteList[pos].instituteId
                binding.etInstituteListUsername.setSelection(pos)
                binding.etInstituteUsername.setText(mGetInstituteList[pos].name)
                api_checkInstituteName(mGetInstituteList[pos].userName)


            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        }

    }

    private fun onClickListener() {

        /*binding.etInstituteUserne.setOnClickListener{
            setSpinnerInstitute(-1, -1,retailerListName)
        }*/



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

        binding.info.setOnClickListener {
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


            binding.llBottom.visibility = View.VISIBLE

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

//        binding.etInstituteUsername.addTextChangedListener(textWatcher)

        binding.etUsername.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text has been changed.
                val text =  s.toString()
                // Do something with the updated text
                if(text.toString().isNotEmpty())
                {
                    timer.cancel()
                    timer = Timer()
                    timer.schedule(
                        object : TimerTask() {
                            override fun run() {
                                // TODO: Do what you need here (refresh list).
                                // You will probably need to use
                                // runOnUiThread(Runnable action) for some
                                // specific actions (e.g., manipulating views).
                                apiCheckUserName(binding.etUsername.text.toString())
//                                progressDialog = UtilsFunctions().showCustomProgressDialog(this@SignUpActivity)
                            }
                        },
                        DELAY
                    )
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

    }

    private fun apiCheckUserName(userName: String) {

        val call: Call<ResponseBody> = RetrofitClient.getInstance(this@SignUpActivity).myApi.api_CheckUserName(userName)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
//                    progressDialog!!.dismiss()

                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)
                    val message = responseObject.getString("status")
//                    Toast.makeText(this@SignUpActivity,message,Toast.LENGTH_LONG).show()

                    if (!responseJson.isNullOrEmpty()) {
                        binding.tvValidUserName1.text = message
                        binding.tvValidUserName1.visibility = View.VISIBLE
                        binding.tvValidUserName1.setTextColor(ContextCompat.getColor(this@SignUpActivity, com.softwill.alpha.R.color.green));

                    }
                } else {
                    binding.tvValidUserName1.visibility = View.VISIBLE
                    binding.tvValidUserName1.text = "Invalid"
                    binding.tvValidUserName1.setTextColor(ContextCompat.getColor(this@SignUpActivity, R.color.red))
                    UtilsFunctions().handleErrorResponse(response, this@SignUpActivity);
//                    progressDialog!!.dismiss()

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
//                progressDialog!!.dismiss()

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


    private fun apiCheckInstitute() {

        val call: Call<ResponseBody> = RetrofitClient.getInstance(this@SignUpActivity).myApi.api_GetInstituteList()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    if (!responseJson.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<GetInstitueListResponseItem>>() {}.type
                        val mList: List<GetInstitueListResponseItem> = Gson().fromJson(responseJson, listType)
                        mGetInstituteList.clear()
                        mGetInstituteList.addAll(mList)

                        val item1 = GetInstitueListResponseItem(
                            avtarUrl = "",
                            instituteId = -1,
                            name = "Select Institute",
                            userName = ""
                        )
                        // Add item at the first position
                        mGetInstituteList.add(0, item1)

                        for (commonSpinner in mGetInstituteList) {

                        /*    val text = commonSpinner.name + "\n" + commonSpinner.userName
                            val spannableString = SpannableString(text)

                            // Find the position of newline character
                            val newlineIndex = text.indexOf("\n")

                            // Apply grey color to the text after newline character
                            spannableString.setSpan(
                                ForegroundColorSpan(Color.parseColor("#B70101")), // Use the hex color code for grey
                                newlineIndex + 1, // Start index after the newline character
                                text.length, // End index at the end of the string
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )

                            retailerListName.add(spannableString.toString())*/
                            retailerListName.add(commonSpinner.name+"\n"+(Html.fromHtml("<font color=\"#808080\">" + commonSpinner.userName + "</font>")))
                        }
                        setSpinnerInstitute(-1, -1,retailerListName)
                        resetSpinners()
                    }

                } else {
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
//        mFacilitiesList.add(0,"Select Department")

        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item2, mFacilitiesList)
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        binding.spinnerFaculty.adapter = adapter
        binding.spinnerFaculty.setSelection(-1)
        binding.spinnerFaculty.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, pos: Int, l: Long) {
                val value = adapterView.getItemAtPosition(pos).toString()
                binding.etFaculty.setText(binding.spinnerFaculty.selectedItem.toString())

                mFacultyId = instituteModel?.faculties?.get(pos)?.facultyId!!

                mStreamsList.clear()
                for (streams in instituteModel?.faculties?.get(pos)?.streams!!) {
//                    mStreamsList.add(0,"Select Branch")
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
//                        mClassList.add(0,"Select Class")
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

    private fun setSpinnerInstitute(
        facultyPos: Int,
        streamPos: Int,
        retailerListName: ArrayList<String>
    ) {
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item2, retailerListName)
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        binding.etInstituteListUsername.adapter = adapter
        binding.etInstituteListUsername.setTitle("Select Institute")
        binding.etInstituteListUsername.setPositiveButton("OK")

    }

    private fun api_checkInstituteName(name: String) {
        val jsonObject = JsonObject().apply {
            addProperty("instituteUsername", name)
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@SignUpActivity).myApi.api_CheckInstitute(jsonObject)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    if (!responseJson.isNullOrEmpty()) {
//                        binding.tvValidUserName.text = "Valid"
                        binding.tvValidUserName.visibility = View.VISIBLE
//                        binding.tvValidUserName.setTextColor(ContextCompat.getColor(this@SignUpActivity, com.softwill.alpha.R.color.green));

                        instituteModel = parseInstituteFromJson(responseJson)

                        mFacilitiesList.clear()

                        for (faculty in instituteModel?.faculties!!) {
//                            mFacilitiesList.add(0,"Select Department")
                            mFacilitiesList.add(faculty.name)
                        }

//                        val textView = findViewById<View>(com.softwill.alpha.R.id.etInstituteUsername) as AutoCompleteTextView


                        mInstituteId = instituteModel!!.instituteId

                        if (mFacilitiesList.isNotEmpty()) {
                            setSpinnerFaculties()
                        }

                    }
                    else{
                        resetSpinners()
                    }
                } else {
                    binding.tvValidUserName.visibility = View.VISIBLE
//                    binding.tvValidUserName.text = "Invalid"
//                    binding.tvValidUserName.setTextColor(ContextCompat.getColor(this@SignUpActivity, R.color.red))
                    UtilsFunctions().handleErrorResponse(response, this@SignUpActivity);
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
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
            val jsonObject = JSONObject(jsonString!!)
            val instituteId = jsonObject.getInt("instituteId")

            val facultiesArray = jsonObject.getJSONArray("faculties")
            val faculties = mutableListOf<FacultyModel>()
            val streams = mutableListOf<StreamModel>()
            val streamClasses = mutableListOf<StreamClassModel>()

            for (i in 0 until facultiesArray.length()) {
                val facultyObject = facultiesArray.getJSONObject(i)
                val facultyId = facultyObject.getInt("facultyId")
                val facultyName = facultyObject.getString("name")

                val streamsArray = facultyObject.getJSONArray("streams")


                for (j in 0 until streamsArray.length()) {
                    val streamObject = streamsArray.getJSONObject(j)
                    val streamId = streamObject.getInt("streamId")
                    val streamName = streamObject.getString("streamName")

                    val streamClassesArray = streamObject.getJSONArray("stream_classes")

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
            val item1 = StreamModel(-1, streamClasses, "Select Stream")
            streams.add(0,item1)

            val item = StreamClassModel(-1, "Select Class")
            streamClasses.add(0,item)

            val item2 = FacultyModel(-1, "Select Faculty", streams)
            faculties.add(0,item2)

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
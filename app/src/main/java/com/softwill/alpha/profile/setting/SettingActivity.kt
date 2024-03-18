package com.softwill.alpha.profile.setting

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivitySettingBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.otp.OTPActivity
import com.softwill.alpha.profile.tabActivity.PostModel
import com.softwill.alpha.signIn.SignInActivity
import com.softwill.alpha.signUp.model.FacultyModel
import com.softwill.alpha.signUp.model.GetInstitueListResponseItem
import com.softwill.alpha.signUp.model.InstituteModel
import com.softwill.alpha.signUp.model.StreamClassModel
import com.softwill.alpha.signUp.model.StreamModel
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import com.softwill.alpha.utils.YourPreference
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SettingActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySettingBinding
    var yourPreference: YourPreference? = null
    val mFacultiesModel = ArrayList<FacultiesListModel>()
    val mStreamsListModel = ArrayList<StreamsListModel>()
    val mClassesListModel = ArrayList<ClassesListModel>()

    private var mSelectedId: Int = -1
    var mSpinnerList: ArrayList<String> = ArrayList()
    lateinit var spinner: Spinner
    lateinit var etSelectedValue: EditText

    var IsStudentLogin: Boolean = true


    private val mGetInstituteList = java.util.ArrayList<GetInstitueListResponseItem>()

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

    lateinit var etInstituteUsername: EditText
    lateinit var etInstituteListUsername: SearchableSpinner
    lateinit var etFaculty: EditText
    lateinit var etStream: EditText
    lateinit var etClass: EditText
    lateinit var spinnerFaculty: Spinner
    lateinit var spinnerStream: Spinner
    lateinit var spinnerClass: Spinner
    lateinit var btnChange: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)
        yourPreference = YourPreference(this@SettingActivity)
        IsStudentLogin = yourPreference?.getData(Constant.IsStudentLogin).toBoolean()

        setupBack()

        if (!IsStudentLogin) {
            binding.rlClass.visibility = View.GONE
            binding.rlStream.visibility = View.GONE
            binding.viewClass.visibility = View.GONE
            binding.viewStream.visibility = View.GONE
        }


        binding.tvChangeRole.setOnClickListener(this)
        binding.tvChangeUsername.setOnClickListener(this)
        binding.tvChangeInstitute.setOnClickListener(this)
        binding.tvChangeFaculty.setOnClickListener(this)
        binding.tvChangeStream.setOnClickListener(this)
        binding.tvChangeClass.setOnClickListener(this)
        binding.tvChangeMobile.setOnClickListener(this)
        binding.rlExit.setOnClickListener(this)

        // Set up a click listener for the SwitchCompat
        binding.deleteCurrentUser.setOnCheckedChangeListener { _, isChecked ->
            // Handle the click event here
            if (isChecked) {
                // The switch is checked (ON)
                // Perform actions when the switch is turned on
                // For example, enable some feature or start a service
                apiDeleteCurrentUser()
                binding.deleteCurrentUser.isClickable=true
            } else {
                // The switch is unchecked (OFF)
                // Perform actions when the switch is turned off
                // For example, disable some feature or stop a service
                binding.deleteCurrentUser.isClickable=false
            }
        }

    }

    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_settings)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        supportActionBar!!.title = (Html.fromHtml("<font color=\"#060B13\">" + getString(R.string.title_settings) + "</font>"));

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tvChangeRole -> {
                ChangeRoleBottomSheet()
            }
            R.id.tvChangeUsername -> {
                changeUsernameBottomSheet()
            }
            R.id.tvChangeInstitute -> {

                println("InstituteUserName : "+yourPreference?.getData(Constant.instituteName))
                println("FacultyId : "+yourPreference?.getData(Constant.instituteFacultyId))
                println("StreamId : "+yourPreference?.getData(Constant.instituteStreamId))
                println("ClassId : "+yourPreference?.getData(Constant.instituteStreamClassId))

                apiCheckInstitute()
                changeInstituteBottomSheet()
            }
            R.id.tvChangeFaculty -> {
                changeFacultyBottomSheet("facultyId")
            }
            R.id.tvChangeStream -> {
                changeFacultyBottomSheet("streamId")
            }
            R.id.tvChangeClass -> {
                changeFacultyBottomSheet("classId")
            }
            R.id.tvChangeMobile -> {
                changeMobileBottomSheet()
            }
            R.id.rlExit -> {
                LogoutBottomSheet()
            }

        }
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

    @SuppressLint("SetTextI18n")
    private fun changeUsernameBottomSheet() {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_change_username, null)

        val etUsername = view.findViewById<EditText>(R.id.etUsername)
        val btnChange = view.findViewById<Button>(R.id.btnChange)
        val tvIsNotAvailable = view.findViewById<TextView>(R.id.tvIsNotAvailable)

        if (yourPreference?.getData(Constant.userName) != null && yourPreference?.getData(Constant.userName) != "null") {
            etUsername.setText("${yourPreference?.getData(Constant.userName)}")
        }

        etUsername.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                tvIsNotAvailable.visibility = View.INVISIBLE
            }
        })


        btnChange.setOnClickListener {

            if (etUsername.text.toString().trim().isEmpty()) {
                tvIsNotAvailable.text = "Username can't be empty"
                tvIsNotAvailable.visibility = View.VISIBLE
            } else if (etUsername.text.toString().trim().length < 3) {
                tvIsNotAvailable.text = "Username must contains 3 letters"
                tvIsNotAvailable.visibility = View.VISIBLE
            } else {
                apiUpdateProfileSettings("userName", etUsername.text.toString().trim(), dialog)
            }

        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()


    }

    private fun changeInstituteBottomSheet() {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_change_institute, null)


        etInstituteUsername = view.findViewById<EditText>(R.id.etInstituteUsername)
        etInstituteListUsername = view.findViewById<SearchableSpinner>(R.id.etInstituteListUsername)
        etFaculty = view.findViewById<EditText>(R.id.etFaculty)
        etStream = view.findViewById<EditText>(R.id.etStream)
        etClass = view.findViewById<EditText>(R.id.etClass)
        spinnerFaculty = view.findViewById<Spinner>(R.id.spinnerFaculty)
        spinnerStream = view.findViewById<Spinner>(R.id.spinnerStream)
        spinnerClass = view.findViewById<Spinner>(R.id.spinnerClass)

        btnChange = view.findViewById<AppCompatButton>(R.id.btnChange)

        btnChange.setOnClickListener {
            if(mInstituteId==-1){
                Toast.makeText(this,"Please select Institute", Toast.LENGTH_LONG).show()
            }else if(mFacultyId==-1){
                Toast.makeText(this,"Please select department", Toast.LENGTH_LONG).show()
            }else if(mStreamId==-1){
                Toast.makeText(this,"Please select branch", Toast.LENGTH_LONG).show()
            }else if(mClassId==-1){
                Toast.makeText(this,"Please select class", Toast.LENGTH_LONG).show()
            }else{
                apiUpdateProfileSettings("instituteId", yourPreference?.getData(Constant.userName).toString(), dialog)
            }
            dialog.dismiss()
        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()

        etInstituteListUsername.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                pos: Int,
                l: Long
            ) {


                mInstituteId = mGetInstituteList[pos].instituteId
                etInstituteListUsername.setSelection(pos)
                etInstituteUsername.setText(mGetInstituteList[pos].name)
                api_checkInstituteName(mGetInstituteList[pos].userName)

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        }


    }

    private fun apiCheckInstitute() {

        val call: Call<ResponseBody> = RetrofitClient.getInstance(this@SettingActivity).myApi.api_GetInstituteList()

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

                            retailerListName.add(commonSpinner.name+"\n"+(Html.fromHtml("<font color=\"#808080\">" + commonSpinner.userName + "</font>")))
                        }
                        setSpinnerInstitute(-1, -1,retailerListName)
                        resetSpinners()
                    }

                } else {
                    UtilsFunctions().handleErrorResponse(response, this@SettingActivity);
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
        etFaculty.text.clear()
        setSpinnerFaculties()


        //Clear Stream Spinner
        mStreamsList.clear()
        mStreamId = -1;
        etStream.text.clear()
        setSpinnerStream(-1)

        //Clear Stream Spinner
        mClassList.clear()
        mClassId = -1;
        etClass.text.clear()
        setSpinnerClass(-1, -1)
    }


    private fun setSpinnerFaculties() {

        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item2, mFacilitiesList)
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        spinnerFaculty.adapter = adapter

        if(yourPreference?.getData(Constant.instituteStreamClassId)!!.toInt()!=-1) {
            etFaculty.setText(yourPreference?.getData(Constant.facultyName))
            spinnerFaculty.setSelection(mFacilitiesList.indexOf(yourPreference?.getData(Constant.facultyName)))
        }else{
            spinnerFaculty.setSelection(0)
        }

        spinnerFaculty.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, pos: Int, l: Long) {
                val value = adapterView.getItemAtPosition(pos).toString()
                etFaculty.setText(spinnerFaculty.selectedItem.toString())

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
        spinnerStream.adapter = adapter
        if(yourPreference?.getData(Constant.instituteStreamId)!!.toInt()!=-1){

            etStream.setText(yourPreference?.getData(Constant.streamName))
            spinnerStream.setSelection(mStreamsList.indexOf(yourPreference?.getData(Constant.instituteStreamId)))
        }else {
            spinnerStream.setSelection(0)
        }

        spinnerStream.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                pos: Int,
                l: Long
            ) {

                val value = adapterView.getItemAtPosition(pos).toString()

                etStream.setText(spinnerStream.selectedItem.toString())

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

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        }
    }

    private fun setSpinnerInstitute(
        facultyPos: Int,
        streamPos: Int,
        retailerListName: ArrayList<String>
    ) {
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item2, retailerListName)
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        etInstituteListUsername.adapter = adapter
        etInstituteListUsername.setTitle("Select Institute")
        etInstituteListUsername.setPositiveButton("OK")
    }

    private fun api_checkInstituteName(name: String) {

        val jsonObject = JsonObject().apply {
            addProperty("instituteUsername", name)
        }

        val call: Call<ResponseBody> = RetrofitClient.getInstance(this@SettingActivity).myApi.api_CheckInstitute(jsonObject)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    if (!responseJson.isNullOrEmpty()) {

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
//                    tvValidUserName.visibility = View.VISIBLE
//                    tvValidUserName.text = "Invalid"
//                    tvValidUserName.setTextColor(ContextCompat.getColor(this@SettingActivity, R.color.red))
                    UtilsFunctions().handleErrorResponse(response, this@SettingActivity);
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
        spinnerClass.adapter = adapter
        if(yourPreference?.getData(Constant.instituteStreamClassId)!!.toInt()!=-1) {
            etClass.setText(yourPreference?.getData(Constant.className))
            spinnerClass.setSelection(mClassList.indexOf(yourPreference?.getData(Constant.className)))
        }else{
            spinnerClass.setSelection(0)
        }
        spinnerClass.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                pos: Int,
                l: Long
            ) {
                val value = adapterView.getItemAtPosition(pos).toString()
                etClass.setText(spinnerClass.selectedItem.toString())


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

    private fun changeFacultyBottomSheet(type: String) {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_change_faculty_stream_class, null)


        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvSubTitle = view.findViewById<TextView>(R.id.tvSubTitle)
        val btnChange = view.findViewById<Button>(R.id.btnChange)
        spinner = view.findViewById(R.id.spinner)
        etSelectedValue = view.findViewById(R.id.etSelectedValue)


        if (type == "facultyId") {
            tvTitle.text = getString(R.string.change_faculty)
            tvSubTitle.text = getString(R.string.select_faculty)
        } else if (type == "streamId") {
            tvTitle.text = getString(R.string.change_stream)
            tvSubTitle.text = getString(R.string.select_stream)
        } else if (type == "classId") {
            tvTitle.text = getString(R.string.change_class)
            tvSubTitle.text = getString(R.string.select_class)
        }


        apiFacultyStreamClassList(type)

        btnChange.setOnClickListener {
            apiUpdateProfileSettings(type, mSelectedId.toString(), dialog)
        }

        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()

    }


    private fun changeMobileBottomSheet() {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_change_mobile, null)


        val btnChange = view.findViewById<Button>(R.id.btnChange)
        val etNumber = view.findViewById<EditText>(R.id.etNumber)
        val tvError = view.findViewById<TextView>(R.id.tvError)


        btnChange.setOnClickListener {

            if (etNumber.text.toString().trim().isEmpty()) {
                tvError.setText("Enter mobile number")
            } else if (etNumber.text.toString().trim().length < 10) {
                tvError.setText("Enter valid mobile number")
            } else {
                apiChangeMobileNumber(etNumber.text.toString().trim(), dialog);

            }


        }
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()

    }


    private fun LogoutBottomSheet() {

        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_logout, null)


        val btnNo = view.findViewById<Button>(R.id.btnNo)
        val btnYes = view.findViewById<Button>(R.id.btnYes)

        btnYes.setOnClickListener {
            dialog.dismiss()
            apiLogout()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }


        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun ChangeRoleBottomSheet() {
        val dialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottomsheet_change_role, null)


        val btnChange = view.findViewById<Button>(R.id.btnChange)
        val tvChangeString = view.findViewById<TextView>(R.id.tvChangeString)


        val wordtoSpan: Spannable = SpannableString("Enter “Student” in the box")

        wordtoSpan.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.primary_color)), 6, 14,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tvChangeString.setText(wordtoSpan)



        btnChange.setOnClickListener {
            dialog.dismiss()
        }


        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()

    }

    private fun apiChangeMobileNumber(value: String, dialog: BottomSheetDialog) {

        val jsonObject = JsonObject().apply {
            addProperty("mobile", value)
        }

        val call: Call<ResponseBody> = RetrofitClient.getInstance(this@SettingActivity).myApi.api_ChangeMobileNumber(jsonObject)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        // Updated successfully
                        var message = responseObject.getString("message")
                        if (message == "Otp sent successfully") {

                            val intent = Intent(this@SettingActivity, OTPActivity::class.java)
                            intent.putExtra("mNumber", value)
                            intent.putExtra("mChangeMobile", true)
                            startActivity(intent)

                        } else {
                            UtilsFunctions().showToast(this@SettingActivity, message)
                        }

                        dialog.dismiss()
                    } else if (responseObject.has("error")) {
                        // Error occurred
                        UtilsFunctions().showToast(this@SettingActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@SettingActivity);
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun apiUpdateProfileSettings(type: String, value: String, dialog: BottomSheetDialog) {
        val jsonObject = JsonObject().apply {
            when (type) {
                "userName" -> addProperty("userName", value)
                "instituteId" -> {
                    addProperty("userName", value)
                    addProperty("instituteId", mInstituteId)
                    addProperty("facultyId", mFacultyId)
                    addProperty("streamId", mStreamId)
                    addProperty("classId", mClassId)
                }
            }
        }

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@SettingActivity).myApi.api_UpdateProfileSettings(
                jsonObject
            )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    dialog.dismiss()

                    if (responseObject.has("message")) {

//                        if (type == "userName") {
                        YourPreference.saveData(Constant.userName, value)
//                        } else if (type == "facultyId") {
                        YourPreference.saveData(Constant.instituteUsername, mInstituteId)
                        YourPreference.saveData(
                            Constant.instituteName,
                            etInstituteUsername.text.toString().trim()
                        )
                        YourPreference.saveData(Constant.instituteFacultyId, mFacultyId)
                        YourPreference.saveData(
                            Constant.facultyName,
                            etFaculty.text.toString().trim()
                        )
//                        } else if (type == "streamId") {
                        YourPreference.saveData(Constant.instituteStreamId, mStreamId)
                        YourPreference.saveData(
                            Constant.streamName,
                            etStream.text.toString().trim()
                        )
//                        } else if (type == "classId") {
                        YourPreference.saveData(Constant.instituteStreamClassId, mClassId)
                        YourPreference.saveData(
                            Constant.className,
                            etClass.text.toString().trim()
                        )
//                        }

                        UtilsFunctions().showToast(
                            this@SettingActivity,
                            responseObject.getString("message")
                        )

                    } else if (responseObject.has("error")) {
                        // Error occurred
                        UtilsFunctions().showToast(
                            this@SettingActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@SettingActivity);
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun apiLogout() {

        val call: Call<ResponseBody> = RetrofitClient.getInstance(this@SettingActivity).myApi.api_logout()

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        // Updated successfully

                        YourPreference.clearPreference(this@SettingActivity)
                        YourPreference.saveData(Constant.AuthToken, "")
                        YourPreference.saveData(Constant.IsLogin, false)



                        UtilsFunctions().showToast(
                            this@SettingActivity,
                            responseObject.getString("message")
                        )

                        val intent = Intent(applicationContext, SignInActivity::class.java)
                        startActivity(intent)
                        finishAffinity()


                    } else if (responseObject.has("error")) {
                        // Error occurred
                        UtilsFunctions().showToast(
                            this@SettingActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@SettingActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun apiFacultyStreamClassList(type: String) {

        val retrofit = RetrofitClient.getInstance(this@SettingActivity).myApi
        val call: Call<ResponseBody> = when (type) {
            "facultyId" -> retrofit.api_FacultiesList(
                Integer.parseInt(
                    yourPreference?.getData(
                        Constant.instituteId
                    ).toString()
                )
            )
            "streamId" -> retrofit.api_StreamsList(
                Integer.parseInt(
                    yourPreference?.getData(Constant.instituteFacultyId).toString()
                )
            )
            "classId" -> retrofit.api_ClassesList(
                Integer.parseInt(
                    yourPreference?.getData(Constant.instituteStreamId).toString()
                )
            )
            else -> throw IllegalArgumentException("Invalid type: $type")
        }



        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    if (!responseJson.isNullOrEmpty()) {
                        mFacultiesModel.clear()
                        mStreamsListModel.clear()
                        mSpinnerList.clear()


                        try {
                            val jsonArray = JSONArray(responseJson)

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                var id: Int
                                var name: String

                                when (type) {
                                    "facultyId" -> {
                                        id = jsonObject.getInt("facultyId")
                                        name = jsonObject.getString("name")
                                        mFacultiesModel.add(FacultiesListModel(id, name))
                                        mSpinnerList.add(name)
                                    }
                                    "streamId" -> {
                                        id = jsonObject.getInt("streamId")
                                        name = jsonObject.getString("streamName")
                                        mStreamsListModel.add(StreamsListModel(id, name))
                                        mSpinnerList.add(name)
                                    }
                                    "classId" -> {
                                        id = jsonObject.getInt("classId")
                                        name = jsonObject.getString("className")
                                        mClassesListModel.add(ClassesListModel(id, name))
                                        mSpinnerList.add(name)
                                    }
                                }

                            }

                            if (mSpinnerList.isNotEmpty()) {
                                setSpinner(type)
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }


                    } else {
                        mFacultiesModel.clear()
                        mStreamsListModel.clear()
                        mClassesListModel.clear()
                        mSelectedId = -1;
                        mSpinnerList.clear()
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@SettingActivity);
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun setSpinner(type: String) {
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item2, mSpinnerList)
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        spinner.adapter = adapter
        if (type == "facultyId") {
            spinner.setSelection(mSpinnerList.indexOf(yourPreference?.getData(Constant.facultyName)))
        } else if (type == "streamId") {
            spinner.setSelection(mSpinnerList.indexOf(yourPreference?.getData(Constant.streamName)))
        } else if (type == "classId") {
            spinner.setSelection(mSpinnerList.indexOf(yourPreference?.getData(Constant.className)))
        }
        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                pos: Int,
                l: Long
            ) {
                val value = adapterView.getItemAtPosition(pos).toString()
                etSelectedValue.setText(spinner.selectedItem.toString())
                if (type == "facultyId") {
                    mSelectedId = mFacultiesModel[pos].facultyId
                } else if (type == "streamId") {
                    mSelectedId = mStreamsListModel[pos].streamId
                } else if (type == "classId") {
                    mSelectedId = mClassesListModel[pos].classId
                }

                System.out.println("$type : $mSelectedId")
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }


    private fun apiDeleteCurrentUser() {
        val call: Call<ResponseBody> = RetrofitClient.getInstance(this@SettingActivity).myApi.api_DeleteCurrentUser()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)
                    if (responseObject.has("message")) {
                        // Updated successfully

                        YourPreference.clearPreference(this@SettingActivity)
                        YourPreference.saveData(Constant.AuthToken, "")
                        YourPreference.saveData(Constant.IsLogin, false)



                        UtilsFunctions().showToast(
                            this@SettingActivity,
                            responseObject.getString("message")
                        )

                        val intent = Intent(applicationContext, SignInActivity::class.java)
                        startActivity(intent)
                        finishAffinity()


                    } else if (responseObject.has("error")) {
                        // Error occurred
                        UtilsFunctions().showToast(
                            this@SettingActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@SettingActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

}
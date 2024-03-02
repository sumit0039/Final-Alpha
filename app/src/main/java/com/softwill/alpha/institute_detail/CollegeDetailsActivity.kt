package com.softwill.alpha.institute_detail

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityCollegeDetailsBinding
import com.softwill.alpha.institute_detail.adapter.*
import com.softwill.alpha.institute_detail.model.*
import com.softwill.alpha.institute_detail.model.facilities.FacilitiesResponseItem
import com.softwill.alpha.institute_detail.model.instituteDetailsModel.InstituteDetailsResponse
import com.softwill.alpha.institute_detail.model.placement.PlacementCompaniesResponseItem
import com.softwill.alpha.institute_detail.model.placement.PlacementStudentsResponseItem
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CollegeDetailsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityCollegeDetailsBinding
    private lateinit var mCollegeGalleryAdapter: CollegeGalleryAdapter
    private lateinit var mFacultyStreamAdapter: FacultyStreamAdapter
    private lateinit var mInstituteEntranceExamAdapter: InstituteEntranceExamAdapter
    private lateinit var mFacilitiesAdapter: FacilitiesAdapter
    private lateinit var mOurPartnerAdapter: OurPartnerAdapter
    private lateinit var mSelectedStudentsAdapter: SelectedStudentsAdapter

    private val mList2: ArrayList<EntranceExamResponseItem> = ArrayList()
    private val mList3: ArrayList<FacilitiesResponseItem> = ArrayList()
    private val facultyStreamList: ArrayList<FacultyStreamResponseItem> = ArrayList()
    private val partnerList: ArrayList<PlacementCompaniesResponseItem> = ArrayList()
    private val studentsList: ArrayList<PlacementStudentsResponseItem> = ArrayList()
    private val galleriesList: ArrayList<GalleriesResponseItem> = ArrayList()
    private var mMyInstitute: Boolean? = false
    private var mInstituteId: Int = 0
    private var instituteId: Int = 0
    private var profileImg: String? = null

    private val mInstituteFaculties: ArrayList<InstituteFacultyModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_college_details)
        val bundle: Bundle? = intent.extras
        mMyInstitute = bundle?.getBoolean("mMyInstitute")
        mInstituteId = bundle!!.getInt("mInstituteId")
        setupBack()



        binding.tvInfo.setOnClickListener(this)
        binding.tvFacultyStream.setOnClickListener(this)
        binding.tvEntranceExam.setOnClickListener(this)
        binding.tvFacilities.setOnClickListener(this)
        binding.tvPlacement.setOnClickListener(this)
        binding.tvGovernance.setOnClickListener(this)
        binding.tvGallery.setOnClickListener(this)
        binding.ivProfileImage.setOnClickListener(this)
        binding.tvConnect.setOnClickListener(this)

        apiInstituteDetail()
    }

    private fun apiInstituteDetail() {
        val retrofit = RetrofitClient.getInstance(this@CollegeDetailsActivity).myApi
        val call: Call<ResponseBody> = when (mMyInstitute) {
            true -> retrofit.api_MyInstitute()
            else -> retrofit.api_InstituteDetail(mInstituteId)
        }


        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val instituteDetail = Gson().fromJson(responseJson, InstituteDetailsResponse::class.java)
                    Log.e(TAG, "onInstituteStreamResponse: ${responseJson.toString()}")
                    instituteId = instituteDetail.institute.id
                    binding.tvCollegeName.text = instituteDetail.instituteName
                    binding.tvInfoEmail.text = instituteDetail.email
                    binding.tvInfoPhone.text = instituteDetail.mobile
                    binding.tvInfoAddress.text = instituteDetail.address
                    binding.tvState.text = instituteDetail.stateName
                    binding.tvConnection.text = instituteDetail.connections.toString()
                    binding.tvInfoWebsite.text = Html.fromHtml(instituteDetail.institute.website)
                    binding.tvInfoWebsite.setOnClickListener {
                        val uri = Uri.parse(instituteDetail.institute.website) // missing 'http://' will cause crashed
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)
                    }
                    binding.tvInfoAboutUs.text = instituteDetail.institute.aboutUs

                    binding.placementProgressBar.setProgress(instituteDetail.institute.placementRating.toInt())
                    binding.tvPlacementRate.text = instituteDetail.institute.placementRating.toString()
                    binding.staffProgressBar.setProgress(instituteDetail.institute.staffRating.toInt())
                    binding.tvStaffRate.text = instituteDetail.institute.staffRating.toString()
                    binding.teachingProgressBar.setProgress(instituteDetail.institute.teachingRating.toInt())
                    binding.tvTeachingRate.text = instituteDetail.institute.teachingRating.toString()
                    binding.enviromentProgressBar.setProgress(instituteDetail.institute.environmentRating.toInt())
                    binding.tvEnviromentRate.text = instituteDetail.institute.environmentRating.toString()
                    binding.downloadBrocher.setOnClickListener {
                        var download= getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                        var PdfUri = Uri.parse(instituteDetail.institute.brochurePath)
                        var getPdf = DownloadManager.Request(PdfUri)
                        getPdf.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        download.enqueue(getPdf)
                        Toast.makeText(this@CollegeDetailsActivity,"Download Started", Toast.LENGTH_LONG).show()
                    }


                    /*Governance*/

                    binding.aboutUsTv.text = instituteDetail.institute.aboutUs ?: ""
                    binding.policyTv.text = instituteDetail.institute.policy ?: ""
                    binding.dean.text = instituteDetail.institute.dean?.name ?: ""
                    binding.educationsTv.text = instituteDetail.institute.deanEducation ?: ""
                    val parts = instituteDetail.institute.assignDeanDate?.split("T")

                    binding.fromTv.text = parts?.get(0).toString() ?:""
                    binding.descDean.text = instituteDetail.institute.desc ?: ""
                    /*Governance*/

                    profileImg = instituteDetail.avtarUrl
                    Glide.with(this@CollegeDetailsActivity).load(instituteDetail.avtarUrl)
                        .placeholder(R.drawable.baseline_account_circle_24).into(binding.ivProfileImage)

                    if (instituteDetail.institute.instituteRating != null) {
                        binding.rating.rating = instituteDetail.institute.instituteRating.toFloat()
                        binding.institudeRating.rating = instituteDetail.institute.instituteRating.toFloat()
                        binding.tvInstitudeRate.text = instituteDetail.institute.instituteRating.toString() + " / " + "5.0"
                    }


//                    if (!instituteDetail.institute.institute_faculties.isNullOrEmpty()) {
//                        mInstituteFaculties.clear()
//                        mInstituteFaculties.addAll(instituteDetail.institute_faculties)
//                        mFacultyStreamAdapter.notifyDataSetChanged()
//                        binding.rvFacultyStream.visibility = View.VISIBLE
//                    } else {
//                        binding.rvFacultyStream.visibility = View.GONE
//                    }

                } else {
                    UtilsFunctions().handleErrorResponse(response, this@CollegeDetailsActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun apiEntranceExams(){
        val call: Call<ResponseBody> = RetrofitClient.getInstance(this@CollegeDetailsActivity).myApi
            .api_entranceExams()
        call.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    val responseJson = response.body()?.string()
                    val entranceExam = Gson().fromJson(responseJson, Array<EntranceExamResponseItem>::class.java)
                    mList2.clear()
                    mList2.addAll(entranceExam)

                    mInstituteEntranceExamAdapter = InstituteEntranceExamAdapter(mList2, this@CollegeDetailsActivity)
                    binding.rvEntranceExam.adapter = mInstituteEntranceExamAdapter
                    mInstituteEntranceExamAdapter.notifyDataSetChanged()

                }else {
                    UtilsFunctions().handleErrorResponse(response, this@CollegeDetailsActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

    private fun apiFacilities(){
        val call: Call<ResponseBody> = RetrofitClient.getInstance(this@CollegeDetailsActivity).myApi
            .api_facilities()
        call.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    val responseJson = response.body()?.string()
                    val facilitiesList = Gson().fromJson(responseJson, Array<FacilitiesResponseItem>::class.java)
                    mList3.clear()
                    mList3.addAll(facilitiesList)
                    mFacilitiesAdapter = FacilitiesAdapter(mList3, this@CollegeDetailsActivity)
                    binding.rvFacilities.adapter = mFacilitiesAdapter
                    mFacilitiesAdapter.notifyDataSetChanged()
                }else {
                    UtilsFunctions().handleErrorResponse(response, this@CollegeDetailsActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

    private fun apiOurPartners(){
        val call : Call<ResponseBody> = RetrofitClient.getInstance(this@CollegeDetailsActivity).myApi
            .api_Our_Partners()
        call.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    val responseJson = response.body()?.string()
                    val ourPartnersList = Gson().fromJson(responseJson,Array<PlacementCompaniesResponseItem>::class.java)
                    partnerList.clear()
                    partnerList.addAll(ourPartnersList)
                    mOurPartnerAdapter = OurPartnerAdapter(partnerList,this@CollegeDetailsActivity)
                    binding.rvOurPartner.layoutManager = GridLayoutManager(this@CollegeDetailsActivity,3)
                    binding.rvOurPartner.adapter = mOurPartnerAdapter
                    mOurPartnerAdapter.notifyDataSetChanged()
                }else {
                    UtilsFunctions().handleErrorResponse(response, this@CollegeDetailsActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

    private fun apiStudents(){
        val call : Call<ResponseBody> = RetrofitClient.getInstance(this@CollegeDetailsActivity).myApi
            .api_Students(instituteId)
        call.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    binding.llPlacementView.visibility = View.VISIBLE
                    val responseJson = response.body()?.string()
                    val selectedStudentList = Gson().fromJson(responseJson,Array<PlacementStudentsResponseItem>::class.java)
                    studentsList.addAll(selectedStudentList)
                    mSelectedStudentsAdapter = SelectedStudentsAdapter(studentsList,this@CollegeDetailsActivity)
                    binding.rvSelectedStudent.adapter = mSelectedStudentsAdapter
                    mSelectedStudentsAdapter.notifyDataSetChanged()
                }else {
                    UtilsFunctions().handleErrorResponse(response, this@CollegeDetailsActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

    private fun apiGalleries(){
        val call: Call<ResponseBody> = RetrofitClient.getInstance(this@CollegeDetailsActivity).myApi
            .api_Galleries()
        call.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    val responseJson = response.body()?.string()
                    val galleriesPhotoList = Gson().fromJson(responseJson, Array<GalleriesResponseItem>::class.java)
                    galleriesList.clear()
                    galleriesList.addAll(galleriesPhotoList)
                    mCollegeGalleryAdapter = CollegeGalleryAdapter(galleriesList,this@CollegeDetailsActivity)
                    binding.rvGallery.layoutManager = GridLayoutManager(this@CollegeDetailsActivity,3)
                    binding.rvGallery.adapter = mCollegeGalleryAdapter
                    mCollegeGalleryAdapter.notifyDataSetChanged()

                }else {
                    UtilsFunctions().handleErrorResponse(response, this@CollegeDetailsActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        if (mMyInstitute == true) {
            //binding.rating.visibility = View.INVISIBLE
            supportActionBar!!.title = (Html.fromHtml("<font color=\"#060B13\">" + resources.getString(R.string.title_my_institute) + "</font>"));
            binding.tvConnect.visibility = View.GONE
        } else {
            // binding.rating.visibility = View.VISIBLE
//            actionBar?.title = "Institute Detail"
            supportActionBar!!.title = (Html.fromHtml("<font color=\"#060B13\">" + "Institute Detail" + "</font>"));

            binding.tvConnect.visibility = View.VISIBLE
        }
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        actionBar?.setDisplayUseLogoEnabled(true)


    }

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (mMyInstitute == false) {
                menuInflater.inflate(com.softwill.alpha.R.menu.fav_menu2, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }*/


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            /*R.id.menu_fav -> {
                val intent = Intent(applicationContext, FavoriteCollegeActivity::class.java)
                startActivity(intent)
                return true
            }*/


        }
        return super.onOptionsItemSelected(item)
    }

    fun openImage() {

        val inflater = LayoutInflater.from(this)
        val popupview = inflater.inflate(R.layout.popup_profile_image, null, false)

        val image = popupview.findViewById<ImageView>(R.id.image)
        val ibBack = popupview.findViewById<ImageButton>(R.id.ibBack)

        image.setImageResource(R.drawable.icon_no_image)

        val builder = PopupWindow(
            popupview,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            true
        )
        builder.animationStyle = R.style.DialogAnimation
        builder.setBackgroundDrawable(getDrawable(R.drawable.bg_rounded_2))
        builder.showAtLocation(this.binding.root, Gravity.CENTER, 0, 0)

        Glide.with(this@CollegeDetailsActivity).load(profileImg)
            .placeholder(R.drawable.baseline_account_circle_24).into(image)

        image.setOnClickListener {
            builder.dismiss()
        }

        ibBack.setOnClickListener {
            builder.dismiss()
        }

    }

    override fun onClick(view: View?) {

        when (view?.id) {
            R.id.tvInfo -> {

                binding.tvInfo.setTextColor(
                    ContextCompat.getColor(applicationContext, R.color.blue))
                binding.tvInfo.setBackgroundResource(R.drawable.bg_rounded_3_selected)
                binding.tvFacultyStream.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvFacultyStream.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvEntranceExam.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvEntranceExam.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvFacilities.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvFacilities.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvPlacement.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvPlacement.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvGovernance.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvGovernance.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvGallery.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvGallery.setBackgroundResource(R.drawable.bg_rounded_3)

                binding.llInfoView.visibility = View.VISIBLE
                binding.llFacultyStreamView.visibility = View.GONE
                binding.llEntranceExamView.visibility = View.GONE
                binding.llFacilitiesView.visibility = View.GONE
                binding.llPlacementView.visibility = View.GONE
                binding.llGovernanceView.visibility = View.GONE
                binding.llGalleryView.visibility = View.GONE
            }

            R.id.tvFacultyStream -> {

                apiFacultyStream()

                binding.tvInfo.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvInfo.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvFacultyStream.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.blue
                    )
                )
                binding.tvFacultyStream.setBackgroundResource(R.drawable.bg_rounded_3_selected)
                binding.tvEntranceExam.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvEntranceExam.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvFacilities.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvFacilities.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvPlacement.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvPlacement.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvGovernance.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvGovernance.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvGallery.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvGallery.setBackgroundResource(R.drawable.bg_rounded_3)

                binding.llInfoView.visibility = View.GONE
                binding.llFacultyStreamView.visibility = View.VISIBLE
                binding.llEntranceExamView.visibility = View.GONE
                binding.llFacilitiesView.visibility = View.GONE
                binding.llPlacementView.visibility = View.GONE
                binding.llGovernanceView.visibility = View.GONE
                binding.llGalleryView.visibility = View.GONE


                /* val itemModel = ArrayList<FacultyStreamItem2Model>()
                 itemModel.add(FacultyStreamItem2Model("1. Mechanical"))
                 itemModel.add(FacultyStreamItem2Model("2. Electricals and telecommunication"))

                 val itemModel2 = ArrayList<FacultyStreamItem2Model>()

                 mList.clear()
                 mList.add(FacultyStreamItemModel("Medical", false, itemModel2))
                 mList.add(FacultyStreamItemModel("Engineering", false, itemModel))*/


            }

            R.id.tvEntranceExam -> {
                binding.tvInfo.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvInfo.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvFacultyStream.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvFacultyStream.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvEntranceExam.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.blue
                    )
                )
                binding.tvEntranceExam.setBackgroundResource(R.drawable.bg_rounded_3_selected)
                binding.tvFacilities.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvFacilities.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvPlacement.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvPlacement.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvGovernance.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvGovernance.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvGallery.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvGallery.setBackgroundResource(R.drawable.bg_rounded_3)

                binding.llInfoView.visibility = View.GONE
                binding.llFacultyStreamView.visibility = View.GONE
                binding.llEntranceExamView.visibility = View.VISIBLE
                binding.llFacilitiesView.visibility = View.GONE
                binding.llPlacementView.visibility = View.GONE
                binding.llGovernanceView.visibility = View.GONE
                binding.llGalleryView.visibility = View.GONE

                apiEntranceExams()


            }

            R.id.tvFacilities -> {
                binding.tvInfo.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvInfo.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvFacultyStream.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvFacultyStream.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvEntranceExam.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvEntranceExam.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvFacilities.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.blue
                    )
                )
                binding.tvFacilities.setBackgroundResource(R.drawable.bg_rounded_3_selected)
                binding.tvPlacement.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvPlacement.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvGovernance.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvGovernance.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvGallery.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvGallery.setBackgroundResource(R.drawable.bg_rounded_3)


                binding.llInfoView.visibility = View.GONE
                binding.llFacultyStreamView.visibility = View.GONE
                binding.llEntranceExamView.visibility = View.GONE
                binding.llFacilitiesView.visibility = View.VISIBLE
                binding.llPlacementView.visibility = View.GONE
                binding.llGovernanceView.visibility = View.GONE
                binding.llGalleryView.visibility = View.GONE

                apiFacilities()


            }

            R.id.tvPlacement -> {
                binding.tvInfo.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvInfo.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvFacultyStream.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvFacultyStream.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvEntranceExam.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvEntranceExam.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvFacilities.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvFacilities.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvPlacement.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.blue
                    )
                )
                binding.tvPlacement.setBackgroundResource(R.drawable.bg_rounded_3_selected)
                binding.tvGovernance.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvGovernance.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvGallery.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvGallery.setBackgroundResource(R.drawable.bg_rounded_3)


                binding.llInfoView.visibility = View.GONE
                binding.llFacultyStreamView.visibility = View.GONE
                binding.llEntranceExamView.visibility = View.GONE
                binding.llFacilitiesView.visibility = View.GONE
                binding.llGovernanceView.visibility = View.GONE
                binding.llGalleryView.visibility = View.GONE


                apiOurPartners()
                apiStudents()



            }

            R.id.tvGovernance -> {
                binding.tvInfo.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvInfo.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvFacultyStream.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvFacultyStream.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvEntranceExam.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvEntranceExam.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvFacilities.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvFacilities.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvPlacement.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvPlacement.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvGovernance.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.blue
                    )
                )
                binding.tvGovernance.setBackgroundResource(R.drawable.bg_rounded_3_selected)
                binding.tvGallery.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvGallery.setBackgroundResource(R.drawable.bg_rounded_3)

                binding.llInfoView.visibility = View.GONE
                binding.llFacultyStreamView.visibility = View.GONE
                binding.llEntranceExamView.visibility = View.GONE
                binding.llFacilitiesView.visibility = View.GONE
                binding.llPlacementView.visibility = View.GONE
                binding.llGovernanceView.visibility = View.VISIBLE
                binding.llGalleryView.visibility = View.GONE
            }

            R.id.tvGallery -> {
                binding.tvInfo.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvInfo.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvFacultyStream.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvFacultyStream.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvEntranceExam.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvEntranceExam.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvFacilities.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvFacilities.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvPlacement.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvPlacement.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvGovernance.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.black
                    )
                )
                binding.tvGovernance.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvGallery.setTextColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.blue
                    )
                )
                binding.tvGallery.setBackgroundResource(R.drawable.bg_rounded_3_selected)


                binding.llInfoView.visibility = View.GONE
                binding.llFacultyStreamView.visibility = View.GONE
                binding.llEntranceExamView.visibility = View.GONE
                binding.llFacilitiesView.visibility = View.GONE
                binding.llPlacementView.visibility = View.GONE
                binding.llGovernanceView.visibility = View.GONE
                binding.llGalleryView.visibility = View.VISIBLE
                apiGalleries()

            }

            R.id.ivProfileImage -> {
                openImage()
            }
            R.id.tvConnect -> {
                binding.tvConnect.visibility = View.GONE
            }

        }


    }

    private fun apiFacultyStream() {

        val call : Call<ResponseBody> = RetrofitClient.getInstance(this@CollegeDetailsActivity).myApi
            .api_InstituteFees()
        call.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) =
                if (response.isSuccessful){
                    val responseJson = response.body()?.string()
                    val facultyStreamResponseItem = Gson().fromJson(responseJson,Array<FacultyStreamResponseItem>::class.java)

                    if (facultyStreamResponseItem.isNotEmpty()) {

                        // Group data by "instituteStreamId"
                        val groupedData = facultyStreamResponseItem.groupBy { it.instituteStreamId }
                        // Print the grouped data
                        groupedData.forEach { (instituteStreamId, streamDataList) ->
                            println("Institute Stream ID: $instituteStreamId")
//                            println("FilterFacultyStreamData : $streamDataList")
                            streamDataList.forEach { data ->
                                println("FilterFacultyStreamData : $data")
                                println("   Class Name: ${data.stream}, Fees: ${data.fees.joinToString { "${it.caste}: ${it.fees}" }}")
                            }
                        }

                        facultyStreamList.clear()
                        mFacultyStreamAdapter = FacultyStreamAdapter(facultyStreamList,groupedData, this@CollegeDetailsActivity)
                        binding.rvFacultyStream.adapter = mFacultyStreamAdapter
                        mFacultyStreamAdapter.notifyDataSetChanged()
                        facultyStreamList.addAll(facultyStreamResponseItem)
//                        mFacultyStreamAdapter.notifyDataSetChanged()
                        binding.llFacultyStreamView.visibility = View.VISIBLE
                    } else {
                        binding.llFacultyStreamView.visibility = View.GONE
                    }
                }else {
                    UtilsFunctions().handleErrorResponse(response, this@CollegeDetailsActivity)
                }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }

        })

    }

}
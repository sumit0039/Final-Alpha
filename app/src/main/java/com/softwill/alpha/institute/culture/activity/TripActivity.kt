package com.softwill.alpha.institute.culture.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.institute.culture.adapter.TripsAdapter
import com.softwill.alpha.institute.culture.model.CultureTripModel
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TripActivity : AppCompatActivity(), TripsAdapter.CallbackInterface {


    private lateinit var binding: com.softwill.alpha.databinding.ActivityTripBinding
    var mTripsAdapter: TripsAdapter? = null
    private var mDelayHandler: Handler? = null


    val mCultureTripModel = java.util.ArrayList<CultureTripModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            com.softwill.alpha.R.layout.activity_trip
        )



        setupBack()
        setupSwipeListener()
        setupAdapter()
        apiInstituteTrips()
    }

    private fun setupAdapter() {


        mTripsAdapter = TripsAdapter(applicationContext, this,mCultureTripModel)
        binding.rvTrips.adapter = mTripsAdapter
        mTripsAdapter!!.notifyDataSetChanged()


    }

    private fun setupSwipeListener() {
        mDelayHandler = Handler()
        binding.swiperefresh.setColorSchemeColors(resources.getColor(R.color.blue))
        binding.swiperefresh.setOnRefreshListener {
            mDelayHandler!!.postDelayed(mRunnable, Constant.SWIPE_DELAY)
        }


    }

    private val mRunnable: Runnable = Runnable {
        binding.swiperefresh.isRefreshing = false
        apiInstituteTrips()

    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_trips)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        supportActionBar!!.title = (Html.fromHtml("<font color=\"#060B13\">" + resources.getString(R.string.title_trips) + "</font>"));

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

    private fun apiInstituteTrips() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@TripActivity).myApi.api_InstituteTrips()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<CultureTripModel>>() {}.type
                        val mList: List<CultureTripModel> = Gson().fromJson(responseBody, listType)


                        // Update the mTransportTeamMember list with the new data
                        mCultureTripModel.clear()
                        mCultureTripModel.addAll(mList)


                        if (mCultureTripModel.isNotEmpty()) {
                            mTripsAdapter?.notifyDataSetChanged()
                            binding.rvTrips.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                        } else {
                            binding.rvTrips.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.rvTrips.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@TripActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    fun openImage(picUrl: String) {

        var inflater = LayoutInflater.from(this)
        var popupview = inflater.inflate(R.layout.popup_image, null, false)

        var image = popupview.findViewById<ImageView>(R.id.image)
        var ivClose = popupview.findViewById<ImageView>(R.id.ivClose)


        Glide.with(this@TripActivity).load(picUrl).placeholder(R.drawable.icon_no_image)
            .into(image)


        var builder = PopupWindow(
            popupview,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            true
        )
        builder.animationStyle = R.style.DialogAnimation
        builder.setBackgroundDrawable(getDrawable(R.drawable.bg_rounded_2))
        builder.showAtLocation(this.binding.root, Gravity.CENTER, 0, 0)

        image.setOnClickListener {
            builder.dismiss()
        }

    }

    override fun onImageCallback(position: Int, picUrl: String) {
        openImage(picUrl)
    }

}
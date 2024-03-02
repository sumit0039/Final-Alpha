package com.softwill.alpha.institute.culture.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.institute.culture.adapter.TeamCultureAdapter
import com.softwill.alpha.institute.culture.model.CultureTeamMember
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.Constant
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeamCultureActivity : AppCompatActivity() {

    private lateinit var binding: com.softwill.alpha.databinding.ActivityTeamCultureBinding
    private var mDelayHandler: Handler? = null
    var mTeamCultureAdapter: TeamCultureAdapter? = null

    val mCultureTeamMember = java.util.ArrayList<CultureTeamMember>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            com.softwill.alpha.R.layout.activity_team_culture
        )


        setupBack()
        setupSwipeListener()
        setupAdapter()
        apiCultureTeamMember()
    }

    private fun setupAdapter() {


        mTeamCultureAdapter = TeamCultureAdapter(this@TeamCultureActivity, mCultureTeamMember)
        binding.rvTeam.adapter = mTeamCultureAdapter
        mTeamCultureAdapter!!.notifyDataSetChanged()


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
        apiCultureTeamMember()

    }

    private fun apiCultureTeamMember() {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@TeamCultureActivity).myApi.api_CultureTeamMember()

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<CultureTeamMember>>() {}.type
                        val mList: List<CultureTeamMember> = Gson().fromJson(responseBody, listType)

                        // Update the mTransportTeamMember list with the new data
                        mCultureTeamMember.clear()
                        mCultureTeamMember.addAll(mList)


                        if (mCultureTeamMember.isNotEmpty()) {
                            mTeamCultureAdapter?.notifyDataSetChanged()
                            binding.rvTeam.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE
                        } else {
                            binding.rvTeam.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.rvTeam.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@TeamCultureActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_team_member)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        supportActionBar!!.title = (Html.fromHtml("<font color=\"#060B13\">" + resources.getString(R.string.title_team_member) + "</font>"));

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
}
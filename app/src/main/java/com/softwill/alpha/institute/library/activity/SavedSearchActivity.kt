package com.softwill.alpha.institute.library.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivitySavedSearchBinding
import com.softwill.alpha.institute.library.Model.SavedBookCategory
import com.softwill.alpha.institute.library.adapter.SavedBookCategoryAdapter
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SavedSearchActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySavedSearchBinding

    var mSavedBookCategoryAdapter: SavedBookCategoryAdapter? = null

    private val mSavedBookCategory: ArrayList<SavedBookCategory> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.softwill.alpha.R.layout.activity_saved_search)

        supportActionBar!!.hide();

        setupBack()
        setupAdapter()

        //API CALL
        apiSavedBook("")
        binding.searchView.requestFocus()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.length >= 3) {
                    apiSavedBook(newText)
                } else {
                    apiSavedBook("")
                }
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

        })

    }

    private fun setupAdapter() {
        mSavedBookCategoryAdapter = SavedBookCategoryAdapter(this@SavedSearchActivity, mSavedBookCategory)
        binding.rvSavedCategory.adapter = mSavedBookCategoryAdapter
        mSavedBookCategoryAdapter!!.notifyDataSetChanged()



    }

    private fun apiSavedBook(searchText: String) {
        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@SavedSearchActivity).myApi.api_SavedBook(searchText)

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    if (!responseBody.isNullOrEmpty()) {
                        val listType = object : TypeToken<List<SavedBookCategory>>() {}.type
                        val mList: List<SavedBookCategory> = Gson().fromJson(responseBody, listType)

                        mSavedBookCategory.clear()
                        mSavedBookCategory.addAll(mList)


                        if (mSavedBookCategory.isNotEmpty()) {
                            mSavedBookCategoryAdapter?.notifyDataSetChanged()
                            binding.rvSavedCategory.visibility = View.VISIBLE
                            binding.tvNoData.visibility = View.GONE

                        } else {
                            binding.rvSavedCategory.visibility = View.GONE
                            binding.tvNoData.visibility = View.VISIBLE
                        }
                    }
                } else {
                    binding.rvSavedCategory.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, this@SavedSearchActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun setupBack() {

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        /*
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_saved)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)*/
    }


}
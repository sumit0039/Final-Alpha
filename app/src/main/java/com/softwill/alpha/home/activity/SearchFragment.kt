package com.softwill.alpha.home.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.softwill.alpha.R
import com.softwill.alpha.databinding.FragmentHomeBinding
import com.softwill.alpha.databinding.FragmentSearchBinding
import com.softwill.alpha.home.adapter.SearchAdapter
import com.softwill.alpha.home.model.SearchResponse
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentSearchBinding
    var mSearchAdapter: SearchAdapter? = null
    var mSearchType: String = "All"
    private val searchResponse: ArrayList<SearchResponse> = ArrayList()
    private val filterResponse: ArrayList<SearchResponse> = ArrayList()
    private var mSearchText: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.setContentView(requireActivity(), com.softwill.alpha.R.layout.fragment_search)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvAll.setOnClickListener(this@SearchFragment)
        binding.tvStudent.setOnClickListener(this)
        binding.tvInstitute.setOnClickListener(this)

        mSearchAdapter = SearchAdapter(searchResponse, requireContext())
        binding.rvAll.adapter = mSearchAdapter
        mSearchAdapter!!.notifyDataSetChanged()

         // Locate the EditText in listview_main.xml
       binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

           override fun onQueryTextChange(newText: String): Boolean {

               if (newText.length >= 3) {
                   mSearchText = newText
                   apiSearch()
               } else {
                   searchResponse.clear()
                   binding.rvAll.visibility = View.GONE
                   binding.ivNoData.visibility = View.VISIBLE
               }
               return false
           }

           override fun onQueryTextSubmit(query: String): Boolean {
               return false
           }

       })

    }

    private fun apiSearch() {

        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(requireContext()).myApi.api_Search(
                mSearchType,
                mSearchText
            )

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    if (!responseJson.isNullOrEmpty()) {
                        searchResponse.clear()
                        searchResponse.addAll(
                            Gson().fromJson(responseJson, Array<SearchResponse>::class.java)
                                .toList()
                        )

                        setAdapter(mSearchType)



                    } else {
                        binding.rvAll.visibility = View.GONE
                        binding.ivNoData.visibility = View.VISIBLE
                    }
                } else {
                    binding.rvAll.visibility = View.GONE
                    binding.ivNoData.visibility = View.VISIBLE
                    UtilsFunctions().handleErrorResponse(response, requireContext());
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun onClick(p0: View?) {

        if(UtilsFunctions().singleClickListener()) return
        when (view?.id) {
            R.id.tvAll -> {
                binding.tvAll.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primary_color
                    )
                )
                binding.tvStudent.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray_color
                    )
                )
                binding.tvInstitute.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray_color
                    )
                )

                binding.tvAll.setBackgroundResource(R.drawable.bg_rounded_7_selected)
                binding.tvStudent.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvInstitute.setBackgroundResource(R.drawable.bg_rounded_3)

                mSearchType = "All"
                setAdapter(mSearchType)

            }
            R.id.tvStudent -> {
                binding.tvAll.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray_color
                    )
                )
                binding.tvStudent.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primary_color
                    )
                )
                binding.tvInstitute.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray_color
                    )
                )

                binding.tvAll.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvStudent.setBackgroundResource(R.drawable.bg_rounded_7_selected)
                binding.tvInstitute.setBackgroundResource(R.drawable.bg_rounded_3)



                mSearchType = "Student"
                setAdapter(mSearchType)
            }
            R.id.tvInstitute -> {
                binding.tvAll.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray_color
                    )
                )
                binding.tvStudent.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray_color
                    )
                )
                binding.tvInstitute.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primary_color
                    )
                )

                binding.tvAll.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvStudent.setBackgroundResource(R.drawable.bg_rounded_3)
                binding.tvInstitute.setBackgroundResource(R.drawable.bg_rounded_7_selected)


                mSearchType = "Institute"
                setAdapter(mSearchType)
            }

        }

    }

    private fun setAdapter(mSearchType: String) {
        filterResponse.clear()

        for (data in searchResponse) {
            when (mSearchType) {
                "All" -> filterResponse.add(data)
                "Student" -> {
                    if (data.userTypeId == 2 || data.userTypeId == 3) {
                        filterResponse.add(data)
                    }
                }
                "Institute" -> {
                    if (data.userTypeId == 1) {
                        filterResponse.add(data)
                    }
                }
            }
        }

        binding.rvAll.visibility = if (filterResponse.isNotEmpty()) View.VISIBLE else View.GONE
        binding.ivNoData.visibility = if (filterResponse.isNotEmpty()) View.GONE else View.VISIBLE

        mSearchAdapter?.notifyDataSetChanged()
    }
}
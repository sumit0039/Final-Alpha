package com.softwill.alpha.institute.library.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import com.softwill.alpha.R
import com.softwill.alpha.chat.ChatActivity
import com.softwill.alpha.chat.FirebaseUtil
import com.softwill.alpha.chat.FirebaseUtil.urlToBase64
import com.softwill.alpha.chat.PDFViewerActivity
import com.softwill.alpha.databinding.ActivityReadBookBinding
import com.softwill.alpha.networking.RetrofitClient
import com.softwill.alpha.utils.UtilsFunctions
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReadBookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReadBookBinding
    var progressDialog: Dialog? = null

    private var mScreenTitle: String? = null
    private var mThumbnailUrl: String? = null
    private var mPdfUrl: String? = null
    private var mName: String? = null
    private var mWriterName: String? = null
    private var mBookId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_read_book)

        val bundle: Bundle? = intent.extras
        mScreenTitle = bundle?.getString("mScreenTitle")
        mBookId = bundle!!.getInt("mBookId")
        mThumbnailUrl = bundle.getString("mThumbnailUrl")
        mPdfUrl = bundle.getString("mPdfUrl")
        mName = bundle.getString("mName")
        mWriterName = bundle.getString("mWriterName")


        setupBack()

        Glide.with(this).load(mThumbnailUrl).placeholder(R.drawable.icon_no_image).into(binding.ivImage)
        binding.tvName.text = mName
        binding.tvWriter.text = mWriterName

        binding.btnRead.setOnClickListener {

            val jsonObject = JsonObject().apply {
                addProperty("bookId", mBookId)
            }

            val call: Call<ResponseBody> = RetrofitClient.getInstance(this@ReadBookActivity).myApi.api_ReadBookByID(jsonObject)


            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        progressDialog!!.dismiss()
                        val responseJson = response.body()?.string()
                        if (!responseJson.isNullOrEmpty()) {
                        }

                    } else {
                       UtilsFunctions().handleErrorResponse(response, this@ReadBookActivity);
                        progressDialog!!.dismiss()

                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    progressDialog!!.dismiss()

                }
            })


            if(mPdfUrl!=null) {
//                val base64String = urlToBase64(mPdfUrl!!)

//                FirebaseUtil.openPdfFromBase64(this@ReadBookActivity, base64String!!, mName!!)
                try {
                    val intent = Intent(this@ReadBookActivity, PDFViewerActivity::class.java)
                    intent.putExtra("pdf_uri_file_path", mPdfUrl)
                    intent.putExtra("pdf_uri_file_name", mName)
                    startActivity(intent)
               if (mBookId != -1){
                   apiMarkRecentRead()
               }

//               startActivity(intent)

           } catch (e: ActivityNotFoundException) {
               // PDF viewer app not found on the device
               Toast.makeText(this, "No PDF viewer app installed", Toast.LENGTH_SHORT).show()
           }
//                Toast.makeText(this, mPdfUrl, Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this, "No data", Toast.LENGTH_LONG).show()

            }


        }

    }

    private fun apiMarkRecentRead() {


        val call: Call<ResponseBody> =
            RetrofitClient.getInstance(this@ReadBookActivity).myApi.api_MarkRecentRead(
                mBookId
            )

        call.enqueue(object : Callback<ResponseBody> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseJson = response.body()?.string()
                    val responseObject = JSONObject(responseJson)

                    if (responseObject.has("message")) {
                        var message = responseObject.getString("message");

                        if (message == "Save successfully") {

                        }

                    } else if (responseObject.has("error")) {
                        UtilsFunctions().showToast(
                            this@ReadBookActivity, responseObject.getString("error")
                        )
                    }
                } else {
                    UtilsFunctions().handleErrorResponse(response, this@ReadBookActivity)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }


    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = mScreenTitle
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        supportActionBar!!.title = (Html.fromHtml("<font color=\"#060B13\">$mScreenTitle</font>"));

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }
}
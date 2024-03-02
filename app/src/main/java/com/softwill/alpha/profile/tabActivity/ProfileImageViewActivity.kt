package com.softwill.alpha.profile.tabActivity

import android.os.Bundle
import android.text.Html
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.softwill.alpha.R
import com.softwill.alpha.home.model.HomePostModel

class ProfileImageViewActivity : AppCompatActivity() {

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    var type : String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_image_view)
        val imageView = findViewById<ImageView>(R.id.imageView6)

        setupBack()
        type = intent.getStringExtra("type")
        Glide.with(this).load(intent.getStringExtra("data"))
            .placeholder(R.drawable.baseline_account_box_24)
            .error(R.drawable.baseline_account_box_24)
            .into(imageView)

    }

    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        if(type=="ProfileImage") {
            supportActionBar!!.title = (Html.fromHtml("<font color=\"#060B13\">" + "Profile Photo" + "</font>"));
        }else{
            supportActionBar!!.title = (Html.fromHtml("<font color=\"#060B13\">" + "Gallery" + "</font>"));
        }

    }

}
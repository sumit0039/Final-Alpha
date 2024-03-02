package com.softwill.alpha.institute.transport.activity

import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.softwill.alpha.R

class TrackBusActivity : AppCompatActivity() {

    private lateinit var binding: com.softwill.alpha.databinding.ActivityTrackBusBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, com.softwill.alpha.R.layout.activity_track_bus)


        setupBack()
    }

    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.title = getString(R.string.title_track_bus)
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        supportActionBar!!.title = (Html.fromHtml("<font color=\"#060B13\">" + resources.getString(R.string.title_track_bus) + "</font>"));

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
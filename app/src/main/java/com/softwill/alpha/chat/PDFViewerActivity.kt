package com.softwill.alpha.chat

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.databinding.DataBindingUtil
import com.softwill.alpha.R
import com.softwill.alpha.databinding.ActivityPdfviewerBinding
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.softwill.alpha.chat.FirebaseUtil.downloadPdfFromUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PDFViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfviewerBinding

    private var mPdfFileName: String = ""

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_pdfviewer)


        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val file = intent.getStringExtra("pdf_file_path")
            mPdfFileName = intent.getStringExtra("pdf_file_name").toString()
//            Toast.makeText(this, mPdfFileName, Toast.LENGTH_LONG).show()
            // Load the PDF file
            if (file != null) {
                binding.pdfView.fromFile(File(file))
                    .onLoad {
                        // PDF file has been loaded
                    }
                    .load()
//                Toast.makeText(this, file, Toast.LENGTH_LONG).show()

            }
            else {
                // Handle null file path
                Toast.makeText(this, "File not found!!", Toast.LENGTH_LONG).show()

            }

            setupBack()

        }
    }

    private fun setupBack() {
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true);
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        supportActionBar!!.title = (Html.fromHtml("<font color=\"#060B13\">$mPdfFileName</font>"));
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
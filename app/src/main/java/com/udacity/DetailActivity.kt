package com.udacity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    companion object {
        const val FILE_NAME = "file_name"
        const val FILE_URL = "file_url"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        val fileName: TextView =findViewById(R.id.file)
        fileName.text=intent.getStringExtra(FILE_NAME)
        val downloadingStatus: TextView =findViewById(R.id.status)


        go_back.setOnClickListener {
            motion_layout.transitionToEnd {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}

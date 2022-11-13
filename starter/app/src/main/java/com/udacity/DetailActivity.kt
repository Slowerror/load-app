package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.Constants.KEY_FILE_NAME
import com.udacity.Constants.KEY_STATUS
import com.udacity.databinding.ActivityDetailBinding


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)


        val fileName = intent.getStringExtra(KEY_FILE_NAME)
        val status = intent.getStringExtra(KEY_STATUS)

        setupValues(fileName, status)

        binding.layoutContentDetail.goBackButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelNotification()
    }

    private fun setupValues(fileName: String?, status: String?) {
        binding.layoutContentDetail.nameFileDescription.text = fileName
        if (status == getString(R.string.success_download_status)) {
            binding.layoutContentDetail.statusDescription.setTextColor(Color.GREEN)
        } else {
            binding.layoutContentDetail.statusDescription.setTextColor(Color.RED)
        }
        binding.layoutContentDetail.statusDescription.text = status
    }


}

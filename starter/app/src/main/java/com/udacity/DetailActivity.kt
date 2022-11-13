package com.udacity

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.MainActivity.Companion.KEY_FILE_NAME
import com.udacity.MainActivity.Companion.KEY_STATUS
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

        binding.layoutContentDetail.nameFileDescription.text = fileName
        if (status == getString(R.string.success_download_status)) {
            binding.layoutContentDetail.statusDescription.setTextColor(Color.GREEN)
        } else {
            binding.layoutContentDetail.statusDescription.setTextColor(Color.RED)
        }
        binding.layoutContentDetail.statusDescription.text = status

        binding.layoutContentDetail.goBackButton.setOnClickListener {
            finish()
        }
    }
}

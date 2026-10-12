package com.smitronix.instamod.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smitronix.instamod.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("instamod_prefs", Context.MODE_PRIVATE)

        initToggles()
    }

    private fun initToggles() {
        binding.switchFloatingNav.isChecked = prefs.getBoolean("enable_floating_nav", true)
        binding.switchReelsDownload.isChecked = prefs.getBoolean("enable_reels_download", true)
        binding.switchStoriesDownload.isChecked = prefs.getBoolean("enable_stories_download", true)
        binding.switchPostsDownload.isChecked = prefs.getBoolean("enable_posts_download", true)

        binding.switchFloatingNav.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("enable_floating_nav", isChecked).apply()
        }
        binding.switchReelsDownload.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("enable_reels_download", isChecked).apply()
        }
        binding.switchStoriesDownload.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("enable_stories_download", isChecked).apply()
        }
        binding.switchPostsDownload.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("enable_posts_download", isChecked).apply()
        }
    }

}

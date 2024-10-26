package com.dicoding.asclepius.view.news

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityNewsBinding

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the binding layout
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

       setupToolbar()
    }


    private fun setupToolbar(){
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.navigationIcon?.setTint(Color.WHITE)

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

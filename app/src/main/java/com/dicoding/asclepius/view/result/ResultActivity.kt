package com.dicoding.asclepius.view.result

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
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
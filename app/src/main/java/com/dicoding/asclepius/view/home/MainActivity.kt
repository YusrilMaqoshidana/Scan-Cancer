package com.dicoding.asclepius.view.home

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.view.history.HistoryActivity
import com.dicoding.asclepius.view.news.NewsActivity
import com.dicoding.asclepius.view.result.ResultActivity
import com.yalantis.ucrop.UCrop
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            btnNews.setOnClickListener{
                navigateNews()
            }
            btnHistory.setOnClickListener{
                navigateHistory()
            }
            btnGalerry.setOnClickListener{
                startGallery()
            }
            btnAnaliize.setOnClickListener{
                analyzeImage()
            }
        }



    }

    private fun navigateNews(){
        val intent = Intent(this, NewsActivity::class.java)
        startActivity(intent)
    }

    private fun navigateHistory(){
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
            startUCrop(uri)
        } else {
            showToast("No picture selected")
        }
    }

    private fun startUCrop(uri: Uri) {
        val options = UCrop.Options().apply {
            setCompressionQuality(90)
            setToolbarColor(ContextCompat.getColor(this@MainActivity, R.color.primaryLight))
            setActiveControlsWidgetColor(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.surfaceBright
                )
            )
            setStatusBarColor(ContextCompat.getColor(this@MainActivity, R.color.primaryLight))
            setToolbarWidgetColor(Color.WHITE)
        }

        val uCrop = UCrop.of(uri, Uri.fromFile(File(cacheDir, "cropped_image")))
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)
            .withOptions(options)

        uCrop.start(this)
    }
    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage() {
        // TODO: Menganalisa gambar yang berhasil ditampilkan.
        if (currentImageUri != null) {
            moveToResult()
        }
    }

    private fun moveToResult() {
        val intent = Intent(this, ResultActivity::class.java)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
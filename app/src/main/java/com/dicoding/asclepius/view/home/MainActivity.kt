package com.dicoding.asclepius.view.home

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.view.history.HistoryActivity
import com.dicoding.asclepius.view.news.NewsActivity
import com.dicoding.asclepius.view.result.ResultActivity
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File

class MainActivity : AppCompatActivity(), ImageClassifierHelper.ClassifierListener {
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imageClassifierHelper = ImageClassifierHelper(this, this)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        with(binding) {
            btnNews.setOnClickListener { navigateNews() }
            btnHistory.setOnClickListener { navigateHistory() }
            galleryButton.setOnClickListener { startGallery() }
            analyzeButton.setOnClickListener { analyzeImage() }
        }
    }

    private fun navigateNews() {
        val intent = Intent(this, NewsActivity::class.java)
        startActivity(intent)
    }

    private fun navigateHistory() {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            handleImageSelection(it)
        } ?: showToast("No picture selected")
    }
    private fun startUCrop(uri: Uri) {
        val options = getUCropOptions()
        val uCrop = UCrop.of(uri, getCroppedImageUri())
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)
            .withOptions(options)

        uCrop.start(this)
    }

    private fun handleImageSelection(uri: Uri) {
        currentImageUri = uri
        showImage()
        startUCrop(uri)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            currentImageUri = data?.let { UCrop.getOutput(it) }
            showImage()
        } else if (resultCode == UCropActivity.RESULT_CANCELED) {
            data?.let { UCrop.getError(it) }?.let {
                showToast("Crop error: $it")
            }
        }
    }

    private fun showImage() {
        currentImageUri?.let { uri ->
            binding.previewImageView.setImageURI(uri)
        }
    }

    private fun analyzeImage() {
        currentImageUri?.let {
            binding.progressIndicator.visibility = View.VISIBLE
            imageClassifierHelper.classifyStaticImage(it)
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun getUCropOptions() = UCrop.Options().apply {
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

    private fun getCroppedImageUri() = Uri.fromFile(File(cacheDir, "cropped_image"))


    private fun moveToResult(result: String, accuracy: Float) {
        currentImageUri?.let { uri ->
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, uri.toString())
            intent.putExtra(ResultActivity.EXTRA_RESULT, result)
            intent.putExtra(ResultActivity.EXTRA_CONFIDENCE_SCORE, accuracy)
            startActivity(intent)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onResults(results: List<Classifications>?, ignoredInferenceTime: Long) {
        runOnUiThread {
            binding.progressIndicator.visibility = View.GONE
            try {
                results?.let {
                    val category = it[0].categories[0].label
                    val confidence = it[0].categories[0].score
                    moveToResult(category, confidence)
                }
            } catch (e: Exception) {
                onError(e.message.toString())
            }
        }
    }

    override fun onError(error: String) {
        binding.progressIndicator.visibility = View.GONE
        showToast(error)
    }

}
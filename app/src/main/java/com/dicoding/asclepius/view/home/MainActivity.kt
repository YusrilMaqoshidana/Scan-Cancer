package com.dicoding.asclepius.view.home

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
        Log.d(TAG, "MainActivity created")

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
        Log.d(TAG, "Navigating to NewsActivity")
    }

    private fun navigateHistory() {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
        Log.d(TAG, "Navigating to HistoryActivity")
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        Log.d(TAG, "Opening gallery to select an image")
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            handleImageSelection(it)
        } ?: showToast("No picture selected")
    }

    private fun handleImageSelection(uri: Uri) {
        currentImageUri = uri
        Log.d(TAG, "Image selected: $uri")
        showImage()
        startUCrop(uri)
    }

    private fun startUCrop(uri: Uri) {
        val options = getUCropOptions()
        val uCrop = UCrop.of(uri, getCroppedImageUri())
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)
            .withOptions(options)

        Log.d(TAG, "Starting UCrop")
        uCrop.start(this)
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            currentImageUri = data?.let { UCrop.getOutput(it) }
            Log.d(TAG, "Cropped image URI: $currentImageUri")
            showImage()
        } else if (resultCode == UCropActivity.RESULT_CANCELED) {
            val cropError = data?.let { UCrop.getError(it) }
            cropError?.let {
                Log.e(TAG, "Crop error: $it")
                showToast("Crop error: $it")
            }
        }
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

    private fun showImage() {
        currentImageUri?.let { uri ->
            binding.previewImageView.setImageURI(uri)
            Log.d(TAG, "Showing image: $uri")
        }
    }

    private fun analyzeImage() {
        if (currentImageUri != null) {
            binding.progressIndicator.visibility = View.VISIBLE
            imageClassifierHelper.classifyStaticImage(currentImageUri!!)
        } else {
            showToast(getString(R.string.empty_image_warning))
        }
    }

    private fun moveToResult(result: String, accuracy: Float) {
        currentImageUri?.let { uri ->
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, uri.toString())
            intent.putExtra(ResultActivity.EXTRA_RESULT, result)
            intent.putExtra(ResultActivity.EXTRA_CONFIDENCE_SCORE, accuracy)
            startActivity(intent)
            Log.d(TAG, "Moving to ResultActivity with: $uri, $result, $accuracy")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Log.d(TAG, "Showing toast message: $message")
    }

    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
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
        Log.e(TAG, "Error during classification: $error")
        showToast(error)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
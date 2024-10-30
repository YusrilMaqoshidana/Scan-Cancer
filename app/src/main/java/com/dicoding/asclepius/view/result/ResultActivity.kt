package com.dicoding.asclepius.view.result

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.Results
import com.dicoding.asclepius.data.local.model.HistoryEntity
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.utils.FormatPercentage.formatPercentage
import com.dicoding.asclepius.view.HistoryViewModelFactory
import com.dicoding.asclepius.view.history.HistoryViewModel
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private val viewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        showImage()
        showClassificationResult()
    }

    private fun saveHistory(imageUri: String?, classificationResult: String?, confidenceScore: Float) {
        try {
            if (imageUri != null && classificationResult != null) {
                val imageByteArray = convertImageUriToByteArray(Uri.parse(imageUri))
                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

                val history = HistoryEntity(
                    category = classificationResult,
                    confidenceScore = confidenceScore,
                    imageUri = imageByteArray,
                    timestamp = timestamp,
                )

                viewModel.addHistories(history).observe(this) { results ->
                    when (results) {
                        is Results.Success -> {
                            Toast.makeText(this, "History saved successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        is Results.Error -> {
                            Toast.makeText(this, "Failed to save history: ${results.error}", Toast.LENGTH_SHORT).show()
                        }
                        is Results.Loading -> {
                            binding.progressBar.visibility = android.view.View.VISIBLE
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Cannot save history: Missing image or result", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error saving history: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertImageUriToByteArray(uri: Uri): ByteArray {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val outputStream = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        return outputStream.toByteArray()
    }

    private fun setupToolbar() {
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

    private fun showImage() {
        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        imageUri?.let {
            binding.
            resultImage.setImageURI(it)
        } ?: run {
            "Image URI is null".showToast()
        }
    }
    private fun String.showToast() {
        Toast.makeText(this@ResultActivity, this, Toast.LENGTH_SHORT).show()
    }
    private fun showClassificationResult() {
        val category = intent.getStringExtra(EXTRA_RESULT)
        val confidenceScore = intent.getFloatExtra(EXTRA_CONFIDENCE_SCORE, 0f)
        val percentage = formatPercentage(confidenceScore)
        val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)

        if (category != null) {
            binding.resultText.text = getString(R.string.classification_result, category, percentage)
        } else {
            binding.resultText.text = getString(R.string.result_not_available)
        }
        binding.btnSave.setOnClickListener {
            saveHistory(imageUri, category, confidenceScore)
        }
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
        const val EXTRA_CONFIDENCE_SCORE = "extra_confidence_score"
    }
}


package com.dicoding.asclepius.view.history

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.Results
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.view.HistoryViewModelFactory
import com.dicoding.asclepius.view.adapter.HistoryAdapter

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private val viewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory.getInstance(this)
    }
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var rvHistory: RecyclerView

    companion object {
        private const val TAG = "HistoryActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        Log.d(TAG, "Setting up toolbar")
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.navigationIcon?.setTint(Color.WHITE)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "Menu item selected: ${item.itemId}")
        return when (item.itemId) {
            android.R.id.home -> {
                Log.d(TAG, "Back button pressed")
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "Setting up RecyclerView")
        rvHistory = binding.rvHistory
        rvHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        historyAdapter = HistoryAdapter()
        rvHistory.adapter = historyAdapter

        Log.d(TAG, "Observing history data")
        viewModel.getHistories().observe(this) { history ->
            if (history != null) {
                when (history) {
                    is Results.Loading -> {
                        Log.d(TAG, "Loading data...")
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Results.Success -> {
                        Log.d(TAG, "Data loaded successfully")
                        binding.progressBar.visibility = View.GONE
                        if (history.data.isEmpty()) {
                            Log.d(TAG, "No history data found")
                            binding.tvNoHistory.visibility = View.VISIBLE
                        } else {
                            Log.d(TAG, "History data available, updating adapter")
                            historyAdapter.submitList(history.data)
                            Toast.makeText(this, "Data loaded successfully : ${history.data.size}", Toast.LENGTH_SHORT).show()
                            binding.tvNoHistory.visibility = View.GONE
                        }
                    }
                    is Results.Error -> {
                        Log.e(TAG, "Error loading data: ${history.error}")
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, "Error: ${history.error}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Log.w(TAG, "No data returned")
            }
        }
    }
}

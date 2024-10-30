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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupRecyclerView()
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

    private fun setupRecyclerView() {
        rvHistory = binding.rvHistory
        rvHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        historyAdapter = HistoryAdapter()
        rvHistory.adapter = historyAdapter

        viewModel.getHistories().observe(this) { history ->
            if (history != null) {
                when (history) {
                    is Results.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Results.Success -> {
                        binding.progressBar.visibility = View.GONE
                        if (history.data.isEmpty()) {
                            binding.tvNoHistory.visibility = View.VISIBLE
                        } else {
                            historyAdapter.submitList(history.data)
                            Log.d("HistoryActivity", history.data[0].timestamp)
                            Toast.makeText(this, "Data loaded successfully", Toast.LENGTH_SHORT).show()
                            binding.tvNoHistory.visibility = View.GONE
                        }
                    }
                    is Results.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this, "Error: ${history.error}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else
            {
                Toast.makeText(this, "Data is null", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

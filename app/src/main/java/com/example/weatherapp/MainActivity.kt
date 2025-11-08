package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope // <-- Импортируем
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch // <-- Импортируем
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WeatherAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var errorTextView: TextView
    private lateinit var retryButton: Button
    private lateinit var toolbar: Toolbar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViews()
        setupToolbar()
        setupRecyclerView()
        loadWeatherData()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        errorTextView = findViewById(R.id.errorTextView)
        retryButton = findViewById(R.id.retryButton)
        toolbar = findViewById(R.id.toolbar)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupRecyclerView() {
        adapter = WeatherAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadWeatherData() {
        showLoading(true)

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()

        val service = retrofit.create(WeatherApiService::class.java)

        // Запускаем корутину в lifecycleScope активити
        lifecycleScope.launch {
            try {
                // Теперь можно вызывать suspend функцию напрямую
                val response = service.getWeather(55.7558, 37.6173)

                showLoading(false)
                if (response.isSuccessful) {
                    val weatherResponse: WeatherResponse? = response.body()
                    if (weatherResponse != null) {
                        val weatherItems = mapApiResponseToWeatherItems(weatherResponse)
                        adapter.updateData(weatherItems)
                        showError(false)
                    } else {
                        Log.e("MainActivity", "Response body is null")
                        showError(true)
                    }
                } else {
                    Log.e("MainActivity", "API request failed: ${response.code()} - ${response.message()}")
                    showError(true)
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error fetching weather data", e)
                showLoading(false)
                showError(true)
            }
        }
    }

    private fun mapApiResponseToWeatherItems(response: WeatherResponse): List<WeatherItem> {
        val daily = response.daily
        val list = mutableListOf<WeatherItem>()
        for (i in daily.time.indices) {
            list.add(
                WeatherItem(
                    date = daily.time[i],
                    maxTemp = daily.temperature_2m_max[i],
                    weatherCode = daily.weathercode[i],
                    rainProbability = daily.precipitation_probability_max?.get(i)
                )
            )
        }
        return list
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
        errorTextView.visibility = View.GONE
        retryButton.visibility = View.GONE
    }

    private fun showError(show: Boolean) {
        if (show) {
            errorTextView.visibility = View.VISIBLE
            retryButton.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            progressBar.visibility = View.GONE
        } else {
            errorTextView.visibility = View.GONE
            retryButton.visibility = View.GONE
        }
    }


}
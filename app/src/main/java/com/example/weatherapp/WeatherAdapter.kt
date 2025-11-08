package com.example.weatherapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R

class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    private var weatherList: List<WeatherItem> = listOf()

    fun updateData(newList: List<WeatherItem>) {
        weatherList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weather, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(weatherList[position])
    }

    override fun getItemCount(): Int = weatherList.size

    inner class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CardView = itemView.findViewById(R.id.cardView)
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        private val tempTextView: TextView = itemView.findViewById(R.id.tempTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        private val rainTextView: TextView = itemView.findViewById(R.id.rainTextView)
        private val iconImageView: ImageView = itemView.findViewById(R.id.weatherIconImageView)

        fun bind(item: WeatherItem) {
            val context = itemView.context

            dateTextView.text = context.getString(R.string.weather_item_date, item.date)
            tempTextView.text = context.getString(R.string.weather_item_temp, item.maxTemp)

            // Описание погоды на основе кода (упрощённо)
            val description = getWeatherDescription(item.weatherCode)
            descriptionTextView.text = context.getString(R.string.weather_item_description, description)

            // Описание осадков
            val rainText = if (item.rainProbability != null && item.rainProbability > 0) {
                context.getString(R.string.weather_item_rain, "${item.rainProbability}%")
            } else {
                context.getString(R.string.no_rain)
            }
            rainTextView.text = rainText

            // Выбор цвета фона карточки
            val cardColorRes = getCardColorForWeather(item.maxTemp, item.weatherCode)
            cardView.setCardBackgroundColor(ContextCompat.getColorStateList(context, cardColorRes))

            // Выбор иконки (упрощённо, можно улучшить)
            val iconRes = getWeatherIcon(item.weatherCode)
            iconImageView.setImageResource(iconRes)
        }

        private fun getWeatherDescription(code: Int): String {
            // Упрощённое сопоставление кодов (реальные коды см. в документации API)
            return when (code) {
                0 -> "Ясно"
                1, 2, 3 -> "Облачно"
                45, 48 -> "Туман"
                51, 53, 55, 56, 57 -> "Дождь"
                61, 63, 65, 66, 67 -> "Снег"
                71, 73, 75, 77 -> "Снег"
                80, 81, 82 -> "Дождь"
                85, 86 -> "Снег"
                95, 96, 99 -> "Гроза"
                else -> "Неизвестно ($code)"
            }
        }

        private fun getCardColorForWeather(temp: Double, code: Int): Int {
            // Определение цвета в зависимости от температуры и осадков
            return if (code in listOf(51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 71, 73, 75, 77, 80, 81, 82, 85, 86)) {
                // Если есть код осадков
                if (code in listOf(61, 63, 65, 66, 67, 71, 73, 75, 77, 85, 86)) R.color.cardSnow else R.color.cardRain
            } else {
                // Иначе по температуре
                when {
                    temp < 0 -> R.color.cardCold
                    temp < 15 -> R.color.cardMild
                    temp < 25 -> R.color.cardWarm
                    else -> R.color.cardHot
                }
            }
        }

        private fun getWeatherIcon(code: Int): Int {
            // Упрощённый выбор иконки
            return when (code) {
                0 -> R.drawable.ic_clear // Предполагается наличие иконки
                1, 2, 3 -> R.drawable.ic_cloud
                51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82, 85, 86 -> R.drawable.ic_rain
                45, 48 -> R.drawable.ic_fog
                71, 73, 75, 77 -> R.drawable.ic_snow
                95, 96, 99 -> R.drawable.ic_storm
                else -> R.drawable.ic_unknown // Заглушка
            }
        }
    }
}
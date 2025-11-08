import com.example.weatherapp.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun getWeather( //
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("daily") daily: String = "weathercode,temperature_2m_max,precipitation_probability_max",
        @Query("timezone") timezone: String = "auto"
    ): Response<WeatherResponse>
}
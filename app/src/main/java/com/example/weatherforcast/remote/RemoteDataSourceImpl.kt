import android.util.Log
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.remote.RemoteDataSource
import com.example.weatherforcast.remote.RetrofitClient

class RemoteDataSourceImpl : RemoteDataSource {
    override suspend fun getCurrentWeather(lat: Double, lon: Double): Result<WeatherResponse> {
        return try {
            val response = RetrofitClient.apiService.getCurrentWeather(lat, lon)
            if (response.isSuccessful) {
                val weatherResponse = response.body()
                if (weatherResponse != null) {
                    Result.success(weatherResponse)
                } else {
                    Result.failure(Throwable("No weather data available"))
                }
            } else {
                Result.failure(Throwable(response.message()))
            }
        } catch (ex: Exception) {
            Log.e("RemoteDataSourceImpl", "Error: ${ex.message}")
            Result.failure(ex)
        }
    }
}

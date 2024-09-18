import android.util.Log
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.remote.RemoteDataSource
import com.example.weatherforcast.remote.RetrofitClient
import com.example.weatherforcast.utils.Result

class RemoteDataSourceImpl : RemoteDataSource {
    override suspend fun getCurrentWeather(lat: Double, lon: Double):Result<WeatherResponse> {
        return try {
            val response = RetrofitClient.apiService.getCurrentWeather(lat, lon)
            if (response.isSuccessful) {
                val weatherResponse = response.body()
                if (weatherResponse != null) {
                    Result.Success(weatherResponse)
                } else {
                    Result.Error("No weather data available")
                }
            } else {
                Result.Error(response.message())
            }
        } catch (ex: Exception) {
            Log.e("RemoteDataSourceImpl", "Error: ${ex.message}")
            Result.Error(ex.message.toString())
        }
    }
}

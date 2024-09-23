import android.util.Log
import com.example.weatherforcast.pojo.current_weather.WeatherResponse
import com.example.weatherforcast.pojo.days_weather.DaysWeatherResponse
import com.example.weatherforcast.remote.RemoteDataSource
import com.example.weatherforcast.remote.RetrofitClient
import com.example.weatherforcast.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemoteDataSourceImpl : RemoteDataSource {
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double
    ): Flow<Result<WeatherResponse>> =
        flow {
            try {

               emit(Result.Loading())
                val response = RetrofitClient.apiService.getCurrentWeather(lat, lon)
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    if (weatherResponse != null) {
                        emit( Result.Success(weatherResponse))
                    } else {
                        emit(Result.Error("No weather data available"))
                    }
                } else {
                    emit(Result.Error(response.message()))
                }
            } catch (ex: Exception) {
                Log.e("RemoteDataSourceImpl", "Error: ${ex.message}")
                emit(Result.Error(ex.message.toString()))
            }
        }

    override suspend fun getDaysWeather(
        lat: Double,
        lon: Double
    ): Flow<Result<DaysWeatherResponse>> =
        flow {
            try {
                emit(Result.Loading())
                val response = RetrofitClient.apiService.getDaysWeather(lat, lon)
                if (response.isSuccessful) {
                    val daysWeatherResponse = response.body()
                    if (daysWeatherResponse != null) {
                        emit(Result.Success(daysWeatherResponse))
                    } else {
                        emit(Result.Error("No weather data available"))
                    }
                } else {
                    emit(Result.Error(response.message()))
                }
            } catch (ex: Exception) {
                Log.e("RemoteDataSourceImpl", "Error: ${ex.message}")
                emit(Result.Error(ex.message.toString()))
            }
        }
}


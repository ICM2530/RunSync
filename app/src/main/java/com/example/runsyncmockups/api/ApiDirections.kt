package com.example.runsyncmockups.api



import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.google.gson.annotations.SerializedName

data class DirectionsResponse(
    val status: String? = null,
    @SerializedName("error_message") val errorMessage: String? = null,
    val routes: List<Route> = emptyList()
)

data class Route(
    @SerializedName("overview_polyline") val overviewPolyline: OverviewPolyline?
)

data class OverviewPolyline(val points: String)

interface DirectionsApi {
    @GET("directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,          // "lat,lng"
        @Query("destination") destination: String,// "lat,lng"
        @Query("mode") mode: String = "driving",
        @Query("key") apiKey: String
    ): retrofit2.Response<DirectionsResponse> // <-- Response<...>
}

object RetrofitDirectionsClient {
    private const val BASE_URL = "https://maps.googleapis.com/maps/api/"

    val api: DirectionsApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()) // Igual que tu otro proyecto
        .build()
        .create(DirectionsApi::class.java)
}

object DirectionsRepo {
    suspend fun fetchRoutePoints(
        origin: LatLng,
        dest: LatLng,
        apiKey: String,
        mode: String = "driving"
    ): List<LatLng> {
        val o = "${origin.latitude},${origin.longitude}"
        val d = "${dest.latitude},${dest.longitude}"

        val http = RetrofitDirectionsClient.api.getDirections(o, d, mode, apiKey)

        // 1) Errores HTTP (4xx/5xx)
        if (!http.isSuccessful) {
            android.util.Log.e("DIR", "HTTP ${http.code()}  err=${http.errorBody()?.string()}")
            return emptyList()
        }

        val resp = http.body() ?: return emptyList()

        // 2) Errores de la API (REQUEST_DENIED, ZERO_RESULTS, etc.)
        if (resp.status != "OK") {
            android.util.Log.e("DIR", "status=${resp.status} msg=${resp.errorMessage}")
            return emptyList()
        }

        // 3) Polyline
        val poly = resp.routes.firstOrNull()?.overviewPolyline?.points
        if (poly.isNullOrBlank()) {
            android.util.Log.e("DIR", "Sin overview_polyline en routes")
            return emptyList()
        }
        return com.google.maps.android.PolyUtil.decode(poly)
    }
}

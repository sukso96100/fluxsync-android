package xyz.youngbin.fluxsync.connect

import android.content.Context
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import xyz.youngbin.fluxsync.FluxSyncApp


/**
 * Created by youngbin on 2017. 7. 18..
 */
interface AuthApi{


    @GET("/reqkey")
    fun requestToken(@Query("hostname") hostname: String, @Query("id") id: String) : Call<ResultData>
}

data class ResultData(val result: String)

class AuthClient(addrWithPort: String){
    var api: AuthApi
    var address: String
    init {
        address = addrWithPort
        var retroFit = Retrofit.Builder()
                .baseUrl("http:/${address}")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        api = retroFit.create(AuthApi::class.java)
    }

    fun sendInfo(context: Context, result: (Boolean) -> Unit){
        var app: FluxSyncApp = context.applicationContext as FluxSyncApp
        val call = api.requestToken(app.hostname, app.deviceId)
        call.enqueue(object : Callback<ResultData> {
            override fun onFailure(call: Call<ResultData>?, t: Throwable?) {
                result(false)
            }

            override fun onResponse(call: Call<ResultData>?, response: Response<ResultData>?) {
                if(response!!.code() == 204){
                    result(true)
                }
            }
        })
    }
}
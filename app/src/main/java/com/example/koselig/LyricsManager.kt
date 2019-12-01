package com.example.koselig

import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.net.URLEncoder


class LyricsManager {

    // OkHttp is a library used to make network calls
    private val okHttpClient: OkHttpClient

    private var oAuthToken: String? = null

    // This runs extra code when Lyric Manager is created (e.g. the constructor)
    init {
        val builder = OkHttpClient.Builder()

        // This sets network timeouts (in case the phone can't connect
        // to the server or the server is down)
        builder.connectTimeout(20, TimeUnit.SECONDS)
        builder.readTimeout(20, TimeUnit.SECONDS)
        builder.writeTimeout(20, TimeUnit.SECONDS)

        // This causes all network traffic to be logged to the console
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(logging)

        okHttpClient = builder.build()
    }

    fun retrieveSongLyrics (
        titleInput: String,
        artistInput: String,
        successCallback: (List<String>) -> Unit,
        errorCallback: (Exception) -> Unit
    ){
        //prepare strings for query (replace spaces with %20)
        val title = URLEncoder.encode(titleInput, "UTF-8");
        val artist = URLEncoder.encode(artistInput, "UTF-8");
        //get url request to find song
        //variable to hold song lyrics
        // https://api.musixmatch.com/ws/1.1/matcher.lyrics.get?format=jsonp&callback=callback&q_track=ride&q_artist=lil%20nas&apikey=f076665c50ff3af5fba6f7b96531e20b
        var urlrequest: String = "https://api.musixmatch.com/ws/1.1/matcher.lyrics.get?format=jsonp&callback=callback&q_track=$title&q_artist=$artist&apikey=f076665c50ff3af5fba6f7b96531e20b\n"

        val request = Request.Builder()
            .url(urlrequest)
            .header("api_key", R.string.lyricsapi.toString())
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Same error handling to last time
                errorCallback(e)
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                // Similar success / error handling to last time
                val lyrics = mutableListOf<String>()
                val responseString1 = response.body?.string()
                //get rid of callback in response
                var responseString = responseString1
                if (responseString1 != null){
                    val repsonseString2 = responseString1.replace("callback(", "")
                    responseString = repsonseString2.replace(");", "")
                }

                if (response.isSuccessful && responseString != null) {

                    val track = JSONObject(responseString).getJSONObject("message").getJSONObject("body").getJSONObject("lyrics")
                    lyrics.add(track.getString("lyrics_body"))
                    successCallback(lyrics)

                } else {
                    // Invoke the callback passed to our [retrieveTweets] function.
                    errorCallback(Exception("get lyrics call failed"))
                }
            }
        })
    }
}


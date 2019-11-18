package com.example.koselig


import android.location.Address
import android.location.Location
import android.telecom.Call
import android.util.EventLogTags
import android.util.Log
import android.widget.TextView
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

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
        val title = titleInput.replace(" ", "%20")
        val artist = artistInput.replace(" ", "%20")
        //get url request to find song
        //variable to hold song lyrics

        var urlrequest: String = "matcher.lyrics.get?q_track=$title&q_artist=$artist"

        val request = Request.Builder()
            .url(urlrequest)
            .header("api_key", "f076665c50ff3af5fba6f7b96531e20b")
            .build()
        okHttpClient.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Same error handling to last time
                errorCallback(e)
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                // Similar success / error handling to last time
                val lyrics = mutableListOf<String>()
                val responseString2 = response.body?.string()

                if (response.isSuccessful && responseString2 != null) {
                    val track = JSONObject(responseString2).getJSONObject("body").getJSONArray("lyrics")
                    if (track.length() > 0) {
                        val curr = track.getJSONObject(0)
                        lyrics[0] = curr.getString("lyrics_body")

                    }
                    successCallback(lyrics)

                    //...
                } else {
                    // Invoke the callback passed to our [retrieveTweets] function.
                    errorCallback(Exception("get lyrics call failed"))
                }
            }
        })
    }
    }


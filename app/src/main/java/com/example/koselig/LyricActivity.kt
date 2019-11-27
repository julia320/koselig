package com.example.koselig

import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import android.widget.Toast
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import java.io.IOException

class LyricActivity : AppCompatActivity() {

    private val lyricManager: LyricsManager = LyricsManager()
    private lateinit var recyclerView: RecyclerView
    private var translate: Translate? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lyric)

        recyclerView = findViewById(R.id.lyric_recycler)

        // Set the direction of our list to be vertical
        recyclerView.layoutManager = LinearLayoutManager(this)

        val inputTitle = intent.getStringExtra("titleInput")
        val inputArtist = intent.getStringExtra("artistInput")


        // Get translation
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        try {
            resources.openRawResource(R.raw.credentials).use { `is` ->
                val myCredentials = GoogleCredentials.fromStream(`is`)
                val translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build()
                translate = translateOptions.service
            }

        }
        catch (ioe: IOException) {
            ioe.printStackTrace()
        }


        //get path
        lyricManager.retrieveSongLyrics(
            titleInput = inputTitle,
            artistInput = inputArtist,
            successCallback = { lyrics ->
                runOnUiThread {
                    // Get the translation
                    val translation = translate!!.translate (
                        lyrics,
                        Translate.TranslateOption.targetLanguage("es"),
                        Translate.TranslateOption.model("base")
                    )

                    // Create the adapter and assign it to the RecyclerView
                    recyclerView.adapter = LyricAdapter(lyrics)
                    if (lyrics.isEmpty()){
                        Toast.makeText(this@LyricActivity, R.string.lyric_error, Toast.LENGTH_LONG).show()
                    }

                    // Convert the Translation list to String list and assign to RecyclerView
                    var translatedText = mutableListOf<String>()
                    var i = 0
                    for (lyric in translation) {
                        translatedText.add(lyrics[i])
                        translatedText.add(lyric.translatedText)
                        i++
                    }
                    recyclerView.adapter = LyricAdapter(translatedText)
                }
            },
            errorCallback = {
                runOnUiThread {
                    // Runs if we have an error
                    Toast.makeText(this@LyricActivity, "Error retrieving lyrics", Toast.LENGTH_LONG).show()
                }
            }
        )


    }
}


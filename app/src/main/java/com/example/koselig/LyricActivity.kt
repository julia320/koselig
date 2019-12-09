package com.example.koselig

import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
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
        val language = intent.getStringExtra("language")


        // Set up translation class
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


        // Find code that matches language selection
        var code = "es" // use Spanish as default
        for (lang in translate!!.listSupportedLanguages()) {
            val list = lang.toString().split("=")
            val name = list[2].substring(0, list[2].length-1)
            if (name.compareTo(language) == 0) {
                code = list[1].substring(0, 2)
                break
            }
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
                        Translate.TranslateOption.targetLanguage(code),
                        Translate.TranslateOption.model("base")
                    )

                    // Create the adapter and assign it to the RecyclerView
                    recyclerView.adapter = LyricAdapter(lyrics)
                    if (lyrics.isEmpty()){
                        Toast.makeText(this@LyricActivity, R.string.lyric_error, Toast.LENGTH_LONG).show()
                    }

                    // Convert the Translation list to String list and assign to RecyclerView
                    var translatedText = mutableListOf<String>()
                    for ((i,lyric) in translation.withIndex()) {
                        translatedText.add(lyrics[i])
                        translatedText.add(lyric.translatedText)
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
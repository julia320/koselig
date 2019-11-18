package com.example.koselig

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.NotificationCompat.getExtras
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class LyricActivity : AppCompatActivity() {

    private val lyricManager: LyricsManager = LyricsManager()

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lyric)

        recyclerView = findViewById(R.id.lyric_recycler)

        // Set the direction of our list to be vertical
        recyclerView.layoutManager = LinearLayoutManager(this)

        val inputTitle = intent.getStringExtra("titleInput")
        val inputArtist = intent.getStringExtra("artistInput")

        //get path
        lyricManager.retrieveSongLyrics(
            titleInput = inputTitle,
            artistInput = inputArtist,
            successCallback = { lyrics ->
                runOnUiThread {
                    // Create the adapter and assign it to the RecyclerView
                    recyclerView.adapter = LyricAdapter(lyrics)
                    if (lyrics.isEmpty()){
                        Toast.makeText(this@LyricActivity, "The lyrics could not found or do not exist in the database", Toast.LENGTH_LONG).show()
                    }
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

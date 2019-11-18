package com.example.koselig

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.app.AlertDialog
import android.location.Geocoder
import android.location.Location
import android.widget.ArrayAdapter
import android.widget.Toast
import android.content.SharedPreferences
import com.example.koselig.R
import kotlinx.android.synthetic.main.activity_lyric.*
import java.util.*

class MainActivity : AppCompatActivity() {


    private lateinit var title: EditText

    private lateinit var artist: EditText

    private lateinit var submit_button: Button

    /**
     * Creating an "anonymous class" here (e.g. we're creating a class which implements
     * TextWatcher, but not creating an explicit class).
     *
     * object : TextWatcher == "creating a new object which implements TextWatcher"
     */
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // We're calling .getText() here, but in Kotlin you can omit the "get" or "set"
            // on a getter / setter and "pretend" you're using an actual variable.
            //      username.getText() == username.text
            val inputtedTitle: String = title.text.toString().trim()
            val inputtedArtist: String = artist.text.toString().trim()
            val enableButton: Boolean = inputtedTitle.isNotEmpty() && inputtedArtist.isNotEmpty();

            // Like above, this is really doing login.setEnabled(enableButton) under the hood
            submit_button.isEnabled = enableButton
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("MainActivity", "onCreate called")
        AlertDialog.Builder(this)

            .setTitle("Welcome!")
            .setMessage(getString(R.string.promptMsg))
            .setPositiveButton("OKAY") { dialog, which ->
                // User pressed OK
            }
            .show()

        title = findViewById(R.id.song_title_search)
        artist = findViewById(R.id.song_artist_search)
        submit_button = findViewById(R.id.submit_search)

        //disable button until text is entered
        submit_button.isEnabled = false
        title.addTextChangedListener(textWatcher)
        artist.addTextChangedListener(textWatcher)

        submit_button.setOnClickListener {
            Log.d("MainActivity", "Submit search Clicked")

            // Pass a context (e.g. Activity) and locale
            //get info inputted
            val artistName = artist.text.toString().trim()
            val titleName = title.text.toString().trim()

            // Using an Intent to start our TweetsActivity and send a small amount of data to it
//            val intent: Intent = Intent(this, TweetsActivity::class.java)
//            intent.putExtra("location", "Washington D.C.")

            val intent: Intent = Intent(this, LyricActivity::class.java)
            intent.putExtra("titleInput", titleName)
            intent.putExtra("artistInput", artistName)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy called")
    }
}

package com.example.koselig

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.app.AlertDialog
import android.os.StrictMode
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import java.io.IOException
import android.widget.ArrayAdapter
import android.widget.Spinner


class SearchActivity : AppCompatActivity() {


    private lateinit var title: EditText
    private lateinit var artist: EditText
    private lateinit var submit_button: Button

    private var translate: Translate? = null

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
        setContentView(R.layout.activity_search)

        Log.d("SearchActivity", "onCreate called")
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

        // Put list of languages in drop down menu
        val languages = mutableListOf<String>()
        for (lang in translate!!.listSupportedLanguages()) {
            var name = lang.toString().split("=")[2]
            name = name.substring(0, name.length-1)
            languages.add(name)
        }

        val spinner = findViewById<Spinner>(R.id.spinner)
        val adapter = ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, languages)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner.setAdapter(adapter)


        submit_button.setOnClickListener {
            Log.d("SearchActivity", "Submit search Clicked")

            // Pass a context (e.g. Activity) and locale
            //get info inputted
            val artistName = artist.text.toString().trim()
            val titleName = title.text.toString().trim()
            val language = spinner.getSelectedItem().toString()

            val intent: Intent = Intent(this, LyricActivity::class.java)
            intent.putExtra("titleInput", titleName)
            intent.putExtra("artistInput", artistName)
            intent.putExtra("language", language)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("SearchActivity", "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("SearchActivity", "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d("SearchActivity", "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d("SearchActivity", "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("SearchActivity", "onDestroy called")
    }
}

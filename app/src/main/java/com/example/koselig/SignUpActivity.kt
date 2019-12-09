package com.example.koselig

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser

import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var signUp: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val username = findViewById(R.id.username) as EditText
        val password = findViewById(R.id.password) as EditText
        signUp = findViewById(R.id.signUp)

        signUp.setOnClickListener {
            val inputtedUsername: String = username.text.toString().trim()
            val inputtedPassword: String = password.text.toString().trim()
            firebaseAuth
                .createUserWithEmailAndPassword(inputtedUsername, inputtedPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentUser: FirebaseUser? = firebaseAuth.currentUser
                        val email = currentUser?.email
                        Toast.makeText(this, "Registered as $email", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, SearchActivity::class.java)
                        startActivity(intent)
                    } else {
                        val exception = task.exception
                        Toast.makeText(this, "Registration failed: $exception", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

}

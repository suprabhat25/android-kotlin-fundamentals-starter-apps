package com.example.messenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.messenger.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity :AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button.setOnClickListener(){
            val email=email_edittext.text.toString()
            val password = password_edittext.text.toString()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
//                .addOnCompleteListener{}
//                .addOnFailureListener{}
            Log.d("Main Activity","Email is "+ email)
            Log.d("Main Activity","password is: $password")
        }
        back_registration.setOnClickListener(){
            finish();
        }
    }
}
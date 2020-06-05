package com.example.messenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register_button.setOnClickListener() {
            performRegister()
        }

        already_registered.setOnClickListener(){
          val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent);
        }

        select_photo_button.setOnClickListener() {
            Log.d("Photo","Photo is clicked")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }

    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode:Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            //Check and proceed what the selected image was.
            Log.d("MainActivity","Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            select_photo_button.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun performRegister()
    {
        val email=email_edittext.text.toString()
        val password = password_edittext.text.toString()

        if(email.isEmpty() || password.isEmpty() )
        {
            Toast.makeText(this,"Please enter text in email/password",Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("Main Activity","Email is "+ email)
        Log.d("Main Activity","password is: $password")

        //Firebase Authentication
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener

                //else if successful
                // Log.d("Main","Successfully Created with uid: ${it.result.user.uid}")
             uploadImageToFirebaseStorage()
            }
            .addOnFailureListener{
                Toast.makeText(this,"Failed to create user",Toast.LENGTH_SHORT).show()
                Log.d("Main","Failed to create user: ${it.message}")
            }
    }
    private fun uploadImageToFirebaseStorage(){
        if(selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/Image/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("Main", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    Log.d("Main", "File location: $it")
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener{
                        //
                    }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid,username_edittext.text.toString(),profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener{
                Log.d("Main","Saved the user to firebase database")
            }
    }
}

class User(val uid:String, val username:String, val profileImageUrl:String )
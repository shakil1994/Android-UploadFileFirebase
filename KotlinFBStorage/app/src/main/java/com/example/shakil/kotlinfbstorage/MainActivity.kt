package com.example.shakil.kotlinfbstorage

import android.app.AlertDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var alertDialog: AlertDialog
    lateinit var storageReference: StorageReference

    companion object {
        private val PICK_IMAGE_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Init
        alertDialog = SpotsDialog.Builder().setContext(this).build()

        //Create name for file
        storageReference = FirebaseStorage.getInstance().getReference("image_upload")

        btn_upload.setOnClickListener(View.OnClickListener {
            //We will write code to pickup image
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select picture"), PICK_IMAGE_CODE)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_CODE) {
            //Show dialog
            alertDialog.show()

            val uploadTask = storageReference.putFile(data!!.data!!)

            //Create task
            val task = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Failed", Toast.LENGTH_SHORT).show()

                    alertDialog.dismiss()
                }
                storageReference.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val downloadUri = task.result
                    val url = downloadUri!!.toString().substring(0, downloadUri.toString().indexOf("&token"))
                    Log.d("DIRECTLINK", url)
                    Picasso.get().load(url).into(image_view)

                    alertDialog.dismiss()
                }
            }
        }
    }
}

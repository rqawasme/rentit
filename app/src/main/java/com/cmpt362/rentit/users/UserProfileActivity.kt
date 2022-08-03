package com.cmpt362.rentit.users

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.cmpt362.rentit.Constants
import com.cmpt362.rentit.R
import com.cmpt362.rentit.Utils
import com.cmpt362.rentit.details.DetailViewPagerAdapter
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.File

class UserProfileActivity : AppCompatActivity() {
    private lateinit var textViewUsername: TextView
    private lateinit var textInputEditTextUsername: TextInputEditText
    private lateinit var textInputEditTextEmail: TextInputEditText
    private lateinit var textInputEditTextPhone: TextInputEditText
    private lateinit var textInputEditTextPostalCode: TextInputEditText
    private lateinit var textInputEditTextPassword: TextInputEditText

    private lateinit var shapeableImageViewProfilePicture: ShapeableImageView
    private lateinit var db: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storageReference: StorageReference

    private lateinit var tempProfilePhotoFile: File
    private lateinit var tempProfilePhotoUri: Uri
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var galleryResult: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        Utils.checkPermissions(this)
        initializeElements()
        populateUserProfile()
    }

    private fun populateUserProfile() {
        val user = firebaseAuth.currentUser
        if (user != null){
            val userId = user.uid
            val myRefUsers = db.getReference(Constants.USERS_TABLE_NAME)

            val userDataSnapshot = myRefUsers.child(userId).get()

            userDataSnapshot.addOnSuccessListener {
                if (it.exists()){
                    val username = it.child(Constants.USERNAME_PATH).getValue(String::class.java)
                    textViewUsername.text = username
                    textInputEditTextUsername.setText(username)
                    textInputEditTextPhone.setText(it.child(Constants.PHONE_PATH).getValue(String::class.java))
                    textInputEditTextPostalCode.setText(it.child(Constants.POSTAL_CODE_PATH).getValue(String::class.java))
                }
            }

            textInputEditTextEmail.setText(user.email.toString())
        }
    }

    private fun initializeElements() {
        textViewUsername = findViewById(R.id.userProfileActivity_textView_username)
        textInputEditTextUsername = findViewById(R.id.userProfileActivity_textInputEditText_username)
        textInputEditTextEmail = findViewById(R.id.userProfileActivity_textInputEditText_email)
        textInputEditTextPhone = findViewById(R.id.userProfileActivity_textInputEditText_phone)
        textInputEditTextPostalCode = findViewById(R.id.userProfileActivity_textInputEditText_postalCode)
        textInputEditTextPassword = findViewById(R.id.userProfileActivity_textInputEditText_password)

        textInputEditTextPassword.setOnClickListener {
            textInputEditTextPassword.setText("")
        }

        db = Firebase.database
        firebaseAuth = FirebaseAuth.getInstance()

        shapeableImageViewProfilePicture = findViewById(R.id.userProfileActivity_imageView_UserProfile)
        setupImageViewOnclick()
        setUpProfilePhoto()

    }

    private fun getRealPathFromURI(contentURI: Uri?, context: Activity): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentURI?.let { context.contentResolver.query(it, projection, null, null, null) }
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        return columnIndex?.let { cursor.getString(it) }
    }

    private fun setUpProfilePhoto(){
        val user = firebaseAuth.currentUser
        val storageReference = FirebaseStorage.getInstance().reference.child(Constants.USERS_FOLDER + user!!.uid)

        val localFile = File.createTempFile(Constants.USER_PROFILE_PIC_PREFIX, Constants.USER_PROFILE_PIC_SUFFIX)
        storageReference.getFile(localFile).addOnSuccessListener{
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            shapeableImageViewProfilePicture.setImageBitmap(bitmap)
        }.addOnFailureListener{
            Toast.makeText(this, Constants.FAILED_TO_RETRIEVE_PROFILE_PICTURE_ERROR, Toast.LENGTH_SHORT).show()
        }
        tempProfilePhotoFile = File(getExternalFilesDir(null), Constants.TEMP_PROFILE_PHOTO_FILE_NAME)

        tempProfilePhotoUri = FileProvider.getUriForFile(this, Constants.URI_AUTHORITY,
            tempProfilePhotoFile)

        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK){
                val bitmap = Utils.getBitmap(this, tempProfilePhotoUri)
                shapeableImageViewProfilePicture.setImageBitmap(bitmap)
            }
        }

        galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {
                result: ActivityResult ->

            if (result.resultCode == Activity.RESULT_OK){

                deleteTempUserProfilePhoto()
                val galleryPhotoUri = result.data?.data!!
                val galleryPhotoFile = File(getRealPathFromURI(galleryPhotoUri, this))
                galleryPhotoFile.copyTo(tempProfilePhotoFile)
                val bitmap = Utils.getBitmap(this, galleryPhotoUri)
                shapeableImageViewProfilePicture.setImageBitmap(bitmap)
            }
        }
    }

    private fun setupImageViewOnclick() {
        lateinit var dialog: Dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle(Constants.PICK_PROFILE_PHOTO_DIALOG_TITLE)
        builder.setItems(R.array.profile_photo_options, DialogInterface.OnClickListener{
                dialog, which ->
            if (which == Constants.OPEN_CAMERA_DIALOG_OPTION_POSITION){
                openCamera()
            }
            else if (which == Constants.SELECT_FROM_GALLERY_DIALOG_OPTION_POSITION){
                selectFromGallery()
            }
        })
        dialog = builder.create()
        shapeableImageViewProfilePicture.setOnClickListener{
            dialog.show()
        }
    }

    private fun openCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempProfilePhotoUri)
        cameraResult.launch(intent)
    }

    private fun selectFromGallery(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempProfilePhotoUri)
        galleryResult.launch(intent)
    }

    fun saveUserInfo(view: View){
        val user = firebaseAuth.currentUser
        if (tempProfilePhotoFile.exists()){
            storageReference = FirebaseStorage.getInstance().getReference("Users/" + user!!.uid)
            storageReference.putFile(tempProfilePhotoUri)

        }

        if (textInputEditTextEmail.text.toString() != user!!.email.toString()){
            changeEmail(user)
        }

        exit(view)
    }

    private fun changeEmail(user: FirebaseUser) {
        user.updateEmail(textInputEditTextEmail.text.toString()).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("User Profile Activity", "User email address updated.")
            }
        }.addOnFailureListener{
                println(it)
        }
    }

    private fun deleteTempUserProfilePhoto(){
        if (tempProfilePhotoFile.exists()){
            tempProfilePhotoFile.delete()
        }
    }

    fun exit(view: View){
        deleteTempUserProfilePhoto()
        finish()
    }
}
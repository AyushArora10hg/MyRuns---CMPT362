package ca.sfu.cmpt362.ayusharora.myruns.userprofile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import ca.sfu.cmpt362.ayusharora.myruns.R
import ca.sfu.cmpt362.ayusharora.myruns.dialogs.InputDialogFragment
import ca.sfu.cmpt362.ayusharora.myruns.dialogs.OptionDialogFragment
import java.io.File

class UserProfileActivity : AppCompatActivity() {

    // Constants
    private val profileData = "MyRuns_UserProfile"
    private val finalImgFileName = "profile_img.jpg"
    private val tempImgFileName = "temp_profile_img.jpg"

    // UI elements
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var classEditText: EditText
    private lateinit var majorEditText: EditText
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var userProfileImageView: ImageView

    // Files
    private lateinit var finalImgFile: File
    private lateinit var tempImgUri: Uri
    private lateinit var tempImgFile: File

    // Activity launchers
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var galleryResult: ActivityResultLauncher<Intent>

    // View model for live data
    private lateinit var userProfileViewModel: UserProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        setupUI()
        setupImageFiles()
        registerActivityResults()
        handleButtons()
        observerProfileImage()
    }

    // Initialize UI views and load data to them (EditTexts, RadioGroups etc)
    private fun setupUI(){

        nameEditText = findViewById(R.id.up_edittext_name)
        emailEditText = findViewById(R.id.up_edittext_email)
        phoneEditText = findViewById(R.id.up_edittext_phone)
        classEditText = findViewById(R.id.up_edittext_class)
        majorEditText = findViewById(R.id.up_edittext_major)
        genderRadioGroup = findViewById(R.id.up_radiogroup_gender)
        userProfileImageView = findViewById(R.id.up_image_view)
        loadProfile()

    }

    // Load all image files to store profile picture
    // Code adapted from XD's lecture demos
    fun setupImageFiles(){
        finalImgFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), finalImgFileName)
        if (finalImgFile.exists()) {
            userProfileImageView.setImageURI(Uri.fromFile(finalImgFile))
        } else {
            userProfileImageView.setImageResource(R.drawable.default_profile)
        }

        tempImgFile = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            tempImgFileName
        )
        tempImgUri = FileProvider.getUriForFile(this,
            "ca.sfu.cmpt362.ayusharora.myruns", tempImgFile)
    }

    // Register for camera and gallery results
    // Code adapted from XD's lecture demos
    private fun registerActivityResults(){
        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _->
            if (tempImgFile.exists()) {
                userProfileViewModel.userImageUri.value = tempImgUri
            }
        }

        // Some code taken from chatGPT regarding how to register for a gallery result
        galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    val input = contentResolver.openInputStream(it)
                    val output = tempImgFile.outputStream()
                    input?.copyTo(output)
                    input?.close()
                    output.close()

                    userProfileViewModel.userImageUri.value = tempImgUri
                }
            }
        }
    }

    // View model to observe image changes
    // Code adpated from XD's lecture demos
    private fun observerProfileImage(){
        userProfileViewModel = ViewModelProvider(this)[UserProfileViewModel::class.java]
        userProfileViewModel.userImageUri.observe(this) { it ->
            userProfileImageView.setImageURI(it)
        }
    }

    // Load saved date from shared preferences and set them to edit texts
    private fun loadProfile(){

        val sharedPreference = getSharedPreferences(profileData, MODE_PRIVATE)

        nameEditText.setText(sharedPreference.getString("name", ""))
        emailEditText.setText(sharedPreference.getString("email", ""))
        phoneEditText.setText(sharedPreference.getString("phone", ""))
        classEditText.setText(sharedPreference.getString("class", ""))
        majorEditText.setText(sharedPreference.getString("major", ""))

        val savedGender = sharedPreference.getString("gender", "")
        if (savedGender == getString(R.string.radiobutton_male)) {
            genderRadioGroup.check(R.id.up_radiobutton_male)
        } else if (savedGender == getString(R.string.radiobutton_female)) {
            genderRadioGroup.check(R.id.up_radiobutton_female)
        }
    }

    // Save data to shared preferences from user inputs to edit texts
    private fun saveProfile(){

        val sharedPreference = getSharedPreferences(profileData, MODE_PRIVATE)
        sharedPreference.edit {
            putString("name", nameEditText.text.toString())
            putString("email", emailEditText.text.toString())
            putString("phone", phoneEditText.text.toString())
            putString("class", classEditText.text.toString())
            putString("major", majorEditText.text.toString())

            putString(
                "gender",   when (genderRadioGroup.checkedRadioButtonId) {
                    R.id.up_radiobutton_male -> getString(R.string.radiobutton_male)
                    R.id.up_radiobutton_female -> getString(R.string.radiobutton_female)
                    else -> ""
                }
            )
        }

        if (tempImgFile.exists()) {
            tempImgFile.copyTo(finalImgFile, overwrite = true)
            tempImgFile.delete()
        }
    }

    // Add listeners to save, cancel and change button
    // Save button: call saveProfile(), finish
    // Cancel button: finish
    // Change: show a dialog to choose how to select profile image
    private fun handleButtons(){

        val changeButton : Button = findViewById(R.id.up_button_camera)
        changeButton.setOnClickListener{
            showImageSelectionDialog()
        }

        val saveButton : Button = findViewById(R.id.up_button_save)
        saveButton.setOnClickListener {
            saveProfile()
            Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show()
            finish()
        }

        val cancelButton : Button = findViewById(R.id.up_button_cancel)
        cancelButton.setOnClickListener {
            if (tempImgFile.exists()) tempImgFile.delete()
            finish()
        }
    }

    // Create a dialog with two options (Open Camera, Select from Gallery)
    private fun showImageSelectionDialog() {
        val dialog = OptionDialogFragment()
        val args = Bundle()
        args.putString(InputDialogFragment.Companion.TITLE_KEY, "Select Profile Image")
        args.putStringArray(OptionDialogFragment.Companion.OPTIONS, resources.getStringArray(R.array.user_profile_options))
        dialog.arguments = args
        dialog.show(supportFragmentManager, "UserProfileDialog")

        supportFragmentManager.setFragmentResultListener("selectedChoice", this) { requestKey, bundle ->
            when (bundle.getInt("choice")) {
                0 -> launchCamera()
                1 -> launchGallery()
            }
        }
    }

    // Open camera
    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri)
        cameraResult.launch(intent)
    }

    // Open gallery
    private fun launchGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryResult.launch(intent)
    }
}
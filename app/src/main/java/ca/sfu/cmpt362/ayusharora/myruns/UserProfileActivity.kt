package ca.sfu.cmpt362.ayusharora.myruns

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import java.io.File
import androidx.core.content.edit

class UserProfileActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var tempImgUri: Uri
    private val tempImgFileName = "temp_profile_img.jpg"
    private lateinit var tempImgFile: File
    private val finalImgFileName = "profile_img.jpg"
    private lateinit var finalImgFile: File
    private lateinit var myViewModel: MyViewModel
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private val PROFILE_DATA = "MyRuns_UserProfile"
    private lateinit var changeButton: Button
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var classEditText: EditText
    private lateinit var majorEditText: EditText
    private lateinit var genderRadioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        setup()
        loadProfile()
        setupButtons()
        setupCameraLauncher()
    }
    private fun loadProfile(){

        val sharedPreference = getSharedPreferences(PROFILE_DATA, Context.MODE_PRIVATE)

        nameEditText.setText(sharedPreference.getString("name", ""))
        emailEditText.setText(sharedPreference.getString("email", ""))
        phoneEditText.setText(sharedPreference.getString("phone", ""))
        classEditText.setText(sharedPreference.getString("class", ""))
        majorEditText.setText(sharedPreference.getString("major", ""))

        val savedGender = sharedPreference.getString("gender", "")
        if (savedGender == getString(R.string.maleRadioButton)) {
            genderRadioGroup.check(R.id.up_radiobutton_male)
        } else if (savedGender == getString(R.string.femaleRadioButton)) {
            genderRadioGroup.check(R.id.up_radiobutton_female)
        }
    }
    private fun saveProfile(){

        val sharedPreference = getSharedPreferences(PROFILE_DATA, Context.MODE_PRIVATE)
        sharedPreference.edit {
            putString("name", nameEditText.text.toString())
            putString("email", emailEditText.text.toString())
            putString("phone", phoneEditText.text.toString())
            putString("class", classEditText.text.toString())
            putString("major", majorEditText.text.toString())

            putString(
                "gender", when (genderRadioGroup.checkedRadioButtonId) {
                    R.id.up_radiobutton_male -> getString(R.string.maleRadioButton)
                    R.id.up_radiobutton_female -> getString(R.string.femaleRadioButton)
                    else -> ""
                }
            )
        }
    }
    private fun setup(){

        imageView = findViewById(R.id.up_image_view)
        changeButton = findViewById(R.id.up_button_camera)
        saveButton = findViewById(R.id.up_button_save)
        cancelButton = findViewById(R.id.up_button_cancel)
        nameEditText = findViewById(R.id.up_edittext_name)
        emailEditText = findViewById(R.id.up_edittext_email)
        phoneEditText = findViewById(R.id.up_edittext_phone)
        classEditText = findViewById(R.id.up_edittext_class)
        majorEditText = findViewById(R.id.up_edittext_major)
        genderRadioGroup = findViewById(R.id.up_radiogroup_gender)


        // Some of the code below is adapted from lecture 2 demo (CameraDemoKotlin)
        finalImgFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), finalImgFileName)
        if (finalImgFile.exists()) {
            imageView.setImageURI(Uri.fromFile(finalImgFile))
        } else {
            imageView.setImageResource(R.drawable.default_profile)
        }

        tempImgFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            tempImgFileName)
        tempImgUri = FileProvider.getUriForFile(this,
            "ca.sfu.cmpt362.ayusharora.myruns", tempImgFile)

        myViewModel = ViewModelProvider(this)[MyViewModel::class.java]
        myViewModel.userImage.observe(this) { it ->
            imageView.setImageBitmap(it)
        }
    }

    //The code for this function is derived from lecture 2 demo (CameraDemoKotlin)
    private fun setupCameraLauncher(){

        cameraResult = registerForActivityResult(StartActivityForResult())
        { result: ActivityResult ->
            if (tempImgFile.exists()) {
                val bitmap = Util.getBitmap(this, tempImgUri)
                myViewModel.userImage.value = bitmap
            }
        }

    }

    //The code for this function is derived and extended from lecture 2 demo (CameraDemoKotlin)
    private fun setupButtons(){

        changeButton.setOnClickListener(){

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri)
            cameraResult.launch(intent)
        }

        saveButton.setOnClickListener {
            saveProfile()

            if (tempImgFile.exists()) {
                tempImgFile.copyTo(finalImgFile, overwrite = true)
                tempImgFile.delete()
            }
            Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show()
            finish()
        }

        cancelButton.setOnClickListener {
            if (tempImgFile.exists()) tempImgFile.delete()
            finish()
        }
    }
}
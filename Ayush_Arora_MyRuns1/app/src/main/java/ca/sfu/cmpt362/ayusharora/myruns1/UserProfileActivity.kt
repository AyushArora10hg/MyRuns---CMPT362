package ca.sfu.cmpt362.ayusharora.myruns1

import android.app.Activity
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
import androidx.core.content.ContextCompat
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
    private lateinit var userData: SharedPreferences
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
        setContentView(R.layout.user_profile_activity)
        Util.checkPermissions(this)
        setup()
        loadProfile()
        setupButtons()
        setupCameraLauncher()
    }
    private fun loadProfile(){

        nameEditText.setText(userData.getString("name", ""))
        emailEditText.setText(userData.getString("email", ""))
        phoneEditText.setText(userData.getString("phone", ""))
        classEditText.setText(userData.getString("class", ""))
        majorEditText.setText(userData.getString("major", ""))

        val savedGender = userData.getString("gender", "")
        if (savedGender == getString(R.string.maleRadioButton)) {
            genderRadioGroup.check(R.id.maleRadioButton)
        } else if (savedGender == getString(R.string.femaleRadioButton)) {
            genderRadioGroup.check(R.id.femaleRadioButton)
        }
    }
    private fun saveProfile(){

        userData.edit {
            putString("name", nameEditText.text.toString())
            putString("email", emailEditText.text.toString())
            putString("phone", phoneEditText.text.toString())
            putString("class", classEditText.text.toString())
            putString("major", majorEditText.text.toString())

            putString(
                "gender", when (genderRadioGroup.checkedRadioButtonId) {
                    R.id.maleRadioButton -> getString(R.string.maleRadioButton)
                    R.id.femaleRadioButton -> getString(R.string.femaleRadioButton)
                    else -> ""
                }
            )
        }
    }
    private fun setup(){

        imageView = findViewById(R.id.imageProfile)
        changeButton = findViewById(R.id.cameraButton)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        classEditText = findViewById(R.id.classEditText)
        majorEditText = findViewById(R.id.majorEditText)
        genderRadioGroup = findViewById(R.id.genderRadioGroup)

        userData = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)

        finalImgFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), finalImgFileName)
        if (finalImgFile.exists()) {
            imageView.setImageURI(Uri.fromFile(finalImgFile))
        } else {
            imageView.setImageResource(R.drawable.default_profile)
        }

        tempImgFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            tempImgFileName)
        tempImgUri = FileProvider.getUriForFile(this,
            "ca.sfu.cmpt362.ayusharora.myruns1", tempImgFile)

        myViewModel = ViewModelProvider(this)[MyViewModel::class.java]
        myViewModel.userImage.observe(this) { it -> imageView.setImageBitmap(it) }

    }
    private fun setupCameraLauncher(){

        cameraResult = registerForActivityResult(StartActivityForResult())
        { result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                if (tempImgFile.exists()) {
                    imageView.setImageURI(Uri.fromFile(tempImgFile))
                }
            }
        }

    }
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
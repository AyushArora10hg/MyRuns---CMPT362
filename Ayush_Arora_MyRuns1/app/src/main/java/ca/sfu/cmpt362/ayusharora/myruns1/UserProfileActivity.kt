package ca.sfu.cmpt362.ayusharora.myruns1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import java.io.File

class UserProfileActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var button: Button
    private lateinit var tempImgUri: Uri
    private val tempImgFileName = "temp_profile_img.jpg"
    private lateinit var myViewModel: MyViewModel
    private lateinit var cameraResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.user_profile_activity)

        imageView = findViewById(R.id.imageProfile)
        button = findViewById(R.id.cameraButton)
        Util.checkPermissions(this)

        val tempImgFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            tempImgFileName)
        tempImgUri = FileProvider.getUriForFile(this,
            "ca.sfu.cmpt362.ayusharora.myruns1", tempImgFile)

        button.setOnClickListener(){
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri)
            cameraResult.launch(intent)
        }

        myViewModel = ViewModelProvider(this)[MyViewModel::class.java]

        cameraResult = registerForActivityResult(StartActivityForResult())
        { result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                val bitmap = Util.getBitmap(this, tempImgUri)
                myViewModel.userImage.value = bitmap

            }
        }

                myViewModel.userImage.observe(this) { it -> imageView.setImageBitmap(it)
        }

        if (tempImgFile.exists()) {
            val bitmap = Util.getBitmap(this, tempImgUri)
            imageView.setImageBitmap(bitmap)
        }

    }
}
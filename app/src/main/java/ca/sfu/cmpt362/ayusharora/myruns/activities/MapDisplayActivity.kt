package ca.sfu.cmpt362.ayusharora.myruns.activities

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import ca.sfu.cmpt362.ayusharora.myruns.R

class MapDisplayActivity: AppCompatActivity() {

    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_display)

        setup()
        handleButtonClicks()
    }

    private fun setup(){
        saveButton = findViewById(R.id.md_button_save)
        cancelButton = findViewById(R.id.md_button_cancel)
    }
    private fun handleButtonClicks(){

        saveButton.setOnClickListener {
            finish()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }
}
package ca.sfu.cmpt362.ayusharora.myruns.activities

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import ca.sfu.cmpt362.ayusharora.myruns.R

class MapDisplayActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_display)
        handleButtonClicks()
    }
    private fun handleButtonClicks(){

        val saveButton = findViewById<Button>(R.id.md_button_save)
        saveButton.setOnClickListener {
            finish()
        }

        val cancelButton = findViewById<Button>(R.id.md_button_cancel)
        cancelButton.setOnClickListener {
            finish()
        }
    }
}
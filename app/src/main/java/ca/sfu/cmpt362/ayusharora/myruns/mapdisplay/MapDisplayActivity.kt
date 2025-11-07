package ca.sfu.cmpt362.ayusharora.myruns.mapdisplay

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import ca.sfu.cmpt362.ayusharora.myruns.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class MapDisplayActivity: AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_display)

        showGoogleMap()
        handleButtonClicks()
    }

    private fun showGoogleMap(){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.md_fragment_container)
                as SupportMapFragment
        mapFragment.getMapAsync (this)
    }

    override fun onMapReady(googleMap: GoogleMap){
        mMap = googleMap
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
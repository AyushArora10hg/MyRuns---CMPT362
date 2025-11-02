package ca.sfu.cmpt362.ayusharora.myruns.main

import android.content.Intent
import android.os.Bundle
import androidx.core.net.toUri
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import ca.sfu.cmpt362.ayusharora.myruns.R
import ca.sfu.cmpt362.ayusharora.myruns.WorkoutFormatter
import ca.sfu.cmpt362.ayusharora.myruns.userprofile.UserProfileActivity

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        this.addPreferencesFromResource(R.xml.preference)

        PreferenceManager.setDefaultValues(requireContext(), R.xml.preference, false)

        val userProfilePreference = findPreference<Preference>("user_profile_preference")
        userProfilePreference?.setOnPreferenceClickListener {
            val intent = Intent(requireContext(), UserProfileActivity::class.java)
            startActivity(intent)
            true
        }

        val webpagePreference = findPreference<Preference>("webpage_preference")
        webpagePreference?.setOnPreferenceClickListener {
            val url = getString(R.string.link_webpage)
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(intent)
            true
        }

        val unitPreference = findPreference<Preference>("unit_preference")
        unitPreference?.setOnPreferenceChangeListener{ _,newValue ->
            val unitArray = resources.getStringArray(R.array.unit_values)
            if (newValue == unitArray[1]){
                WorkoutFormatter.convertToImperial()
            } else {
                WorkoutFormatter.convertToMetric()
            }
            true
        }
    }
}
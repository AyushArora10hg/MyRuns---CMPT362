package ca.sfu.cmpt362.ayusharora.myruns

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.core.net.toUri

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        this.addPreferencesFromResource(R.xml.preference)

        val userProfilePreference = findPreference<Preference>("user_profile_preference")
        userProfilePreference?.setOnPreferenceClickListener {
            val intent = Intent(requireContext(), UserProfileActivity::class.java)
            startActivity(intent)
            true
        }

        val webpagePreference = findPreference<Preference>("webpage_preference")
        webpagePreference?.setOnPreferenceClickListener {
            val url = getString(R.string.webpageURL)
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(intent)
            true
        }
    }
}
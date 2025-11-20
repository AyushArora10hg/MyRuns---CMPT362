package ca.sfu.cmpt362.ayusharora.myruns

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import ca.sfu.cmpt362.ayusharora.myruns.main.history.HistoryFragment
import ca.sfu.cmpt362.ayusharora.myruns.main.SettingsFragment
import ca.sfu.cmpt362.ayusharora.myruns.main.StartFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.ArrayList

// Most of the code for this activity is adapted from XD's class demos/lectures
class MainActivity : AppCompatActivity() {
    private lateinit var fragments: ArrayList<Fragment>
    private lateinit var tabLayoutMediator: TabLayoutMediator

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)
        checkPermissions(this)
        setupFragments()
        handleTabLayout()
    }
    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
    }

    private fun setupFragments(){
        fragments = ArrayList()
        val startFragment = StartFragment()
        val historyFragment = HistoryFragment()
        val settingFragment = SettingsFragment()

        fragments.add(startFragment)
        fragments.add(historyFragment)
        fragments.add(settingFragment)
    }

    private fun handleTabLayout(){
        val viewPager2: ViewPager2 = findViewById(R.id.main_viewpager)
        val myFragmentStateAdapter = MyFragmentStateAdapter(this, fragments)
        viewPager2.adapter = myFragmentStateAdapter
        viewPager2.offscreenPageLimit = 2

        val tabConfigurationStrategy: TabLayoutMediator.TabConfigurationStrategy =
            TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab, position: Int ->
                tab.text = resources.getStringArray(R.array.main_tab_titles)[position]
            }
        val tabLayout: TabLayout = findViewById(R.id.main_tablayout)
        tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager2, tabConfigurationStrategy)
        tabLayoutMediator.attach()
    }

    // Ask for user permission
    // Code copied from XD's lecture demos
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkPermissions(activity: Activity?) {
        activity ?: return

        val permissions = mutableListOf<String>()
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        permissions.add(Manifest.permission.POST_NOTIFICATIONS)

        // Camera permission (available on all versions)
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA)
        }

        // Storage permissions - different for different Android versions
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES)
            != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        }

        // Request all needed permissions at once
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, permissions.toTypedArray(), 0)
        }
    }
}
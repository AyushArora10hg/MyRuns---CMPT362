package ca.sfu.cmpt362.ayusharora.myruns

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout

private lateinit var mainTabLayout : TabLayout
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)
        Util.checkPermissions(this)

        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFragmentContainer, StartFragment())
            .commit()

        mainTabLayout = findViewById(R.id.mainTabLayout)

        mainTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val fragment = when (tab.position) {
                    0 -> StartFragment()
                    1 -> HistoryFragment()
                    2 -> SettingsFragment()
                    else -> StartFragment()
                }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.mainFragmentContainer, fragment)
                    .commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) = Unit
            override fun onTabReselected(tab: TabLayout.Tab?) = Unit
        })
    }
}
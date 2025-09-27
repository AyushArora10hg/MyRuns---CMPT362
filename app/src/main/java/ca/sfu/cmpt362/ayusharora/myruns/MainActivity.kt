package ca.sfu.cmpt362.ayusharora.myruns

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import java.util.ArrayList

private lateinit var viewPager2: ViewPager2
private lateinit var tabLayout: TabLayout
private lateinit var myMyFragmentStateAdapter: MyFragmentStateAdapter
private lateinit var fragments: ArrayList<Fragment>
private lateinit var startFragment: StartFragment
private lateinit var historyFragment: HistoryFragment
private lateinit var settingFragment: SettingsFragment
private val tabTitles = arrayOf("START", "HISTORY", "SETTINGS")
private lateinit var tabConfigurationStrategy: TabConfigurationStrategy
private lateinit var tabLayoutMediator: TabLayoutMediator
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)
        Util.checkPermissions(this)

        viewPager2 = findViewById(R.id.main_viewpager)
        tabLayout = findViewById(R.id.tab)

        fragments = ArrayList()
        startFragment = StartFragment()
        historyFragment = HistoryFragment()
        settingFragment = SettingsFragment()
        fragments.add(startFragment)
        fragments.add(historyFragment)
        fragments.add(settingFragment)

        myMyFragmentStateAdapter = MyFragmentStateAdapter(this, fragments)
        viewPager2.adapter = myMyFragmentStateAdapter

        tabConfigurationStrategy = TabConfigurationStrategy {
                tab: TabLayout.Tab, position: Int ->
            tab.text = tabTitles[position] }
        tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager2, tabConfigurationStrategy)
        tabLayoutMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
    }
}

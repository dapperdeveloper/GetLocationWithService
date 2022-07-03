package com.dapper.matrixtestinkotlin

/**
 * Created by Tahseen Khan on 30 June 2022
 * */
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.dapper.matrixtestinkotlin.databinding.ActivityMainBinding
import com.dapper.matrixtestinkotlin.fragment.Home
import com.dapper.matrixtestinkotlin.fragment.Settings
import com.dapper.matrixtestinkotlin.utils.Sessions

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val home = Home()
    private val settings = Settings()

    lateinit var sessions: Sessions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        sessions = Sessions(this)
        replaceFragment(home)
        binding.navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navigation_1 -> replaceFragment(home)
                R.id.navigation_2 -> replaceFragment(settings)
            }
            true
        }
    }
    private fun replaceFragment(fragment: Fragment){
        if (fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayout_main, fragment)
            transaction.commit()
        }
    }


    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()

        }
        else{
            doubleBackToExitPressedOnce = true
            Toast.makeText(
                this,
                resources.getString(R.string.Please_click_BACK_again_to_exit),
                Toast.LENGTH_SHORT
            ).show()

            Handler().postDelayed({
                doubleBackToExitPressedOnce = false
            }, 2000)

        }
    }
    override fun onDestroy() {
        super.onDestroy()
        sessions.setServiceStarted(false)
    }
}
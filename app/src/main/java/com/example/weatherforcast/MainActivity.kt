package com.example.weatherforcast

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.weatherforcast.alert.view.AlarmReceiver
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var navigationView: NavigationView
   private lateinit var drawerLayout: DrawerLayout
   private lateinit var actionBar: ActionBar
   private lateinit var appBarConfiguration: AppBarConfiguration

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationView = findViewById(R.id.navigation)
        drawerLayout = findViewById(R.id.drawerLayout)

        actionBar = supportActionBar!!

        actionBar.setHomeAsUpIndicator(R.drawable.menu_ic)
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)


        val navController = findNavController(this, R.id.fragmentContainerView)

        appBarConfiguration = AppBarConfiguration.Builder(
            R.id.homeFragment, R.id.favouriteFragment, R.id.alertFragment, R.id.settingsFragment
        )
            .setOpenableLayout(drawerLayout)
            .build()
        setupActionBarWithNavController(this, navController, appBarConfiguration!!)
        setupWithNavController(navigationView, navController)


        if (intent.getBooleanExtra("REQUEST_NOTIFICATION_PERMISSION", false)) {
            requestNotificationPermission()
        }
    }

    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
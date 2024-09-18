package com.example.weatherforcast

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var navigationView: NavigationView
   private lateinit var drawerLayout: DrawerLayout
   private lateinit var actionBar: ActionBar
   private lateinit var appBarConfiguration: AppBarConfiguration


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
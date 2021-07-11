package com.unisel.carrental.ui.user

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.unisel.carrental.R
import com.unisel.carrental.databinding.LoginActivityMainBinding

class LoginMainActivity : AppCompatActivity(){
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding:LoginActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LoginActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.loginToolbar)

        val navController = findNavController(R.id.login_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.login_fragment_content_main)

        return  navController.navigateUp(appBarConfiguration)
                ||super.onSupportNavigateUp()
    }
}
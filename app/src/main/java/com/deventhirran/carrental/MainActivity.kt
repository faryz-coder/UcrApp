package com.deventhirran.carrental

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.deventhirran.carrental.databinding.ActivityMainBinding
import com.deventhirran.carrental.databinding.ActivityMainRentalBinding
import com.deventhirran.carrental.ui.user.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    var username: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        username = intent.getStringExtra("username").toString()
        val type = intent.getStringExtra("type").toString()
        var viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        viewModel.id = username
        viewModel.type = type

        Toast.makeText(applicationContext, "Welcome: ${viewModel.id}", Toast.LENGTH_SHORT).show()

        signInAnonymously()

        if (type == "owner") {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            val navView: BottomNavigationView = binding.navView
            setSupportActionBar(binding.toolbar)
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        } else {
            var binding2: ActivityMainRentalBinding = ActivityMainRentalBinding.inflate(layoutInflater)
            setContentView(binding2.root)
            val navView: BottomNavigationView = binding2.navView
            setSupportActionBar(binding2.toolbar)
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }



    }

    private fun signInAnonymously() {
        // [START signin_anonymously
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("bomoh", "signin:success")
                    val user = auth.currentUser
                    Log.d("bomoh", "currentUser:$user")
                } else {
                    Log.d("bomoh", "signin:failed")

                }
            }
    }
}
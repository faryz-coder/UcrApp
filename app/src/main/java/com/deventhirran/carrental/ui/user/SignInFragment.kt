package com.deventhirran.carrental.ui.user

import android.content.Intent
import android.os.Bundle
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.deventhirran.carrental.MainActivity
import com.deventhirran.carrental.R
import com.deventhirran.carrental.databinding.FragmentSignInBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signupInputUsername.setOnClickListener {
            Toast.makeText(context, "Input Username", Toast.LENGTH_SHORT).show()
        }
        binding.signupInputPassword.setOnClickListener {
            Toast.makeText(context, "Input Password", Toast.LENGTH_SHORT).show()
        }
        binding.buttonSignUp.setOnClickListener {
            // check if the form is valid
            if (valid()){
                // proceed to sign in process
                db.collection("user").document(binding.signupInputUsername.text.toString())
                    .get()
                    .addOnSuccessListener {
                        d("debugLogin", "signIn:Found Matching Username")
                        // check if the password the same
                        if (binding.signupInputPassword.text.toString() == it.getField<String>("password").toString()) {
                            // proceeed to login
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            intent.putExtra("username", binding.signupInputUsername.text.toString())
                            intent.putExtra("type", it.getField<String>("type").toString())
                            startActivity(intent)
                        } else {
                            Toast.makeText(context, "Wrong Password", Toast.LENGTH_SHORT).show()
                            d("debugLogin", "signIn:Wrong Password")
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "FirebaseError: $e", Toast.LENGTH_SHORT).show()
                        d("debugLogin", "FirebaseError: $e")
                    }
            } else {
                Snackbar.make(requireView(), "Please Enter Username & Password", Snackbar.LENGTH_SHORT).show()
            }
        }
        binding.signUpSignin.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
    }

    private fun valid(): Boolean {
        var valid = true
        if (binding.signupInputUsername.text.isNullOrEmpty()) {
            binding.signupInputUsername.error = "Enter Username"
            valid = false
        } else {
            binding.signupInputUsername.error = null
        }

        if (binding.signupInputPassword.text.isNullOrEmpty()) {
            binding.signupInputPassword.error = "Enter Password"
            valid = false
        } else {
            binding.signupInputPassword.error = null
        }

        return valid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
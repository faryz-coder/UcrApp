package com.unisel.carrental.ui.user

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.unisel.carrental.R
import com.unisel.carrental.databinding.FragmentSignUpBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    val db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signupInputUsername.setOnClickListener {
            Toast.makeText(requireContext(), "Enter Username", Toast.LENGTH_SHORT).show()
        }
        binding.signupInputPassword.setOnClickListener {
            Toast.makeText(requireContext(), "Enter Password", Toast.LENGTH_SHORT).show()
        }
        binding.buttonSignUp.setOnClickListener {
            // check if info is valid
            if (valid()) {
                Snackbar.make(requireView(), "Processing ... ", Snackbar.LENGTH_SHORT).show()
                binding.buttonSignUp.isEnabled = false
                // Create Account
                val data = hashMapOf(
                    "username" to binding.signupInputUsername.text.toString(),
                    "password" to binding.signupInputPassword.text.toString(),
                    "type" to if (binding.radioButtonRental.isChecked) {
                        "rental"
                    } else {
                        "owner"
                    }
                )
                db.collection("user").document(binding.signupInputUsername.text.toString())
                    .set(data)
                    .addOnSuccessListener {
                        Snackbar.make(view, "Account Created", Snackbar.LENGTH_SHORT).show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            requireView().findNavController().popBackStack()
                        },1300)
                    }
                binding.buttonSignUp.isEnabled = true
            }
        }

        binding.signUpSignin.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
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

        if (!binding.radioButtonOwner.isChecked and !binding.radioButtonRental.isChecked) {
            Toast.makeText(requireContext(), "Select Account Type", Toast.LENGTH_SHORT).show()
            d("debugSignup", "debugSignup:${binding.radioButtonOwner.isChecked}, ${binding.radioButtonRental.isChecked}")
            binding.radioButtonRental.error = ""
            binding.radioButtonOwner.error = ""
            valid = false
        } else {
            binding.radioButtonRental.error = null
            binding.radioButtonOwner.error = null
        }

        return valid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
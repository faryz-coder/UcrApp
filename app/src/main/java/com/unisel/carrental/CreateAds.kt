package com.unisel.carrental

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.unisel.carrental.databinding.FragmentCreateAdsBinding
import com.unisel.carrental.ui.user.viewmodel.LoginViewModel
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class CreateAds : Fragment() {

    private var _binding: FragmentCreateAdsBinding? = null

    private val binding get() = _binding!!
    private var imageUri : Uri? = null
    private val storage = Firebase.storage
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCreateAdsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var viewModel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)
        d("debugCreateAds", "debugCreateAds: id:${viewModel.id}")
        binding.buttonConfirm.setOnClickListener {
            // Check form
            if (valid()) {
                d("bomoh", "form is valid")
                // [UPLOAD] Data
                //upload image
                val data = hashMapOf(
                    "title" to binding.editListingName.text.toString(),
                    "charge" to binding.editHourlyRate.text.toString(),
                    "id" to viewModel.id.toString()
                )
                db.collection("ads")
                    .add(data)
                    .addOnSuccessListener {
                        val documentId = it.id
                        // UPLOAD IMAGE
                        val storageRef = storage.reference.child(documentId)
                        val uploadImg = storageRef.putFile(imageUri!!)

                        val urlTask = uploadImg.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let {
                                    throw it
                                }
                            }
                            storageRef.downloadUrl
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val imgUrl = task.result.toString()
                                val imgData = hashMapOf(
                                    "image" to imgUrl
                                )
                                db.collection("ads").document(documentId)
                                    .set(imgData, SetOptions.merge())
                                    .addOnSuccessListener {
                                        // [ADD TO USER DB]
                                        val data2 = hashMapOf(
                                            "title" to binding.editListingName.text.toString(),
                                            "charge" to binding.editHourlyRate.text.toString(),
                                            "image" to imgUrl
                                        )
                                        db.collection("user").document(viewModel.id.toString()).collection("Post").document(documentId)
                                            .set(data2)
                                            .addOnSuccessListener {
                                                Toast.makeText(requireContext(), "Ads Posted!", Toast.LENGTH_SHORT).show()
                                                Handler(Looper.getMainLooper()).postDelayed({
                                                    requireView().findNavController().popBackStack()
                                                }, 1200)
                                            }
                                            .addOnFailureListener { e ->
                                                d("debugCreateAds", "debugCreateAds:Error: $e")
                                            }
                                    }
                            }
                        }

                    }

            }
        }

        var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            imageUri = result.data?.data
            d("bomoh", "image uri: $imageUri")
            binding.imageView.isGone = false
            binding.imageView.setImageURI(imageUri)

        }

        // SELECT IMAGE
        binding.buttonAddImage.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            resultLauncher.launch(gallery)

        }


    }

    private fun valid(): Boolean {
        var valid = true

        val listingName = binding.editListingName
        val hourlyRate = binding.editHourlyRate

        if (listingName.text.isNullOrEmpty()) {
            listingName.error = "Please Enter Listing Name"
            valid = false
        } else {
            listingName.error = null
        }

        if (hourlyRate.text.isNullOrEmpty()) {
            hourlyRate.error = "Please Enter Hourly Rate"
            valid = false
        } else {
            hourlyRate.error = null
        }

        if (imageUri.toString().isNullOrEmpty()) {
            Toast.makeText(context, "Please Select Image", Toast.LENGTH_SHORT).show()
        }

        return valid

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
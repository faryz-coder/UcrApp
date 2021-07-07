package com.deventhirran.carrental

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import com.deventhirran.carrental.databinding.FragmentCreateAdsBinding


class CreateAds : Fragment() {

    private var _binding: FragmentCreateAdsBinding? = null

    private val binding get() = _binding!!
    private var imageUri : Uri? = null
    private var imageUploadURL : String? = null

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

        binding.buttonConfirm.setOnClickListener {
            // Check form
            if (valid()) {
                d("bomoh", "form is valid")
            }
        }

        var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            imageUri = result.data?.data
            d("bomoh", "image uri: $imageUri")
            binding.imageView.isGone = false
            binding.imageView.setImageURI(imageUri)

        }


        // UPLOAD IMAGE
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

        return valid

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
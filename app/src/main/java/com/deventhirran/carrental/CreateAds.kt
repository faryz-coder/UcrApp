package com.deventhirran.carrental

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.deventhirran.carrental.databinding.FragmentCreateAdsBinding


class CreateAds : Fragment() {

    private var _binding: FragmentCreateAdsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCreateAdsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

}
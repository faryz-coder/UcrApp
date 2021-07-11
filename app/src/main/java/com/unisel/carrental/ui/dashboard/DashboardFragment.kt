package com.unisel.carrental.ui.dashboard

import android.os.Bundle
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.unisel.carrental.R
import com.unisel.carrental.databinding.FragmentDashboardBinding
import com.unisel.carrental.ui.user.viewmodel.LoginViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null
    val db = Firebase.firestore

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textDashboard
//        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        var viewModel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)
        val recyclerview = binding.listPostedAdsRecyclerView
        val listAds = mutableListOf<ListAds>()
        val listAdapter = AdsList(listAds)

        recyclerview.apply {
            layoutManager = LinearLayoutManager(this@DashboardFragment.context)
            adapter = listAdapter
        }

        // listen to realtime updates
        val docRef = db.collection("user").document(viewModel.id.toString()).collection("Post")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snapshot != null) {
                listAds.clear()
                for (result in snapshot) {

                    val adsTitle = result.getField<String>("title").toString()
                    val adsPrice = result.getField<String>("charge").toString()
                    val adsImg = result.getField<String>("image").toString()
                    d("debugAdsList", "debugAdsList: ${result.id}, $adsPrice")
                    listAds.add(ListAds(result.id, adsTitle, adsPrice, adsImg, viewModel.type.toString()))
                    listAdapter.notifyDataSetChanged()
                }
            }
        }

//        db.collection("user").document(viewModel.id.toString()).collection("Post")
//            .get()
//            .addOnSuccessListener { results ->
//                for (result in results) {
//                    d("debugAdsList", "debugAdsList: ${result.id}")
//
//                }
//            }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingActionButtonCreateAds.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_dashboard_to_createAds)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
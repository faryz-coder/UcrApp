package com.deventhirran.carrental.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.deventhirran.carrental.R
import com.deventhirran.carrental.databinding.FragmentHomeBinding
import com.deventhirran.carrental.ui.dashboard.AdsList
import com.deventhirran.carrental.ui.dashboard.ListAds
import com.deventhirran.carrental.ui.notifications.BookingAds
import com.deventhirran.carrental.ui.notifications.BookingList
import com.deventhirran.carrental.ui.user.viewmodel.LoginViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private val db = Firebase.firestore

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        var viewModel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)

        val recyclerview = binding.listPostedAdsRecyclerView
        val listAds = mutableListOf<ListAds>()
        val listAdapter = AdsList(listAds)



        recyclerview.apply {
            layoutManager = LinearLayoutManager(this@HomeFragment.context)
            adapter = listAdapter
        }

        //admin
        val recyclerView2 = binding.bookingRecyclerview
        val AdsBooking = mutableListOf<BookingAds>()
        val adapterBooking = BookingList(AdsBooking)

        recyclerView2.apply {
            layoutManager = LinearLayoutManager(this@HomeFragment.context)
            adapter = adapterBooking
        }

        if (viewModel.type != "owner") {
            recyclerview.isGone = false
            recyclerView2.isGone = true
        } else {
            recyclerview.isGone = true
            recyclerView2.isGone = false
        }

        // listen to realtime updates
        if (viewModel.type != "owner") {
            val docRef = db.collection("ads")
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
                        Log.d("debugAdsList", "debugAdsList: ${result.id}, $adsPrice")
                        listAds.add(ListAds(result.id, adsTitle, adsPrice, adsImg, viewModel.type.toString()))
                        listAdapter.notifyDataSetChanged()
                    }
                }
            }
        } else {
            val docRef = db.collection("user").document(viewModel.id.toString()).collection("Booking")
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    AdsBooking.clear()
                    for (result in snapshot) {
                        val Title = result.getField<String>("title").toString()
                        val Price = result.getField<String>("charge").toString()
                        val Img = result.getField<String>("image").toString()
                        val Total = result.getField<String>("total").toString()
                        val Status = result.getField<String>("status").toString()
                        val StartDate = result.getField<String>("start_date").toString()
                        val StartTime = result.getField<String>("start_time").toString()
                        val EndDate = result.getField<String>("end_date").toString()
                        val EndTime = result.getField<String>("end_time").toString()
                        val ClientId = result.getField<String>("clientId").toString()
                        val AdsOwnerId = result.getField<String>("adsOwnerId").toString()

                        if (Status == "pending") {
                            AdsBooking.add(BookingAds(Title, Price, Total, Img, StartDate, StartTime, EndDate, EndTime, ClientId, Status, viewModel.type.toString(), viewModel.id.toString(), result.id, AdsOwnerId))
                            adapterBooking.notifyDataSetChanged()
                        }
                    }
                }
            }

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
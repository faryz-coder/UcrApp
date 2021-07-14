package com.unisel.carrental.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.unisel.carrental.databinding.FragmentNotificationsBinding
import com.unisel.carrental.ui.user.viewmodel.LoginViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null
    private val db = Firebase.firestore

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)
        val viewModel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val recyclerView = binding.bookingRecyclerview
        val AdsBooking = mutableListOf<BookingAds>()
        val adapterBooking = BookingList(AdsBooking)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@NotificationsFragment.context)
            adapter = adapterBooking
        }

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
                    val ClientName = result.getField<String>("clientName").toString()
                    val ClientPhone = result.getField<String>("clientPhone").toString()

                    if (viewModel.type != "owner") {
                        AdsBooking.add(BookingAds(Title, Price, Total, Img, StartDate, StartTime, EndDate, EndTime, ClientId, Status, "null", viewModel.id.toString(), result.id, AdsOwnerId, ClientName, ClientPhone))
                        adapterBooking.notifyDataSetChanged()
                    } else if (viewModel.type == "owner" && Status != "pending") {
                        AdsBooking.add(BookingAds(Title, Price, Total, Img, StartDate, StartTime, EndDate, EndTime, ClientId, Status, "null", viewModel.id.toString(), result.id, AdsOwnerId,  ClientName, ClientPhone))
                        adapterBooking.notifyDataSetChanged()
                    }
//                    if (Status != "pending") {
//                        AdsBooking.add(BookingAds(Title, Price, Total, Img, StartDate, StartTime, EndDate, EndTime, ClientId, Status, "null", viewModel.id.toString(), result.id, AdsOwnerId))
//                        adapterBooking.notifyDataSetChanged()
//                    }

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
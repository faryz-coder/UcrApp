package com.deventhirran.carrental.ui.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.deventhirran.carrental.R
import com.deventhirran.carrental.databinding.FragmentDetailsBinding
import com.deventhirran.carrental.ui.user.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class DetailsFragment : Fragment() {
    private var _binding : FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    val db = Firebase.firestore
    val timeFormat: String = "hh:mm aa" // in 12 hour format // aa = am||pm
    var startDate : String = ""
    var startTime: String = ""
    var endDate : String = ""
    var endTime : String = ""
    var uid: String? = null
    var price: String? = null
    var total: Float? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        uid = arguments?.getString("uid").toString()
        db.collection("ads").document(uid!!)
            .get()
            .addOnSuccessListener {
                price = it.getField<String>("charge").toString()
                binding.detailsTitle.text = it.getField<String>("title").toString()
                binding.detailsPrice.text = "RM" + price
            }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var viewModel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)

        binding.detailsDateStart.setOnClickListener {
            setDate(true) // set Start
        }
        binding.detailsDateEnd.setOnClickListener {
            setDate(false) // set End
        }
        binding.detailsButtonBooked.setOnClickListener {
            if (valid()) {
                /// Booking process
                db.collection("ads").document(uid!!)
                    .get()
                    .addOnSuccessListener {
                        val title = it.getField<String>("title")
                        val img = it.getField<String>("image")
                        val price = it.getField<String>("charge")
                        val adsOwnerId = it.getField<String>("id")
//                        val data = hashMapOf(
//                            "title" to title,
//                            "image" to img,
//                            "charge" to price,
//                            "id" to adsOwnerId,
//                            "status" to "pending"
//                        )
                        val data = hashMapOf(
                            "title" to title,
                            "image" to img,
                            "total" to total.toString(),
                            "clientId" to viewModel.id.toString(),
                            "start_date" to startDate,
                            "start_time" to startTime,
                            "end_date" to endDate,
                            "end_time" to endTime,
                            "status" to "pending"
                        )

                        // add to the user db
                        db.collection("user").document(viewModel.id.toString()).collection("Booking").document(uid!!)
                            .set(data)
                            .addOnSuccessListener {

                                db.collection("user").document(adsOwnerId.toString()).collection("Booking").document(uid!!)
                                    .set(data)
                                    .addOnSuccessListener {
                                        d("debugDetails", "debugDetails:SubmitBooking:Success")
                                        Snackbar.make(requireView(), "Thanks for booking", Snackbar.LENGTH_SHORT).show()
//                                binding.detailsButtonBooked.isEnabled = false
                                    }
                            }
                    }
            }
        }


    }

    private fun valid(): Boolean {
        var valid = true
        if (startDate.isNotEmpty() && startTime.isNotEmpty() && endDate.isNotEmpty() && endTime.isNotEmpty()) {
            binding.detailsDateStart.error = null
            binding.detailsDateEnd.error = null
        }else {
            Snackbar.make(requireView(), "Please Choose Booking Time", Snackbar.LENGTH_SHORT).show()
            binding.detailsDateStart.error = ""
            binding.detailsDateEnd.error = ""
            valid = false
        }
        return valid
    }

    private fun setDate(b: Boolean) {
        val calendar = Calendar.getInstance()
        val theDay = calendar.get(Calendar.DAY_OF_MONTH)
        val theMonth = calendar.get(Calendar.MONTH)
        val theYear = calendar.get(Calendar.YEAR)
        DatePickerDialog(this.requireContext(),DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            d("debugDetails", "debugDetails: $year, $month, $dayOfMonth")
            if (b) {
                // Set Start
                startDate = "$year/$month/$dayOfMonth"
                setTime(b)
            } else {
                endDate = "$year/$month/$dayOfMonth"
                setTime(b)
            }

        }, theYear, theMonth, theDay).show()

    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun setTime(b: Boolean) {
        var t: String? = null
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            val time = SimpleDateFormat(timeFormat).format(cal.time)
            d("debugDetails", "debugDetails: $time")
            if (b) {
                startTime = time
                binding.detailsDateStart.text = "$startDate $startTime"
                if (startDate.isNotEmpty() && startTime.isNotEmpty() && endDate.isNotEmpty() && endTime.isNotEmpty()) {
                    getTotal()
                }
            } else {
                endTime = time
                binding.detailsDateEnd.text = "$endDate $endTime"
                if (startDate.isNotEmpty() && startTime.isNotEmpty() && endDate.isNotEmpty() && endTime.isNotEmpty()) {
                    getTotal()
                }
            }
        }
        TimePickerDialog(this.context, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
    }

    private fun getTotal() {
        // Calculate the total cost
        val formatter = DateTimeFormatter.ofPattern("yyyy/M/d h:m a");
        val date1 = LocalDateTime.parse("$startDate $startTime", formatter)
        val date2 = LocalDateTime.parse("$endDate $endTime", formatter)

        val diff = ChronoUnit.HOURS.between(date1,date2)
        d("debugDetails", "TIME: $diff")
        d("debugDetails", "COST: ${price!!.toFloat() * diff}")
        total = price!!.toFloat() * diff

    }


}
package com.unisel.carrental.ui.notifications

import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.unisel.carrental.R
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import java.util.*

class BookingList(private val adsBooking: MutableList<BookingAds>) :
    RecyclerView.Adapter<BookingList.ViewHolder>() {
    private val db = Firebase.firestore
    class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val title : TextView = itemView.findViewById(R.id.booking_list_title)
        val img: ImageView = itemView.findViewById(R.id.booking_list_img)
        val clientId: TextView = itemView.findViewById(R.id.booking_list_clientId)
        val startDate: TextView = itemView.findViewById(R.id.booking_list_startDate)
        val endDate: TextView = itemView.findViewById(R.id.booking_list_endDate)
        val startTime: TextView = itemView.findViewById(R.id.booking_list_startTime)
        val endTime: TextView = itemView.findViewById(R.id.booking_list_endTime)
        val total: TextView = itemView.findViewById(R.id.booking_list_total)
        val status: TextView = itemView.findViewById(R.id.booking_list_status)
        val name: TextView = itemView.findViewById(R.id.booking_list_name)
        val phone: TextView = itemView.findViewById(R.id.booking_list_phone)
        val bookingConfirmation : ConstraintLayout = itemView.findViewById(R.id.booking_list_for_rental)
        val btnApprove : Button = itemView.findViewById(R.id.booking_list_button_approve)
        val btnReject: Button = itemView.findViewById(R.id.booking_list_button_reject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.booking_list_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val loc = adsBooking[position]
        holder.title.text = loc.title
        holder.name.text = loc.clientName
        holder.phone.text = loc.clientPhone
        holder.clientId.text = loc.clientId
        holder.startDate.text = loc.startDate
        holder.endDate.text = loc.endDate
        holder.startTime.text = loc.startTime
        holder.endTime.text = loc.endTime
        holder.total.text = "RM "+loc.total
        holder.status.text = loc.status.uppercase(Locale.getDefault())
        Picasso.get().load(loc.image).into(holder.img)

        if (loc.type != "owner") {
            holder.itemView.findViewById<TextView>(R.id.textView7).isGone = true
            holder.clientId.isGone = true
            holder.bookingConfirmation.isGone = true
        } else {
            holder.bookingConfirmation.isGone = false
            holder.btnApprove.setOnClickListener {
                d("debugBookingList", "clientid:${loc.clientId}, AdsId:${loc.adsId}, OwnerId:${loc.ownerId}")
                // change customer booked success to Approved
                val data = hashMapOf(
                    "status" to "approved"
                )
                db.collection("user").document(loc.clientId).collection("Booking").document(loc.adsId)
                    .set(data, SetOptions.merge())
                db.collection("user").document(loc.ownerId).collection("Booking").document(loc.adsId)
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener {
                        // delete adds
                        db.collection("ads").document(loc.adsId)
                            .delete()
                            .addOnSuccessListener {
                                d("debugBookingList", "debugBookingList:remove")
                                db.collection("user").document(loc.adsOwnerId).collection("Post").document(loc.adsId)
                                    .delete()
                                    .addOnSuccessListener {
                                        d("debugBookingList", "debugBookingList:remove owner ads")
                                    }
                            }

                    }
            }
            holder.btnReject.setOnClickListener {
                d("debugBookingList", "clientid:${loc.clientId}, AdsId:${loc.adsId}, OwnerId:${loc.ownerId}")
                // change customer booked success to Approved
                val data = hashMapOf(
                    "status" to "rejected"
                )
                db.collection("user").document(loc.clientId).collection("Booking").document(loc.adsId)
                    .set(data, SetOptions.merge())
                db.collection("user").document(loc.ownerId).collection("Booking").document(loc.adsId)
                    .delete()
            }
        }
    }

    override fun getItemCount(): Int {
        return adsBooking.size
    }

}

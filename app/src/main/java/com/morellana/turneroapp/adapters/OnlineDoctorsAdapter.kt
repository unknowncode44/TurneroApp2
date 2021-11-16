package com.morellana.turneroapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.morellana.turneroapp.R
import com.morellana.turneroapp.dataclass.OnlineDoctor

class OnlineDoctorsAdapter(private val availableList: List<OnlineDoctor>): RecyclerView.Adapter<OnlineDoctorsAdapter.OnlineDoctorsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlineDoctorsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.available_doctor_card, parent, false)
        return OnlineDoctorsViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnlineDoctorsViewHolder, position: Int) {
        val currentItem = availableList[position]
        holder.dName.text = currentItem.name
        holder.dSpeciality.text = currentItem.speciality
        holder.rating.rating = currentItem.rating!!
    }

    override fun getItemCount(): Int {
        return availableList.size
    }


    class OnlineDoctorsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val dName: TextView = itemView.findViewById(R.id.available_doctor_title)
        val dSpeciality: TextView = itemView.findViewById(R.id.available_doctor_subtitle)
        val rating: RatingBar = itemView.findViewById(R.id.doctor_rating)
    }


}
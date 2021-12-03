package com.morellana.turneroapp.dashboarduser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.morellana.turneroapp.R
import com.morellana.turneroapp.dashboarduser.dataclass.OnlineDoctorData

class OnlineDoctorsAdapter(private val context: Context): RecyclerView.Adapter<OnlineDoctorsAdapter.OnlineDoctorsViewHolder>() {

    private var dataList = mutableListOf<OnlineDoctorData>()

    fun setListData(data: MutableList<OnlineDoctorData>){
        dataList = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlineDoctorsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.available_doctor_card,
            parent, false)
        return OnlineDoctorsViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnlineDoctorsViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.bindView(currentItem)
    }

    override fun getItemCount(): Int {
        return if (dataList.size > 0){
            dataList.size
        } else {
            0
        }
    }

    inner class OnlineDoctorsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindView(onlineDoctorData: OnlineDoctorData){
            itemView.findViewById<TextView>(R.id.available_doctor_title).text = onlineDoctorData.name
            itemView.findViewById<TextView>(R.id.available_doctor_subtitle).text = onlineDoctorData.speciality
            itemView.findViewById<RatingBar>(R.id.doctor_rating).rating = onlineDoctorData.rating!!
        }
    }
}
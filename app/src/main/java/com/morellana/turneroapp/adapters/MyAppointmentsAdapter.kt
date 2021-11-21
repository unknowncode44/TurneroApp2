package com.morellana.turneroapp.adapters

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.morellana.turneroapp.R
import com.morellana.turneroapp.dataclass.Appointment

class MyAppointmentsAdapter(private val myAppointments: List<Appointment>):  RecyclerView.Adapter<MyAppointmentsAdapter.MyAppointmentsViewHolder>(){

    class MyAppointmentsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val appointmentProfessional: TextView = itemView.findViewById(R.id.doctorName)
        val appointmentDate: TextView = itemView.findViewById(R.id.appointmentDate)
        val appointmentTime: TextView = itemView.findViewById(R.id.appointmentTime)
        val appointmentSpeciality: TextView = itemView.findViewById(R.id.appointmentSpeciality)
        val appointmentPlace: TextView = itemView.findViewById(R.id.appointmentPlace)
        val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.layout)
        val linearLayout: LinearLayout = itemView.findViewById(R.id.linearLayout4)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAppointmentsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.my_appointment_expandable, parent, false)
        return MyAppointmentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyAppointmentsViewHolder, position: Int) {
        val currentItem = myAppointments[position]
        holder.appointmentProfessional.text = currentItem.professional
        holder.appointmentDate.text = currentItem.date
        holder.appointmentTime.text = currentItem.time
        holder.appointmentSpeciality.text = currentItem.speciality
        holder.appointmentPlace.text = currentItem.place

        val isVisible: Boolean = currentItem.visibility
        holder.constraintLayout.visibility = if(isVisible) View.VISIBLE else View.GONE
        holder.linearLayout.setOnClickListener{
            currentItem.visibility = !currentItem.visibility
            notifyItemChanged(position)
        }


    }

    override fun getItemCount(): Int {
        return myAppointments.size
    }
}


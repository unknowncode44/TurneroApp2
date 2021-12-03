package com.morellana.turneroapp.adapters

import android.content.Context
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

class MyAppointmentsAdapter (private val context: Context):  RecyclerView.Adapter<MyAppointmentsAdapter.MyAppointmentsViewHolder>(){

    private var dataList = mutableListOf<Appointment>()

    fun setListData(data: MutableList<Appointment>){
        dataList = data
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAppointmentsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.my_appointment_expandable,
            parent, false)

        return MyAppointmentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyAppointmentsViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.bindView(currentItem)
        val isVisible: Boolean = currentItem.visibility
        holder.constraintLayout.visibility = if(isVisible) View.VISIBLE else View.GONE
        holder.linearLayout.setOnClickListener{
            currentItem.visibility = !currentItem.visibility
            notifyItemChanged(position)
        }

    }

    inner class MyAppointmentsViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.layout)
        val linearLayout: LinearLayout = itemView.findViewById(R.id.linearLayout4)

        fun bindView (appointment: Appointment){
            itemView.findViewById<TextView>(R.id.doctorName).text = appointment.professional
            itemView.findViewById<TextView>(R.id.appointmentDate).text = appointment.date
            itemView.findViewById<TextView>(R.id.appointmentTime).text = appointment.time
            itemView.findViewById<TextView>(R.id.appointmentSpeciality).text = appointment.speciality
            itemView.findViewById<TextView>(R.id.appointmentPlace).text = appointment.place

        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}


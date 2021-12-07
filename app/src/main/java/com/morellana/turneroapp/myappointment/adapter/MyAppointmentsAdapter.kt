package com.morellana.turneroapp.myappointment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.morellana.turneroapp.R
import com.morellana.turneroapp.myappointment.dataclass.AppointmentData

class MyAppointmentsAdapter(private val context: Context):  RecyclerView.Adapter<MyAppointmentsAdapter.MyAppointmentsViewHolder>(){

    private var dataList = mutableListOf<AppointmentData>()

    fun setListData(data: MutableList<AppointmentData>){
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

    override fun getItemCount(): Int {
        return if (dataList.size > 0){
            dataList.size
        } else {
            0
        }
    }

    inner class MyAppointmentsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindView (appointmentData: AppointmentData){
            itemView.findViewById<TextView>(R.id.doctorName).text = appointmentData.professional
            itemView.findViewById<TextView>(R.id.appointmentDate).text = appointmentData.date
            itemView.findViewById<TextView>(R.id.appointmentTime).text = appointmentData.time
            itemView.findViewById<TextView>(R.id.appointmentSpeciality).text = appointmentData.speciality
            itemView.findViewById<TextView>(R.id.appointmentPlace).text = appointmentData.place

        }

        val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.layout)
        val linearLayout: LinearLayout = itemView.findViewById(R.id.linearLayout4)

    }





}


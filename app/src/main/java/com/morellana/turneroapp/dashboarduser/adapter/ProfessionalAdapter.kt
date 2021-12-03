package com.morellana.turneroapp.dashboarduser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.morellana.turneroapp.R
import com.morellana.turneroapp.dashboarduser.dataclass.ProfessionalData

// creamos clase que extiende RecyclerView.Adapter, adicionalmente creamos la sub-clase MyViewHolder para manejar las vistas
class ProfessionalAdapter(private val context: Context): RecyclerView.Adapter<ProfessionalAdapter.DoctorCardViewHolder>() {

    private var dataList = mutableListOf<ProfessionalData>()

    fun setListData(data: MutableList<ProfessionalData>){
        dataList = data
    }

    //on create view holder nos traer el diseno de la vista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.row_doctor_card,
            parent, false)
        return DoctorCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorCardViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.bindView(currentItem)
    }


    // este metodo cuenta la cantidad de tarjetas que se mostraran
    override fun getItemCount(): Int {
        // ##   AQUI IMPLEMENTAMOS LA CANTIDAD DE TARJETAS, ESTO LO PODEMOS DEFINIR DESDE LA BASE DE DATOS
        return if (dataList.size > 0){
            dataList.size
        } else {
            0
        }
    }


    // subclase MyViewHolder se encargara de manejar los datos que mostraremos en la tarjeta.
    inner class DoctorCardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bindView(professionalData: ProfessionalData){

            itemView.findViewById<TextView>(R.id.doctor_name).text = professionalData.name
            itemView.findViewById<TextView>(R.id.speciality).text = professionalData.speciality
            itemView.findViewById<TextView>(R.id.ate_days).text = professionalData.ateDays
            itemView.findViewById<TextView>(R.id.ate_time).text = professionalData.ateTime
            itemView.findViewById<TextView>(R.id.mat_prof).text = professionalData.matProf

        }

    }
}
package com.morellana.turneroapp.dashboarduser.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.morellana.turneroapp.R
import com.morellana.turneroapp.dashboarduser.dataclass.SpecialtyData
import org.w3c.dom.Text

class SpecialtyAdapter(private val context: Context): RecyclerView.Adapter<SpecialtyAdapter.SpecialityCardViewHolder>()  {

    private var dataList = mutableListOf<SpecialtyData>()

    fun setListData(data: MutableList<SpecialtyData>){
        dataList = data
    }

    //on create view holder nos traer el diseno de la vista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialityCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.speciality_icon_card,
            parent, false)
        return SpecialityCardViewHolder(view)
    }
    override fun onBindViewHolder(holder: SpecialityCardViewHolder, position: Int) {
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

    inner class SpecialityCardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindView(specialtyData: SpecialtyData){
            itemView.findViewById<TextView>(R.id.speciality_name).text = specialtyData.name
        }
    }
}
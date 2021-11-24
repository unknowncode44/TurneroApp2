package com.morellana.turneroapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.morellana.turneroapp.R

class CalendarAdapter(private var daysOfMonth: ArrayList<String>, private var onItemListener: OnItemListener): RecyclerView.Adapter<CalendarViewHolder>() {
    public fun CalendarAdapter(daysOfMonth: ArrayList<String>){
        this.daysOfMonth = daysOfMonth
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.calendar_cell, parent, false)
        val layoutParams: ViewGroup.LayoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.166666).toInt()
        return CalendarViewHolder(view, onItemListener)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val currentCell = daysOfMonth[position]
        holder.dayOfMonth.text = currentCell
        holder._onItemListener.onItemClick(currentCell.toInt(), currentCell)

    }

    override fun getItemCount(): Int {
        return daysOfMonth.size
    }
}

class CalendarViewHolder(itemView: View, onItemListener: OnItemListener): RecyclerView.ViewHolder(itemView) {
    var _onItemListener = onItemListener
    val dayOfMonth: TextView = itemView.findViewById(R.id.cellDaytext)
//    val listener: View.OnClickListener = itemView.setOnClickListener(this)
    fun onClick(p0: View?) {
        _onItemListener.onItemClick(adapterPosition, dayOfMonth.text as String)
    }



}
interface OnItemListener {
    fun onItemClick(position: Int, dayText: String);
}

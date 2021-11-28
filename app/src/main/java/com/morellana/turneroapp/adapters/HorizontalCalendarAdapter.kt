package com.morellana.turneroapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.util.Log.ASSERT
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.morellana.turneroapp.R
import com.morellana.turneroapp.dataclass.AtteDays
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HorizontalCalendarAdapter(private val context: Context, private val data: ArrayList<Date>, currentDate: Calendar, changeMonth: Calendar?,
                                private val atteDays: ArrayList<String?> = arrayListOf("null", "null", "null", "null", "null","null","null")
): RecyclerView.Adapter<HorizontalCalendarAdapter.CalendarViewHolder>() {
    private var mListener: OnItemClickListener? = null // lo usaremos para los eventos de click en las celdas de los dias
    private var index = 0 // definimos el index en -1 porque...
    lateinit var db: DatabaseReference;
    private var selectCurrentDate: Boolean = true //
    private val currentMonth = currentDate[Calendar.MONTH] // mes actual
    private val currentYear = currentDate[Calendar.YEAR] // anio actual
    private val currentDay = currentDate[Calendar.DAY_OF_MONTH] // dia actual
    private val selectedDay = // aca manejamos los cambios cuando se selecciona un dia
        when {
            changeMonth != null -> changeMonth.getActualMinimum(Calendar.DAY_OF_MONTH) // si el cambio de mes no es nulo, es decir existe cambio, instanciara el dia seleccionado
            else -> currentDay
        }
    private val selectedMonth =
        when {
            changeMonth != null -> changeMonth[Calendar.MONTH] //instanciara el mes del dia seleccionado
            else -> currentMonth
        }
    private val selectedYear =
        when {
            changeMonth != null -> changeMonth[Calendar.YEAR] // instanciara el anio del dia seleccionado
            else -> currentYear
        }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalCalendarAdapter.CalendarViewHolder{
        val inflater = LayoutInflater.from(context)
        return CalendarViewHolder(inflater.inflate(R.layout.custom_calendar_day, parent, false), mListener!!)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: HorizontalCalendarAdapter.CalendarViewHolder, @SuppressLint(
        "RecyclerView"
    ) position: Int) {

        val currentItem = data[position]
        val sdf = SimpleDateFormat("EEE MMMM dd HH:mm:ss", Locale.US) // usamos el formatter para darle forma a la fecha
        val cal = Calendar.getInstance() // obtenemos una instancia del calendario
        cal.time = currentItem

        val displayMonth = cal[Calendar.MONTH]
        val displayYear = cal[Calendar.YEAR]
        val displayDay = cal[Calendar.DAY_OF_MONTH]

        try {
            val dayInWeek = sdf.parse(cal.time.toString())!!
            sdf.applyPattern("EEE")
            var textDay: String = sdf.format(dayInWeek).toString()
            when {
                textDay.equals("Mon", true) -> textDay = "Mar"
                textDay.equals("Tue", true) -> textDay = "Mie"
                textDay.equals("Wed", true) -> textDay = "Jue"
                textDay.equals("Thu", true) -> textDay = "Vie"
                textDay.equals("Fri", true) -> textDay = "Sab"
                textDay.equals("Sat", true) -> textDay = "Dom"
                textDay.equals("Sun", true) -> textDay = "Lun"
            }
            holder.txtDayInWeek.text = textDay


        } catch (ex: ParseException) {
            Log.v("Exception", ex.localizedMessage!!)
        }
        holder.txtDay.text = cal[Calendar.DAY_OF_MONTH].toString()


        if (displayYear >= currentYear)
            if (displayMonth >= currentMonth || displayYear > currentYear)
                if (displayDay >= currentDay || displayMonth > currentMonth || displayYear > currentYear) {
                    holder.linearLayout.setOnClickListener {
                        index = position
                        selectCurrentDate = false
                        holder.listener.onItemClick(position)

                        notifyDataSetChanged()
                    }

                    if (index == position)
                        makeItemSelected(holder)
                    else {
                        if (displayDay == selectedDay
                            && displayMonth == selectedMonth
                            && displayYear == selectedYear
                            && selectCurrentDate)
                            makeItemSelected(holder)

                        else {
                            val dayInWeek = sdf.parse(cal.time.toString())!!
                            sdf.applyPattern("EEE")
                            val textDay: String = sdf.format(dayInWeek).toString()
                            var match = false
                            when {
                                textDay.equals(atteDays[0], true) -> match = true
                                textDay.equals(atteDays[1], true) -> match = true
                                textDay.equals(atteDays[2], true) -> match = true
                                textDay.equals(atteDays[3], true) -> match = true
                                textDay.equals(atteDays[4], true) -> match = true
                                textDay.equals(atteDays[5], true) -> match = true
                                textDay.equals(atteDays[6], true) -> match = true
                            }
                            if (match) {
                               makeItemDefault(holder)
                            }
                            else {
                                makeItemDisabled(holder)
                            }

                        }

                    }
                } else makeItemDisabled(holder)
            else makeItemDisabled(holder)
        else makeItemDisabled(holder)

    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class CalendarViewHolder(itemView: View, val listener: OnItemClickListener): RecyclerView.ViewHolder(itemView) {
        var txtDay: TextView = itemView.findViewById(R.id.txt_date)
        var txtDayInWeek: TextView = itemView.findViewById(R.id.txt_day)
        var linearLayout: LinearLayout = itemView.findViewById(R.id.calendar_linear_layout)
        var sideBorderLeft: View = itemView.findViewById(R.id.side_border_left)
        var sideBorderRight: View = itemView.findViewById(R.id.side_border_right)

    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    private fun makeItemDisabled(holder: HorizontalCalendarAdapter.CalendarViewHolder) {
        holder.linearLayout.elevation = 10.0f
        holder.txtDay.setTextColor(ContextCompat.getColor(context, R.color.LightGrey))
        holder.txtDayInWeek.setTextColor(ContextCompat.getColor(context, R.color.LightGrey))
        holder.linearLayout.setBackgroundColor(Color.WHITE)
        holder.linearLayout.isEnabled = false
    }
    private fun makeItemSelected(holder: HorizontalCalendarAdapter.CalendarViewHolder) {
        holder.linearLayout.elevation = 10.0f
        holder.txtDay.setTextColor(ContextCompat.getColor(context, R.color.White))
        holder.txtDayInWeek.setTextColor(ContextCompat.getColor(context, R.color.White))
        holder.linearLayout.background = ContextCompat.getDrawable(context, R.drawable.shape_toolbar05)
//        holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.Secondary))
        holder.linearLayout.isEnabled = false
        holder.sideBorderLeft.setBackgroundColor(ContextCompat.getColor(context, R.color.Secondary))
        holder.sideBorderRight.setBackgroundColor(ContextCompat.getColor(context, R.color.Secondary))
    }

    private fun makeItemDefault(holder: HorizontalCalendarAdapter.CalendarViewHolder) {
        holder.linearLayout.elevation = 10.0f
        holder.txtDay.setTextColor(ContextCompat.getColor(context, R.color.Secondary))
        holder.txtDayInWeek.setTextColor(ContextCompat.getColor(context, R.color.Secondary))
        holder.linearLayout.setBackgroundColor(Color.WHITE)
        holder.linearLayout.isEnabled = true
        holder.sideBorderLeft.setBackgroundColor(ContextCompat.getColor(context, R.color.Secondary))
        holder.sideBorderLeft.setBackgroundColor(ContextCompat.getColor(context, R.color.Secondary))
    }






}

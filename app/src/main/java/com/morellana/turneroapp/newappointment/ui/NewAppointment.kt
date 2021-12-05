package com.morellana.turneroapp.newappointment.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.morellana.turneroapp.MainActivity
import com.morellana.turneroapp.R
import com.morellana.turneroapp.newappointment.adapters.HorizontalCalendarAdapter
import com.morellana.turneroapp.databinding.FragmentNewAppointmentBinding
import com.morellana.turneroapp.dashboarduser.dataclass.ProfessionalData
import com.morellana.turneroapp.newappointment.dataclass.AtteDays
import com.morellana.turneroapp.newappointment.dataclass.NewAppointmentProfessionalData
import com.morellana.turneroapp.newappointment.models.NewAppointmentViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NewAppointment : Fragment() {
    // variables de trabajo
    private var _binding: FragmentNewAppointmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var specialityArrayName: ArrayList<String>
    lateinit var professionalArrayName: ArrayList<String?>
    lateinit var professionalArrayInfo: ArrayList<NewAppointmentProfessionalData>
    private lateinit var dbRef: DatabaseReference
    lateinit var atteDays: ArrayList<String>
    lateinit var specialityInput: TextInputLayout
    private lateinit var autocompleteSpeciality: AutoCompleteTextView
    private lateinit var autocompleteProfessional: AutoCompleteTextView
    lateinit var layoutProfessionalInput: LinearLayout
    private lateinit var textCurrentMonth: TextView
    private lateinit var calendarConstraintLayout: ConstraintLayout
    private lateinit var calendarConstraintLayoutRV: ConstraintLayout
    lateinit var prevButton: Button
    lateinit var nextButton: Button
    lateinit var showAppointmentsLy: ConstraintLayout
    lateinit var professionalUid: String

    private val newAppointmentViewModel by lazy { ViewModelProviders.of(this).get(NewAppointmentViewModel::class.java) }

    //variables de calendario
    private val lastDayInCalendar = Calendar.getInstance(Locale("ES", "ar"))
    private val sdf = SimpleDateFormat("MMMM yyyy", Locale("ES", "ar"))
    private val cal = Calendar.getInstance(Locale("ES", "ar")) // FECHA ACTUAL

    // recycler calendario
    lateinit var calendarRecyclerView: RecyclerView


    //fecha actual
    private val currentDate = Calendar.getInstance(Locale("ES", "ar"))//FECHA ACTUAL
    private val currentDay = currentDate[Calendar.DAY_OF_MONTH] //DIA ACTUAL
    private val currentMonth = currentDate[Calendar.MONTH]//MES ACTUAL
    private val currentYear = currentDate[Calendar.YEAR]// ANIO ACTUAL

    //seleccion
    private var selectedDay: Int = currentDay
    private var selectedMonth: Int = currentMonth
    private var selectedYear: Int = currentYear

    // array fechas
    private val dates = ArrayList<Date>() // CONTENDRA TODAS LAS FECHAS DEL MES EN CURSO



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //lo que hacemos es animar el inflar y el desinflar
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_rigth)
        exitTransition = inflater.inflateTransition(R.transition.slide_left)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNewAppointmentBinding.inflate(inflater, container, false)
        calendarConstraintLayout = binding.calendarConstraintLayout
        calendarConstraintLayoutRV = binding.calendarRecyclerViewCL
        calendarRecyclerView = binding.calendarRecyclerView
        showAppointmentsLy = binding.showAppointments
        layoutProfessionalInput = binding.professionalInput

        professionalUid = ""

        // escondemos contenederos que luego mostraremos
        layoutProfessionalInput.isVisible = false
        calendarConstraintLayoutRV.isVisible = false
        calendarRecyclerView.isVisible = false
        calendarConstraintLayout.isVisible = false
        showAppointmentsLy.isVisible = false

        // realizamos las instancias para autocomplete
        specialityArrayName = arrayListOf<String>()
        professionalArrayName = arrayListOf<String?>()
        professionalArrayInfo = arrayListOf<NewAppointmentProfessionalData>()
        atteDays =  arrayListOf<String>()
        specialityInput = binding.newAppointmentSpeciality

        observeSpeciality() // observamos las especialidades de nuestro modelo para utilizarlos en las acciones del usuario

        // INPUT ESPECIALIDADES
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, specialityArrayName) // creamos adaptador para el array de especialiades
        autocompleteSpeciality = binding.autoCompleteSpeciality // instanciamos el autocomplete de especialidades
        autocompleteSpeciality.setAdapter(arrayAdapter) // pasamos el adaptador
        autocompleteProfessional = binding.autoCompleteProfessional // instanciamos el contenedor del input para los professionales.

        autocompleteSpeciality.setOnItemClickListener { adapterView, view, i, l -> //seteamos un click listener para encontrar el valor seleccionado
            layoutProfessionalInput.isVisible = false
            professionalArrayName.clear() // antes que nada limpiamos el array para que no se acumulen professionales
            val value: String = specialityArrayName[i].toString().lowercase() // con la posicion del array lo buscamos y lo pasamos en minusculas
            observeProfessionals(value)
            val professionalArrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, professionalArrayName)// creamos el adaptador y se lo pasamos
            autocompleteProfessional.setAdapter(professionalArrayAdapter)
            if (showAppointmentsLy.isVisible){
                calendarConstraintLayout.isVisible = false
                calendarConstraintLayoutRV.isVisible = false
                calendarRecyclerView.isVisible = false
                showAppointmentsLy.isVisible = false
            }
            layoutProfessionalInput.alpha = 0f
            layoutProfessionalInput.isVisible = true // hacemos que el contenedor con el input sea visible
            layoutProfessionalInput.animate()
                .alpha(1f)
                .setDuration(750)
                .setListener(null)


        }

        autocompleteProfessional.setOnItemClickListener{ adapterView, view, i, l ->
            val profName: String = professionalArrayName[i].toString()
            var profId: NewAppointmentProfessionalData = professionalArrayInfo[i]
            for (i in professionalArrayInfo) {
                if (i.name == profName){
                    profId = i
                }
            }
            Log.d("TRUE DAYS",professionalArrayInfo.toString())
            val selectedAtteDays: AtteDays = profId.atteDays

            Log.d("TRUE DAYS",selectedAtteDays.toString())
            atteDays.clear()
            if (selectedAtteDays.dom) {atteDays.add("Sun")}else{atteDays.add("null")}
            if (selectedAtteDays.lun) {atteDays.add("Mon")}else{atteDays.add("null")}
            if (selectedAtteDays.mar) {atteDays.add("Tue")}else{atteDays.add("null")}
            if (selectedAtteDays.mie) {atteDays.add("Wed")}else{atteDays.add("null")}
            if (selectedAtteDays.jue) {atteDays.add("Thu")}else{atteDays.add("null")}
            if (selectedAtteDays.vie) {atteDays.add("Fri")}else{atteDays.add("null")}
            if (selectedAtteDays.sab) {atteDays.add("Sat")}else{atteDays.add("null")}
            Log.d("ATTE", atteDays.toString())


            if (showAppointmentsLy.isVisible){
                showAppointmentsLy.isVisible = false
            }
            calendarConstraintLayout.alpha = 0f
            calendarConstraintLayout.isVisible = true
            calendarRecyclerView.alpha = 0f
            calendarRecyclerView.isVisible = true
            calendarConstraintLayoutRV.alpha = 0f
            calendarConstraintLayoutRV.isVisible = true

            calendarConstraintLayout.animate()
                .alpha(1f)
                .setDuration(750)
                .setListener(null)

            calendarRecyclerView.animate()
                .alpha(1f)
                .setDuration(750)
                .setListener(null)

            calendarConstraintLayoutRV.animate()
                .alpha(1f)
                .setDuration(1000)
                .setListener(null)


            setUpCalendar(null,atteDays)
            calendarRecyclerView.smoothScrollToPosition(selectedDay)

        }

        //## CALENDARIO ##
        calendarRecyclerView = binding.calendarRecyclerView
        textCurrentMonth = binding.txtCurrentMonth
        prevButton = binding.calendarPrevBtn
        nextButton = binding.calendarNextBtn

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(calendarRecyclerView)



        lastDayInCalendar.add(Calendar.MONTH, 2)

        setUpCalendar(null,atteDays)

        prevButton.setOnClickListener{
            if(cal.after(currentDate)) {
                cal.add(Calendar.MONTH, -1)
                if(cal == currentDate) {
                    setUpCalendar(null,atteDays)
                    calendarRecyclerView.layoutManager!!.scrollToPosition(0)
                }
                else
                    setUpCalendar(changeMonth = cal)
            }
        }
        nextButton.setOnClickListener{
            if(cal.before(lastDayInCalendar)) {
                cal.add(Calendar.MONTH, 1)
                setUpCalendar(changeMonth = cal, atteDays)
                calendarRecyclerView.smoothScrollToPosition(0)
            }
        }


        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "FragmentLiveDataObserve")
    fun observeSpeciality() {
        newAppointmentViewModel.fetchAppointmentSpeciality().observe(this, androidx.lifecycle.Observer {
            for (i in it){

                specialityArrayName.add(i)
            }
        })
    }
    @SuppressLint("NotifyDataSetChanged", "FragmentLiveDataObserve")
    fun observeProfessionals(speciality: String) {
        newAppointmentViewModel.fetchAppointmentProfessionals(speciality).observe(this, androidx.lifecycle.Observer {
            for (i in it){
                professionalArrayName.add(i.name)
                professionalArrayInfo.add(i)
            }
        })
    }



    private fun setUpCalendar(changeMonth: Calendar? = null, _atteDays: ArrayList<String> = arrayListOf()) {
        // primera parte
        textCurrentMonth.text = sdf.format(cal.time).capitalize()
        val timetext: String = sdf.format(cal.time)
        val monthCalendar = cal.clone() as Calendar
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        selectedDay =
            when {
                changeMonth != null ->
                    changeMonth.getActualMinimum(Calendar.DAY_OF_MONTH)

                else -> currentDay
            }
        selectedMonth =
            when {
                changeMonth != null ->changeMonth[Calendar.MONTH]
                else -> currentMonth
            }
        selectedYear =
            when {
                changeMonth != null ->changeMonth[Calendar.YEAR]
                else -> currentYear
            }

        // segunda parte

        var currentPosition = 0
        dates.clear()
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        selectedDay = monthCalendar[Calendar.DAY_OF_MONTH]




        while (dates.size < maxDaysInMonth){
            val tamanoArray: String = dates.size.toString()


            if(monthCalendar[Calendar.DAY_OF_MONTH] == selectedDay){
                currentPosition = dates.size
                dates.add(monthCalendar.time)
                monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
                selectedDay = monthCalendar[Calendar.DAY_OF_MONTH]
            }

        }


        // tercera parte
        val context_: Context = requireContext()
        calendarRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val calendarAdapter = HorizontalCalendarAdapter(context_, dates, currentDate, changeMonth, _atteDays)

        calendarRecyclerView.adapter = calendarAdapter

        when{
            currentPosition > 2 -> calendarRecyclerView.scrollToPosition(currentPosition -3)
            maxDaysInMonth - currentPosition < 2 -> calendarRecyclerView.scrollToPosition(currentPosition)
            else -> calendarRecyclerView.scrollToPosition(currentPosition)
        }

        calendarAdapter.setOnItemClickListener(object: HorizontalCalendarAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                val clickCalendar = Calendar.getInstance()
                clickCalendar.time = dates[position]
                selectedDay = clickCalendar[Calendar.DAY_OF_MONTH]
                showAppointmentsLy.alpha = 0f
                showAppointmentsLy.isVisible = true
                showAppointmentsLy.animate()
                    .alpha(1f)
                    .setDuration(750)
                    .setListener(null)

            }

        })

    }

}
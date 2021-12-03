package com.morellana.turneroapp.ui

import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.morellana.turneroapp.R
import com.morellana.turneroapp.adapters.HorizontalCalendarAdapter
import com.morellana.turneroapp.databinding.FragmentNewAppointmentBinding
import com.morellana.turneroapp.dashboarduser.dataclass.ProfessionalData
import com.morellana.turneroapp.dashboarduser.dataclass.SpecialtyData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NewAppointment : Fragment() {
    // variables de trabajo
    private var _binding: FragmentNewAppointmentBinding? = null
    private val binding get() = _binding!!
    lateinit var specialtyDataArray: ArrayList<SpecialtyData>
    lateinit var specialityArrayName: ArrayList<String?>
    lateinit var professionalArrayName: ArrayList<String?>
    private lateinit var dbRef: DatabaseReference
    lateinit var atteDays: ArrayList<String?>
    lateinit var profRef: DatabaseReference
    lateinit var specialityInput: TextInputLayout
    private lateinit var autocompleteSpeciality: AutoCompleteTextView
    private lateinit var autocompleteProfessional: AutoCompleteTextView
    lateinit var layoutProfessionalInput: LinearLayout
    private lateinit var textCurrentMonth: TextView
    private lateinit var calendarConstraintLayout: ConstraintLayout
    lateinit var prevButton: Button
    lateinit var nextButton: Button
    lateinit var showAppointmentsLy: ConstraintLayout
    lateinit var professionalUid: String



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
        calendarRecyclerView = binding.calendarRecyclerView
        showAppointmentsLy = binding.showAppointments
        layoutProfessionalInput = binding.professionalInput

        professionalUid = ""


        // escondemos contenederos que luego mostraremos
        layoutProfessionalInput.isVisible = false
        calendarRecyclerView.isVisible = false
        calendarConstraintLayout.isVisible = false
        showAppointmentsLy.isVisible = false





        // ejecutamos funcion para obtener especialidades
        getSpecialityData() // obtenemos array de especialidades

        // realizamos las instancias para autocomplete
        specialityArrayName = arrayListOf<String?>()
        specialtyDataArray = arrayListOf<SpecialtyData>()
        professionalArrayName = arrayListOf<String?>()
        atteDays =  arrayListOf<String?>()
        specialityInput = binding.newAppointmentSpeciality

        // INPUT ESPECIALIDADES
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, specialityArrayName) // creamos adaptador para el array de especialiades
        autocompleteSpeciality = binding.autoCompleteSpeciality // instanciamos el autocomplete de especialidades
        autocompleteSpeciality.setAdapter(arrayAdapter) // pasamos el adaptador

        autocompleteProfessional = binding.autoCompleteProfessional // instanciamos el contenedor del input para los professionales.

        autocompleteSpeciality.setOnItemClickListener { adapterView, view, i, l -> //seteamos un click listener para encontrar el valor seleccionado
            professionalArrayName.clear() // antes que nada limpiamos el array para que no se acumulen professionales
            val value: String = specialityArrayName[i].toString().lowercase() // con la posicion del array lo buscamos y lo pasamos en minusculas
            getProfessionalUid(value)  // corremos funcion para llenar el array de profesionales y mostramos el mismo input
            val professionalArrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, professionalArrayName)// creamos el adaptador y se lo pasamos
            autocompleteProfessional.setAdapter(professionalArrayAdapter)
            if (showAppointmentsLy.isVisible){
                calendarConstraintLayout.isVisible = false
                calendarRecyclerView.isVisible = false
                showAppointmentsLy.isVisible = false
            }
            layoutProfessionalInput.alpha = 0f
            layoutProfessionalInput.isVisible = true // hacemos que el contenedor con el input sea visible
            layoutProfessionalInput.animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(null)
        }

        autocompleteProfessional.setOnItemClickListener{ adapterView, view, i, l ->
            if (showAppointmentsLy.isVisible){
                showAppointmentsLy.isVisible = false
            }
            calendarConstraintLayout.alpha = 0f
            calendarConstraintLayout.isVisible = true
            calendarRecyclerView.alpha = 0f
            calendarRecyclerView.isVisible = true

            calendarConstraintLayout.animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(null)

            calendarRecyclerView.animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(null)


        }

        //## CALENDARIO ##
        calendarRecyclerView = binding.calendarRecyclerView
        textCurrentMonth = binding.txtCurrentMonth
        prevButton = binding.calendarPrevBtn
        nextButton = binding.calendarNextBtn

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(calendarRecyclerView)

        lastDayInCalendar.add(Calendar.MONTH, 2)




        setUpCalendar()


        prevButton.setOnClickListener{
            if(cal.after(currentDate)) {
                cal.add(Calendar.MONTH, -1)
                if(cal == currentDate) {
                    setUpCalendar()
                    calendarRecyclerView.layoutManager!!.scrollToPosition(0)
                }
                else
                    setUpCalendar(changeMonth = cal)
            }
        }
        nextButton.setOnClickListener{
            if(cal.before(lastDayInCalendar)) {
                cal.add(Calendar.MONTH, 1)
                setUpCalendar(changeMonth = cal)
                calendarRecyclerView.smoothScrollToPosition(0)
            }
        }





        return binding.root
    }

    // funcion buscar professionales por UID, usa INNER JOIN
    private fun getProfessionalUid(speciality:String) {  // pasamos la especialidad en la cual queremos encontrar professionales
        val path: String = "specialities/$speciality/professionals" // creamos el path
        dbRef = FirebaseDatabase.getInstance().getReference(path) // creamos la instancia con ese path
        dbRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) { //hacemos snapshot a los professionales de esa especialidad
                if (snapshot.exists()){
                    for (uidSnapshot in snapshot.children){ // en este bucle:
                        val uid = uidSnapshot.getValue<String>() // obtendremos los valores uid dentro del nodo professionals
                        professionalUid = uid.toString()
                        profRef = FirebaseDatabase.getInstance().getReference("users/professionals/$uid") // los buscaremos en el nodo user/professionals
                        profRef.addValueEventListener(object: ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()){
                                    populateAtteDays(professionalUid)
                                    val professional: ProfessionalData? = snapshot.getValue(ProfessionalData::class.java)
                                    val name = professional?.name.toString() // y obtendremos su nombre
                                    professionalArrayName.add(name) // lo anadiremos a este array
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                    }
                }
                else {
//                    debemos implmementar funcion que avise que no hay profesionales para esa especialidad.
                    Toast.makeText(context, "SNAPSHOT VACIO", Toast.LENGTH_SHORT).show() // si no hay professionales
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    // buscamos en la base de datos las especialidades

    private fun getSpecialityData() {
        dbRef = FirebaseDatabase.getInstance().getReference("specialities")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (specialitySnapshot in snapshot.children){
                        val specialities = specialitySnapshot.getValue(SpecialtyData::class.java)
                        specialityArrayName.add(specialities!!.name)
//                        specialityArray.add(specialities!!)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun setUpCalendar(changeMonth: Calendar? = null) {
        // primera parte
        textCurrentMonth.text = sdf.format(cal.time).capitalize()
        val timetext: String = sdf.format(cal.time)
        Log.i("COMPILACION", "++++++$timetext")
        val monthCalendar = cal.clone() as Calendar
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        selectedDay =
            when {
                changeMonth != null -> changeMonth.getActualMinimum(Calendar.DAY_OF_MONTH)
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
        val calendarAdapter = HorizontalCalendarAdapter(context_, dates, currentDate, changeMonth)
        calendarRecyclerView.adapter = calendarAdapter

        when{
            currentPosition > 2 -> calendarRecyclerView.scrollToPosition(currentPosition -3)
            maxDaysInMonth - currentPosition < 2 -> calendarRecyclerView.scrollToPosition(currentPosition)
            else -> calendarRecyclerView.scrollToPosition(currentPosition)
        }

        calendarAdapter.setOnItemClickListener(object: HorizontalCalendarAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                showAppointmentsLy.alpha = 0f
                showAppointmentsLy.isVisible = true
                showAppointmentsLy.animate()
                    .alpha(1f)
                    .setDuration(500)
                    .setListener(null)
                val clickCalendar = Calendar.getInstance()
                clickCalendar.time = dates[position]
                selectedDay = clickCalendar[Calendar.DAY_OF_MONTH]
            }

        })

    }

    private fun populateAtteDays(uid: String) {
        atteDays.clear()
        dbRef = FirebaseDatabase.getInstance().getReference("users/professionals/$uid/atteDays")
        dbRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var position: Int = 0;
                for (daySnapshot in snapshot.children) {
                    if (snapshot.value == true){
                        val weekDay:String = snapshot.key.toString()
                        Log.v("ATTEDAYS", "###### $weekDay")
                        if (position <= 6) {
                            atteDays[position] = weekDay
                            position++
                        }
                        else {
                            atteDays[position] = "null"
                            position++
                        }
                    }

                }
                Log.v("ATTEDAYS", "###### $atteDays")

            }
            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException()
            }
        })

    }




}
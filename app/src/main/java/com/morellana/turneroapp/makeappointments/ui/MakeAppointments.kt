package com.morellana.turneroapp.makeappointments.ui

import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.morellana.turneroapp.R
import com.morellana.turneroapp.databinding.FragmentMakeAppointmentsBinding
import com.morellana.turneroapp.dashboarduser.dataclass.SpecialtyData
import com.morellana.turneroapp.makeappointments.dataclass.AvailableAppointment
import com.morellana.turneroapp.makeappointments.dataclass.ProfessionalInfoDataClass
import com.morellana.turneroapp.newappointment.dataclass.AtteDays
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters

class MakeAppointments : Fragment() {
    //variables de trabajo
    private var _binding: FragmentMakeAppointmentsBinding? = null
    private val binding get() = _binding!!
    lateinit var specialtyDataArray: ArrayList<SpecialtyData>
    lateinit var specialityArrayName: ArrayList<String?>
    lateinit var professionalInfoArray: ArrayList<ProfessionalInfoDataClass>
    lateinit var selectedProfessionalUid: String
    lateinit var professionalArrayName: ArrayList<String?>
    private lateinit var dbRef: DatabaseReference
    lateinit var profRef: DatabaseReference
    lateinit var mkAppUidRef: DatabaseReference
    lateinit var specialityInput: TextInputLayout
    private lateinit var autocompleteSpeciality: AutoCompleteTextView
    private lateinit var autocompleteProfessional: AutoCompleteTextView
    lateinit var layoutProfessionalInput: LinearLayout
    lateinit var currentDate: LocalDateTime
    lateinit var dateTextView: TextView
    lateinit var mon: CheckBox
    lateinit var tue: CheckBox
    lateinit var wed: CheckBox
    lateinit var thu: CheckBox
    lateinit var fri: CheckBox
    lateinit var sat: CheckBox
    lateinit var sun: CheckBox
    lateinit var since: Spinner
    lateinit var timeArray: ArrayList<String>
    lateinit var durationArray: ArrayList<String>
    lateinit var intervalArray: ArrayList<String>
    lateinit var until: Spinner
    lateinit var appointmentDuration: Spinner
    lateinit var appointmentInterval: Spinner
    lateinit var mkAppointmentsBtn: Button
    lateinit var atteDays: AtteDays




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
        _binding = FragmentMakeAppointmentsBinding.inflate(inflater, container, false)

        // instanciamos los checkboxes
        mon = binding.checkboxMonday
        tue = binding.checkboxTuesday
        wed = binding.checkboxWednesday
        thu = binding.checkboxThursday
        fri = binding.checkboxFriday
        sat = binding.checkboxSaturday
        sun = binding.checkboxSunday

        // instanciamos los spinners
        until = binding.untilHour
        since = binding.sinceHour
        appointmentDuration = binding.appointmentDuration
        appointmentInterval = binding.appointmentInterval

        // instanciamos el boton
        mkAppointmentsBtn = binding.btnCreateAppointments

        // instanciamos el uid
        selectedProfessionalUid = ""

        //instanciamos los arrays
        timeArray = arrayListOf<String>() //array para dropdown de Desde y Hasta spinners
        intervalArray = arrayListOf<String>() //array para dropdown de Intervalo spinner
        durationArray = arrayListOf<String>() //array para dropdown de Duracion del turno spinner
        professionalInfoArray = arrayListOf<ProfessionalInfoDataClass>() // aqui se almacenan los datos del professional
        atteDays = AtteDays(false, false, false,false, false, false, false)

        //poblando los dropdown

        //poblamos el de intervalo
        var intervalMin: Int = 5
        var intervalMax: Int = 30
        var intervalCount: Int = intervalMin
        while (intervalCount <= intervalMax) {
            intervalArray.add(intervalCount.toString())
            intervalCount += intervalMin
        }
        // poblamos la duracion de los turnos
        var durationMin = 10
        var durationMax = 90
        var durationCount: Int = durationMin
        while (durationCount <= durationMax) {
            durationArray.add(durationCount.toString())
            durationCount += durationMin
        }


        // obtenemos la fecha de hoy
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        currentDate = LocalDateTime.now()
        var nextmonday = currentDate.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
//        val currentDateTime: String = dateFormatter.format(nextmonday).toString()
//        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME
        val localTime = LocalTime.MIN // = 00:00
        val startTime = localTime.plusHours(7) // = 07:00 inicio de jornada
        timeArray.add(startTime.toString())
        val finishTime = localTime.plusHours(22) // = 22:00 fin jornada
        val interval: Long = 10 //  intervalo de 10 minutos
        val appointmentDurationMin: Long = 20 // duracion del turno
        var timeCount = startTime // contador
        while (timeCount <= finishTime ) {
            timeArray.add(timeCount.toString())
            timeCount = timeCount.plusMinutes(interval+appointmentDurationMin)
        }

        // instanciamos los adaptadores para los array de los dropdown
        val timeArrayAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), R.layout.dropdown_item, timeArray)
        val intervalArrayAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), R.layout.dropdown_item, intervalArray)
        val durationArrayAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), R.layout.dropdown_item, durationArray)
        until.adapter = timeArrayAdapter
        since.adapter = timeArrayAdapter
        appointmentInterval.adapter = intervalArrayAdapter
        appointmentDuration.adapter = durationArrayAdapter



        dateTextView = binding.todayDate
        dateTextView.text = currentDate.toString()


        // escondemos contenedor de profesionales
        layoutProfessionalInput = binding.professionalInput
        layoutProfessionalInput.isVisible = false

        // ejecutamos funcion para obtener especialidades
        getSpecialityData() // obtenemos array de especialidades

        // realizamos las instancias para autocomplete
        specialityArrayName = arrayListOf<String?>()
        specialtyDataArray = arrayListOf<SpecialtyData>()
        professionalArrayName = arrayListOf<String?>()
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
            layoutProfessionalInput.isVisible = true // hacemos que el contenedor con el input sea visible
        }

        autocompleteProfessional.setOnItemClickListener {adapterView, view, i, l ->
            for (item in professionalInfoArray) {
                var name = professionalArrayName[i].toString()
                if (name == item.name){
                    selectedProfessionalUid = item.id

                }
            }
            println("####UID ----------> $selectedProfessionalUid")

        }

        mkAppointmentsBtn.setOnClickListener {
            makeAppointments(selectedProfessionalUid)
            val s = since.selectedItemPosition
            val u = until.selectedItemPosition
            val i = appointmentInterval.selectedItemPosition
            val d = appointmentDuration.selectedItemPosition
            val sinceTimeText = timeArray[s].toString()
            val untilTimeText = timeArray[u].toString()
            val selectedInterval = intervalArray[i].toLong()
            val selectedDuration = durationArray[i].toLong()
            var sinceLocalTime = LocalTime.parse(sinceTimeText)
            var untilLocalTime = LocalTime.parse(untilTimeText)
            var currentDate = LocalDate.now()
            var dayOfWeek: String = currentDate.dayOfWeek.toString()
            var capitalizeAtteDays = arrayListOf<String>()
            var atteTime: ArrayList<String> = arrayListOf<String>()
            var availableAppointments: ArrayList<AvailableAppointment> = arrayListOf<AvailableAppointment>()
            var atteTimeDBObject = AvailableAppointment()
            var countTime: LocalTime
            if (atteDays.lun) { capitalizeAtteDays.add("MONDAY") }
            if (atteDays.mar) { capitalizeAtteDays.add("TUESDAY") }
            if (atteDays.mie) { capitalizeAtteDays.add("WEDNESDAY") }
            if (atteDays.jue) { capitalizeAtteDays.add("THURSDAY") }
            if (atteDays.vie) { capitalizeAtteDays.add("FRIDAY") }
            if (atteDays.sab) { capitalizeAtteDays.add("SATURDAY") }
            if (atteDays.dom) { capitalizeAtteDays.add("SUNDAY") }
            for (item in capitalizeAtteDays) {
                println(item)
                if(dayOfWeek == item) {
                    var weekCounter = 1
                    var date: LocalDate = currentDate
                    while (weekCounter <= 4) {
                        countTime = sinceLocalTime
                        atteTime.clear()
                        while (countTime <= untilLocalTime){
                            atteTime.add(countTime.toString())
                            countTime = countTime.plusMinutes(selectedDuration+selectedInterval)
                        }

                        atteTimeDBObject.date = date.toString()
                        atteTimeDBObject.timeList = atteTime
                        var dbRef = FirebaseDatabase.getInstance().getReference("users/professionals/$selectedProfessionalUid/AvailableAppointments/$date")
                        dbRef.setValue(atteTimeDBObject)
                        date = date.plusDays(7)
                        availableAppointments.add(atteTimeDBObject)
                        weekCounter++
                    }

                }
                if (dayOfWeek != item){
                    var date: LocalDate = currentDate.plusDays(1)
                    var newWeekDay: String = date.dayOfWeek.toString()
                    while (newWeekDay != item) {
                        date.plusDays(1)
                    }
                    var weekCounter = 1
                    while (weekCounter <= 4) {
                        countTime = sinceLocalTime
                        atteTime.clear()
                        while (countTime <= untilLocalTime){
                            atteTime.add(countTime.toString())
                            countTime = countTime.plusMinutes(selectedDuration+selectedInterval)
                        }
                        atteTimeDBObject.date = date.toString()
                        atteTimeDBObject.timeList = atteTime
                        var dbRef = FirebaseDatabase.getInstance().getReference("users/professionals/$selectedProfessionalUid/AvailableAppointments/$date")
                        dbRef.setValue(atteTimeDBObject)
                        date = date.plusDays(7)
                        availableAppointments.add(atteTimeDBObject)
                        weekCounter++
                    }

                }

            }

        }







        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // funcion buscar professionales por UID, usa INNER JOIN
    private fun getProfessionalUid(speciality:String) {  // pasamos la especialidad en la cual queremos encontrar professionales
        val path: String = "specialities/$speciality/professionals" // creamos el path
        dbRef = FirebaseDatabase.getInstance().getReference(path) // creamos la instancia con ese path
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) { //hacemos snapshot a los professionales de esa especialidad
                if (snapshot.exists()){
                    for (uidSnapshot in snapshot.children){ // en este bucle:
                        val uid = uidSnapshot.getValue<String>() // obtendremos los valores uid dentro del nodo professionals
                        profRef = FirebaseDatabase.getInstance().getReference("users/professionals/$uid") // los buscaremos en el nodo user/professionals
                        profRef.addValueEventListener(object: ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()){
                                    val professional: ProfessionalInfoDataClass? = snapshot.getValue(ProfessionalInfoDataClass::class.java)
                                    professionalInfoArray.add(professional!!)
                                    val name = professional.name.toString() // y obtendremos su nombre
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



    private fun makeAppointments(uid: String){
        mkAppUidRef = FirebaseDatabase.getInstance().getReference("users/professionals/$uid")
        var mkAppAtteDaysRef = FirebaseDatabase.getInstance().getReference("users/professionals/$uid/atteDays")
        if (sun.isChecked) {atteDays.dom=true}
        if (mon.isChecked) {atteDays.lun=true}
        if (tue.isChecked) {atteDays.mar=true}
        if (wed.isChecked) {atteDays.mie=true}
        if (thu.isChecked) {atteDays.jue=true}
        if (fri.isChecked) {atteDays.vie=true}
        if (sat.isChecked) {atteDays.sab=true}
        mkAppAtteDaysRef.setValue(atteDays)

        println("####### $atteDays")

    }



}
package com.morellana.turneroapp.ui

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
import com.morellana.turneroapp.databinding.FragmentNewAppointmentBinding
import com.morellana.turneroapp.dataclass.Profesional
import com.morellana.turneroapp.dataclass.Speciality

class NewAppointment : Fragment() {
    // variables de trabajo
    private var _binding: FragmentNewAppointmentBinding? = null
    private val binding get() = _binding!!
    lateinit var specialityArray: ArrayList<Speciality>
    lateinit var specialityArrayName: ArrayList<String?>
    lateinit var professionalArrayName: ArrayList<String?>
    private lateinit var dbRef: DatabaseReference
    lateinit var profRef: DatabaseReference
    lateinit var specialityInput: TextInputLayout
    private lateinit var autocompleteSpeciality: AutoCompleteTextView
    private lateinit var autocompleteProfessional: AutoCompleteTextView
    lateinit var layoutProfessionalInput: LinearLayout


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

        // escondemos contenedor de profesionales
        layoutProfessionalInput = binding.professionalInput
        layoutProfessionalInput.isVisible = false

        // ejecutamos funcion para obtener especialidades
        getSpecialityData() // obtenemos array de especialidades

        // realizamos las instancias para autocomplete
        specialityArrayName = arrayListOf<String?>()
        specialityArray = arrayListOf<Speciality>()
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
                        profRef = FirebaseDatabase.getInstance().getReference("users/professionals/$uid") // los buscaremos en el nodo user/professionals
                        profRef.addValueEventListener(object: ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()){
                                    val professional: Profesional? = snapshot.getValue(Profesional::class.java)
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
                        val specialities = specialitySnapshot.getValue(Speciality::class.java)
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







}
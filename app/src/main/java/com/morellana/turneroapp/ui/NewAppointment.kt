package com.morellana.turneroapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.morellana.turneroapp.R
import com.morellana.turneroapp.databinding.FragmentNewAppointmentBinding
import com.morellana.turneroapp.dataclass.Profesional
import com.morellana.turneroapp.dataclass.Speciality

class NewAppointment : Fragment() {
    private var _binding: FragmentNewAppointmentBinding? = null
    private val binding get() = _binding!!
    lateinit var specialityArray: ArrayList<Speciality>
    lateinit var specialityArrayName: ArrayList<String?>
    lateinit var professionalArrayName: ArrayList<String?>
    lateinit var dbRef: DatabaseReference
    lateinit var profRef: DatabaseReference
    lateinit var specialityInput: TextInputLayout
    lateinit var autocompleteSpeciality: AutoCompleteTextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNewAppointmentBinding.inflate(inflater, container, false)

        getSpecialityData() // obtenemos array de especialidades


        specialityArrayName = arrayListOf<String?>()
        specialityArray = arrayListOf<Speciality>()
        professionalArrayName = arrayListOf<String?>()
        specialityInput = binding.newAppointmentSpeciality

        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, specialityArrayName)
        autocompleteSpeciality = binding.autoCompleteSpeciality
        autocompleteSpeciality.setAdapter(arrayAdapter)


        autocompleteSpeciality.setOnItemClickListener { adapterView, view, i, l -> //seteamos un click listener para encontrar el valor seleccionado
            val value: String = specialityArrayName[i].toString().lowercase() // con la posicion del array lo buscamos y lo pasamos
            binding.autoCompleteDoctor.setText("")
            binding.container2.visibility = View.VISIBLE

            getProfessionalUid(value)  // corremos funcion para llenar el array de profesionales y mostramos el mismo input

            val arrayAdapter2 = ArrayAdapter(requireContext(), R.layout.dropdown_item, professionalArrayName)
            binding.autoCompleteDoctor.setAdapter(arrayAdapter2)
            binding.autoCompleteDoctor.setOnItemClickListener { adapterView, view, i, l ->

            }

        }

        return binding.root
    }
    //
    private fun getProfessionalUid(speciality:String) {
        val path: String = "specialities/$speciality/professionals"
        dbRef = FirebaseDatabase.getInstance().getReference(path)
        dbRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (uidSnapshot in snapshot.children){
                        val uid = uidSnapshot.getValue<String>()
                        profRef = FirebaseDatabase.getInstance().getReference("users/professionals/$uid")
                        profRef.addValueEventListener(object: ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()){
                                    val professional: Profesional? = snapshot.getValue(Profesional::class.java)
                                    val name = professional?.name.toString()
                                    professionalArrayName.add(name)
                                    Log.i("ARRAY", professionalArrayName.toString())
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })

                    }


                }
                else {
                    Toast.makeText(context, "SNAPSHOT VACIO", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

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
package com.morellana.turneroapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.morellana.turneroapp.R
import com.morellana.turneroapp.adapters.SpecialityCardAdapter
import com.morellana.turneroapp.databinding.FragmentNewAppointmentBinding
import com.morellana.turneroapp.dataclass.Speciality

class NewAppointment : Fragment() {
    private var _binding: FragmentNewAppointmentBinding? = null
    private val binding get() = _binding!!
    lateinit var specialityArray: ArrayList<Speciality>
    lateinit var specialityArrayName: ArrayList<String?>
    lateinit var dbRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNewAppointmentBinding.inflate(inflater, container, false)

        getSpecialityData()

        specialityArrayName = arrayListOf<String?>()
        specialityArray = arrayListOf<Speciality>()
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, specialityArrayName)
        binding.autoCompleteSpeciality.setAdapter(arrayAdapter)

        return binding.root
    }

    override fun onDestroyView() {
     super.onDestroyView()
     _binding = null
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
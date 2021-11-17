package com.morellana.turneroapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.morellana.turneroapp.R
import com.morellana.turneroapp.adapters.DoctorCardAdapter
import com.morellana.turneroapp.adapters.SpecialityCardAdapter
import com.morellana.turneroapp.dataclass.Speciality

class SpecialistFragment : Fragment() {
    private lateinit var dbRef : DatabaseReference
    lateinit var specialitiesRecyclerView: RecyclerView
    lateinit var specialitiesArrayList: ArrayList<Speciality>
    lateinit var adapter: DoctorCardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_specialist, container, false)

        specialitiesRecyclerView = view.findViewById(R.id.specialitiesRecyclerView)

        specialitiesArrayList = arrayListOf<Speciality>()

        getSpecialityData()
        //Estas lineas son para poner el recycler de forma horizontal
        //specialitiesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        //specialitiesRecyclerView.setHasFixedSize(true)


        return view
    }

    private fun getSpecialityData() {
        dbRef = FirebaseDatabase.getInstance().getReference("specialities")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (specialitySnapshot in snapshot.children){
                        val specialities = specialitySnapshot.getValue(Speciality::class.java)
                        specialitiesArrayList.add(specialities!!)
                    }
                    specialitiesRecyclerView.adapter = SpecialityCardAdapter(specialitiesArrayList)
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}
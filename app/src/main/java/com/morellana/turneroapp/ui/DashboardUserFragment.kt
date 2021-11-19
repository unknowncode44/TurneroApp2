package com.morellana.turneroapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.morellana.turneroapp.R
import com.morellana.turneroapp.SearchDoctor
import com.morellana.turneroapp.adapters.DoctorCardAdapter
import com.morellana.turneroapp.adapters.OnlineDoctorsAdapter
import com.morellana.turneroapp.adapters.SpecialityCardAdapter
import com.morellana.turneroapp.dataclass.OnlineDoctor
import com.morellana.turneroapp.dataclass.Profesional
import com.morellana.turneroapp.dataclass.Speciality

class DashboardUserFragment : Fragment() {

    lateinit var dbRef : DatabaseReference
    lateinit var doctorCardsRecyclerView: RecyclerView
    lateinit var specialitiesRecyclerView: RecyclerView
    lateinit var availableRecyclerView: RecyclerView
    lateinit var professionalArrayList: ArrayList<Profesional>
    lateinit var specialitiesArrayList: ArrayList<Speciality>
    lateinit var onlineArrayList: ArrayList<OnlineDoctor>
    lateinit var adapter: DoctorCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard_user, container, false)

        val search: FloatingActionButton = view.findViewById(R.id.search)

        search.setOnClickListener {
            val searchIntent = Intent(context, SearchDoctor::class.java)
            startActivity(searchIntent)
            activity?.overridePendingTransition(R.anim.enter, R.anim.leave)
        }

        doctorCardsRecyclerView = view.findViewById(R.id.recycler_dr_cards)
        doctorCardsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        doctorCardsRecyclerView.setHasFixedSize(true)

        specialitiesRecyclerView = view.findViewById(R.id.specialitiesRecyclerView)
        specialitiesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        specialitiesRecyclerView.setHasFixedSize(true)

        availableRecyclerView = view.findViewById(R.id.available_doctor_recycler)
        availableRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        specialitiesRecyclerView.setHasFixedSize(true)


        specialitiesArrayList = arrayListOf<Speciality>()
        professionalArrayList = arrayListOf<Profesional>()
        onlineArrayList = arrayListOf<OnlineDoctor>()

        getProfesionalData()
        getSpecialityData()
        getAvailableDoctor()

        return view
    }

    private fun getProfesionalData() {
        dbRef = FirebaseDatabase.getInstance().getReference("users/professionals")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (professionalSnapshot in snapshot.children){
                        val professionals = professionalSnapshot.getValue(Profesional::class.java)
                        professionalArrayList.add(professionals!!)
                    }
                    doctorCardsRecyclerView.adapter = DoctorCardAdapter(professionalArrayList)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
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
                        specialitiesArrayList.add(specialities!!)
                    }
                    specialitiesRecyclerView.adapter = SpecialityCardAdapter(specialitiesArrayList)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    private fun getAvailableDoctor() {
        dbRef = FirebaseDatabase.getInstance().getReference("online")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (onlineSnapshot in snapshot.children){
                        val online = onlineSnapshot.getValue(OnlineDoctor::class.java)
                        onlineArrayList.add(online!!)
                    }
                    availableRecyclerView.adapter = OnlineDoctorsAdapter(onlineArrayList)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}